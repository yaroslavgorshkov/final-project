package com.example.finalproject.services;

import com.example.finalproject.entity.ProTask;
import com.example.finalproject.repositories.ProTaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProTaskServiceTest {

    @Mock
    private ProTaskRepository proTaskRepository;

    @InjectMocks
    private ProTaskService proTaskService;

    @Test
    void testGetAllProTasks() {
        List<ProTask> proTasks = new ArrayList<>();
        proTasks.add(new ProTask());
        proTasks.add(new ProTask());

        when(proTaskRepository.findAll()).thenReturn(proTasks);

        List<ProTask> result = proTaskService.getAllProTasks();

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllTasksByUserId() {
        Long userId = 1L;
        List<ProTask> proTasks = new ArrayList<>();
        proTasks.add(new ProTask());
        proTasks.add(new ProTask());

        when(proTaskRepository.findAllByUserId(userId)).thenReturn(proTasks);

        List<ProTask> result = proTaskService.getAllTasksByUserId(userId);

        assertEquals(2, result.size());
    }

    @Test
    void testSaveProTask() {
        ProTask proTask = new ProTask();

        when(proTaskRepository.save(proTask)).thenReturn(proTask);

        ProTask result = proTaskService.saveProTask(proTask);

        assertNotNull(result);
        assertEquals(proTask, result);
    }

    @Test
    void testDeleteProTaskById() {
        Long id = 1L;

        proTaskService.deleteProTaskById(id);

        verify(proTaskRepository, times(1)).deleteById(id);
    }

    /*@Test
    void testGetProTaskById() {
        Long id = 1L;
        ProTask proTask = new ProTask();

        when(proTaskRepository.getProTaskById(id)).thenReturn(Optional.of(proTask));

        ProTask result = proTaskService.getProTaskById(id);

        assertNotNull(result);
        assertEquals(proTask, result);
    }*/
}

