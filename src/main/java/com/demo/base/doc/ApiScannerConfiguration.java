package com.demo.base.doc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

public class ApiScannerConfiguration{
    @Value("${api.exposure.path}")
    private String BASEMENT = "/api";

    @Bean
    public APIMetaDataCollector apiMetadataCollector() {
        return new APIMetaDataCollector();
    }

    @Bean
    @ConditionalOnBean(APIMetaDataCollector.class)
    public RouterFunction<ServerResponse> api(APIMetaDataCollector collector){
        return route(GET(BASEMENT),collector::getApiRegistry);
    }

}