package com.example.finalproject.entity;

import com.example.finalproject.util.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
@ToString(exclude = "password")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username must not be blank")
    @Size(min = 5, max = 20, message = "Username length must be less than 20 and bigger than 5")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 5, max = 20, message = "Password length must be less than 20 and bigger than 5")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "User role must not be blank")
    private UserRole userRole;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProTask> proTasks = new ArrayList<>();
}
