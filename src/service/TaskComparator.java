package service;
import taskmanagement.Task;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

public class TaskComparator  implements Comparator<Task> {
    @Override
    public int compare(Task task1, Task task2) {
        Optional<LocalDateTime> startTime1 = task1.getStartTime();
        Optional<LocalDateTime> startTime2 = task2.getStartTime();

        if (startTime1.isPresent() && startTime2.isPresent()) {
            return startTime1.get().compareTo(startTime2.get());
        } else if (startTime1.isPresent()) {
            return -1;
        } else if (startTime2.isPresent()) {
            return 1;
        } else {
            return Integer.compare(task1.getId(), task2.getId());
        }
    }
}
