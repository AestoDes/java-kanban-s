package tasktracker.manager;

import tasktracker.model.Epic;
import tasktracker.model.Subtask;
import tasktracker.model.Task;
import tasktracker.model.TaskStatus;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final Logger logger = Logger.getLogger(FileBackedTaskManager.class.getName());
    private final File file;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // Сохранение задач в файл
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic,duration,startTime\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            logger.severe("Ошибка при сохранении в файл: " + e.getMessage());
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    // Преобразование задачи в строку для сохранения
    private String toString(Task task) {
        String startTime = task.getStartTime() != null ? task.getStartTime().format(formatter) : "";
        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                task instanceof Subtask ? ((Subtask) task).getEpicId() : "",
                duration,
                startTime);
    }

    // Преобразование строки в задачу при загрузке
    private Task fromString(String value) {
        String[] fields = value.split(",");
        if (fields.length < 8) {
            logger.warning("Некорректная строка (меньше 8 полей): " + value);
            return null;
        }

        try {
            int id = Integer.parseInt(fields[0]);
            String type = fields[1];
            String title = fields[2];
            TaskStatus status = fields[3].isEmpty() ? TaskStatus.NEW : TaskStatus.valueOf(fields[3]);
            String description = fields[4];
            Integer epicId = fields[5].isEmpty() ? null : Integer.parseInt(fields[5]);
            Duration duration = fields[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(fields[6]));
            LocalDateTime startTime = fields[7].isEmpty() ? null : LocalDateTime.parse(fields[7], formatter);

            switch (type) {
                case "EPIC":
                    Epic epic = new Epic(title, description, id, status);
                    epic.setDuration(duration);
                    epic.setStartTime(startTime);
                    return epic;
                case "SUBTASK":
                    if (epicId == null) {
                        logger.warning("Subtask должен иметь epicId");
                        return null;
                    }
                    Subtask subtask = new Subtask(title, description, id, status, epicId);
                    subtask.setDuration(duration);
                    subtask.setStartTime(startTime);
                    return subtask;
                default: // TASK
                    Task task = new Task(title, description, id, status);
                    task.setDuration(duration);
                    task.setStartTime(startTime);
                    return task;
            }
        } catch (Exception e) {
            logger.warning("Ошибка при разборе строки: " + value + ". Причина: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    // Загрузка менеджера из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Пропускаем заголовок
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = manager.fromString(line);
                if (task == null) continue;
                if (task instanceof Epic) {
                    manager.createEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.createSubtask((Subtask) task);
                } else {
                    manager.createTask(task);
                }
            }
        } catch (IOException e) {
            logger.severe("Ошибка при загрузке из файла: " + e.getMessage());
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }
        return manager;
    }
}
