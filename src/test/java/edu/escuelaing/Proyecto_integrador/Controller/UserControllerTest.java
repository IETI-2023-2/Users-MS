package edu.escuelaing.Proyecto_integrador.Controller;
import edu.escuelaing.controller.UserController;
import edu.escuelaing.entity.User;
import edu.escuelaing.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {

    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userController = new UserController(userService);
    }

    // Prueba para el método getAllUsers()
    @Test
    public void shouldReturnAllUsers() {
        // Mockeamos la respuesta del servicio de usuarios
        List<User> users = new ArrayList<>();
        users.add(new User("1", "user1", "password1", "role1"));
        users.add(new User("2", "user2", "password2", "role2"));
        Mockito.when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Verificamos que la respuesta sea correcta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    // Prueba para el método getRoleByUsername()
    @Test
    public void shouldReturnUserRole() {
        // Se mockea la respuesta del servicio user
        String username = "user1";
        String userRole = "role1";
        Mockito.when(userService.getRoleByUsername(username)).thenReturn(userRole);

        // Llamamos al método getRoleByUsername()
        ResponseEntity<String> response = userController.getRoleByUsername(username);

        // Verificamos que la respuesta sea correcta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userRole, response.getBody());
    }

    // Prueba para el método getUserById()
    @Test
    public void shouldReturnUserById() {
        // Se mockea la respuesta del servicio de usuarios
        String id = "1";
        User user = new User(id, "user1", "password1", "role1");
        Mockito.when(userService.getUserById(id)).thenReturn(Optional.of(user));

        // Llamamos al método getUserById()
        ResponseEntity<User> response = userController.getUserById(id);

        // Verificamos que la respuesta sea correcta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    // Prueba para el método getPasswordByUsername()
    @Test
    public void shouldReturnPasswordByUsername() {
        // Se mockea la respuesta del servicio de usuarios
        String username = "user1";
        String password = "password1";
        Mockito.when(userService.getPasswordByUsername(username)).thenReturn(password);

        // Llamamos al método getPasswordByUsername()
        String actualPassword = userController.getPasswordByUsername(username);

        // Verificamos que la respuesta sea correcta
        assertEquals(password, actualPassword);
    }

    // Prueba para el método updateUser()
    @Test
    public void shouldUpdateUser() {
        // Se mockea la respuesta del servicio de usuarios
        String id = "1";
        User updatedUser = new User(id, "user1_updated", "password1_updated", "role1_updated");
        Mockito.when(userService.updateUser(id, updatedUser)).thenReturn(Optional.of(updatedUser));

        // Llamamos al método updateUser()
        ResponseEntity<User> response = userController.updateUser(id, updatedUser);

        // Verificamos que la respuesta sea correcta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
    }
}

