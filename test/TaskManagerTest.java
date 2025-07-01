import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

abstract class TaskManagerTest {

    protected TaskManager manager;

    public void setUpTest(TaskManager manager) {
        this.manager = manager;
    }

    @Test
    public void shouldCreateAndGetTask() {
        Epic epic = new Epic("Epic", "it's a big task.", Status.DONE);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId());
        manager.createSubtask(subtask);
        Task task = new Task("Task", "it's a usual task.", Status.NEW);
        manager.createTask(task);

        Assertions.assertSame(task, manager.getTaskById(task.getId()));
        Assertions.assertSame(epic, manager.getEpicById(epic.getId()));
        Assertions.assertSame(subtask, manager.getSubtaskById(subtask.getId()));

    }

    @Test
    public void shouldUpdateTask() {
        Epic epic = new Epic("Epic", "it's a big task.", Status.NEW);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId());
        manager.createSubtask(subtask);
        Task task = new Task("Task", "it's a usual task.", Status.NEW);
        manager.createTask(task);

        Epic newEpic = new Epic("New Epic", "new desc", Status.NEW);
        newEpic.setId(epic.getId());
        manager.updateEpic(newEpic);
        Subtask newSubtask = new Subtask("New sub", "new desc", Status.NEW, epic.getId());
        newSubtask.setId(subtask.getId());
        manager.updateSubtask(newSubtask);
        Task newTask = new Task("New task", "new desc", Status.NEW);
        newTask.setId(task.getId());
        manager.updateTask(newTask);


        Assertions.assertEquals(epic, manager.getEpicById(epic.getId()), "not the same epic");
        Assertions.assertEquals(subtask, manager.getSubtaskById(subtask.getId()), "not the same subtask");
        Assertions.assertEquals(task, manager.getTaskById(task.getId()), "not the same task");
    }

    @Test
    public void shouldRemoveTask() {
        Epic epic = new Epic("Epic", "it's a big task.", Status.DONE);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, 0);
        manager.createSubtask(subtask);
        Task task = new Task("Task", "it's a usual task.", Status.NEW);
        manager.createTask(task);

        manager.removeTask(task.getId());
        manager.removeEpic(epic.getId());
        manager.removeSubtask(subtask.getId());

        Assertions.assertNull(manager.getTaskById(task.getId()));
        Assertions.assertNull(manager.getEpicById(epic.getId()));
        Assertions.assertNull(manager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void isTaskUnchangedAfterAddToManager() {
        Task task = new Task("task", "task description", Status.DONE);
        manager.createTask(task);
        Task task1 = manager.getTaskById(task.getId());
        Assertions.assertEquals(task.getTitle(), task1.getTitle(), "titles are not the same");
        Assertions.assertEquals(task.getDescription(), task1.getDescription(), "description are not the same");
        Assertions.assertEquals(task.getStatus(), task1.getStatus(), "status are not the same");
    }

    @Test
    public void shouldCreateAndLinkSubtaskToEpic() {
        Epic epic = new Epic("Epic", "Epic desc", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Sub", "desc", Status.NEW, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(45));
        manager.createSubtask(subtask);

        List<Subtask> epicSubtasks = manager.getSubtasksFromEpic(epic.getId());
        Assertions.assertEquals(1, epicSubtasks.size());
        Assertions.assertEquals(subtask, epicSubtasks.get(0));
    }

    @Test
    public void shouldRemoveEpicAndItsSubtasks() {
        Epic epic = new Epic("Epic", "Epic desc", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Sub", "desc", Status.NEW, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(45));
        manager.createSubtask(subtask);

        manager.removeEpic(epic.getId());

        Assertions.assertTrue(manager.getEpics().isEmpty());
        Assertions.assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    public void shouldUpdateEpicStatusBasedOnSubtasks() {
        Epic epic = new Epic("Epic", "Epic desc", Status.NEW);
        manager.createEpic(epic);

        Subtask s1 = new Subtask("Sub1", "desc", Status.NEW, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        Subtask s2 = new Subtask("Sub2", "desc", Status.DONE, epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));

        manager.createSubtask(s1);
        manager.createSubtask(s2);
        System.out.println("manager.getEpicById(epic.getId()).getStatus() = " + manager.getEpicById(epic.getId()).getStatus());

        // должен стать IN_PROGRESS из-за смешанных статусов
        Assertions.assertSame(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus(), "Not expected " + "state");
    }

    @Test
    public void shouldTrackHistory() {
        Task task = new Task("Task", "Desc", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(20));
        manager.createTask(task);

        manager.getTaskById(task.getId());
        List<Task> history = manager.getHistory();

        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(task, history.get(0));
    }

}
