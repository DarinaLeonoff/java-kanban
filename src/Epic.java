import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task{
    private ArrayList<Subtask> subtasks;

    public Epic(String title, String description, int id, Status status) {
        super(title, description, id, status);
        subtasks = new ArrayList<>();
    }

    @Override
    protected void setStatus(Status status) {
        int doneCount = 0;
        if (subtasks != null){
            for (Subtask subtask : subtasks){
                doneCount += subtask.getStatus() == Status.DONE ? 1 : 0;
            }
            if(doneCount == subtasks.size()) super.setStatus(Status.DONE);
            else if(doneCount == 0) super.setStatus(Status.NEW);
            else super.setStatus(Status.IN_PROGRESS);
        } else super.setStatus(Status.NEW);
    }

    public void setSubtasks(Subtask subtask){
        subtasks.add(subtask);
    }
    public ArrayList<Subtask> getSubtasks(){
        return subtasks;
    }
}
