package tasktracker.manager;

import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    @Test
    public void shouldLoadFromEmptyFile() throws IOException {
        // Создаём временный пустой файл
        File file = File.createTempFile("empty_tasks", ".csv");
        file.deleteOnExit(); // Удаляется после завершения теста

        // Загружаем менеджер из пустого файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        // Проверяем, что данные пустые
        assertTrue(loadedManager.getAllTasks().isEmpty(), "Tasks list should be empty");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Epics list should be empty");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Subtasks list should be empty");
    }

    @Test
    public void shouldSaveAndLoadTasks() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = new Task("Task", "Test task", manager.generateId(), TaskStatus.NEW);
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getAllTasks().size(), "There should be 1 task");
        assertEquals(task.getTitle(), loadedManager.getAllTasks().get(0).getTitle());
    }
}
