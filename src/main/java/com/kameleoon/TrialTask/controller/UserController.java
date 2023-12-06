package com.kameleoon.TrialTask.controller;

import com.kameleoon.TrialTask.dto.ApiResponse;
import com.kameleoon.TrialTask.dto.ApiResponseSingleOk;
import com.kameleoon.TrialTask.dto.UserAuthDto;
import com.kameleoon.TrialTask.dto.UserDto;
import com.kameleoon.TrialTask.exception.RequiredRequestParamIsMissing;
import com.kameleoon.TrialTask.model.User;
import com.kameleoon.TrialTask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<UserDto> getUser(Principal principal){
        return new ResponseEntity<>(new UserDto(userService.findUserByUserName(principal.getName())), HttpStatus.OK);
    }

    @PutMapping("/user/update")
    public ResponseEntity<ApiResponse> updateUser(Principal principal, @Valid @RequestBody(required = false) UserAuthDto userAuthDto){
        if(userAuthDto == null)
            throw new RequiredRequestParamIsMissing("Required request param UserAuthDto is missing");

        userService.updateUser(principal.getName(), userAuthDto);
        return new ResponseEntity<>(new ApiResponseSingleOk("Update User", "User [" + principal.getName() + "] updated!"), HttpStatus.OK);
    }
}
