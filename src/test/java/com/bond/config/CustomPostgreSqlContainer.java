package com.bond.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class CustomPostgreSqlContainer extends PostgreSQLContainer<CustomPostgreSqlContainer> {
    private static final String IMAGE_VERSION = "postgres:latest";
    private static CustomPostgreSqlContainer container;

    private CustomPostgreSqlContainer() {
        super(IMAGE_VERSION);
    }

    public static CustomPostgreSqlContainer getInstance() {
        if (container == null) {
            container = new CustomPostgreSqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.password", container.getPassword());
    }

    @Override
    public void stop() {

    }
}
