package model;

import java.util.HashSet;

public class Epic extends Task{
    private HashSet<Integer> subtasks = new HashSet<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public void setSubtask(int id){
        subtasks.add(id);
    }
    public void setSubtasks(HashSet<Integer> subtasks){
        this.subtasks = subtasks;
    }
    public HashSet<Integer> getSubtasks(){
        return subtasks;
    }
    public void removeSubtask(int id){
        subtasks.remove(id);
    }
}
