package taskmanagement;


import service.TaskType;

public class Subtask extends Task {
    private int epicId;
    private TaskType type;

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

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "taskmanagement.Subtask{" +
                "epicID='" + epicId + '\'' +
                ", subtaskID=" + getId() +
                ", subtaskName='" + getName() + '\'' +
                ", subtaskDescription='" + getDescription() + '\'' +
                ", subtaskStatus=" + getStatus() +
                '}';
    }
}