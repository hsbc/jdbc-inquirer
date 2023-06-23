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
        arguments = new Arguments(new HashMap<>() {{
            put(Arguments.CLASS_NAME, Arguments.CLASS_NAME);
            put(Arguments.USER,       Arguments.USER);
            put(Arguments.PWD, Arguments.PWD);
            put(Arguments.URL,        Arguments.URL);
            put(Arguments.SQL,        Arguments.SQL);
            put(Arguments.RUN_EXTENDED_TESTS, "true");
        }});
    }

    @DisplayName("Checks if null functions checks for empty, null and blank Strings")
    @Test
    void nullCheck() {
        Assertions.assertTrue(arguments.isNullAndEmpty(""));
        Assertions.assertTrue(arguments.isNullAndEmpty(null));
        Assertions.assertTrue(arguments.isNullAndEmpty(new String()));
        Assertions.assertTrue(arguments.isNullAndEmpty(" "));
        Assertions.assertFalse(arguments.isNullAndEmpty("A"));
    }

    @DisplayName("Check that the mandatory null checks are done")
    @Test
    void checkForMissingMandatoryFields() {
        Assertions.assertThrows(NullPointerException.class, Arguments::new);
        Map<String, String> args = new HashMap<>();

        args.put(Arguments.USER, Arguments.USER);
        Assertions.assertThrows(NullPointerException.class, () -> new Arguments(args));

        args.put(Arguments.CLASS_NAME, Arguments.CLASS_NAME);
        Assertions.assertThrows(NullPointerException.class, () -> new Arguments(args));

        args.put(Arguments.URL, Arguments.URL);
        Assertions.assertDoesNotThrow(() -> { new Arguments(args); });

        args.put(Arguments.PWD, Arguments.PWD);
        Assertions.assertDoesNotThrow(() -> { new Arguments(args); });


    }
}
