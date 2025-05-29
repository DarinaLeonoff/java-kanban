package historyManager;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task);
        linkLast(task);
    }

    @Override
    public void remove(Task task) {
        if (task == null) {
            return;
        }
        Node node = history.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public void linkLast(Task task) {//add Task to node and in the end of history
        Node newNode = new Node(task);
        if (last != null) {
            last.next = newNode;
            newNode.prev = last;
        } else {
            first = newNode;
        }
        last = newNode;
        history.put(task.getId(), newNode);
    }

    public List<Task> getTasks() {// get list of tasks
        List<Task> tasks = new ArrayList<>();
        Node current = first;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    public void removeNode(Node node) { //remove node from history
        if (node.prev == null) {
            first = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            last = node.prev;
        } else {
            node.next.prev = node.prev;
        }
        history.remove(node.task.getId());
    }

    private class Node {
        private Node next;
        private Node prev;
        private final Task task;

        public Node(Task task) {
            this.task = task;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Node node = (Node) o;
            return Objects.equals(task, node.task);
        }

        @Override
        public int hashCode() {
            return Objects.hash(task);
        }
    }

}
