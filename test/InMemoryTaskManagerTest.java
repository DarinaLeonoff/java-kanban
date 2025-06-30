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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Test
    public void epicStartFinishDateFromSubtasks(){
        Epic epic = new Epic("t", "d", Status.NEW);
        manager.createEpic(epic);
        LocalDateTime nowTime = LocalDateTime.now();
        Subtask subtask1 = new Subtask("t1", "d1", Status.NEW, 0, nowTime, Duration.ofMinutes(20));
        Subtask subtask2 = new Subtask("t1", "d1", Status.NEW, 0, subtask1.getEndTime().get(), Duration.ofMinutes(20));
        Subtask subtask3 = new Subtask("t1", "d1", Status.NEW, 0, subtask2.getEndTime().get(), Duration.ofMinutes(20));
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        Assertions.assertEquals(epic.getStartTime().get(), subtask1.getStartTime().get(), "Not same start date");
        Assertions.assertEquals(epic.getEndTime().get(), subtask3.getEndTime().get(), "Not same finish date");
        Assertions.assertEquals(Duration.ofMinutes(60), epic.getDuration(), "Not same duration");
    }

    @Test
    public void prioritizedTaskLiastCheck(){
        LocalDateTime nowTime = LocalDateTime.now();
        List<Task> expectedList = new ArrayList<>();
        Epic epic = new Epic("Epic", "it's a big task.", Status.DONE);
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(), nowTime,
                Duration.ofMinutes(20));
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(),
                subtask1.getEndTime().get(), Duration.ofMinutes(20));
        manager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(),
                subtask2.getEndTime().get(), Duration.ofMinutes(20));
        manager.createSubtask(subtask3);
        Task task = new Task("Task", "it's a usual task.", Status.NEW,  subtask3.getEndTime().get(),
                Duration.ofMinutes(20));
        manager.createTask(task);
        expectedList.add(subtask1);
        expectedList.add(subtask2);
        expectedList.add(subtask3);
        expectedList.add(task);

        List<Task> prioritizedTasks = ((InMemoryTaskManager)manager).getPrioritizedTasks();

        Assertions.assertArrayEquals(expectedList.toArray(), prioritizedTasks.toArray(), "Not same array");

    }
}
