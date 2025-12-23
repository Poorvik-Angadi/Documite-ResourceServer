package com.angadi.springresourceserver.controllers;

import com.angadi. springresourceserver.business.domain.Document;
import com.angadi.springresourceserver.business.requests.DocumentsRequest;
import com.angadi.springresourceserver.business. service.DocumentsService;
import com.fasterxml.jackson.databind. ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit. jupiter.api.Test;
import org.springframework.beans.factory.annotation. Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security. core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web. servlet.MockMvc;

import java.time.LocalDate;
import java.util. Arrays;
import java.util. Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers. any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito. Mockito. verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import static org.springframework. test.web.servlet.request. MockMvcRequestBuilders. post;
import static org.springframework. test.web.servlet.result. MockMvcResultMatchers.*;

@WebMvcTest(DocumentsController.class)
class DocumentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentsService documentsService;

    @Autowired
    private ObjectMapper objectMapper;

    private DocumentsRequest documentsRequest;
    private List<Document> testDocuments;
    private Document testDocument;

    @BeforeEach
    void setUp() {
        documentsRequest = new DocumentsRequest();
        documentsRequest.setDocName(Arrays.asList("PDF", "DOCX"));

        testDocument = new Document();
        testDocument.setDocName("TestDoc");
        testDocument.setDocLocation("https://example.com/doc");
        testDocument.setDocType("PDF");
        testDocument.setDocYear("2025");
        testDocument.setDocValidity(new LocalDate[]{LocalDate.now(), LocalDate.now().plusYears(1)});

        testDocuments = Arrays.asList(testDocument);
    }

    @Test
    void testGetDocuments_Success() throws Exception {
        // Arrange
        when(documentsService.getDocumentsByType(anyCollection(), any(Authentication.class)))
                .thenReturn(testDocuments);

        // Act & Assert
        mockMvc.perform(post("/getdocuments")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].docName").value("TestDoc"))
                .andExpect(jsonPath("$[0].docType").value("PDF"))
                .andExpect(jsonPath("$[0].docLocation").value("https://example.com/doc"));

    }

    @Test
    void testGetDocuments_EmptyResult() throws Exception {
        // Arrange
        when(documentsService.getDocumentsByType(anyCollection(), any(Authentication.class)))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(post("/getdocuments")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetDocuments_WithAuthentication() throws Exception {
        // Arrange
        when(documentsService.getDocumentsByType(anyCollection(), any(Authentication.class)))
                .thenReturn(testDocuments);

        // Act & Assert
        mockMvc.perform(post("/getdocuments")
                        . with(jwt().jwt(jwt -> jwt.claim("email", "test@example.com")
                                .claim("given_name", "testUser")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper. writeValueAsString(documentsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

    }

    @Test
    void testGetDocuments_MultipleDocuments() throws Exception {
        // Arrange
        Document doc2 = new Document();
        doc2.setDocName("TestDoc2");
        doc2.setDocType("DOCX");
        List<Document> multipleDocuments = Arrays.asList(testDocument, doc2);

        when(documentsService.getDocumentsByType(anyCollection(), any(Authentication.class)))
                .thenReturn(multipleDocuments);

        // Act & Assert
        mockMvc.perform(post("/getdocuments")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(documentsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].docName").value("TestDoc"))
                .andExpect(jsonPath("$[1].docName").value("TestDoc2"));
    }
}