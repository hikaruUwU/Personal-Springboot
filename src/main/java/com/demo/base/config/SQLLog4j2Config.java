package com.demo.base.config;

import com.mybatisflex.core.audit.AuditManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class SQLLog4j2Config {
    @PostConstruct
    public void init() {
        AuditManager.setAuditEnable(true);

        AuditManager.setMessageCollector(auditMessage -> log.info("SQL PLAN >>> {}", auditMessage));
    }
}
