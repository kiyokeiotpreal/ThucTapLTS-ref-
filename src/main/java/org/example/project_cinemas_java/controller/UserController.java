package org.example.project_cinemas_java.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.payload.request.auth_request.ChangePasswordRequest;
import org.example.project_cinemas_java.service.implement.UserService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) throws Exception {
        try{
            String result =  userService.changePassword(changePasswordRequest);
            return ResponseEntity.ok().body(result);

        }catch (DataNotFoundException e){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
    }

}
