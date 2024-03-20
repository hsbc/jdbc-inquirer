package com.hsbc.engineering;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Reads in arguments - via environment variables or system properties. Checks for nulls.
 *
 */
public class Arguments {
    /**
     * Default logger
     */
    public final static Logger logger = Logger.getLogger(Arguments.class.getName());

    /**
     * JDBC Class to load
     */
    public final static String CLASS_NAME   = "JDBC_CLASS_NAME";

    /**
     * User of database
     */
    public final static String USER         = "JDBC_USER";

    /**
     * PASSWORD for database
     */
    public final static String PWD          = "JDBC_PASSWORD";

    /**
     * URL of database to connect to
     */
    public final static String URL          = "JDBC_URL";

    /**
     * Optional - SQL to run against database
     */
    public final static String SQL          = "JDBC_SQL";
    /**
     * Default sql that is run
     */
    final static String DEFAULT_SQL         = "SELECT '1'";

    /** Constant <code>RUN_EXTENDED_TESTS="RUN_EXTENDED_TESTS"</code> */
    public final static String RUN_EXTENDED_TESTS = "RUN_EXTENDED_TESTS";
    /** Constant <code>RUN_PERFORMANCE_EXTRACTION_TEST="RUN_PERFORMANCE_EXTRACTION_TEST"</code> */
    public final static String RUN_PERFORMANCE_EXTRACTION_TEST = "RUN_PERFORMANCE_EXTRACTION_TEST";
    static boolean bool_run_extended_tests = false;
    static boolean bool_run_performance_extraction_test = false;
    private Map<String, String> arguments;

    /**
     * <p>Constructor for Arguments.</p>
     */
    public Arguments() {
        if (System.getenv().containsKey(CLASS_NAME)) {
            logger.log(Level.INFO, "Found environment variables set and using variables");
            set(System.getenv());
        }
        else if (System.getProperties().containsKey(CLASS_NAME)) {
            logger.log(Level.INFO, "Found properties set and using variables");
            Map<String, String> args = System.getProperties().entrySet().stream()
                    .collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
            set(args);
        }
        else {
            logger.log(Level.SEVERE, "Neither the environment variables not set nor the system properties set");
            help();
        }
    }

    /**
     * <p>Constructor for Arguments.</p>
     *
     * @param systemEnvs a {@link java.util.Map} object
     */
    public Arguments(Map<String, String> systemEnvs) {
        set(systemEnvs);
    }
    /**
     * <p>set.</p>
     *
     * @param systemEnvs a {@link java.util.Map} object
     * @return a {@link java.util.Map} object
     */
    public Map<String, String> set(Map<String, String> systemEnvs) {
        final boolean bool_run_extended_tests = Boolean.parseBoolean(systemEnvs.get(RUN_EXTENDED_TESTS));
        String sqlQuery = isNullOrEmpty(systemEnvs.get(SQL)) ? DEFAULT_SQL : systemEnvs.get(SQL);

        arguments = new HashMap<>() {{
                put(CLASS_NAME, systemEnvs.get(CLASS_NAME));
                put(URL, systemEnvs.get(URL));
                put(RUN_EXTENDED_TESTS, Boolean.toString(bool_run_extended_tests));
                put(RUN_PERFORMANCE_EXTRACTION_TEST, Boolean.toString(bool_run_performance_extraction_test));
                put(SQL, DEFAULT_SQL);
        }};

        if (systemEnvs.containsKey(USER))
            arguments.put(USER, systemEnvs.get(USER));

        if (systemEnvs.containsKey(PWD))
            arguments.put(PWD, systemEnvs.get(PWD));

        if (systemEnvs.containsKey(SQL))
            arguments.put(SQL, sqlQuery);

        if (systemEnvs.containsKey(RUN_PERFORMANCE_EXTRACTION_TEST))
            arguments.put(RUN_PERFORMANCE_EXTRACTION_TEST,  Boolean.valueOf(systemEnvs.get(RUN_PERFORMANCE_EXTRACTION_TEST)).toString());

        if (systemEnvs.containsKey(RUN_EXTENDED_TESTS))
            arguments.put(RUN_EXTENDED_TESTS, Boolean.valueOf(systemEnvs.get(RUN_EXTENDED_TESTS)).toString());

        checkForNulls(arguments);
        return this.arguments;
    }

    /**
     * <p>Gets the arguments object</p>
     *
     * @return a {@link java.util.Map} object
     */
    public Map<String, String> get() {
        return  this.arguments;
    }

    /**
     * <p>Gets an item from the arguments object</p>
     *
     * @param key a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public String getItem(String key) {
        return  this.arguments.get(key);
    }

    /**
     * Runs a validation against inputs to ensure all args are there
     *
     * @param arguments The input args to validate against
     */
    public void checkForNulls(Map<String, String> arguments) {
        if (arguments.keySet()
                .stream()
                .filter(a -> a.matches(String.join("|", CLASS_NAME, URL)))
                .filter(a -> isNullOrEmpty(arguments.get(a)))
                .peek(a -> System.out.println("The following mandatory argument was null or blank: " + a))
            .anyMatch(a -> true)) {
                help();
            }
    }

    /**
     * Prints out help on using
     */
    public void help() {
        System.out.println("\n\nJDBC Inquirer - tests connection via JDBC and also checks if JDBC specs are met");
        System.out.println("SYNOPSIS");
        System.out.println("     Using system properties: java -cp [directory that contains this jar and the jdbc jars] com.hsbc.engineering -DJDBC_USER=<user> -DJDBC_PASSWORD=<password> -DJDBC_URL=<url> -DJDBC_CLASS_NAME=<jdbc_class_name> -DJDBC_SQL=<optional sql to run>");
        System.out.println("     Using environment variables: ");
        System.out.println("       export JDBC_CLASS_NAME=<jdbc_class_name>");
        System.out.println("       export JDBC_USER=<jdbc_user>");
        System.out.println("       export JDBC_PASSWORD=<jdbc_password>");
        System.out.println("       export JDBC_URL=<jdbc_password>");
        System.out.println("       (optional) export JDBC_SQL=<jdbc_password>");
        System.out.println("       java -cp [directory that contains this jar and the jdbc jars] com.hsbc.engineering.JDBCInquirer");
        System.out.println("DESCRIPTION");
        System.out.println("      Tests connection to a data source via JDBC connection. By default, it makes a connection and runs  " + DEFAULT_SQL);
        System.out.println("      Example:");
        System.out.println("           java -cp \"target/*\" com.hsbc.engineering.JDBCInquirer -DJDBC_CLASS_NAME=\"org.apache.hive.jdbc.HiveDriver\" -DJDBC_USER=\"I_AM_A_USER\" -DJDBC_PASSWORD=\"I_AM_A_TOKEN\" -DJDBC_URL=\"jdbc:hive2://server:9999/schema\"");
        System.out.println("           java -cp \"target/*\" com.hsbc.engineering.JDBCInquirer -DJDBC_CLASS_NAME=\"org.apache.hive.jdbc.HiveDriver\" -DJDBC_USER=\"I_AM_A_USER\" -DJDBC_PASSWORD=\"I_AM_A_TOKEN\" -DJDBC_URL=\"jdbc:hive2://server:9999/schema\" -DJDBC_SQL=\"show databases\"");

        throw new IllegalArgumentException("Mandatory fields are not registered");
    }

    /**
     * <p>Checks if string is null or empty</p>
     *
     * @param s a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean isNullOrEmpty(String s) {
        if (s == null || s.isBlank())
            return true;

        return false;
    }
}
