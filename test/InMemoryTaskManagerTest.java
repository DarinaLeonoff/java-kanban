import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;

import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest {
    private static final TaskManager manager = new InMemoryTaskManager();

    @BeforeAll
    public static void checkHistoryIsEmpty() {
        Assertions.assertTrue(manager.getHistory().isEmpty());
    }

    @BeforeEach
    public void beforeEach() {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeEpics();
    }

    @Test
    public void creatingTasksTest() { //work only as single test
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
    public void updateTasksTest() { //work only as single test
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

        Assertions.assertEquals(epic, manager.getEpicById(epic.getId()));
        Assertions.assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
        Assertions.assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void removeTasksTest() { // return null
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
    public void setIdBeforeCreating() {
        Task task = new Task("t", "d", Status.DONE);
        task.setId(1);
        manager.createTask(task);
        Assertions.assertNotEquals(task, manager.getTaskById(1));
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
    public void taskInHistoryNotEqualsTaskAfterApdate() {
        Task task1 = new Task("Title", "Descriprion", Status.NEW);
        manager.createTask(task1);
        manager.getTaskById(task1.getId());
        Task task2 = new Task("Title 1", "Description 1", Status.NEW);
        task2.setId(task1.getId());
        manager.updateTask(task2);
        Task historyTask = manager.getHistory().getLast();
        Assertions.assertEquals(task1.getTitle(), historyTask.getTitle(), "title isn't equals");
        Assertions.assertEquals(task1.getDescription(), historyTask.getDescription(), "description isn't equals");
        Assertions.assertEquals(task1.getStatus(), historyTask.getStatus(), "status isn't equals");
        Assertions.assertEquals(task2, historyTask);
    }
}
