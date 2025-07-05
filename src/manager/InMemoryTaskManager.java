package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(task -> task.getStartTime().orElse(LocalDateTime.MAX)));

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addPrioritizedTask(Task task) {
        if (task.getStartTime().isPresent()) {
            prioritizedTasks.add(task);
        }
    }

    private boolean isNoIntersections(Task task) {
        return prioritizedTasks.stream().noneMatch(t -> isIntersecting(task, t));
    }

    private boolean isIntersecting(Task t1, Task t2) {
        if (t1.equals(t2)) {
            return false;
        }
        if (t1.getStartTime().isEmpty() || t2.getStartTime().isEmpty()) {
            return false;
        }

        LocalDateTime start1 = t1.getStartTime().get();
        LocalDateTime end1 = t1.getEndTime().orElse(start1);
        LocalDateTime start2 = t2.getStartTime().get();
        LocalDateTime end2 = t2.getEndTime().orElse(start2);

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    //Task methods
    @Override
    public void createTask(Task task) {
        task.setId(getNewID());
        if (isNoIntersections(task)) {
            addPrioritizedTask(task);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateTask(Task task) {
        prioritizedTasks.remove(tasks.get(task.getId()));
        if (isNoIntersections(task)) {
            tasks.put(task.getId(), task);
            addPrioritizedTask(task);
        }

    }

    @Override
    public void removeTask(int taskId) {
        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
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
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        });
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
        epicSubtasksId.stream().map(subtasks::get).forEach(epicSubtasks::add);
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
        List<Status> statuses = epic.getSubtasks().stream() //get status of each subtask
                .map(subId -> subtasks.get(subId).getStatus()).toList();

        if (statuses.contains(Status.IN_PROGRESS)) { //if even 1 in progress - epic status is in progress
            epic.setStatus(Status.IN_PROGRESS);
            return;
        }
        long doneCount = statuses.stream().filter(s -> s == Status.DONE).count();
        long newCount = statuses.size() - doneCount;
        epic.setStatus(doneCount == statuses.size() ? Status.DONE : (newCount == statuses.size() ? Status.NEW : Status.IN_PROGRESS));
    }

    private void updateEpicStartFinish(Epic epic) {
        List<Subtask> sortedList = subtasks.values().stream().filter(subtask -> epic.getSubtasks().contains(subtask.getId()) && subtask.getStartTime().isPresent()).sorted(Comparator.comparing((Task t) -> t.getStartTime().get())).toList();
        if (sortedList.isEmpty()) {
            epic.setStartFinish(null, null);
        } else {
            epic.setStartFinish(sortedList.getFirst().getStartTime().get(), sortedList.getLast().getEndTime().get());
        }
    }

    @Override
    public void removeEpic(int epicId) {
        epics.get(epicId).getSubtasks().forEach(subtaskId -> {
            Task subtask = subtasks.get(subtaskId);
            historyManager.remove(subtaskId);
            prioritizedTasks.remove(subtask);
            subtasks.remove(subtaskId);
        });
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
        } else if (isNoIntersections(subtask)) {
            int newId = getNewID();
            subtask.setId(newId);
            epic.setSubtask(newId);
            subtasks.put(subtask.getId(), subtask);
            updateEpicState(epic);
            updateEpicStartFinish(epic);
            addPrioritizedTask(subtask);
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
        if (isNoIntersections(subtask)) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateEpicState(epic);
            updateEpicStartFinish(epic);
            addPrioritizedTask(subtask);
        }
    }

    @Override
    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask == null) {
            System.out.println("RemoveSubtask: subtask is null. no such id in subtasks");
            return;
        }
        historyManager.remove(subtask.getId());
        prioritizedTasks.remove(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtaskId);
        subtasks.remove(subtaskId);
        updateEpicState(epic);
        updateEpicStartFinish(epic);
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.removeSubtasks();
            epic.setStatus(Status.NEW);
        });
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int getNewID() {
        return id++;
    }

}
