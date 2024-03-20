package com.hsbc.engineering;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>JDBCSpecTest class.</p>
 *
 */
public class JDBCSpecTest {

    @DisplayName("Checks if tables can be extracted via jdbc")
    @ParameterizedTest
    @ArgumentsSource(ConnectionProvider.class)
    void check_if_list_of_tables_can_be_extracted(Connection conn) {
        int count = 0;

        try (ResultSet rs = conn.getMetaData().getTables(null, null, "%", null)) {
            while (rs.next()) {
                if (rs.getString(3).length() > 0) {
                    ++count;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }

        assertNotEquals(count, 0);
    }
}

class ConnectionProvider implements ArgumentsProvider {
    Connection conn;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        if (conn == null) {
            com.hsbc.engineering.Arguments args = new com.hsbc.engineering.Arguments();
            conn = JDBCInquirer.getConnection(args.get());
        }
        return Stream.of(Arguments.of(conn));
    }
}
