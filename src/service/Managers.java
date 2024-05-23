package service;

import java.io.File;
import java.nio.file.Path;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("tasks.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

