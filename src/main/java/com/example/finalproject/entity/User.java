package com.example.finalproject.entity;

import com.example.finalproject.util.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="users")
@Getter
@Setter
@ToString(exclude = "password")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id of the user")
    private Long id;

    @NotBlank(message = "Username must not be blank")
    @Size(min = 5, max = 20, message = "Username length must be less than 20 and bigger than 5 characters")
    @Schema(description = "Username of the user", minLength = 5, maxLength = 20)
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 5, max = 100, message = "Password length must be less than 100 and bigger than 5 characters")
    @Schema(description = "Password of the user", minLength = 5, maxLength = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Role of the user", example = "ADMIN")
    private UserRole userRole;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProTask> proTasks = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password) && userRole == user.userRole;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, userRole);
    }
}
