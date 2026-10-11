package com.inventory.procurement.controller;

import com.inventory.procurement.dto.AuditLogResponse;
import com.inventory.procurement.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService
            auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>>
    getAllLogs() {

        return ResponseEntity.ok(
                auditLogService
                        .getAllLogs()
        );
    }
}