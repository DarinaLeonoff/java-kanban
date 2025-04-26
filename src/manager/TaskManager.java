package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TaskManager {
    private int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    //Task methods
    public void createTask(Task task) {
        task.setId(getNewID());
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    //Epic methods
    public void createEpic(Epic epic) {
        epic.setId(getNewID());
        epics.put(epic.getId(), epic);
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasksFromEpic(int epicId) {
        HashSet<Integer> epicSubtasksId = epics.get(epicId).getSubtasks();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (int id : epicSubtasksId) {
            epicSubtasks.add(subtasks.get(id));
        }
        return epicSubtasks;
    }

    public void updateEpic(int epicId, String title, String description) {
        Epic epic = epics.get(epicId);
        Epic newEpic = new Epic(title, description, epic.getStatus());
        newEpic.setId(epicId);
        newEpic.setSubtasks(epic.getSubtasks());
        epics.put(epicId, newEpic);
    }

    private void updeteEpicState(Epic epic) {
        int done = 0;
        for (int subId : epic.getSubtasks()) {
            Status status = subtasks.get(subId).getStatus();
            if (status == Status.IN_PROGRESS) {
                done = -1;
                break;
            }
            done += status == Status.DONE ? 1 : 0;
        }
        if (done == 0) {
            epic.setStatus(Status.NEW);
        } else if (done == -1) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (done == epic.getSubtasks().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void removeEpic(int epicId) {
        for (int subtaskId : epics.get(epicId).getSubtasks()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(epicId);
    }

    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    //Subtask
    public void createSubtask(Subtask subtask) {
        int newId = getNewID();
        subtask.setId(newId);
        epics.get(subtask.getEpicId()).setSubtask(newId);
        subtasks.put(newId, subtask);
        updeteEpicState(epics.get(subtask.getEpicId()));
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void updateSubtask(int subId, Subtask subtask) {
        subtasks.put(subId, subtask);
        updeteEpicState(epics.get(subtask.getEpicId()));
    }

    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtaskId);
        subtasks.remove(subtaskId);
        updeteEpicState(epic);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for(Epic epic : epics.values()){
            epic.removeSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    private int getNewID() {
        return id++;
    }
}
