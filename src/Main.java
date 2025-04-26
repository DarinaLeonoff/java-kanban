import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {
    private static TaskManager manager = new TaskManager();

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
        manager.updateEpic(1, "new title", "new desc");
        manager.updateSubtask(2, new Subtask("titte222", "desc", Status.IN_PROGRESS, 1));
        manager.updateSubtask(5, new Subtask( "subtitle3333", "subdesk", Status.IN_PROGRESS, 4));
        printAll();
//
//        System.out.println("Get subtasks of 1 epic");
//        System.out.println(manager.getEpicById(1));
//        for(Subtask subtask : manager.getSubtasksFromEpic(1)){
//            System.out.println(subtask);
//        }
//
//        System.out.println("Remove task 0, subtask 2, epic 4");
//        manager.removeTask(0);
//        manager.removeSubtask(2);
//        manager.removeEpic(4);
//        printAll();

//        System.out.println("Remove all tasks");
//        manager.removeAll();
//        printAll();
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
