import manager.InMemoryTaskManager;
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
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManagerTest extends TaskManagerTest {
    @BeforeEach
    public void setUp() {
        super.setUpTest(new InMemoryTaskManager());
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
    public void epicStartFinishDateFromSubtasks() {
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
    public void prioritizedTaskListCheck() {
        LocalDateTime nowTime = LocalDateTime.now();
        List<Task> expectedList = new ArrayList<>();
        Epic epic = new Epic("Epic", "it's a big task.", Status.DONE);
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(), nowTime, Duration.ofMinutes(20));
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(), subtask1.getEndTime().get(), Duration.ofMinutes(20));
        manager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(), subtask2.getEndTime().get(), Duration.ofMinutes(20));
        manager.createSubtask(subtask3);
        Task task = new Task("Task", "it's a usual task.", Status.NEW, subtask3.getEndTime().get(), Duration.ofMinutes(20));
        manager.createTask(task);
        expectedList.add(subtask1);
        expectedList.add(subtask2);
        expectedList.add(subtask3);
        expectedList.add(task);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        Assertions.assertArrayEquals(expectedList.toArray(), prioritizedTasks.toArray(), "Not same array");

    }

    @Test
    public void prioritizedTaskWithNoData() {
        LocalDateTime nowTime = LocalDateTime.now();
        List<Task> expectedList = new ArrayList<>();
        Epic epic = new Epic("Epic", "it's a big task.", Status.DONE);
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(), nowTime, Duration.ofMinutes(20));
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(), subtask1.getEndTime().get(), Duration.ofMinutes(20));
        manager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(), subtask2.getEndTime().get(), Duration.ofMinutes(20));
        manager.createSubtask(subtask3);
        Task task = new Task("Task", "it's a usual task.", Status.NEW, null, Duration.ofMinutes(20));
        manager.createTask(task);
        expectedList.add(subtask1);
        expectedList.add(subtask2);
        expectedList.add(subtask3);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        Assertions.assertArrayEquals(expectedList.toArray(), prioritizedTasks.toArray(), "Not same array");
    }

    @Test
    public void prioritizedTaskCheckOnIntersections() {
        LocalDateTime nowTime = LocalDateTime.now();
        List<Task> expectedList = new ArrayList<>();
        Epic epic = new Epic("Epic", "it's a big task.", Status.DONE);
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(), nowTime, Duration.ofMinutes(20));
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(), subtask1.getEndTime().get(), Duration.ofMinutes(20));
        manager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask", "it's a small task.", Status.IN_PROGRESS, epic.getId(), subtask2.getEndTime().get(), Duration.ofMinutes(20));
        manager.createSubtask(subtask3);
        Task task = new Task("Task", "it's a usual task.", Status.NEW, subtask3.getEndTime().get().minusMinutes(10), Duration.ofMinutes(20));
        manager.createTask(task);
        expectedList.add(subtask1);
        expectedList.add(subtask2);
        expectedList.add(subtask3);

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        Assertions.assertArrayEquals(expectedList.toArray(), prioritizedTasks.toArray(), "Not same array");
    }

    @Test
    public void shouldAddTaskBeforeOthersWithoutIntersection() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("T1", "Desc", Status.NEW, now.plusMinutes(30), Duration.ofMinutes(30));
        Task earlyTask = new Task("Early", "Before all", Status.NEW, now, Duration.ofMinutes(10));

        manager.createTask(task1);
        manager.createTask(earlyTask);

        List<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertTrue(tasks.contains(earlyTask), "Early task should be in the list");
    }

    @Test
    public void shouldAddTaskAfterOthersWithoutIntersection() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("T1", "Desc", Status.NEW, now, Duration.ofMinutes(20));
        Task lateTask = new Task("Late", "After all", Status.NEW, now.plusMinutes(30), Duration.ofMinutes(15));

        manager.createTask(task1);
        manager.createTask(lateTask);

        List<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertTrue(tasks.contains(lateTask), "Late task should be in the list");
    }

    @Test
    public void shouldNotAddTaskInsideExistingTask() {
        LocalDateTime now = LocalDateTime.now();

        Task outer = new Task("Outer", "Desc", Status.NEW, now, Duration.ofMinutes(30));
        Task inner = new Task("Inner", "Desc", Status.NEW, now.plusMinutes(5), Duration.ofMinutes(10));

        manager.createTask(outer);
        manager.createTask(inner);

        List<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertFalse(tasks.contains(inner), "Inner task intersects and should be rejected");
    }

    @Test
    public void shouldNotAddTaskWithSameStartTime() {
        LocalDateTime now = LocalDateTime.now();

        Task first = new Task("First", "Desc", Status.NEW, now, Duration.ofMinutes(20));
        Task conflict = new Task("Conflict", "Same start", Status.NEW, now, Duration.ofMinutes(15));

        manager.createTask(first);
        manager.createTask(conflict);

        List<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertFalse(tasks.contains(conflict), "Task with same start time should not be added");
    }

    @Test
    public void shouldNotAddTaskWithSameEndTime() {
        LocalDateTime now = LocalDateTime.now();

        Task first = new Task("First", "Desc", Status.NEW, now, Duration.ofMinutes(20));
        Task conflict = new Task("Conflict", "Same end", Status.NEW, now.minusMinutes(10), Duration.ofMinutes(30));

        manager.createTask(first);
        manager.createTask(conflict);

        List<Task> tasks = manager.getPrioritizedTasks();
        Assertions.assertFalse(tasks.contains(conflict), "Task with overlapping end time should not be added");
    }

    @Test
    public void shouldNotAddTaskThatCoversExistingTask() {
        LocalDateTime now = LocalDateTime.now();

        Task inner = new Task("Inner", "Desc", Status.NEW, now.plusMinutes(5), Duration.ofMinutes(10));
        Task outer = new Task("Outer", "Desc", Status.NEW, now, Duration.ofMinutes(30));

        manager.createTask(inner);
        manager.createTask(outer);

        List<Task> tasks = manager.getPrioritizedTasks();
        System.out.println(tasks.size());
        Assertions.assertFalse(tasks.contains(outer), "Outer task intersects and should be rejected");
    }

}
