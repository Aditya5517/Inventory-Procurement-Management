package com.inventory.procurement.dto;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email is Required") @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is Required")
    private String password;
}


