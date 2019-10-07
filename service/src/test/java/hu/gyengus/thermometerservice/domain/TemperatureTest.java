package hu.gyengus.thermometerservice.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemperatureTest {

    private static final double TEMPERATURE_TEST_VALUE = 1.2;

    private Temperature underTest;

    @BeforeEach
    void setUp() throws Exception {
        underTest = new Temperature(1.2);
    }

    @Test
    void testHashCodeShouldBeEqualsWhenTemperaturesIsEquals() {
        // GIVEN
        Temperature a = new Temperature(TEMPERATURE_TEST_VALUE);
        int hashA = a.hashCode();
        // WHEN
        int hashUnderTest = underTest.hashCode();
        // THEN
        assertEquals(hashA, hashUnderTest);
    }

    @Test
    void testEqualsObjectShouldFalseWhenCompareWithNull() {
        // GIVEN
        // WHEN
        boolean actual = underTest.equals(null);
        // THEN
        assertFalse(actual);
    }

    @Test
    void testEqualsObjectShouldFalseWhenCompareWithAnotherType() {
        // GIVEN
        Double anotherType = Double.valueOf(2.3);
        // WHEN
        boolean actual = underTest.equals(anotherType);
        // THEN
        assertFalse(actual);
    }

    @Test
    void testEqualsObjectShouldFalseWhenCompareWithAnotherTemperature() {
        // GIVEN
        Temperature anotherTemperature = new Temperature(6.6);
        // WHEN
        boolean actual = underTest.equals(anotherTemperature);
        // THEN
        assertFalse(actual);
    }
}
