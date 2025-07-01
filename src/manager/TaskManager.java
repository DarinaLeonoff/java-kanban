package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getPrioritizedTasks();
    //Task methods
    void createTask(Task task);

    void updateTask(Task task);

    void removeTask(int taskId);

    Task getTaskById(int taskId);

    ArrayList<Task> getTasks();

    void removeAllTasks();

    //Epic methods
    void createEpic(Epic epic);

    Epic getEpicById(int epicId);

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasksFromEpic(int epicId);

    void updateEpic(Epic epic);

    void removeEpic(int epicId);

    void removeEpics();

    //Subtask
    void createSubtask(Subtask subtask);

    Subtask getSubtaskById(int subtaskId);

    ArrayList<Subtask> getSubtasks();

    void updateSubtask(Subtask subtask);

    void removeSubtask(int subtaskId);

    void removeAllSubtasks();

    List<Task> getHistory();
}
