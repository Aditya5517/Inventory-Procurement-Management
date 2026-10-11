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
public class NotificationResponse {

    private Long id;

    private String type;

    private String title;

    private String message;

    private Boolean isRead;

    private String relatedEntityType;

    private Long relatedEntityId;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;
}