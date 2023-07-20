package com.hsbc.engineering;

import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.File;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.sql.*;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;


/**
 * This is for testing JDBC Connections. The program will connect to a JDBC data source and then run {@code "SELECT 1"}.
 *
 * JDBC Inquirer - tests connection via JDBC and also checks if JDBC specs are met <br>
 *
 * SYNOPSIS <br>
 * Using system properties: <br>
 * <pre>
 * java -cp [directory that contains this jar and the jdbc jars] -DJDBC_CLASS_NAME='jdbc_class_name' -DJDBC_URL='url' -DJDBC_USER='user (optional)' -DJDBC_PASSWORD='password (optional)' -DJDBC_SQL='optional sql to run' -DRUN_PERFORMANCE_EXTRACTION_TEST='optional to set true/false' com.hsbc.engineering.JDBCInquirer
 * </pre>
 *
 * Using environment variables:
 * <pre>
 * export JDBC_CLASS_NAME='jdbc_class_name'
 * export JDBC_URL='jdbc_url'
 * export JDBC_USER='jdbc_user'                 #optional
 * export JDBC_PASSWORD='jdbc_password'         #optional
 * export JDBC_SQL='jdbc_sql'                   #optional
 * export RUN_PERFORMANCE_EXTRACTION_TEST=true  #optional
 *
 * java -cp [directory that contains this jar and the jdbc jars] com.hsbc.engineering.JDBCInquirer
 * </pre>
 *
 * DESCRIPTION
 * Tests connection to a data source via JDBC connection. By default, it makes a connection and runs  " + DEFAULT_SQL
 * Example:
 * <pre>
 * java -cp "target/*" -DJDBC_CLASS_NAME="org.apache.hive.jdbc.HiveDriver" -DJDBC_USER="I_AM_A_USER" -DJDBC_PASSWORD="I_AM_A_TOKEN" -DJDBC_URL="jdbc:hive2://server:9999/schema" com.hsbc.engineering.JDBCInquirer
 * java -cp "target/*" -DJDBC_CLASS_NAME="org.apache.hive.jdbc.HiveDriver" -DJDBC_USER="I_AM_A_USER" -DJDBC_PASSWORD="I_AM_A_TOKEN" -DJDBC_URL="jdbc:hive2://server:9999/schema" -DJDBC_SQL="show databases" -DRUN_PERFORMANCE_EXTRACTION_TEST='true' com.hsbc.engineering.JDBCInquirer
 * </pre>
**/

public class JDBCInquirer {
    /**
     * Default logger
     */
    final static Logger LOG = Logger.getLogger(JDBCInquirer.class.getName());

    /**
     * Default main method
     * @param args This is not used
     * @throws ClassNotFoundException Raised if the driver class is not found
     * @throws SQLException Raised if the SQL to be run can't be executed
     */
    public static void main( String[] args ) throws ClassNotFoundException, SQLException {
        Arguments arguments = new Arguments();
        printClasspath();

        if (Boolean.parseBoolean(arguments.getItem(Arguments.RUN_PERFORMANCE_EXTRACTION_TEST)))
            timeExtractionTest(arguments.get());
        else
            runSimpleConnectionTest(arguments.get());

        if (Boolean.parseBoolean(arguments.getItem(Arguments.RUN_EXTENDED_TESTS)))
            runExtendedTests();

        System.exit(0);
    }

    /**
     * Prints out the classpath that was loaded
     */
    static void printClasspath() {
        String classpath = System.getProperty("java.class.path");
        String[] classPathValues = classpath.split(File.pathSeparator);

        LOG.info("**** CLASSPATHS ***");
        for (String classPath: classPathValues) {
            LOG.info(classPath);
        }
    }

    /**
     * Gets a jdbc connection
     * @param arguments The list of arguments to connect to the database
     * @return Connection to the database
     * @throws ClassNotFoundException Raised if the driver class is not found
     * @throws SQLException Raised if there is a connection issue
     */
    static Connection getConnection(Map<String, String> arguments) throws ClassNotFoundException, SQLException {
        Class.forName(arguments.get(Arguments.CLASS_NAME));

        Properties properties = new Properties();

        if (arguments.containsKey(Arguments.USER))
            properties.setProperty("user",     arguments.get(Arguments.USER));
        if (arguments.containsKey(Arguments.PWD))
            properties.setProperty("password", arguments.get(Arguments.PWD));

        LOG.info("****** Starting JDBC Connection test *******");
        Connection conn = DriverManager.getConnection(arguments.get(Arguments.URL), properties);
        conn.setAutoCommit(false);

        return conn;
    }

    /**
     * Does a simple database connection test and runs a SQL. Records are then printed out
     * @param arguments The list of arguments to connect to the database
     * @throws ClassNotFoundException Raised if the driver class is not found
     * @throws SQLException Raised if there is a connection issue
     */
    public static void runSimpleConnectionTest(Map<String, String> arguments) throws ClassNotFoundException, SQLException {
        String sqlQuery = arguments.get(Arguments.SQL);
        Connection conn = getConnection(arguments);
        Statement statement = conn.createStatement();
        LOG.info("Running SQL query: " + sqlQuery);
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        if (resultSet.next())
            LOG.info("Results returned: YES");
        else
            LOG.info("Results returned: NO");

        statement.close();
        conn.close();

        LOG.info("JDBC connection test successful!");
    }

    /**
     * Times how long it takes to extract all columns and rows and write to an empty stream
     * @param arguments The list of arguments to connect to the database
     * @throws ClassNotFoundException Raised if the driver class is not found
     * @throws SQLException Raised if there is a connection issue
     */
    public static void timeExtractionTest(Map<String, String> arguments) throws ClassNotFoundException, SQLException {
        String sqlQuery = arguments.get(Arguments.SQL);
        Connection conn = getConnection(arguments);
        Statement statement = conn.createStatement();

        LOG.info("Running SQL query: " + sqlQuery);

        Instant start = Instant.now();

        NullOutputStream emptyStream = new NullOutputStream();
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        int numberOfColumns = resultSet.getMetaData().getColumnCount();
        int numberOfRows = 0;

        while (resultSet.next()) {
            for(int i = 1; i <= numberOfColumns; ++i )
                emptyStream.write(resultSet.getObject(i));

            ++numberOfRows;
        }

        Instant finish = Instant.now();

        LOG.info("Time take to run and extract: " + Duration.between(start, finish).toMillis() + " ms");
        LOG.info("Number of Rows: " + numberOfRows);
        LOG.info("JDBC extraction test successful!");

        statement.close();
        conn.close();
    }

    public static void runExtendedTests() {
        System.out.println("Starting tests ---");

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage("com.hsbc.engineering"))
                .filters(includeClassNamePatterns(".*Test"))
                .listeners()
                .build();

        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        try (LauncherSession session = LauncherFactory.openSession()) {
            Launcher launcher = session.getLauncher();
            // Register a listener of your choice
            launcher.registerTestExecutionListeners(listener);
            // Discover tests and build a test plan
            TestPlan testPlan = launcher.discover(request);

            for (TestIdentifier root : testPlan.getRoots()) {
                System.out.println("Root: " + root.toString());

                for (TestIdentifier test : testPlan.getChildren(root)) {
                    System.out.println("Found test: " + test.toString());
                }
            }
            // Execute test plan
            launcher.execute(testPlan);
        }

        TestExecutionSummary summary = listener.getSummary();
        if (summary.getTestsFailedCount() > 0) {
            LOG.severe("Failed tests");
            summary.getFailures().forEach(f -> System.out.println(f.getTestIdentifier().getLegacyReportingName() + " - " + f.getException()));
        }
        summary.printTo(new PrintWriter(System.out));
        System.out.println("Completing tests ---");
    }
}
