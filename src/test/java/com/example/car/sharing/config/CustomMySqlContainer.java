package com.example.car.sharing.config;

import org.testcontainers.containers.MySQLContainer;

public class CustomMySqlContainer extends MySQLContainer<CustomMySqlContainer> {
    private static final String DB_IMAGE = "mysql:8";
    private static final String URL = "TEST_DB_URL";
    private static final String USERNAME = "TEST_DB_USERNAME";
    private static final String PASSWORD = "TEST_DB_PASSWORD";

    private static CustomMySqlContainer mysqlContainer;

    private CustomMySqlContainer() {
        super(DB_IMAGE);
    }

    public static synchronized CustomMySqlContainer getInstance() {
        if (mysqlContainer == null) {
            mysqlContainer = new CustomMySqlContainer();
        }
        return mysqlContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty(URL, mysqlContainer.getJdbcUrl());
        System.setProperty(USERNAME, mysqlContainer.getUsername());
        System.setProperty(PASSWORD, mysqlContainer.getPassword());
    }

    @Override
    public void stop() {

    }
}
