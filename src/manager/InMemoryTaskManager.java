package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final LinkedList<Integer> history = new LinkedList<>();

    public LinkedList<Integer> getHistory(){
        return history;
    }

    //Task methods
    @Override
    public void createTask(Task task) {
        task.setId(getNewID());
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void removeTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public Task getTaskById(int taskId) {
        addToHistory(taskId);
        return tasks.get(taskId);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    //Epic methods
    @Override
    public void createEpic(Epic epic) {
        epic.setId(getNewID());
        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic getEpicById(int epicId) {
        addToHistory(epicId);
        return epics.get(epicId);
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(int epicId) {
        HashSet<Integer> epicSubtasksId = epics.get(epicId).getSubtasks();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (int id : epicSubtasksId) {
            epicSubtasks.add(subtasks.get(id));
        }
        return epicSubtasks;
    }

    @Override
    public void updateEpic(Epic epic) {
        epic.setSubtasks(epics.get(epic.getId()).getSubtasks());
        updateEpicState(epic);
        epics.put(epic.getId(), epic);
    }

    private void updateEpicState(Epic epic) {
        int done = 0;
        int newSub = 0;
        for (int subId : epic.getSubtasks()) {
            Status status = subtasks.get(subId).getStatus();
            if (status == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
            if(status == Status.DONE) {
                done += 1;
            } else {
                newSub += 1;
            }
        }
        if (done == epic.getSubtasks().size()) {
            epic.setStatus(Status.DONE);
        } else if (newSub == epic.getSubtasks().size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void removeEpic(int epicId) {
        for (int subtaskId : epics.get(epicId).getSubtasks()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(epicId);
    }

    @Override
    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    //Subtask
    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if(epic == null){
            System.out.println("Epic with id " + subtask.getEpicId() + " doesn't exist.");
        } else {
            int newId = getNewID();
            subtask.setId(newId);
            epic.setSubtask(newId);
            subtasks.put(newId, subtask);
            updateEpicState(epic);
        }
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        addToHistory(subtaskId);
        return subtasks.get(subtaskId);
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicState(epics.get(subtask.getEpicId()));
    }

    @Override
    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtaskId);
        subtasks.remove(subtaskId);
        updateEpicState(epic);
    }

    @Override
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

    private void addToHistory(int id){
        history.addLast(id);
        if (history.size() > 10) {
            history.removeFirst();
        }
    }
}
