package com.example.finalproject.repository;

import com.example.finalproject.entity.ProTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProTaskRepository extends JpaRepository<ProTask, Long> {
    List<ProTask> findAll();
    List<ProTask> findAllByUserId(Long userId);
    void deleteById(Long id);
    ProTask getProTaskById(Long id);
}
