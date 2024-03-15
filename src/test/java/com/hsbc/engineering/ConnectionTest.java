package com.hsbc.engineering;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class ConnectionTest {
    static Connection conn;
    static final String user = "sa";
    static final String password = "";
    static final String url = "jdbc:h2:./target/test";

    static final Map<String, String> args = Map.of(
        Arguments.CLASS_NAME, "org.h2.Driver",
        Arguments.USER, user,
        Arguments.PWD, password,
        Arguments.URL, url,
        Arguments.SQL, "SELECT 2");

    @BeforeAll
    static void setUp() throws SQLException {
        conn = DriverManager.
                getConnection(url, user, password);
        conn.setAutoCommit(false);
    }

    @DisplayName("Does a simple connection test against H2")
    @Test
    void selectOne() throws SQLException {
        ResultSet rs = conn.prepareStatement("SELECT 1").executeQuery();
        rs.next();
        Assertions.assertEquals(rs.getInt(1), 1);
    }

    @Test
    void mainFunctionTest() {
        Assertions.assertDoesNotThrow(() -> JDBCInquirer.runSimpleConnectionTest(args));
    }

    @Test
    void perfFunctionTest() {
        Assertions.assertDoesNotThrow(() -> JDBCInquirer.timeExtractionTest(args));
    }

}
