package tasktracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void shouldSortTasksByPriority() {
        Task task1 = new Task("Task 1", "Description 1", taskManager.generateId(), TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.now().plusHours(2));
        task1.setDuration(Duration.ofMinutes(30));
        taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2", taskManager.generateId(), TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofMinutes(45));
        taskManager.createTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(2, prioritizedTasks.size());
        assertEquals(task2, prioritizedTasks.get(0), "Task 2 should be first");
        assertEquals(task1, prioritizedTasks.get(1), "Task 1 should be second");
    }

    @Test
    public void shouldDetectTimeOverlaps() {
        Task task1 = new Task("Task 1", "Description 1", taskManager.generateId(), TaskStatus.NEW);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(60));
        taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2", taskManager.generateId(), TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.now().plusMinutes(30));
        task2.setDuration(Duration.ofMinutes(30));

        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2),
                "Task 2 overlaps with Task 1 and should not be allowed");
    }
}
