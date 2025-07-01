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

public class FileBackedTaskManagerTest extends TaskManagerTest {
    private File file;

    public FileBackedTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        try{
            file = File.createTempFile("TaskManager", ".tmp");
            super.setUpTest(new FileBackedTaskManager(file));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void ifEmpty() {
        assertEquals(0, manager.getTasks().size(), "Tasks should be empty on init");
        assertEquals(0, manager.getEpics().size(), "Epics should be empty on init");
        assertEquals(0, manager.getSubtasks().size(), "Subtasks should be empty on init");
    }

    @Test
    public void shouldSaveAndLoadSingleTaskCorrectly() {
        Task task = new Task("t1", "d1", Status.NEW);
        manager.createTask(task);
        task.setId(0);

        List<Task> loadedTasks = manager.getTasks();
        assertEquals(List.of(task), loadedTasks, "Loaded tasks do not match the saved one");
    }

    @Test
    public void shouldSaveAndLoadSingleEpicCorrectly() {
        Epic epic = new Epic("e1", "ee1", Status.NEW);
        manager.createEpic(epic);
        epic.setId(0);
        List<Epic> loadedEpics = manager.getEpics();
        assertEquals(List.of(epic), loadedEpics, "Loaded epics do not match the saved one");
    }

    @Test
    public void shouldSaveAndLoadSubtasksCorrectly() {
        Epic epic = new Epic("e1", "ee1", Status.NEW);
        manager.createEpic(epic); // ID = 0

        Subtask s1 = new Subtask("s1", "d1", Status.NEW, 0);
        Subtask s2 = new Subtask("s2", "d2", Status.NEW, 0);
        s1.setId(1);
        s2.setId(2);
        manager.createSubtask(s1); // ID = 1
        manager.createSubtask(s2); // ID = 2

        List<Subtask> loadedSubtasks = manager.getSubtasks();
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

        manager.createTask(task);     // ID = 0
        manager.createEpic(epic);     // ID = 1
        manager.createSubtask(sub1);  // ID = 2
        manager.createSubtask(sub2);  // ID = 3


        // load data from file
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);


        //Compare
        assertEquals(List.of(task), loadedManager.getTasks(), "Tasks do not match after reload");
        assertEquals(List.of(epic), loadedManager.getEpics(), "Epics do not match after reload");
        assertEquals(List.of(sub1, sub2), loadedManager.getSubtasks(), "Subtasks do not match after reload");
    }

}
