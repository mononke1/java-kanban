package service;

import taskmanagement.Epic;
import taskmanagement.Subtask;
import taskmanagement.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    void addTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    void updateTask(Task newTask);

    void updateSubtask(Subtask newSubtask);

    void updateEpic(Epic epic);

    ArrayList<Task> getTask();

    ArrayList<Epic> getEpic();

    ArrayList<Subtask> getSubtask();

    void clearTask();

    void clearEpic();

    void clearSubtask();

    Task getTaskByID(int id);

    Subtask getSubtaskByID(int id);

    Epic getEpicByID(int id);

    void removeTaskByID(int id);

    void removeSubtaskByID(int id);

    void removeEpicByID(int id);

    int getId(Task task);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
