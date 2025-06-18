package manager;

import Exceptions.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException {// сохраняет текущее состояние менеджера в указанный файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            List<Task> totalList = new ArrayList<>(this.getTasks());
            totalList.addAll(this.getEpics());
            totalList.addAll(this.getSubtasks());
            for (Task task : totalList) {
                writer.write(toString(task));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Save exception");
            //Исключения вида IOException нужно отлавливать внутри метода save и выкидывать собственное непроверяемое исключение ManagerSaveException.
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) { //восстановление данных из файла
        FileBackedTaskManager fbtm = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            fbtm = new FileBackedTaskManager(file);
            String str = reader.readLine();
            while (str != null) {
                Task task = fromString(str);
                if (task instanceof Subtask) {
                    fbtm.createSubtask((Subtask) task);
                } else if (task instanceof Epic) {
                    fbtm.createEpic((Epic) task);
                } else {
                    fbtm.createTask(task);
                }
                str = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
        return fbtm;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int subtaskId) {
        super.removeSubtask(subtaskId);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    /*храниение данных в формате
        id,type,name,status,description,epic
1,TASK,Task1,NEW,Description task1,
2,EPIC,Epic2,DONE,Description epic2,
3,SUBTASK,Sub Task2,DONE,Description sub task3,2 */
    private String toString(Task task) {
        int epicId = -1;
        TaskTypes type = null;
        if (task instanceof Subtask) {
            type = TaskTypes.SUBTASK;
            epicId = ((Subtask) task).getEpicId();
        } else if (task instanceof Epic) {
            type = TaskTypes.EPIC;
        } else {
            type = TaskTypes.TASK;
        }
        String res = String.format("%d, %s, %s, %s, %s, %s%n", task.getId(), type, task.getTitle(), task.getStatus(), task.getDescription(), epicId == -1 ? "" : epicId);
        return res;
    }

    private static Task fromString(String taskStr) {
        String[] taskArray = taskStr.split(", ");
        switch (TaskTypes.toType(taskArray[1])) {
            case TASK:
                Task task = new Task(taskArray[2], taskArray[4], Status.toStatus(taskArray[3]));
                task.setId(Integer.parseInt(taskArray[0]));
                return task;
            case EPIC:
                Epic epic = new Epic(taskArray[2], taskArray[4], Status.toStatus(taskArray[3]));
                epic.setId(Integer.parseInt(taskArray[0]));
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(taskArray[2], taskArray[4], Status.toStatus(taskArray[3]), Integer.parseInt(taskArray[5]));
                subtask.setId(Integer.parseInt(taskArray[0]));
                return subtask;
            default:
                return null;
        }
    }
}
