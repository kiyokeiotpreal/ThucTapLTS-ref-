package org.example.project_cinemas_java.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.ConfirmEmailExpired;
import org.example.project_cinemas_java.exceptions.ConfirmPasswordIncorrect;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.exceptions.DisabledException;
import org.example.project_cinemas_java.model.ConfirmEmail;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.request.auth_request.*;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.service.implement.AuthService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private ConfirmEmailRepo confirmEmailRepo;

    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private RankCustomerRepo rankCustomerRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserStatusRepo userStatusRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest)  {
        try {
            authService.register(registerRequest);
            return ResponseEntity.ok().body("Kiểm tra email để xác thực tài khoản");
        } catch (DataIntegrityViolationException | IllegalStateException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            var loginDTO = authService.login(loginRequest);
            return ResponseEntity.ok().body(loginDTO);
        } catch (DataNotFoundException e) {
            // Email không tồn tại
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException e) {
            // Sai mật khẩu hoặc thông tin đăng nhập không hợp lệ
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }catch (DisabledException e){
            // taif khoản bị vô hiệu hóa
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            //lỗi khác do serve
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/confirm-register")
    public ResponseEntity<?> confirmEmail (@RequestParam String confirmCode){
        try {
            var isConfirm = authService.confirmEmail(confirmCode);
            if(isConfirm){
                String emailTemporary = authService.getEmailTemporary();
                RegisterRequest registerRequest = authService.getPendingRegistration(emailTemporary);
                User user = new User();
                user.setPoint(0);
                user.setName(registerRequest.getName());
                user.setUserName(registerRequest.getUserName());
                user.setRole(roleRepo.findById(2).orElse(null));
                user.setEmail(emailTemporary);
                user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                user.setPhoneNumber(registerRequest.getPhoneNumber());
                user.setRankcustomer(rankCustomerRepo.findById(1).orElse(null));

                user.setActive(true);
                user.setUserStatus(userStatusRepo.findById(1).orElse(null));
                userRepo.save(user);
            }
            return ResponseEntity.ok().body("Đăng kí thành công");
        } catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (ConfirmEmailExpired ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/requestForgotPassword")
    public ResponseEntity<?> requestForgotPassword(@RequestParam String email){
        try {
            authService.requestForgotPassword(email);
            User user = userRepo.findByEmail(email).orElse(null);
            authService.sendConfirmEmail(email);
            return ResponseEntity.ok().body("Kiểm tra email để đặt lại mật khẩu"   );
        }catch (DataNotFoundException | DisabledException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            //lỗi khác do serve
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/confirmForgotPassword")
    public ResponseEntity<?> confirmForgotPassword(@RequestBody ConfirmForgotPasswordRequest confirmForgotPasswordRequest){
        try {
            String confirmCode = confirmForgotPasswordRequest.getConfirmCode();
            ConfirmEmail confirmEmail = confirmEmailRepo.findConfirmEmailByConfirmCode(confirmCode);
            User user = userRepo.findByConfirmEmails(confirmEmail);
            boolean isConfirm = authService.confirmEmail(confirmCode);
            if(isConfirm){
                authService.confirmForgotPassword(confirmForgotPasswordRequest);

            }
        }catch (DataNotFoundException | ConfirmPasswordIncorrect ex){
            return ResponseEntity.badRequest().body(ex.getMessage());

        }catch (Exception ex){
            return ResponseEntity.badRequest().body(MessageKeys.VERIFICATION_CODE_HAS_EXPIRED);
        }
        return ResponseEntity.ok().body("Đặt lại mật khẩu thành công");
    }
}

