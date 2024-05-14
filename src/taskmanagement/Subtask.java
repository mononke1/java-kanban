package taskmanagement;


public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus status, int epicId, int id) {
        super(name, description, status, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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