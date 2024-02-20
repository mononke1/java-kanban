import taskmanagement.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("задача 1", "dfsfsfds", TaskStatus.NEW);
        Task task2 = new Task("задача 2", "fdgdfgffdg", TaskStatus.NEW);

        Subtask subtask1 = new Subtask("1", "qweqwe", TaskStatus.NEW, "E1");
        Subtask subtask2 = new Subtask("2", "qweqwe", TaskStatus.NEW, "E1");
        Subtask subtask3 = new Subtask("3", "qweqwe", TaskStatus.NEW, "E2");

        ArrayList<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(subtask1);
        subtasks1.add(subtask2);

        ArrayList<Subtask> subtasks2 = new ArrayList<>();
        subtasks2.add(subtask3);

        Epic epic1 = new Epic("E1", "wewewe", subtasks1);
        Epic epic2 = new Epic("E2", "sddsds", subtasks2);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        System.out.println(taskManager.getEpic());
        System.out.println(taskManager.getTask());
        System.out.println(taskManager.getSubtask());

        //изменение статусов подзадач
        Subtask subtask5 = new Subtask("1", "qweqwe", TaskStatus.IN_PROGRESS, "E1");
        Subtask subtask6 = new Subtask("2", "qweqwe", TaskStatus.DONE, "E1");

        ArrayList<Subtask> subtasks3 = new ArrayList<>();
        subtasks3.add(subtask5);
        subtasks3.add(subtask6);

        //последним аргументом посылается id эпика в котором обновляются подзадачи
        Epic epic3 = new Epic("E1", "wewewe", subtasks3, 0);

        taskManager.updateEpic(epic3);

        System.out.println("\n");
        System.out.println(taskManager.getEpic());
        System.out.println(taskManager.getTask());
        System.out.println(taskManager.getSubtask());

        // обновление статусов задач
        Task task3 = new Task("задача 1", "dfsfsfds", TaskStatus.DONE, 5);
        Task task4 = new Task("задача 2", "fdgdfgffdg", TaskStatus.IN_PROGRESS, 6);

        taskManager.updateTask(task3);
        taskManager.updateTask(task4);

        System.out.println("\n");
        System.out.println(taskManager.getTask());

        //удаление одного эпика, одной задачи и одной подзадачи

        taskManager.removeTaskByID(5);
        taskManager.removeEpicByID(3);
        taskManager.removeSubtaskByID(7);

        System.out.println("\n");
        System.out.println(taskManager.getEpic());
        System.out.println(taskManager.getTask());
        System.out.println(taskManager.getSubtask());
    }
}
