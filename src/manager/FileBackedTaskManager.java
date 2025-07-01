package manager;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.Objects;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException {
        Stream.of(getTasks(), getEpics(), getSubtasks()).flatMap(tasks -> tasks.stream()).forEach(task -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
                writer.write(CSVTaskConverter.taskToCSV(task));
                writer.newLine();
            } catch (IOException e) {
                throw new ManagerSaveException("Save exception");
            }
        });
    }

    public static FileBackedTaskManager loadFromFile(File file) { //восстановление данных из файла
        FileBackedTaskManager fbtm;
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            fbtm = new FileBackedTaskManager(file);
            reader.lines()
                    .map(CSVTaskConverter::fromString)
                    .filter(Objects::nonNull)
                    .forEach(task -> {
                        fbtm.setMaxId(task.getId());
                        TaskType type = task.getType();
                        if (type == TaskType.SUBTASK) {
                            fbtm.subtasks.put(task.getId(), (Subtask) task);
                        } else if (type == TaskType.EPIC) {
                            fbtm.epics.put(task.getId(), (Epic) task);
                        } else {
                            fbtm.tasks.put(task.getId(), task);
                        }
                    });
            return fbtm;
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }
        return null;
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

    private void setMaxId(int taskId) {
        id = Integer.max(id, taskId);
    }

    public File getFileToSave(){
        return file;
    }
}
