package model;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

public class Epic extends Task {
    private Optional<LocalDateTime> endTime;
    private HashSet<Integer> subtasks = new HashSet<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }

    public void setSubtask(int id) {
        if (subtasks == null) {
            subtasks = new HashSet<>();
        }
        if (this.getId() != id) {
            subtasks.add(id);
        }
    }

    public void setSubtasks(HashSet<Integer> subtasks) {
        if (subtasks == null) {
            subtasks = new HashSet<>();
        }
        this.subtasks = subtasks;
    }

    public void setStartFinish(LocalDateTime start, LocalDateTime finish) {
        super.startTime = Optional.ofNullable(start);
        endTime = Optional.ofNullable(finish);
        super.duration = start == null ? Duration.ZERO : Duration.between(start, finish);
    }

    public HashSet<Integer> getSubtasks() {
        if (subtasks == null) {
            subtasks = new HashSet<>();
        }
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
    public Optional<LocalDateTime> getEndTime() {
        return endTime;
    }
}
