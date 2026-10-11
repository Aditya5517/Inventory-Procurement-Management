package com.inventory.procurement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogResponse {

    private Long id;

    private String userEmail;

    private String action;

    private String entityType;

    private Long entityId;

    private String oldValue;

    private String newValue;

    private LocalDateTime createdAt;
}