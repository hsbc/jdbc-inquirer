package com.hsbc.engineering;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ArgumentTest {
    static Arguments arguments;
    @BeforeAll
    public static void setup() {
        arguments = new Arguments(Map.of(
            Arguments.CLASS_NAME,   Arguments.CLASS_NAME,
            Arguments.USER,         Arguments.USER,
            Arguments.PWD,          Arguments.PWD,
            Arguments.URL,          Arguments.URL,
            Arguments.SQL,          Arguments.SQL,
            Arguments.RUN_EXTENDED_TESTS, "true"
        ));
    }

    @DisplayName("Checks if null functions checks for empty, null and blank Strings")
    @Test
    void nullCheck() {
        Assertions.assertTrue(arguments.isNullOrEmpty(""));
        Assertions.assertTrue(arguments.isNullOrEmpty(null));
        Assertions.assertTrue(arguments.isNullOrEmpty(new String()));
        Assertions.assertTrue(arguments.isNullOrEmpty(" "));
        Assertions.assertFalse(arguments.isNullOrEmpty("A"));
    }

    @DisplayName("Check that the mandatory null checks are done")
    @Test
    void checkForMissingMandatoryFields() {
        Assertions.assertThrows(IllegalArgumentException.class, Arguments::new);
        Map<String, String> args = new HashMap<>();

        //This should be missing CLASS_NAME and URL
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Arguments(args));

        args.put(Arguments.CLASS_NAME, Arguments.CLASS_NAME);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Arguments(args));

        args.put(Arguments.URL, Arguments.URL);
        Assertions.assertDoesNotThrow(() -> { new Arguments(args); });

        args.put(Arguments.USER, Arguments.USER);
        Assertions.assertDoesNotThrow(() -> { new Arguments(args); });

        args.put(Arguments.PWD, Arguments.PWD);
        Assertions.assertDoesNotThrow(() -> { new Arguments(args); });


    }
}
