package com. angadi.springresourceserver. util;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter. api.Assertions.*;

class DateUtilTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    void testCreateDateFromDateString_ValidDate() {
        // Arrange
        String dateString = "2025-12-16";

        // Act
        Date result = DateUtil.createDateFromDateString(dateString);

        // Assert
        assertNotNull(result);
        assertEquals(dateString, DATE_FORMAT.format(result));
    }

    @Test
    void testCreateDateFromDateString_NullString() {
        // Act
        Date result = DateUtil.createDateFromDateString(null);

        // Assert
        assertNotNull(result);
        // Should return current date
        assertTrue(result.getTime() <= System.currentTimeMillis());
    }

    @Test
    void testCreateDateFromDateString_InvalidFormat() {
        // Arrange
        String invalidDateString = "invalid-date";

        // Act
        Date result = DateUtil.createDateFromDateString(invalidDateString);

        // Assert
        assertNotNull(result);
        // Should return current date when parsing fails
        assertTrue(result. getTime() <= System.currentTimeMillis());
    }

    @Test
    void testCreateDateFromDateString_DifferentYear() {
        // Arrange
        String dateString = "2024-01-01";

        // Act
        Date result = DateUtil.createDateFromDateString(dateString);

        // Assert
        assertNotNull(result);
        assertEquals(dateString, DATE_FORMAT.format(result));
    }

    @Test
    void testCreateDateFromDateString_LeapYear() {
        // Arrange
        String dateString = "2024-02-29";

        // Act
        Date result = DateUtil.createDateFromDateString(dateString);

        // Assert
        assertNotNull(result);
        assertEquals(dateString, DATE_FORMAT.format(result));
    }
}