package model;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;

public class Epic extends Task {
    private LocalDateTime endTime;
    private HashSet<Integer> subtasks = new HashSet<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public boolean setSubtask(int id) {
        if (this.getId() != id) {
            subtasks.add(id);
            return true;
        }
        return false;
    }

    public void setSubtasks(HashSet<Integer> subtasks) {
        this.subtasks = subtasks;
    }

    public void setStartFinish(LocalDateTime start, LocalDateTime finish){
        super.startTime = start;
        endTime = finish;
        super.duration = Duration.between(start, finish);
    }

    public HashSet<Integer> getSubtasks() {
        return subtasks;
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
    }

    public void removeSubtasks() {
        subtasks.clear();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return super.getEndTime();
    }
}
