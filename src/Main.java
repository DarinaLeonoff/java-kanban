import manager.TaskManager;
import model.Subtask;
import model.Task;

public class Main {
    private static TaskManager manager = new TaskManager();

    public static void main(String[] args) {
        Task task1 = manager.createTask("model.Task 1", "desc 1", "NEW");
        Task task2 =manager.createTask("model.Task 2", "desc 2", "NEW");
        Subtask subtask1 = manager.createSubtask(task2, "subTitle 2.1", "subdesc 2", "NEW");
        task2 = manager.getTaskById(task2.getId());
        Subtask subtask2 = manager.createSubtask(task2, "subTitle 2.2", "subdesc 2", "NEW");
        Task task3 =manager.createTask("model.Task 3", "desc 3", "NEW");
        Subtask subtask3 = manager.createSubtask(task3, "subTutle 3.1", "subdesc 3", "NEW");

        printAll();

        System.out.println("Change status");
        manager.changeStatus(0);
        manager.changeStatus(2);
        manager.changeStatus(5);

        printAll();

        System.out.println("Remove task 1 and epic 2");
        manager.removeTask(0);
        manager.removeTask(1);

        printAll();

        System.out.println("Remove all tasks");
        manager.removeAllTasks();

        printAll();
    }

    private static void printAll(){
        for(Task task : manager.getAllTasks()){
            System.out.println(task);
        }
    }
}
