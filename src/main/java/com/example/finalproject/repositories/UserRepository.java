package com.example.finalproject.repositories;

import com.example.finalproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String email);
    List<User> findAll();
    void deleteById(Long id);
    User getUserById(Long id);
}
