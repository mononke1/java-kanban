package taskmanagement;

public class Subtask extends Task {
    private String epicName;
    private int ID;

    public Subtask(String name, String description, TaskStatus status, String epicName) {
        super(name, description, status);
        this.epicName = epicName;
    }

    public Subtask(String name, String description, TaskStatus status, String epicName, int ID) {
        super(name, description, status);
        this.epicName = epicName;
        this.ID = ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        return "taskmanagement.Subtask{" +
                "epicName='" + epicName + '\'' +
                ", subtaskID=" + ID +
                ", subtaskName='" + getName() + '\'' +
                ", subtaskDescription='" + getDescription() + '\'' +
                ", subtaskStatus=" + getStatus() +
                '}';
    }
}
