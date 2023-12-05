package com.kameleoon.TrialTask.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class UserController {
    @GetMapping("/user")
    public ResponseEntity<String> getUsername(Principal principal){
        return new ResponseEntity<>(principal.getName(), HttpStatus.OK);
    }
}
