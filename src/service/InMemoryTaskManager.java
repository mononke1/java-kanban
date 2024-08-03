package service;

import taskmanagement.*;

import java.time.Duration;
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
        prioritizedTasks = new TreeSet<>(new TaskComparator());
    }

    private int getID() {
        return ID++;
    }

    @Override
    public void addTask(Task task) throws TaskOverlapException {
        if (task.getId() == 0) {
            int id = getID();
            task.setId(id);
            addTaskToPrioritizedTasks(task);
            tasks.put(id, task);
        } else {
            if (isValidID(task)) {
                addTaskToPrioritizedTasks(task);
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
    public void addSubtask(Subtask subtask) throws TaskOverlapException {
        if (subtask.getId() == 0) {
            int id = getID();
            subtask.setId(id);
            addTaskToPrioritizedTasks(subtask);
            subtasks.put(id, subtask);
            epics.get(subtask.getEpicId()).addSubtaskInEpic(subtask);
            updateEpicTimes(epics.get(subtask.getEpicId()));
            redefineStatus(epics.get(subtask.getEpicId()));
        } else {
            if (isValidID(subtask)) {
                addTaskToPrioritizedTasks(subtask);
                subtasks.put(subtask.getId(), subtask);
                epics.get(subtask.getEpicId()).addSubtaskInEpic(subtask);
                updateEpicTimes(epics.get(subtask.getEpicId()));
                redefineStatus(epics.get(subtask.getEpicId()));
            }
        }
    }

    @Override
    public void addEpic(Epic epic) throws TaskOverlapException {
        if (epic.getId() == 0) {
            int id = getID();
            epic.setId(id);
            if (!epic.getSubtasks().isEmpty()) {
                for (Subtask subtask : epic.getSubtasks()) {
                    subtask.setEpicId(epic.getId());
                    addSubtask2(subtask);
                }
            }
            epics.put(id, epic);
            updateEpicTimes(epic);
            redefineStatus(epic);
        } else {
            if (isValidID(epic)) {
                epics.put(epic.getId(), epic);
                if (!epic.getSubtasks().isEmpty()) {
                    for (Subtask subtask : epic.getSubtasks()) {
                        addSubtask2(subtask);
                    }
                }
                updateEpicTimes(epic);
                redefineStatus(epic);
            }
        }
    }

    @Override
    public void updateTask(Task newTask) throws TaskOverlapException, TaskNotFoundException {
        Task task = Optional.ofNullable(tasks.get(newTask.getId()))
                .orElseThrow(() -> new TaskNotFoundException("задача с данным id не найдена"));

        if (prioritizedTasks.remove(task)) {
            addTaskToPrioritizedTasks(newTask);
        }

        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) throws TaskOverlapException, TaskNotFoundException {
        if (!subtasks.containsKey(newSubtask.getId())) {
            throw new TaskNotFoundException("задача с данным id не найдена");
        }
        Subtask subtask = subtasks.get(newSubtask.getId());
        if (prioritizedTasks.remove(subtask)) {
            addTaskToPrioritizedTasks(newSubtask);
        }
        subtasks.put(newSubtask.getId(), newSubtask);
        redefineEpic(newSubtask, subtask);
    }

    @Override
    public void updateEpic(Epic epic) throws TaskNotFoundException {
        if (!epics.containsKey(epic.getId())) {
            throw new TaskNotFoundException("задача с данным id не найдена");
        }
        Epic oldEpic = epics.get(epic.getId());

        epic.getSubtasks().forEach(subtask -> subtask.setEpicId(epic.getId()));

        oldEpic.getSubtasks().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            subtasks.remove(subtask.getId());
        });

        epic.getSubtasks().forEach(this::addSubtask2);

        epics.put(epic.getId(), epic);
        redefineStatus(epic);
        updateEpicTimes(epic);
    }

    private static void updateEpicTimes(Epic epic) {
        updateEpicStartTime(epic);
        updateEpicDuration(epic);
        updateEpicEndTime(epic);
    }

    private static void updateEpicStartTime(Epic epic) {
        Optional<LocalDateTime> earliestStartTime = epic.getSubtasks().stream()
                .map(Subtask::getStartTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min(Comparator.naturalOrder());

        epic.setStartTime(earliestStartTime.orElse(null));
    }

    private static void updateEpicDuration(Epic epic) {
        Duration totalDuration = epic.getSubtasks().stream()
                .filter(subtask -> subtask.getStartTime().isPresent())
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setDuration(totalDuration);
    }

    private static void updateEpicEndTime(Epic epic) {
        Optional<LocalDateTime> latestEndTime = epic.getSubtasks().stream()
                .filter(subtask -> subtask.getEndTime().isPresent())
                .flatMap(subtask -> subtask.getEndTime().stream())
                .max(Comparator.naturalOrder());

        epic.setEndTime(latestEndTime.orElse(null));
    }

    private void redefineEpic(Subtask newSubtask, Subtask oldSubtask) {
        epics.get(newSubtask.getEpicId()).getSubtasks().remove(oldSubtask);
        epics.get(newSubtask.getEpicId()).getSubtasks().add(newSubtask);
        redefineStatus(epics.get(newSubtask.getEpicId()));
        updateEpicTimes(epics.get(newSubtask.getEpicId()));
    }

    private void redefineStatus(Epic epic) {
        boolean statusNew = false;
        boolean statusInProgress = false;
        boolean statusDone = false;

        if (epic.getSubtasks().isEmpty() || epic.getSubtasks() == null) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() == TaskStatus.NEW) {
                statusNew = true;
            } else if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                statusInProgress = true;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                statusDone = true;
            }
        }

        if (statusNew && !statusInProgress && !statusDone) {
            epic.setStatus(TaskStatus.NEW);
        } else if (statusDone && !statusNew && !statusInProgress) {
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
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
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
        subtasks.values().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });

        subtasks.clear();

        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
            updateEpicTimes(epic);
            redefineStatus(epic);
        });
    }

    @Override
    public Task getTaskByID(int id) throws TaskNotFoundException {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            throw new TaskNotFoundException("задача с данным id не найдена");
        }
    }

    @Override
    public Subtask getSubtaskByID(int id) throws TaskNotFoundException {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            throw new TaskNotFoundException("задача с данным id не найдена");
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int id) throws TaskNotFoundException {
        if (!epics.containsKey(id)) {
            throw new TaskNotFoundException("Задача с данным ID не найдена");
        }
        historyManager.add(epics.get(id));
        return epics.get(id).getSubtasks();
    }

    @Override
    public Epic getEpicByID(int id) throws TaskNotFoundException {
        if (!epics.containsKey(id)) {
            throw new TaskNotFoundException("Задача с данным ID не найдена");
        }
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void removeTaskByID(int id) throws TaskNotFoundException {
        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException("Задача с данным ID не найдена");
        }
        Task task = tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(task);
    }

    @Override
    public void removeSubtaskByID(int id) throws TaskNotFoundException {
        if (!subtasks.containsKey(id)) {
            throw new TaskNotFoundException("Задача с данным ID не найдена");
        }
        historyManager.remove(id);
        prioritizedTasks.remove(subtasks.get(id));

        Subtask subtaskToRemove = subtasks.get(id);

        epics.values().stream()
                .filter(epic -> epic.getSubtasks().contains(subtaskToRemove))
                .findFirst().ifPresent(epic -> {
            epic.getSubtasks().remove(subtaskToRemove);
            updateEpicTimes(epic);
            redefineStatus(epic);
        });

        subtasks.remove(id);
    }

    @Override
    public void removeEpicByID(int id) throws TaskNotFoundException {
        if (!epics.containsKey(id)) {
            throw new TaskNotFoundException("Задача с данным ID не найдена");
        }
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

    private void addTaskToPrioritizedTasks(Task task) {
        if (task.getStartTime().isPresent()) {
            try {
                if (!hasTaskOverlap(task)) {
                    prioritizedTasks.add(task);
                } else {
                    throw new TaskOverlapException("Задача пересекается с существующей задачей.");
                }
            } catch (TaskOverlapException e) {
                throw new TaskOverlapException("Задача пересекается с существующей задачей.", e);
            }
        } else {
            prioritizedTasks.add(task);
        }
    }

    protected boolean hasTaskOverlap(Task task) {
        LocalDateTime newStartTime = task.getStartTime().orElse(null);
        LocalDateTime newEndTime = task.getEndTime().orElse(null);

        if (newStartTime == null || newEndTime == null) {
            return false;
        }

        for (Task existingTask : prioritizedTasks) {
            if (task.getId() == existingTask.getId()) {
                continue;
            }

            LocalDateTime existStartTime = existingTask.getStartTime().orElse(null);
            LocalDateTime existEndTime = existingTask.getEndTime().orElse(null);

            if (existStartTime == null || existEndTime == null) {
                continue;
            }

            if (!(newEndTime.isBefore(existStartTime) || newStartTime.isAfter(existEndTime))) {
                return true;
            }
        }
        return false;
    }

    private void addSubtask2(Subtask subtask) throws TaskOverlapException {
        int id = getID();
        subtask.setId(id);
        addTaskToPrioritizedTasks(subtask);
        subtasks.put(id, subtask);
    }
}