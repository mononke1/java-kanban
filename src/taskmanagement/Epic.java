package taskmanagement;

import service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description);
        setId(id);
        this.subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, ArrayList<Subtask> subtasks) {
        super(name, description);
        this.subtasks = subtasks;
    }

    public Epic(String name, String description, ArrayList<Subtask> subtasks, int id) {
        super(name, description);
        this.subtasks = subtasks;
        setId(id);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtaskInEpic(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return Optional.ofNullable(endTime);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                ", epicID=" + getId() +
                ", epicName='" + getName() + '\'' +
                ", epicDescription='" + getDescription() + '\'' +
                ", epicStatus=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                '}';
    }
}