package tasktracker.utils;

import tasktracker.manager.TaskManager;

public class TaskManagerContainer {
    private static TaskManager manager;

    public static TaskManager getManager() {
        return manager;
    }

    public static void setManager(TaskManager manager) {
        TaskManagerContainer.manager = manager;
    }
}
