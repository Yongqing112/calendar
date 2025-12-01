package com.calendar.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class AuthController {

    private static final List<String> users = Arrays.asList("爸爸", "媽媽", "哥哥", "妹妹", "admin");
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
		System.out.println("Login:" + payload);
        String username = payload.get("username");

        if(users.contains(username)){
            Map<String, Object> result = new HashMap<>();
            result.put("username", username);
            result.put("role", "member");
            return ResponseEntity.ok(result);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("login fail or user not exist");
        }
    }

}
