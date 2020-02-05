package com.yappyapps.spotlight.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger.web.UiConfiguration;

@Component
@Primary
@EnableAutoConfiguration
public class DocumentationController implements SwaggerResourcesProvider {
 
    @Override
    public List get() {
        List resources = new ArrayList<>();
        resources.add(swaggerResource("spotlight-user-service", "/v2/api-docs", "2.0"));
        resources.add(swaggerResource("spotlight-genre-service", "/v2/api-docs", "2.0"));
        resources.add(swaggerResource("spotlight-event-service", "/v2/api-docs", "2.0"));
        resources.add(swaggerResource("spotlight-viewer-service", "/v2/api-docs", "2.0"));
//        resources.add(swaggerResource("customer-service", "/api/customer/v2/api-docs", "2.0"));
//        resources.add(swaggerResource("product-service", "/api/product/v2/api-docs", "2.0"));
//        resources.add(swaggerResource("transfer-service", "/api/transfer/v2/api-docs", "2.0"));
        return resources;
    }
 
    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
    @Bean
    UiConfiguration uiConfig() {
        return new UiConfiguration("validatorUrl", "list", "alpha", "schema",
                UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, 60000L);
    }
}