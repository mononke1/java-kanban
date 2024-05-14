package taskmanagement;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

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

    @Override
    public String toString() {
        return "taskmanagement.Epic{" +
                "subtasks=" + subtasks +
                ", epicID=" + getId() +
                ", epicName='" + getName() + '\'' +
                ", epicDescription='" + getDescription() + '\'' +
                ", epicStatus=" + getStatus() +
                '}';
    }
}
