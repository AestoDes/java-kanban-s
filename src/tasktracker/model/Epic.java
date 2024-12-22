package tasktracker.model;

import tasktracker.utils.TaskManagerContainer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description, int id, TaskStatus status) {
        super(title, description, id, status);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    @Override
    public LocalDateTime getStartTime() {
        return subtaskIds.stream()
                .map(id -> TaskManagerContainer.getManager().getSubtask(id).getStartTime())
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public Duration getDuration() {
        return subtaskIds.stream()
                .map(id -> TaskManagerContainer.getManager().getSubtask(id).getDuration())
                .filter(Objects::nonNull)
                .reduce(Duration::plus)
                .orElse(Duration.ZERO);
    }

    @Override
    public LocalDateTime getEndTime() {
        return subtaskIds.stream()
                .map(id -> TaskManagerContainer.getManager().getSubtask(id).getEndTime())
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public String getType() {
        return "EPIC";
    }
}
