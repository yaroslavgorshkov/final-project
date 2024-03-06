package com.example.finalproject.services;

import com.example.finalproject.entity.Task;
import com.example.finalproject.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getAllTasksByUserId(Long userId) {
        return taskRepository.findAllByUserId(userId);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }
    public Task getTaskById(Long id) {
        return taskRepository.getTaskById(id);
    }
}
