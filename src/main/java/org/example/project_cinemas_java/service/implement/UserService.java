package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.User;
import org.example.project_cinemas_java.payload.request.auth_request.ChangePasswordRequest;
import org.example.project_cinemas_java.repository.UserRepo;
import org.example.project_cinemas_java.service.iservice.IUserService;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepo userRepo;
    @Override
    public void updateInfoUser(User user) {

    }

    @Override
    public String changePassword(ChangePasswordRequest changePasswordRequest) throws DataNotFoundException{
        User user = userRepo.findByEmail(changePasswordRequest.getEmail()).orElse(null);
        if(user == null){
            throw new DataNotFoundException(MessageKeys.USER_DOES_NOT_EXIST);
        }
        String mkCu = changePasswordRequest.getMatKhauCu();
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        if(!bCrypt.matches(mkCu, user.getPassword())){
            return "Mật khẩu cũ không chính xác";
        }
        String mkMoi = changePasswordRequest.getMatKhauMoi();
        if(!mkMoi.equals(changePasswordRequest.getXacNhanMatKhau())){
            return "Xác nhận mật khẩu không đúng";
        }
        user.setPassword(bCrypt.encode(mkMoi));
        userRepo.save(user);
        return "Thay đổi mật khẩu thành công ";
    }
}
