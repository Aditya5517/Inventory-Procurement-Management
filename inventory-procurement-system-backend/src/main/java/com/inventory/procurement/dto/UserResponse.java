package com.inventory.procurement.dto;
import lombok.*;
import com.inventory.procurement.enums.UserRole;
import java.time.LocalDateTime;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private UserRole role;
    private Boolean isActive;
    private Boolean mustChangePassword;
    private LocalDateTime createdAt;

}
