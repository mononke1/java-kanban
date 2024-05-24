import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;
import taskmanagement.*;

import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Task task1 = new Task("task11111", "test", TaskStatus.NEW);
        Task task2 = new Task("task22222", "test2", TaskStatus.IN_PROGRESS);

        Subtask subtask1 = new Subtask("111111", "test", TaskStatus.NEW);
        Subtask subtask2 = new Subtask("su324234btask1", "test", TaskStatus.DONE);
        Subtask subtask3 = new Subtask("subt234234ask1", "test", TaskStatus.IN_PROGRESS, 3);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        Epic epic = new Epic("epic1", "test", subtasks);

        TaskManager manager = Managers.getDefault();

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic);
        manager.addSubtask(subtask3);

        System.out.println(manager.getTask());
        System.out.println(manager.getEpic());
        System.out.println(manager.getSubtask());
/*
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(new File("tasks.csv"));
        System.out.println(manager2.getTask());
        System.out.println(manager2.getEpic());
        System.out.println(manager2.getSubtask());
        Task task3 = new Task("task1", "test", TaskStatus.IN_PROGRESS);

        manager2.addTask(task3);*/
    }
}
