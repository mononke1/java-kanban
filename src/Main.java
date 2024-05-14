import service.Managers;
import service.TaskManager;
import taskmanagement.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("task1", "task", TaskStatus.NEW);
        Task task2 = new Task("task1", "task", TaskStatus.NEW);

        manager.addTask(task1);
        manager.addTask(task2);
        System.out.println("\n" + "1");
        System.out.println(manager.getTask());

        Task task1Update = new Task("task1", "task", TaskStatus.IN_PROGRESS, 1);
        manager.updateTask(task1Update);
        System.out.println(manager.getTask());
        System.out.println("\n" + "2");
        System.out.println(manager.getTaskByID(1));

        Epic epic = new Epic("epic1", "epic");
        manager.addEpic(epic);
        System.out.println("\n" + "3");
        System.out.println(manager.getEpic());

        Subtask subtask = new Subtask("subtask", "test", TaskStatus.NEW, 3);
        manager.addSubtask(subtask);
        System.out.println("\n" + "4");
        System.out.println(manager.getEpic());
        System.out.println(manager.getSubtask());

        Subtask subtaskUpdate = new Subtask("subtask", "test", TaskStatus.IN_PROGRESS, 3, 4);
        manager.updateSubtask(subtaskUpdate);

        System.out.println("\n" + "5");
        System.out.println(manager.getEpic());
        System.out.println(manager.getSubtask());

        Subtask subtask1 = new Subtask("name", "qweqwe", TaskStatus.DONE, 3);
        Subtask subtask2 = new Subtask("name", "qweqwe", TaskStatus.DONE, 3);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);

        System.out.println("\n" + "6");
        System.out.println(manager.getEpic());
        System.out.println(manager.getSubtask());

        Task task = new Task("name", "sdfsdf", TaskStatus.DONE, 14);
        manager.addTask(task);
        System.out.println("\n" + "7");
        System.out.println(manager.getTask());

        manager.getEpicByID(3);
        manager.getTaskByID(1);
        manager.getTaskByID(2);
        manager.getTaskByID(14);
        manager.getSubtaskByID(4);
        manager.getEpicByID(3);
        manager.getTaskByID(1);
        manager.getTaskByID(2);
        manager.getTaskByID(14);
        manager.getSubtaskByID(4);
        manager.getTaskByID(1);
        manager.getEpicByID(3);

        System.out.println(manager.getHistory());
    }
}
