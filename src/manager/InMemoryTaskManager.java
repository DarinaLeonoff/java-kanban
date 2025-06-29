package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

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
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
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
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
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
        Epic curEpic = epics.get(epic.getId());
        epic.setSubtasks(curEpic.getSubtasks());
        epic.setStatus(curEpic.getStatus());
        epics.put(epic.getId(), epic);
    }

    private void updateEpicState(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        int done = 0;
        int newSub = 0;
        for (int subId : epic.getSubtasks()) {
            Status status = subtasks.get(subId).getStatus();
            if (status == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
            if (status == Status.DONE) {
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

    private void updateEpicStartFinish(Epic epic){
        List<Subtask> sortedList =
                subtasks.values().stream().filter(subtask -> epic.getSubtasks().contains(subtask.getId())).
                sorted(Comparator.comparing(Task::getStartTime)).toList();
        epic.setStartFinish(sortedList.getFirst().getStartTime(), sortedList.getLast().getEndTime());
    }

    @Override
    public void removeEpic(int epicId) {
        for (int subtaskId : epics.get(epicId).getSubtasks()) {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }
        historyManager.remove(epicId);
        epics.remove(epicId);
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        removeAllSubtasks();
    }

    //Subtask
    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Epic with id " + subtask.getEpicId() + " doesn't exist.");
        } else {
            int newId = getNewID();
            subtask.setId(newId);
            epic.setSubtask(newId);
            subtasks.put(newId, subtask);
            updateEpicState(epic);
            updateEpicStartFinish(epic);
        }
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicState(epic);
        updateEpicStartFinish(epic);
    }

    @Override
    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask == null) {
            System.out.println("RemoveSubtask: subtask is null. no such id in subtasks");
            return;
        }
        historyManager.remove(subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtaskId);
        subtasks.remove(subtaskId);
        updateEpicState(epic);
        updateEpicStartFinish(epic);
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int getNewID() {
        return id++;
    }

}
