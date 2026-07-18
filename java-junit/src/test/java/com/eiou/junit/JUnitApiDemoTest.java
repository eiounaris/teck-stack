package com.eiou.junit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JUnitApiDemoTest {
    private int value;

    @BeforeEach
    void setUp() {
        value = 1;
    }

    @Test
    @DisplayName("assertions")
    void assertions() {
        assertAll(
                () -> assertEquals(2, value + 1),
                () -> assertTrue(value > 0)
        );
    }

    @Test
    @DisplayName("assertThrows")
    void exceptionAssertion() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> failIfBlank("")
        );

        assertEquals("value is blank", exception.getMessage());
    }

    @ParameterizedTest(name = "{0} + {1} = {2}")
    @CsvSource({
            "1, 2, 3",
            "2, 3, 5"
    })
    @DisplayName("parameterized test")
    void parameterizedTest(int left, int right, int expected) {
        assertEquals(expected, left + right);
    }

    @Nested
    @DisplayName("nested tests")
    class NestedTests {
        @Test
        @DisplayName("uses outer lifecycle state")
        void usesOuterLifecycleState() {
            assertEquals(1, value);
        }
    }

    private static void failIfBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value is blank");
        }
    }
}
