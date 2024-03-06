package com.example.finalproject.services;

import com.example.finalproject.entity.Task;
import com.example.finalproject.entity.User;
import com.example.finalproject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser (User user) {
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
    public User getUserById(Long id) {
        return userRepository.getUserById(id);
    }
}
