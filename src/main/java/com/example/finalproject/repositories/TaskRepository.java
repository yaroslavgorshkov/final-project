package com.example.finalproject.repositories;

import com.example.finalproject.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAll();
    List<Task> findAllByUserId(Long userId);
    void deleteById(Long id);
    Task getTaskById(Long id);
}
