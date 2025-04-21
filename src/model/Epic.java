package model;

import java.util.HashSet;

public class Epic extends Task{
    private HashSet<Integer> subtasks = new HashSet<>();

    public Epic(String title, String description, int id, Status status) {
        super(id, title, description, status);
    }

    public void setSubtask(int id){
        subtasks.add(id);
    }
    public HashSet<Integer> getSubtasks(){
        return subtasks;
    }
}
