package com.angadi.springresourceserver.business. domain;

import org.junit. jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter. api.Assertions.*;

class DocumentTest {

    @Test
    void testDocumentCreation() {
        // Arrange & Act
        Document document = new Document();
        document.setDocName("TestDoc");
        document.setDocLocation("https://example.com/doc");
        document.setDocType("PDF");
        document.setDocYear("2025");
        LocalDate[] validity = {LocalDate. now(), LocalDate.now().plusYears(1)};
        document.setDocValidity(validity);

        // Assert
        assertEquals("TestDoc", document.getDocName());
        assertEquals("https://example.com/doc", document. getDocLocation());
        assertEquals("PDF", document.getDocType());
        assertEquals("2025", document.getDocYear());
        assertNotNull(document.getDocValidity());
        assertEquals(2, document.getDocValidity().length);
    }

    @Test
    void testDocumentNoArgsConstructor() {
        // Act
        Document document = new Document();

        // Assert
        assertNotNull(document);
        assertNull(document.getDocName());
        assertNull(document.getDocLocation());
        assertNull(document.getDocType());
        assertNull(document.getDocYear());
        assertNull(document. getDocValidity());
    }
}