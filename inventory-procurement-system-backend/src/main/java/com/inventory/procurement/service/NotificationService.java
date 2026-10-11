package com.inventory.procurement.service;

import com.inventory.procurement.dto.NotificationResponse;
import com.inventory.procurement.entity.Notification;
import com.inventory.procurement.entity.User;
import com.inventory.procurement.enums.NotificationType;
import com.inventory.procurement.repository.NotificationRepository;
import com.inventory.procurement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository
            notificationRepository;

    private final UserRepository userRepository;

    @Transactional
    public void createNotification(
            User user,
            NotificationType type,
            String title,
            String message,
            String entityType,
            Long entityId
    ) {

        Notification notification =
                new Notification();

        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);

        notification.setIsRead(false);

        notification.setRelatedEntityType(
                entityType
        );

        notification.setRelatedEntityId(
                entityId
        );

        notificationRepository.save(
                notification
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse>
    getMyNotifications(
            String email
    ) {

        User user =
                getUser(email);

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse>
    getUnreadNotifications(
            String email
    ) {

        User user =
                getUser(email);

        return notificationRepository
                .findByUserIdAndIsReadFalse(
                        user.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(
            Long id,
            String email
    ) {

        User user =
                getUser(email);

        Notification notification =
                notificationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        if (!notification
                .getUser()
                .getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "You cannot access this notification"
            );
        }

        notification.setIsRead(true);

        notification.setReadAt(
                LocalDateTime.now()
        );

        return mapToResponse(
                notificationRepository
                        .save(notification)
        );
    }

    private User getUser(
            String email
    ) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    private NotificationResponse mapToResponse(
            Notification notification
    ) {

        return new NotificationResponse(
                notification.getId(),

                notification.getType()
                        .name(),

                notification.getTitle(),

                notification.getMessage(),

                notification.getIsRead(),

                notification
                        .getRelatedEntityType(),

                notification
                        .getRelatedEntityId(),

                notification.getCreatedAt(),

                notification.getReadAt()
        );
    }
}