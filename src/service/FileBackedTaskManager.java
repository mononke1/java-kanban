package service;

import taskmanagement.Epic;
import taskmanagement.Subtask;
import taskmanagement.Task;
import taskmanagement.TaskStatus;

import java.io.*;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String FIRST_LINE = String.format("%s,%s,%s,%s,%s,%s\n", "id", "type", "name", "status", "description", "epic");

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(FIRST_LINE);

            for (Task task : tasks.values()) {
                String line = toString(task);
                fileWriter.write(line);
            }

            for (Epic epic : epics.values()) {
                String line = toString(epic);
                fileWriter.write(line);
            }

            for (Subtask subtask : subtasks.values()) {
                String line = toString(subtask);
                fileWriter.write(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("исключение при записи в файл: " + file.getName(), e);
        }
    }

    private String toString(Task task) {
        StringBuilder line = new StringBuilder();
        line.append(task.getId()).append(",").append(task.getType()).append(",").append(task.getName())
                .append(",").append(task.getStatus()).append(",").append(task.getDescription()).append(",");
        if (task instanceof Subtask) {
            line.append(((Subtask) task).getEpicId());
        }
        line.append("\n");
        return line.toString();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (FileReader reader = new FileReader(file)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            int maxId = 0;
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                Task task = fromString(line);
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
                switch (task.getType()) {
                    case TASK:
                        fileBackedTaskManager.tasks.put(task.getId(),task);
                    case EPIC:
                        if (task instanceof Epic) {
                            fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                        }
                        break;
                    case SUBTASK:
                        if (task instanceof Subtask) {
                            fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                            fileBackedTaskManager.epics.get(((Subtask) task).getEpicId()).getSubtasks().add((Subtask) task);
                        }
                        break;
                }
            }
            ID = maxId + 1;
        } catch (IOException e) {
            throw new ManagerSaveException("исключение при чтении файла: " + file.getName(), e);
        }
        return fileBackedTaskManager;
    }

    private static Task fromString(String value) {
        String[] values = value.split(",");
        int taskId = Integer.parseInt(values[0]);
        TaskType taskType = TaskType.valueOf(values[1]);
        String taskName = values[2];
        TaskStatus taskStatus = TaskStatus.valueOf(values[3]);
        String taskDescription = values[4];
        int epicId;
        switch (taskType) {
            case EPIC:
                Epic epic = new Epic(taskName, taskDescription, new ArrayList<>(), taskId);
                epic.setStatus(taskStatus);
                return epic;
            case SUBTASK:
                epicId = Integer.parseInt(values[5]);
                return new Subtask(taskName, taskDescription, taskStatus, epicId, taskId);
        }
        return new Task(taskName, taskDescription, taskStatus, taskId);
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
    }

    @Override
    public void clearTask() {
        super.clearTask();
        save();
    }

    @Override
    public void clearEpic() {
        super.clearEpic();
        save();
    }

    @Override
    public void clearSubtask() {
        super.clearSubtask();
        save();
    }

    @Override
    public void removeTaskByID(int id) {
        super.removeTaskByID(id);
        save();
    }

    @Override
    public void removeSubtaskByID(int id) {
        super.removeSubtaskByID(id);
        save();
    }

    @Override
    public void removeEpicByID(int id) {
        super.removeEpicByID(id);
        save();
    }
}
