package org.example.project_cinemas_java.payload.request.auth_request;

import lombok.Data;

@Data
public class ConfirmForgotPasswordRequest {
    private String confirmCode;
    private String matKhauMoi;
    private String xacNhanMatKhau;
}
