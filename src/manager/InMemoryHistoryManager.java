package manager;

import model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private static final int MAX_HISTORY_SIZE = 10;
    private final LinkedList<Task> history = new LinkedList<>();
    @Override
    public void add(Task task) {
        if(task == null) {
            System.out.println("Task not found in InMemoryManager.add()");
            return;
        }
        history.addLast(task);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public int getMaxHistorySize(){
        return MAX_HISTORY_SIZE;
    }
}
