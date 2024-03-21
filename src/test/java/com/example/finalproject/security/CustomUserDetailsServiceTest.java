package com.example.finalproject.security;

import com.example.finalproject.entity.User;
import com.example.finalproject.service.UserService;
import com.example.finalproject.util.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
    private final String DEFAULT_NAME = "Jon";

    private final User DEFAULT_USER = User.builder()
            .username(DEFAULT_NAME)
            .password("password")
            .userRole(UserRole.USER).build();

    @Test
    public void CustomUserDetailsServiceTest_loadUserByUsername_success(){
        Optional<User> optionalUser = Optional.ofNullable(DEFAULT_USER);
        when(userService.findByUsername(DEFAULT_NAME)).thenReturn(optionalUser);
        org.springframework.security.core.userdetails.User userByUsername = customUserDetailsService.loadUserByUsername(DEFAULT_NAME);
        Assertions.assertEquals(optionalUser.get().getUsername(), userByUsername.getUsername());
        Assertions.assertEquals(optionalUser.get().getPassword(), userByUsername.getPassword());
        Assertions.assertTrue(userByUsername.getAuthorities().contains(new SimpleGrantedAuthority(UserRole.USER.toString())));
    }

    @Test
    public void CustomUserDetailsServiceTest_loadUserByUsername_UsernameNotFound(){
        Optional<User> optionalUser = Optional.empty();
        when(userService.findByUsername(DEFAULT_NAME)).thenReturn(optionalUser);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(DEFAULT_NAME));
    }
}