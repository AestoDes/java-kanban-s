package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void getHistoryTest() {
        Task task = new Task("Test Task", "Test Description", taskManager.generateId(), TaskStatus.NEW);
        taskManager.createTask(task);

        // Получаем задачу, чтобы добавить её в историю
        taskManager.getTask(task.getId());

        // Проверяем, что история содержит 1 задачу
        assertEquals(1, taskManager.getHistory().size());
        assertEquals(task, taskManager.getHistory().get(0));
    }

    @Test
    public void testInitialization() {
        assertNotNull(taskManager);
    }
}
