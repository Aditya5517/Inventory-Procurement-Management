package com.inventory.procurement.dto;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String fullName;
    private String role;
}
