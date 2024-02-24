package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.components.JwtTokenUtils;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class   AuthService implements IAuthService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RankCustomerRepo rankCustomerRepo;
    @Autowired
    private UserStatusRepo userStatusRepo;
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
    @Override
    public User register(RegisterRequest registerRequest){
        String email = registerRequest.getEmail();

        if(userRepo.existsByEmail(email)){
            throw new DataIntegrityViolationException(MessageKeys.EMAIL_ALREADY_EXISTS);
        }
        Role userRole = roleRepo.findById(1).orElseThrow(()
            -> new IllegalStateException("Role not found with ID 2"));

        User user = User.builder()
                .point(0)
                .name(registerRequest.getName())
                .email(email)
                .userName(registerRequest.getUserName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(userRole)
                .rankcustomer(rankCustomerRepo.findById(1).orElse(null))
                .userStatus(userStatusRepo.findById(2).orElse(null))
                .build();

        return user;
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
}
