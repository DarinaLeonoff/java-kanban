package model;

public class Subtask extends Task{
    private final int epicId;
    public Subtask(String title, String description, int id, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }
    public int getEpicId(){
        return epicId;
    }
}
