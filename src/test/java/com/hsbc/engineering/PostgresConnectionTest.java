package com.hsbc.engineering;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class PostgresConnectionTest {
    final String user       = "hello";
    final String password   = "moo";
    final String database   = "foo";

    private Map<String, String> getConnectionMap() {
        Map<String, String> args = Map.of(
        Arguments.CLASS_NAME,   "org.postgresql.Driver",
        Arguments.USER,         user,
        Arguments.PWD,          password,
        Arguments.URL,          pSqlContainer.getJdbcUrl(),
        Arguments.SQL,          "SELECT 2");

        return args;
    }

    @Container
    private PostgreSQLContainer pSqlContainer = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName(database)
        .withUsername(user)
        .withPassword(password);

    @Test
    void checkOK() {
        assertTrue(pSqlContainer.isRunning());
    }

     @Test
    void mainFunctionTest() {
        Assertions.assertDoesNotThrow(() -> JDBCInquirer.runSimpleConnectionTest(getConnectionMap()));
    }

    @Test
    void perfFunctionTest() {
        Assertions.assertDoesNotThrow(() -> JDBCInquirer.timeExtractionTest(getConnectionMap()));
    }
}
