package model;

import manager.TaskType;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVTaskConverter {


    public static String taskToCSV(Task task) {
        int epicId = -1;
        TaskType type = task.getType();
        if (type == TaskType.SUBTASK) {
            epicId = ((Subtask) task).getEpicId();
        }
        LocalDateTime startTime = task.getStartTime().orElse(null);
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s,\n", task.getId(), type, task.getTitle(), task.getStatus(), task.getDescription(), epicId, startTime != null ? startTime.format(DateTimeFormatter.ISO_DATE_TIME) : null, task.getDuration().toMinutes());
    }

    public static Task fromString(String taskStr) {
        if (taskStr.isBlank()) {
            return null;
        }
        String[] taskArray = taskStr.split(",");

        //id,type,name,status,description,epic,startTime,duration
        int taskId = Integer.parseInt(taskArray[0]);
        TaskType type = TaskType.valueOf(taskArray[1]);
        String title = taskArray[2];
        Status status = Status.valueOf(taskArray[3]);
        String description = taskArray[4];
        int epicId = Integer.parseInt(taskArray[5]);
        LocalDateTime startTime = taskArray[6].equals("null") ? null : LocalDateTime.parse(taskArray[6], DateTimeFormatter.ISO_DATE_TIME);
        Duration duration = Duration.ofMinutes(Long.parseLong(taskArray[7]));

        switch (type) {
            case TASK:
                Task task;
                if (startTime == null) {
                    task = new Task(title, description, status);
                } else {
                    task = new Task(title, description, status, startTime, duration);
                }
                task.setId(taskId);
                return task;
            case EPIC:
                Epic epic = new Epic(title, description, status);
                if (startTime != null) {
                    epic.setStartFinish(startTime, startTime.plus(duration));
                }
                epic.setId(taskId);
                return epic;
            case SUBTASK:
                Subtask subtask;
                if (startTime == null) {
                    subtask = new Subtask(title, description, status, epicId);
                } else {
                    subtask = new Subtask(title, description, status, epicId, startTime, duration);
                }
                subtask.setId(taskId);
                return subtask;
            default:
                return null;
        }
    }
}
