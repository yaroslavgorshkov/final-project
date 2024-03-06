package com.example.finalproject.services;

import com.example.finalproject.entity.ProTask;
import com.example.finalproject.entity.Task;
import com.example.finalproject.repositories.ProTaskRepository;
import com.example.finalproject.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProTaskService {
    private final ProTaskRepository proTaskRepository;

    public List<ProTask> getAllProTasks() {
        return proTaskRepository.findAll();
    }
    public List<ProTask> getAllTasksByUserId(Long userId) {
        return proTaskRepository.findAllByUserId(userId);
    }

    public ProTask saveProTask(ProTask proTask) {
        return proTaskRepository.save(proTask);
    }

    public void deleteProTaskById(Long id) {
        proTaskRepository.deleteById(id);
    }

    public ProTask getProTaskById(Long id) {
        return proTaskRepository.getProTaskById(id);
    }
}
