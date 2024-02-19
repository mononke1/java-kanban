import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;
    private int ID;

    public Epic(String name, String description, ArrayList<Subtask> subtasks) {
        super(name, description);
        this.subtasks = subtasks;
    }

    public Epic(String name, String description, ArrayList<Subtask> subtasks, int ID) {
        super(name, description);
        this.subtasks = subtasks;
        this.ID = ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
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
        return "Epic{" +
                "subtasks=" + subtasks +
                ", epicID=" + ID +
                ", epicName='" + getName() + '\'' +
                ", epicDescription='" + getDescription() + '\'' +
                ", epicStatus=" + getStatus() +
                '}';
    }
}
