package taskmanagement;


import service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class Subtask extends Task {
    private int epicId;
    private final TaskType type;
    private Duration duration;
    private LocalDateTime startTime;

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.type = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, TaskStatus status, int epicId, int id) {
        super(name, description, status, id);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, description, status);
        this.type = TaskType.SUBTASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Subtask(String name, String description, TaskStatus status, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, status);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Subtask(String name, String description, TaskStatus status, int epicId, int id, Duration duration, LocalDateTime startTime) {
        super(name, description, status, id);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicID='" + epicId + '\'' +
                ", subtaskID=" + getId() +
                ", subtaskName='" + getName() + '\'' +
                ", subtaskDescription='" + getDescription() + '\'' +
                ", subtaskStatus=" + getStatus() +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}