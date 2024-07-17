package service;

import taskmanagement.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;
    protected final TreeSet<Task> prioritizedTasks;
    protected static int ID = 1;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(
                task -> task.getStartTime()
                        .orElseThrow(() -> new IllegalArgumentException("Не установлено время StartTime"))
        ));
    }

    private int getID() {
        return ID++;
    }

    @Override
    public void addTask(Task task) {
        if (task.getId() == 0) {
            int id = getID();
            task.setId(id);
            if (task.getStartTime().isPresent()) {
                try {
                    if (!checkPrioritizedTasks(task.getStartTime().get())) {
                        prioritizedTasks.add(task);
                    } else {
                        throw new TaskOverlapException("Задача пересекается с существующей задачей.");
                    }
                } catch (Exception e) {
                    throw new TaskOverlapException("Задача пересекается с существующей задачей.", e);
                }
            }
            tasks.put(id, task);
        } else {
            if (isValidID(task)) {
                if (task.getStartTime().isPresent()) {
                    try {
                        if (!checkPrioritizedTasks(task.getStartTime().get())) {
                            prioritizedTasks.add(task);
                        } else {
                            throw new TaskOverlapException("Задача пересекается с существующей задачей.");
                        }
                    } catch (Exception e) {
                        throw new TaskOverlapException("Задача пересекается с существующей задачей.", e);
                    }
                }
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
            if (subtask.getStartTime().isPresent()) {
                try {
                    if (!checkPrioritizedTasks(subtask.getStartTime().get())) {
                        prioritizedTasks.add(subtask);
                    } else {
                        throw new TaskOverlapException("Задача пересекается с существующей задачей.");
                    }
                } catch (Exception e) {
                    throw new TaskOverlapException("Задача пересекается с существующей задачей.", e);
                }
            }
            subtasks.put(id, subtask);
            epics.get(subtask.getEpicId()).addSubtaskInEpic(subtask);
            redefineStatus(epics.get(subtask.getEpicId()));
        } else {
            if (isValidID(subtask)) {
                if (subtask.getStartTime().isPresent()) {
                    try {
                        if (!checkPrioritizedTasks(subtask.getStartTime().get())) {
                            prioritizedTasks.add(subtask);
                        } else {
                            throw new TaskOverlapException("Задача пересекается с существующей задачей.");
                        }
                    } catch (Exception e) {
                        throw new TaskOverlapException("Задача пересекается с существующей задачей.", e);
                    }
                }
                subtasks.put(subtask.getId(), subtask);
                epics.get(subtask.getEpicId()).addSubtaskInEpic(subtask);
                redefineStatus(epics.get(subtask.getEpicId()));
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
        Task task = tasks.get(newTask.getId());
        if (prioritizedTasks.remove(task) && newTask.getStartTime().isPresent()) {
            try {
                if (!checkPrioritizedTasks(newTask.getStartTime().get())) {
                    prioritizedTasks.add(newTask);
                } else {
                    throw new TaskOverlapException("Задача пересекается с существующей задачей.");
                }
            } catch (Exception e) {
                throw new TaskOverlapException("Задача пересекается с существующей задачей.", e);
            }
        }
        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        Subtask subtask = subtasks.get(newSubtask.getId());
        if (prioritizedTasks.remove(subtask) && newSubtask.getStartTime().isPresent()) {
            try {
                if (!checkPrioritizedTasks(newSubtask.getStartTime().get())) {
                    prioritizedTasks.add(newSubtask);
                } else {
                    throw new TaskOverlapException("Задача пересекается с существующей задачей.");
                }
            } catch (Exception e) {
                throw new TaskOverlapException("Задача пересекается с существующей задачей.", e);
            }
        }
        subtasks.put(newSubtask.getId(), newSubtask);
        redefineEpic(newSubtask, subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());

        epic.getSubtasks().forEach(subtask -> subtask.setEpicId(epic.getId()));

        oldEpic.getSubtasks().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            subtasks.remove(subtask.getId());
        });

        epic.getSubtasks().forEach(this::addSubtask2);

        epics.put(epic.getId(), epic);
        redefineStatus(epic);
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
        tasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
        });
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
        subtasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        });

        subtasks.clear();

        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
            redefineStatus(epic);
        });
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
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void removeSubtaskByID(int id) {
        historyManager.remove(id);
        prioritizedTasks.remove(subtasks.get(id));
        Subtask subtaskToRemove = subtasks.get(id);
        epics.values().stream()
                .filter(epic -> epic.getSubtasks().contains(subtaskToRemove))
                .findFirst()
                .ifPresent(epic -> epic.getSubtasks().remove(subtaskToRemove));
        subtasks.remove(id);
    }

    @Override
    public void removeEpicByID(int id) {
        historyManager.remove(id);

        epics.get(id).getSubtasks().stream()
                .filter(subtasks::containsValue)
                .forEach(subtask -> {
                    prioritizedTasks.remove(subtask);
                    historyManager.remove(subtask.getId());
                    subtasks.remove(subtask.getId());
                });

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

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private boolean checkPrioritizedTasks(LocalDateTime startTime) {
        return prioritizedTasks.stream()
                .anyMatch(task -> task.getEndTime().isAfter(startTime));
    }

    private void addSubtask2(Subtask subtask) {
        int id = getID();
        subtask.setId(id);
        if (subtask.getStartTime().isPresent()) {
            prioritizedTasks.add(subtask);
        }
        subtasks.put(id, subtask);
    }
}