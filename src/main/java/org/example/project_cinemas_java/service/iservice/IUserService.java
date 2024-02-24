package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.request.auth_request.ChangePasswordRequest;

public interface IUserService {
    void updateInfoUser (User user);

    String changePassword(ChangePasswordRequest changePasswordRequest) throws Exception;
}
