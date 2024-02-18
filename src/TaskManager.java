import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private static int ID = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int getID() {
        return ID++;
    }

    public void addTask(Task task) {
        int id = getID();
        task.setID(id);
        tasks.put(id, task);
    }

    public void addSubtask(Subtask subtask) {
        int id = getID();
        for (Epic epic : epics.values()) {
            if (epic.name.equals(subtask.epicName)) {
                epic.addSubtaskInEpic(subtask);
                subtask.setID(id);
                subtasks.put(id, subtask);
                return;
            }
        }
    }

    private void addSubtask2(Subtask subtask) {
        int id = getID();
        subtask.setID(id);
        subtasks.put(id, subtask);
    }

    public void addEpic(Epic epic) {
        int id = getID();
        epic.setID(id);
        epics.put(id, epic);
        for (Subtask subtask : epic.subtasks) {
            addSubtask2(subtask);
        }
        redefineStatus(epic);
    }

    public void updateTask(Task newTask) {
        for (Task task : tasks.values()) {
            if (task.getID() == newTask.getID()) {
                tasks.put(newTask.getID(), newTask);
            }
        }
    }

    public void updateSubtask(Subtask newSubtask) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getID() == newSubtask.getID()) {
                Subtask oldSubtask = subtask;
                subtasks.put(newSubtask.getID(), newSubtask);
                redefineEpic(newSubtask, oldSubtask);
            }
        }
    }

    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getID());

        for (Subtask subtask : oldEpic.subtasks) {
            subtasks.remove(subtask.getID());
        }

        for (Subtask subtask : epic.subtasks) {
            addSubtask2(subtask);
        }

        epics.put(epic.getID(), epic);
        redefineStatus(epics.get(epic.getID()));
    }

    private void redefineEpic(Subtask newSubtask, Subtask oldSubtask) {
        for (Epic epic : epics.values()) {
            if (epic.name.equals(newSubtask.epicName)) {
                epic.subtasks.remove(oldSubtask);
                epic.subtasks.add(newSubtask);
                redefineStatus(epic);
                return;
            }
        }
    }

    private void redefineStatus(Epic epic) {
        boolean status_NEW = false;
        boolean status_IN_PROGRESS = false;
        boolean status_DONE = false;

        if (epic.subtasks.isEmpty()) {
            epic.status = TaskStatus.NEW;
            return;
        }

        for (Subtask subtask : epic.subtasks) {
            if (subtask.status == TaskStatus.NEW) {
                status_NEW = true;
            } else if (subtask.status == TaskStatus.IN_PROGRESS) {
                status_IN_PROGRESS = true;
            } else if (subtask.status == TaskStatus.DONE) {
                status_DONE = true;
            }
        }

        if (status_NEW && !status_IN_PROGRESS && !status_DONE) {
            epic.status = TaskStatus.NEW;
        } else if (status_DONE && !status_NEW && !status_IN_PROGRESS) {
            epic.status = TaskStatus.DONE;
        } else {
            epic.status = TaskStatus.IN_PROGRESS;
        }
    }

    public ArrayList<Task> getTask() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpic() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    public void clearTask() {
        tasks.clear();
    }

    public void clearEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
            redefineStatus(epic);
        }
    }

    public Task getTaskByID(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskByID(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicByID(int id) {
        return epics.get(id);
    }

    public void removeTaskByID(int id) {
        tasks.remove(id);
    }

    public void removeSubtaskByID(int id) {
        for (Epic epic : epics.values()) {
            if (epic.subtasks.contains(subtasks.get(id))) {
                epic.subtasks.remove(subtasks.get(id));
            }
        }
        subtasks.remove(id);
    }

    public void removeEpicByID(int id) {
        for (Subtask subtask : epics.get(id).subtasks) {
            if (subtasks.containsValue(subtask)) {
                subtasks.remove(subtask.getID());
            }
        }
        epics.remove(id);
    }
}
