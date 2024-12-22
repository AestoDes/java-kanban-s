package tasktracker.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private final String title;
    private final String description;
    private final int id;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String title, String description, int id, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime != null && duration != null
                ? startTime.plus(duration)
                : null;
    }

    public String getType() {
        return "TASK";
    }
}
