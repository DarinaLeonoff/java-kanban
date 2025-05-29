import historyManager.Managers;
import historyManager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // 1. Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач.
        Task task1 = new Task("Task 1", "Description", Status.NEW);
        Task task2 = new Task("Task 2", "Description", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Description", Status.NEW);
        manager.createEpic(epic1);
        Subtask subtask11 = new Subtask("Subtask 1/1", "Description", Status.NEW, epic1.getId());
        Subtask subtask12 = new Subtask("Subtask 1/2", "Description", Status.DONE, epic1.getId());
        Subtask subtask13 = new Subtask("Subtask 1/3", "Description", Status.NEW, epic1.getId());
        manager.createSubtask(subtask11);
        manager.createSubtask(subtask12);
        manager.createSubtask(subtask13);

        Epic epic2 = new Epic("Epic 1", "Description", Status.NEW);
        manager.createEpic(epic2);


        //2. Запросите созданные задачи несколько раз в разном порядке.
        //3. После каждого запроса выведите историю и убедитесь, что в ней нет повторов.
        manager.getTaskById(task2.getId());
        manager.getSubtaskById(subtask12.getId());
        manager.getSubtaskById(subtask11.getId());
        manager.getEpicById(epic1.getId());
        System.out.println("1\n" + manager.getHistory() + "\n");
        manager.getTaskById(task2.getId());
        System.out.println(manager.getHistory() + "\n");


        // 4. Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться.
        manager.removeTask(task2.getId());
        System.out.println("2\n" + manager.getHistory() + "\n");

        //5. Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
        manager.removeEpic(epic1.getId());
        System.out.println("3\n" + manager.getHistory() + "\n");
    }

}
