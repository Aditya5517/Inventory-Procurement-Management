package com.inventory.procurement.service;

import com.inventory.procurement.dto.AuditLogResponse;
import com.inventory.procurement.entity.AuditLog;
import com.inventory.procurement.entity.User;
import com.inventory.procurement.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository
            auditLogRepository;

    @Transactional
    public void log(
            User user,
            String action,
            String entityType,
            Long entityId,
            String oldValue,
            String newValue
    ) {

        AuditLog log =
                new AuditLog();

        log.setUser(user);

        log.setAction(action);

        log.setEntityType(
                entityType
        );

        log.setEntityId(
                entityId
        );

        log.setOldValue(
                oldValue
        );

        log.setNewValue(
                newValue
        );

        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse>
    getAllLogs() {

        return auditLogRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuditLogResponse mapToResponse(
            AuditLog auditLog
    ) {

        return new AuditLogResponse(
                auditLog.getId(),

                auditLog.getUser() != null
                        ? auditLog
                        .getUser()
                        .getEmail()
                        : null,

                auditLog.getAction(),

                auditLog.getEntityType(),

                auditLog.getEntityId(),

                auditLog.getOldValue(),

                auditLog.getNewValue(),

                auditLog.getCreatedAt()
        );
    }
}