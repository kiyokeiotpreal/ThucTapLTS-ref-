package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.exceptions.ConfirmPasswordIncorrect;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.authdtos.LoginDTO;
import org.example.project_cinemas_java.payload.request.auth_request.ChangePasswordRequest;
import org.example.project_cinemas_java.payload.request.auth_request.ConfirmForgotPasswordRequest;
import org.example.project_cinemas_java.payload.request.auth_request.LoginRequest;
import org.example.project_cinemas_java.payload.request.auth_request.RegisterRequest;

public interface IAuthService {
    User register(RegisterRequest registerRequest) throws Exception;

    LoginDTO login(LoginRequest loginRequest) throws Exception;

    void requestForgotPassword(String email) throws Exception;

    void confirmForgotPassword(ConfirmForgotPasswordRequest confirmForgotPasswordRequest) throws Exception;

}
