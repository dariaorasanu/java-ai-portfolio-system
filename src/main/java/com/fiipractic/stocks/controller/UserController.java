package com.fiipractic.stocks.controller;

import com.fiipractic.stocks.dto.CreateUserRequest;
import com.fiipractic.stocks.dto.UserDTO;
import com.fiipractic.stocks.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDTO userDTO = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }
}