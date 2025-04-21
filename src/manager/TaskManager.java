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
    public void createTask(Task task){
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task){
        tasks.put(task.getId(), task);
    }

    public void removeTask(int taskId){
        tasks.remove(taskId);
    }

    public Task getTaskById(int taskId){
        return tasks.get(taskId);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void removeAllTasks(){
        tasks.clear();
    }

    //Epic methods
    private void createEpic(Epic epic){
        epics.put(epic.getId(), epic);
    }
    public Epic getEpicById(int epicId){
        return epics.get(epicId);
    }
    public ArrayList<Epic> getEpics(){
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasksFromEpic(int epicId){
        HashSet<Integer> epicSubtasksId = epics.get(epicId).getSubtasks();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for(int id : epicSubtasksId){
            epicSubtasks.add(subtasks.get(id));
        }
        return epicSubtasks;
    }

    private void updateEpic(Epic epic){
        int doneTasks = 0;
        for (int subtaskId : epic.getSubtasks()){
            if(subtasks.get(subtaskId).getStatus() == Status.DONE){
                doneTasks ++;
            }
        }
        Status status;
        if(doneTasks == epic.getSubtasks().size()) status = Status.DONE;
        else if (doneTasks == 0) status = Status.NEW;
        else status = Status.IN_PROGRESS;
        Epic newEpic = new Epic(epic.getId(), epic.getTitle(), epic.getDescription(), status);
        epics.put(epic.getId(), newEpic);
    }

    public void removeEpic(int epicId){
        for(int subtaskId : epics.get(epicId).getSubtasks()) subtasks.remove(subtaskId);
        epics.remove(epicId);
    }

    public void removeAllEpics(){
        epics.clear();
        removeAllSubtasks();
    }

    //Subtask
    public void createSubtask(Subtask subtask){
        int epicId = subtask.getEpicId();
        if(tasks.containsKey(epicId)){
            createEpic(new Epic(tasks.get(epicId), subtask.getId()));
            removeTask(epicId);
        } else {
            epics.get(epicId).setSubtask(subtask.getId());
        }
        subtasks.put(subtask.getId(), subtask);
    }

    public Subtask getSubtaskById(int subtaskId){
        return subtasks.get(subtaskId);
    }
    public ArrayList<Subtask> getSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    public void updateSubtask(Subtask subtask){
        subtasks.put(subtask.getId(), subtask);
        updateEpic(epics.get(subtask.getEpicId()));
    }

    public void removeSubtask(int subtaskId){
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtaskId);
        subtasks.remove(subtaskId);
        updateEpic(epic);
    }

    public void removeAllSubtasks(){
        subtasks.clear();
    }

    //general methods
    public int getNewID(){
        return id++;
    }
}
