package org.example.project_cinemas_java.service.implement;

import lombok.Getter;
import org.example.project_cinemas_java.components.JwtTokenUtils;
import org.example.project_cinemas_java.exceptions.ConfirmEmailExpired;
import org.example.project_cinemas_java.exceptions.ConfirmPasswordIncorrect;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.ConfirmEmail;
import org.example.project_cinemas_java.model.RefreshToken;
import org.example.project_cinemas_java.model.Role;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.dto.authdtos.LoginDTO;
import org.example.project_cinemas_java.payload.request.auth_request.ChangePasswordRequest;
import org.example.project_cinemas_java.payload.request.auth_request.ConfirmForgotPasswordRequest;
import org.example.project_cinemas_java.payload.request.auth_request.LoginRequest;
import org.example.project_cinemas_java.payload.request.auth_request.RegisterRequest;
import org.example.project_cinemas_java.repository.*;
import org.example.project_cinemas_java.service.iservice.IAuthService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class   AuthService implements IAuthService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private ConfirmEmailRepo confirmEmailRepo;

    private final Map<String, RegisterRequest> pendingRegistrations = new HashMap<>();
    @Getter
    public String emailTemporary = "";
    @Override
    public void register(RegisterRequest registerRequest){
        String email = registerRequest.getEmail();

        if(userRepo.existsByEmail(email)){
            throw new DataIntegrityViolationException(MessageKeys.EMAIL_ALREADY_EXISTS);
        }

        pendingRegistrations.put(email, registerRequest);
        User user = userRepo.findByEmail(registerRequest.getEmail()).orElse(null);
        sendConfirmEmail(email);

        emailTemporary = registerRequest.getEmail();

    }
    public RegisterRequest getPendingRegistration(String email) {
        return pendingRegistrations.get(email);
    }

    @Override
    public LoginDTO login(LoginRequest loginRequest) throws Exception {
        User existingUser = userRepo.findByEmail(loginRequest.getEmail()).orElse(null);
        if(existingUser == null) {
            throw new DataNotFoundException(MessageKeys.EMAIL_DOES_NOT_EXISTS);
        }
        if(!existingUser.isActive()){
            throw new DisabledException(MessageKeys.USER_ACCOUNT_IS_DISABLED);
        }

        //Chuyền email,password, role vào authenticationToken để xac thực ngươi dùng
        UsernamePasswordAuthenticationToken auToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword(),
                existingUser.getAuthorities()
        );
        //Xác thực người dùng (nếu xác thực không thành công VD: sai pass ) thì sẽ ném ra ngoại lệ
        authenticationManager.authenticate(auToken);

        //Lấy role của user
        User userDetails = (User) userDetailsService.loadUserByUsername(loginRequest.getEmail());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        //sinh ngẫu nhiên 1 token từ existingUser
        String token = jwtTokenUtils.generateToken(existingUser);

        RefreshToken refreshTokens = refreshTokenService.createRefreshToken(existingUser.getId());

        LoginDTO loginDTO= LoginDTO.builder()
                .id(existingUser.getId())
                .userName(existingUser.getUserName())
                .token(token)
                .refreshToken(refreshTokens.getToken())
                .roles(roles)
                .build();
        return loginDTO;
    }

    @Override
    public void requestForgotPassword(String email) throws Exception {
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null) {
            throw new DataNotFoundException(MessageKeys.EMAIL_DOES_NOT_EXISTS);
        }
        if(!user.isActive()){
            throw new DisabledException(MessageKeys.USER_ACCOUNT_IS_DISABLED);
        }
    }

    @Override
    public void confirmForgotPassword(ConfirmForgotPasswordRequest confirmForgotPasswordRequest) throws Exception {
        ConfirmEmail confirmEmail = confirmEmailRepo.findConfirmEmailByConfirmCode(confirmForgotPasswordRequest.getConfirmCode());
        if(confirmEmail == null){
            throw new DataNotFoundException(MessageKeys.INCORRECT_VERIFICATION_CODE);
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String mkMoi = confirmForgotPasswordRequest.getMatKhauMoi();
        if(!mkMoi.equals(confirmForgotPasswordRequest.getXacNhanMatKhau())){
            throw new ConfirmPasswordIncorrect(MessageKeys.CONFIRM_PASSWORD_INCORRECT);
        }
        User user = userRepo.findByConfirmEmails(confirmEmail);
        user.setPassword(bCryptPasswordEncoder.encode(mkMoi));
        userRepo.save(user);
        confirmEmail.setUser(null);
        confirmEmailRepo.delete(confirmEmail);
    }

    public User registerRequestToUser(RegisterRequest registerRequest){
        return this.modelMapper.map(registerRequest, User.class);
    }

    @Autowired
    private JavaMailSender javaMailSender;
    private String generateConfirmCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);
    }
    private void sendEmail(String to, String subject, String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("doan77309@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        javaMailSender.send(message);

    }

    public boolean isExpired(ConfirmEmail confirmEmail) {
        return LocalDateTime.now().isAfter(confirmEmail.getExpiredTime());
    }

    @Override
    public void sendConfirmEmail(String email) {
        ConfirmEmail confirmEmail = ConfirmEmail.builder()
                .confirmCode(generateConfirmCode())
                .expiredTime(LocalDateTime.now().plusSeconds(60))
                .isConfirm(false)
                .requiredTime(LocalDateTime.now())
                .build();
        confirmEmailRepo.save(confirmEmail);

        //todo Gửi email với mã code và thông tin
        String subject ="Xác nhận email của bạn";
        String content = "Mã xác thực của bạn là: " + confirmEmail.getConfirmCode();
        sendEmail(email,subject,content);
    }

    @Override
    public boolean confirmEmail(String confirmCode) throws Exception {
        ConfirmEmail confirmEmail = confirmEmailRepo.findConfirmEmailByConfirmCode(confirmCode);

        if(confirmEmail == null ){
            throw  new DataNotFoundException(MessageKeys.INCORRECT_VERIFICATION_CODE);
        }

        if (isExpired(confirmEmail)){
            throw new ConfirmEmailExpired(MessageKeys.VERIFICATION_CODE_HAS_EXPIRED);
        }

        confirmEmailRepo.delete(confirmEmail);

        return true;
    }
}
