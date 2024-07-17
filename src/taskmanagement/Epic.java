package taskmanagement;

import service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;
    private TaskType type;
    private Duration duration;
    private LocalDateTime startTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
        this.type = TaskType.EPIC;
        setStartTime();
    }

    public Epic(String name, String description, int id) {
        super(name, description);
        setId(id);
        this.subtasks = new ArrayList<>();
        this.type = TaskType.EPIC;
        setStartTime();
    }

    public Epic(String name, String description, ArrayList<Subtask> subtasks) {
        super(name, description);
        this.subtasks = subtasks;
        this.type = TaskType.EPIC;
        setStartTime();
        setDuration();
    }

    public Epic(String name, String description, ArrayList<Subtask> subtasks, int id) {
        super(name, description);
        this.subtasks = subtasks;
        this.type = TaskType.EPIC;
        setId(id);
        setStartTime();
        setDuration();
    }

    public ArrayList<Subtask> getSubtasks() {
        setStartTime();
        setDuration();
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
        setStartTime();
        setDuration();
    }

    public void addSubtaskInEpic(Subtask subtask) {
        subtasks.add(subtask);
        setStartTime();
        setDuration();
    }

    private void setStartTime() {
        startTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    private void setDuration() {
        duration = subtasks.stream()
                .filter(subtask -> subtask.getStartTime().isPresent())
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public LocalDateTime getEndTime() {
        return subtasks.stream()
                .filter(subtask -> subtask.getStartTime().isPresent())
                .max(Comparator.comparing(Subtask::getEndTime))
                .map(Subtask::getEndTime)
                .orElse(null);
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                ", epicID=" + getId() +
                ", epicName='" + getName() + '\'' +
                ", epicDescription='" + getDescription() + '\'' +
                ", epicStatus=" + getStatus() +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}