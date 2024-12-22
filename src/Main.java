import tasktracker.manager.FileBackedTaskManager;
import tasktracker.model.Epic;
import tasktracker.model.Subtask;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Task 1", "Description 1", manager.generateId(), TaskStatus.NEW);
        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2", manager.generateId(), TaskStatus.NEW);
        manager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic Description", manager.generateId(), TaskStatus.NEW);
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description", manager.generateId(), TaskStatus.NEW, epic1.getId());
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description", manager.generateId(), TaskStatus.IN_PROGRESS, epic1.getId());
        manager.createSubtask(subtask2);

        System.out.println("Tasks before loading: " + manager.getAllTasks());
        System.out.println("Epics before loading: " + manager.getAllEpics());
        System.out.println("Subtasks before loading: " + manager.getAllSubtasks());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println("Tasks after loading: " + loadedManager.getAllTasks());
        System.out.println("Epics after loading: " + loadedManager.getAllEpics());
        System.out.println("Subtasks after loading: " + loadedManager.getAllSubtasks());
    }
}
