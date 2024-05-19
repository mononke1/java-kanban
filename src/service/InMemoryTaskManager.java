package service;

import taskmanagement.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;
    private static int ID = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    private int getID() {
        return ID++;
    }

    @Override
    public void addTask(Task task) {
        if (task.getId() == 0) {
            int id = getID();
            task.setId(id);
            tasks.put(id, task);
        } else {
            if (isValidID(task)) {
                tasks.put(task.getId(), task);
            }
        }
    }

    private boolean isValidID(Task task) {
        return task.getId() > 0 &&
                !tasks.containsKey(task.getId()) &&
                !epics.containsKey(task.getId()) &&
                !subtasks.containsKey(task.getId());
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask.getId() == 0) {
            int id = getID();
            subtask.setId(id);
            subtasks.put(id, subtask);
            epics.get(subtask.getEpicId()).addSubtaskInEpic(subtask);
        } else {
            if (isValidID(subtask)) {
                subtasks.put(subtask.getId(), subtask);
                epics.get(subtask.getEpicId()).addSubtaskInEpic(subtask);
            }
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic.getId() == 0) {
            int id = getID();
            epic.setId(id);
            epics.put(id, epic);
            if (!epic.getSubtasks().isEmpty()) {
                for (Subtask subtask : epic.getSubtasks()) {
                    subtask.setEpicId(epic.getId());
                    addSubtask2(subtask);
                }
            }
            redefineStatus(epic);
        } else {
            if (isValidID(epic)) {
                epics.put(epic.getId(), epic);
                if (!epic.getSubtasks().isEmpty()) {
                    for (Subtask subtask : epic.getSubtasks()) {
                        addSubtask2(subtask);
                    }
                }
                redefineStatus(epic);
            }
        }
    }

    @Override
    public void updateTask(Task newTask) {
        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getId() == newSubtask.getId()) {
                subtasks.put(newSubtask.getId(), newSubtask);
                System.out.println("qweqweqwe");
                redefineEpic(newSubtask, subtask);
            }
        }

    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        if (!epic.getSubtasks().isEmpty()) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtask.setEpicId(epic.getId());
            }
        }

        for (Subtask subtask : oldEpic.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }

        for (Subtask subtask : epic.getSubtasks()) {
            addSubtask2(subtask);
        }

        epics.put(epic.getId(), epic);
        redefineStatus(epics.get(epic.getId()));
    }

    private void redefineEpic(Subtask newSubtask, Subtask oldSubtask) {
        epics.get(newSubtask.getEpicId()).getSubtasks().remove(oldSubtask);
        epics.get(newSubtask.getEpicId()).getSubtasks().add(newSubtask);
        redefineStatus(epics.get(newSubtask.getEpicId()));
    }

    private void redefineStatus(Epic epic) {
        boolean status_NEW = false;
        boolean status_IN_PROGRESS = false;
        boolean status_DONE = false;

        if (epic.getSubtasks().isEmpty() || epic.getSubtasks() == null) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() == TaskStatus.NEW) {
                status_NEW = true;
            } else if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                status_IN_PROGRESS = true;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                status_DONE = true;
            }
        }

        if (status_NEW && !status_IN_PROGRESS && !status_DONE) {
            epic.setStatus(TaskStatus.NEW);
        } else if (status_DONE && !status_NEW && !status_IN_PROGRESS) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public ArrayList<Task> getTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTask() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void clearEpic() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }

        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtask() {
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }

        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            redefineStatus(epic);
        }
    }

    @Override
    public Task getTaskByID(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicByID(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public void removeTaskByID(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeSubtaskByID(int id) {
        historyManager.remove(id);
        for (Epic epic : epics.values()) {
            if (epic.getSubtasks().contains(subtasks.get(id))) {
                epic.getSubtasks().remove(subtasks.get(id));
            }
        }
        subtasks.remove(id);
    }

    @Override
    public void removeEpicByID(int id) {
        historyManager.remove(id);
        for (Subtask subtask : epics.get(id).getSubtasks()) {
            if (subtasks.containsValue(subtask)) {
                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            }
        }
        epics.remove(id);
    }

    @Override
    public int getId(Task task) {
        return task.getId();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void addSubtask2(Subtask subtask) {
        int id = getID();
        subtask.setId(id);
        subtasks.put(id, subtask);
    }
}
