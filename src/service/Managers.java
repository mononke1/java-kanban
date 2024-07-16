package service;

import java.io.File;
import java.nio.file.Path;

public class Managers {

    private static final String TASKS_FILE_PATH = "tasks.csv";

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File(TASKS_FILE_PATH));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}