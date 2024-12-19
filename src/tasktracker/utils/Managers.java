package tasktracker.utils;

import tasktracker.manager.FileBackedTaskManager;
import tasktracker.manager.TaskManager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("tasks.csv"));
    }
}
