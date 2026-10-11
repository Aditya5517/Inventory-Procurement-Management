package com.inventory.procurement.service;
import com.inventory.procurement.dto.UserResponse;
import com.inventory.procurement.dto.CreateUserRequest;
import com.inventory.procurement.entity.User;
import com.inventory.procurement.repository.UserRepository;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public UserResponse createUser(CreateUserRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already registered: "+ request.getEmail());
        }
        User user=new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole((request.getRole()));
        user.setIsActive(true);
        user.setMustChangePassword(true);

        String hashedPassword=passwordEncoder.encode(request.getPassword());
        user.setPasswordHash(hashedPassword);
        User savedUser=userRepository.save(user);
        return  mapToUserResponse(savedUser);
    }
    private UserResponse mapToUserResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive(),
                user.getMustChangePassword(),
                user.getCreatedAt()
        );
    }
}
