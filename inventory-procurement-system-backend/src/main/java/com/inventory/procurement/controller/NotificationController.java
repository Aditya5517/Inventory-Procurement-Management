package com.inventory.procurement.controller;

import com.inventory.procurement.dto.NotificationResponse;
import com.inventory.procurement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService
            notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>>
    getNotifications(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                notificationService
                        .getMyNotifications(
                                authentication.getName()
                        )
        );
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>>
    getUnreadNotifications(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                notificationService
                        .getUnreadNotifications(
                                authentication.getName()
                        )
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse>
    markAsRead(
            @PathVariable Long id,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                notificationService
                        .markAsRead(
                                id,
                                authentication.getName()
                        )
        );
    }
}