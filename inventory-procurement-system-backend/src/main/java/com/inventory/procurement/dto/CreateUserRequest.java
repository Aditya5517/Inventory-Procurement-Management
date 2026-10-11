package com.inventory.procurement.dto;
import com.inventory.procurement.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateUserRequest {
    @NotBlank(message="Full name is required")
    private String fullName;

    @NotBlank(message="Email required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is Required") @Size(min=6,message = "Password must be atleast 6 character long")
    private String password;

    @NotNull(message = "User role is required")
    private UserRole role;
}
