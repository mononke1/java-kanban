import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;
import taskmanagement.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        LocalDateTime time1 = LocalDateTime.of(2024, 10, 10, 8, 0);
        LocalDateTime time2 = LocalDateTime.of(2024, 10, 10, 9, 0);
        LocalDateTime time3 = LocalDateTime.of(2024, 10, 10, 10, 0);
        LocalDateTime time4 = LocalDateTime.of(2024, 10, 10, 11, 0);
        LocalDateTime time5 = LocalDateTime.of(2024, 10, 10, 12, 0);
        LocalDateTime time6 = LocalDateTime.of(2024, 10, 10, 13, 0);
        LocalDateTime time7 = LocalDateTime.of(2024, 10, 10, 14, 0);
        Duration duration = Duration.ofMinutes(30);

        Task task1 = new Task("task1", "test1", TaskStatus.NEW, duration, time1);
        Task task2 = new Task("task2", "test2", TaskStatus.IN_PROGRESS, duration, time2);
        TaskManager managers = Managers.getDefault();
        managers.addTask(task1);
        managers.addTask(task2);
     //   managers.updateTask(task2);
     //   managers.addTask(task2);
        System.out.println("\n");
        System.out.println(managers.getTask());
        System.out.println(managers.getPrioritizedTasks());
        System.out.println("\n");

     //   Task task3 = new Task("task3", "test3", TaskStatus.DONE, 1);
     //   managers.updateTask(task3);
     //   System.out.println("\n");
     //   System.out.println(managers.getTask());
     //   System.out.println(managers.getPrioritizedTasks());
     //   System.out.println("\n");
      //  managers.removeTaskByID(1);
      //  managers.clearTask();
     //   System.out.println(managers.getTask());
     //   System.out.println(managers.getPrioritizedTasks());

        Subtask subtask1 = new Subtask("subtask1", "test1", TaskStatus.IN_PROGRESS, duration, time3);
        Subtask subtask2 = new Subtask("subtask2", "test2", TaskStatus.IN_PROGRESS, duration, time4);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        Epic epic = new Epic("epic", "test", subtasks);
        managers.addEpic(epic);
        System.out.println(managers.getTask());
        System.out.println(managers.getSubtask());
        System.out.println(managers.getEpic());
        System.out.println(managers.getPrioritizedTasks());

        Subtask subtask3 = new Subtask("subtask3", "test3", TaskStatus.IN_PROGRESS, 3, duration, time5);
        managers.addSubtask(subtask3);

        System.out.println("\n");
        System.out.println(managers.getTask());
        System.out.println(managers.getSubtask());
        System.out.println(managers.getEpic());
        System.out.println(managers.getPrioritizedTasks());

        Subtask subtask4 = new Subtask("subtask4", "test4", TaskStatus.IN_PROGRESS, 3);
        managers.addSubtask(subtask4);
        System.out.println("\n");
        System.out.println(managers.getTask());
        System.out.println(managers.getSubtask());
        System.out.println(managers.getEpic());
        System.out.println(managers.getPrioritizedTasks());

        Task task3 = new Task("task1", "test1", TaskStatus.NEW);
        managers.addTask(task3);

        Epic epic2 = new Epic("epic", "test");
        managers.addEpic(epic2);
        System.out.println("\n");
        System.out.println(managers.getTask());
        System.out.println(managers.getSubtask());
        System.out.println(managers.getEpic());
        System.out.println(managers.getPrioritizedTasks());



/*
        File file = new File("tasks.csv");

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println(fileBackedTaskManager.getTask());
        System.out.println(fileBackedTaskManager.getSubtask());
        System.out.println(fileBackedTaskManager.getEpic());
        System.out.println(fileBackedTaskManager.getPrioritizedTasks());
*/

    }
}