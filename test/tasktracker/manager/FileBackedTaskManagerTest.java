package tasktracker.manager;

import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    public void shouldLoadFromEmptyFile() throws IOException {
        File file = File.createTempFile("empty_tasks", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Tasks list should be empty");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Epics list should be empty");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Subtasks list should be empty");
    }

    @Test
    public void shouldSaveAndLoadTasksWithNewFields() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task = new Task("Task", "Test task", manager.generateId(), TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getAllTasks().size(), "There should be 1 task");
        Task loadedTask = loadedManager.getAllTasks().get(0);

        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getDuration(), loadedTask.getDuration());
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getEndTime(), loadedTask.getEndTime());
    }

    @Test
    public void shouldThrowErrorForInvalidFile() throws IOException {
        File file = File.createTempFile("invalid_tasks", ".csv");
        file.deleteOnExit();

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write("invalid,data");
        }

        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(file),
                "Expected ManagerSaveException to be thrown for invalid file");
    }
}
