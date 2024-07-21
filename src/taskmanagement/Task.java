package taskmanagement;

import service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    private final String name;
    private final String description;
    private TaskStatus status;
    private int id;
    private final TaskType type;
    private Duration duration;
    private LocalDateTime startTime;

    public TaskType getType() {
        return type;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.type = TaskType.TASK;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
    }

    public Task(String name, String description, TaskStatus status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.type = TaskType.TASK;
    }

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.type = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.type = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }

    @Override
    public String toString() {
        return "Task{" + "taskName='" + name + '\'' + ", taskDescription='" + description + '\'' + ", taskStatus=" + status + ", taskID=" + id + ", duration=" + duration + ", startTime=" + startTime + '}';
    }
}
