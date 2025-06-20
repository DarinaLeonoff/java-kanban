package model;

import manager.TaskType;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class CSVTaskConverter {
    public static String taskToCSV(Task task) {
        int epicId = -1;
        TaskType type = task.getType();
        if (type == TaskType.SUBTASK) {
            epicId = ((Subtask) task).getEpicId();
        }
        return String.format("%d,%s,%s,%s,%s,%s%n", task.getId(), type, task.getTitle(), task.getStatus(), task.getDescription(), epicId == -1 ? "" : epicId);
    }

    public static Task fromString(String taskStr) {
        String[] taskArray = taskStr.split(",");

        //id,type,name,status,description,epic
        int taskId = Integer.parseInt(taskArray[0]);
        TaskType type = TaskType.valueOf(taskArray[1]);
        String title = taskArray[2];
        Status status = Status.valueOf(taskArray[3]);
        String description = taskArray[4];
        int epicId = taskArray.length - 1 >= 5 ? Integer.parseInt(taskArray[5]) : -1;

        switch (type) {
            case TASK:
                Task task = new Task(title, description, status);
                task.setId(taskId);
                return task;
            case EPIC:
                Epic epic = new Epic(title, description, status);
                epic.setId(taskId);
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(title, description, status, epicId);
                subtask.setId(taskId);
                return subtask;
            default:
                return null;
        }
    }
}
