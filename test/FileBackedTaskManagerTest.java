import manager.FileBackedTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager fbtm;
    File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = File.createTempFile("TaskManager", ".tmp");
        fbtm = FileBackedTaskManager.loadFromFile(tempFile);
    }

    @Test
    public void ifEmpty() {
        assertEquals(0, fbtm.getTasks().size(), "Tasks should be empty on init");
        assertEquals(0, fbtm.getEpics().size(), "Epics should be empty on init");
        assertEquals(0, fbtm.getSubtasks().size(), "Subtasks should be empty on init");
    }

    @Test
    public void shouldSaveAndLoadSingleTaskCorrectly() {
        Task task = new Task("t1", "d1", Status.NEW);
        fbtm.createTask(task);
        task.setId(0);

        List<Task> loadedTasks = fbtm.getTasks();
        assertEquals(List.of(task), loadedTasks, "Loaded tasks do not match the saved one");
    }

    @Test
    public void shouldSaveAndLoadSingleEpicCorrectly() {
        Epic epic = new Epic("e1", "ee1", Status.NEW);
        fbtm.createEpic(epic);
        epic.setId(0);
        List<Epic> loadedEpics = fbtm.getEpics();
        assertEquals(List.of(epic), loadedEpics, "Loaded epics do not match the saved one");
    }

    @Test
    public void shouldSaveAndLoadSubtasksCorrectly() {
        Epic epic = new Epic("e1", "ee1", Status.NEW);
        fbtm.createEpic(epic); // ID = 0

        Subtask s1 = new Subtask("s1", "d1", Status.NEW, 0);
        Subtask s2 = new Subtask("s2", "d2", Status.NEW, 0);
        s1.setId(1);
        s2.setId(2);
        fbtm.createSubtask(s1); // ID = 1
        fbtm.createSubtask(s2); // ID = 2

        List<Subtask> loadedSubtasks = fbtm.getSubtasks();
        assertEquals(List.of(s1, s2), loadedSubtasks, "Loaded subtasks do not match the saved ones");
    }

    @Test
    public void shouldPersistAndReloadDataCorrectly() throws IOException {
        Task task = new Task("t1", "d1", Status.NEW);
        Epic epic = new Epic("e1", "ee1", Status.NEW);
        Subtask sub1 = new Subtask("s1", "d1", Status.NEW, 1);
        Subtask sub2 = new Subtask("s2", "d2", Status.DONE, 1);

        task.setId(0);
        epic.setId(1);
        sub1.setId(2);
        sub2.setId(3);

        fbtm.createTask(task);     // ID = 0
        fbtm.createEpic(epic);     // ID = 1
        fbtm.createSubtask(sub1);  // ID = 2
        fbtm.createSubtask(sub2);  // ID = 3


        // load data from file
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        //Compare
        assertEquals(List.of(task), loadedManager.getTasks(), "Tasks do not match after reload");
        assertEquals(List.of(epic), loadedManager.getEpics(), "Epics do not match after reload");
        assertEquals(List.of(sub1, sub2), loadedManager.getSubtasks(), "Subtasks do not match after reload");
    }

}
