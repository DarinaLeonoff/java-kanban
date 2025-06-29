package model;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, Status status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }
    public Subtask(String title, String description, Status status, int epicId, LocalDateTime startTime, Duration duration) {
        super(title, description, status);
        this.epicId = epicId;
        super.startTime = startTime;
        super.duration = duration;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}
