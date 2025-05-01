import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {
    private static InMemoryTaskManager manager = new InMemoryTaskManager();

    public static void main(String[] args) {
        manager.createTask(new Task( "title1", "desc1", Status.NEW));

        manager.createEpic(new Epic("title2", "desc2", Status.NEW));
        manager.createSubtask(new Subtask( "subtitle2.1", "subdesk", Status.NEW, 1));
        manager.createSubtask(new Subtask("subtitle2.2", "subdesk", Status.NEW, 1));

        manager.createEpic(new Epic("title3", "desc3", Status.NEW));
        manager.createSubtask(new Subtask("subtitle3.1", "subdesk", Status.NEW, 4));
        printAll();

        System.out.println("\n Change status");
        manager.updateTask(new Task("Title", "Desc", Status.DONE));
        Epic epic = new Epic("title new", "desc", Status.NEW);
        epic.setId(1);
        manager.updateEpic(epic);
        Subtask subtask2 = new Subtask("titte222", "desc", Status.IN_PROGRESS, 1);
        subtask2.setId(2);
        manager.updateSubtask(subtask2);
        Subtask subtask5 = new Subtask( "subtitle3333", "subdesk", Status.IN_PROGRESS, 4);
        subtask5.setId(5);
        manager.updateSubtask(subtask5);
        printAll();

        System.out.println("Get subtasks of 1 epic");
        System.out.println(manager.getEpicById(1));
        for(Subtask subtask : manager.getSubtasksFromEpic(1)){
            System.out.println(subtask);
        }

        System.out.println("Remove task 0, subtask 2, epic 4");
        manager.removeTask(0);
        manager.removeSubtask(2);
        manager.removeEpic(4);
        printAll();

    }

    private static void printAll(){
        for(Task task : manager.getTasks()){
            System.out.println(task);
        }
        for (Epic epic : manager.getEpics()){
            System.out.println(epic);
            for (Subtask subtask : manager.getSubtasksFromEpic(epic.getId())){
                System.out.println(subtask);
            }
        }
    }
}
