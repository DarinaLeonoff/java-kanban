package model;

import manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;
    protected Duration duration = Duration.ZERO;
    protected Optional<LocalDateTime> startTime = Optional.empty();
    //без transient появляется ошибка при вызове gson.fromJson()
    private transient DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DD/MM/YY HH:mm");

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
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
        return String.format(
                "\nmodel.Task{title=%s, description=%s, id=%d, status=%s, start=%s, duration=%d min, " + "end=%s %s}\n",
                title, description, id, status, startTime.<Object>map(localDateTime -> localDateTime.format(formatter)).orElse(null),
                duration.toMinutes(),
                getEndTime().<Object>map(localDateTime -> localDateTime.format(formatter)).orElse(null), this.getClass());
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

    public Optional<LocalDateTime> getEndTime() {
        return startTime.map(localDateTime -> localDateTime.plus(duration));
    }

    public Duration getDuration() {
        return duration == null ? Duration.ZERO : duration;
    }

    public Optional<LocalDateTime> getStartTime() {
        return startTime == null? Optional.empty() : startTime;
    }
}
