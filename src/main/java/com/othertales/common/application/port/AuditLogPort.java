package com.othertales.common.application.port;

import java.util.Map;
import java.util.UUID;

public interface AuditLogPort {

    void record(UUID userId, String actionType, String entityId, Map<String, Object> details, String ipAddress, String userAgent);
}
