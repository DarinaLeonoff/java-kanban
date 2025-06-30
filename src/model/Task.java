package model;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;
    protected Duration duration;
    protected Optional<LocalDateTime> startTime;

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
        duration = Duration.ZERO;
        startTime = Optional.empty();
    }

    public Task(String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = Optional.ofNullable(startTime);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "\nmodel.Task{" + "title='" + title + '\'' + ", description='" + description + '\'' + ", id=" + id + ", status=" + status + ", " + getClass() + '}';
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Status getStatus() {
        return status;
    }

    public Optional<LocalDateTime> getEndTime(){
        if(startTime.isPresent()) {
            return Optional.of(startTime.get().plus(duration));
        } else {
            return Optional.empty();
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public Optional<LocalDateTime> getStartTime() {
        return startTime;
    }
}
