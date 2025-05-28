package manager;

import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private final LinkedList<Task> history = new LinkedList<>();
    @Override
    public void add(Task task) {
        if(task == null) {
            System.out.println("Task not found in HistoryManager.add()");
            return;
        }
        history.remove(task);
        history.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

}
