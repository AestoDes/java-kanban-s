package tasktracker.manager;

import tasktracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int HISTORY_LIMIT = 10;

    private final CustomLinkedList history = new CustomLinkedList();
    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private static class CustomLinkedList {
        Node<Task> head;
        Node<Task> tail;

        void linkLast(Task task, Node<Task> node) {
            if (tail == null) {
                head = tail = node;
            } else {
                tail.next = node;
                node.prev = tail;
                tail = node;
            }
        }

        void removeNode(Node<Task> node) {
            if (node == null) return;

            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
            }

            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }
        }

        List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            Node<Task> current = head;
            while (current != null) {
                tasks.add(current.data);
                current = current.next;
            }
            return tasks;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        Node<Task> newNode = new Node<>(task);
        history.linkLast(task, newNode);
        historyMap.put(task.getId(), newNode);

        if (historyMap.size() > HISTORY_LIMIT) {
            remove(history.head.data.getId());
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyMap.remove(id);
        if (node != null) {
            history.removeNode(node);
        }
    }
}
