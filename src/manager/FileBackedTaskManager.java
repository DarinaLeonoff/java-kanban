package manager;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            for (Task task : getTasks()) {
                writer.write(CSVTaskConverter.taskToCSV(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(CSVTaskConverter.taskToCSV(epic));
                writer.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(CSVTaskConverter.taskToCSV(subtask));
                writer.newLine();
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
                Task task = CSVTaskConverter.fromString(str);
                fbtm.setMaxId(task.getId());
                TaskType type = task.getType();
                if (type == TaskType.SUBTASK) {
                    fbtm.createSubtask((Subtask) task);

                } else if (type == TaskType.EPIC) {
                    fbtm.createEpic((Epic) task);
                } else {
                    fbtm.createTask(task);
                }
                reader.readLine(); //read blank line
                str = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
        return fbtm;
    }

    @Override
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
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
        epics.put(epic.getId(), epic);
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
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.setSubtask(subtask.getId());
        updateEpic(epic);
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

    private void setMaxId(int taskId) {
        id = Integer.max(id, taskId);
    }

}
