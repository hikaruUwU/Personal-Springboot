package com.demo.base.config;

import com.mybatisflex.core.audit.AuditManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Log4j2
@Configuration
public class SQLLog4j2Config {
//    private final DataSource dataSource;
//
//    public SQLLog4j2Config(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }

    @PostConstruct
    public void init() throws SQLException {
        AuditManager.setAuditEnable(true);

        //dataSource.getConnection().close();

        AuditManager.setMessageCollector(auditMessage -> log.info("SQL PLAN >>> {}", auditMessage));
    }
}
