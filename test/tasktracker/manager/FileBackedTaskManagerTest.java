package tasktracker.manager;

import org.junit.jupiter.api.Test;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    public void shouldHandleCorruptedFile() throws IOException {
        File file = File.createTempFile("corrupted_tasks", ".csv");
        file.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic,duration,startTime\n");
            writer.write("1,TASK,Corrupted,,MissingFields,,invalid_duration,\n"); // Некорректная строка
            writer.write("2,TASK,Valid Task,NEW,Valid description,,30,2024-12-23T10:00\n"); // Корректная строка
        }

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getAllTasks().size(), "Только корректные задачи должны быть загружены");
        Task loadedTask = loadedManager.getAllTasks().get(0);

        assertEquals("Valid Task", loadedTask.getTitle(), "Имя задачи должно быть загружено корректно");
        assertEquals("Valid description", loadedTask.getDescription(), "Описание задачи должно быть корректным");
    }

    @Test
    public void shouldHandleTaskWithNullStartTime() throws IOException {
        File file = File.createTempFile("null_start_time_tasks", ".csv");
        file.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic,duration,startTime\n");
            writer.write("1,TASK,Task with null start time,NEW,Test task,,30,\n"); // Строка с null startTime
        }

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getAllTasks().size(), "Должна быть 1 задача");
        Task loadedTask = loadedManager.getAllTasks().get(0);

        assertEquals("Task with null start time", loadedTask.getTitle(), "Имя задачи должно совпадать");
        assertNull(loadedTask.getStartTime(), "Start time должно быть null");
        assertEquals(Duration.ofMinutes(30), loadedTask.getDuration(), "Длительность должна совпадать");
    }

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

        assertEquals(task.getTitle(), loadedTask.getTitle(), "Заголовок задачи должен совпадать");
        assertEquals(task.getDuration(), loadedTask.getDuration(), "Длительность должна совпадать");
        assertEquals(task.getStartTime(), loadedTask.getStartTime(), "Start time должен совпадать");
        assertEquals(task.getEndTime(), loadedTask.getEndTime(), "End time должен совпадать");
    }
}
