import manager.HistoryManager;
import manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {
    @Test
    public void returnDefaultTaskManagerTest(){
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    public void returnDefaultHistoryTest(){
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}
