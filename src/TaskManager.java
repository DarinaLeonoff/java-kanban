import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private int id = 0;
    private HashMap<Integer, Task> tasks;

    public TaskManager(){
        tasks = new HashMap<>();
    }

    public Task createTask(String title, String description, String status){
        Status stat;
        if(status.equals("NEW")||status.equals("IN_PROGRESS")||status.equals("DONE")) stat = Status.valueOf(status);
        else stat = Status.NEW;
        Task task = new Task(title, description, getNewID(), stat);
        tasks.put(task.getId(), task);
        return task;
    }

    public Subtask createSubtask(Task epic, String title, String description, String status){
        if(epic == null) return null;
        if(epic.getClass() != Epic.class) {
            epic = new Epic(epic.getTitle(), epic.getDescription(), epic.getId(), epic.getStatus());
            tasks.put(epic.getId(), epic);
        }
        int newId = getNewID();
        Status stat;
        if(status.equals("NEW")||status.equals("IN_PROGRESS")||status.equals("DONE")) stat = Status.valueOf(status);
        else stat = Status.NEW;
        Subtask subtask = new Subtask(title,description, newId, stat, epic.getId());
        ((Epic)epic).setSubtasks(subtask);
        tasks.put(newId, subtask);
        return subtask;
    }

    private int getNewID(){
        return id++;
    }

    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    public Task getTaskById(int id){
        return tasks.get(id);
    }

    public void changeStatus(int id){
        Task task = getTaskById(id);
        if(task == null) return;
        task.setStatus(Status.DONE);
        if (task.getClass() == Subtask.class) tasks.get(((Subtask)task).getEpicId()).setStatus(Status.DONE);
    }

    public void removeTask(int id){
        Task task = getTaskById(id);
        if(task == null) return;
        if(task.getClass() == Epic.class){
            for(Task t : ((Epic) task).getSubtasks()) tasks.remove(t.getId());
        }
        tasks.remove(id);
    }

    public void removeAllTasks(){
        tasks = new HashMap<>();
    }
}
