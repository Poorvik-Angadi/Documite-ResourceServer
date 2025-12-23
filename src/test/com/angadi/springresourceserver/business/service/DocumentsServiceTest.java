package com.angadi.springresourceserver.business.service;

import com.angadi.springresourceserver.business.domain.Document;
import com.angadi.springresourceserver.business.domain.UsersDomain;
import com.angadi.springresourceserver.data.entities.GDocument;
import com.angadi. springresourceserver.data.entities.Users;
import com.angadi. springresourceserver.data.repository.GDocumentRepository;
import com.angadi.springresourceserver.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter. api.Test;
import org. junit.jupiter.api.extension. ExtendWith;
import org. mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito. junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework. security.core.Authentication;
import org.springframework.security.oauth2.jwt. Jwt;

import java.time. Instant;
import java.time.LocalDate;
import java.util.*;

import static org.junit. jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito. Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentsServiceTest {

    @Mock
    private GDocumentRepository gDocumentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Jwt jwt;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DocumentsService documentsService;

    private Users testUser;
    private GDocument testGDocument;
    private List<GDocument> testGDocuments;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new Users();
        testUser.setUserId(1L);
        testUser.setUserName("testUser");
        testUser.setEmail("test@example.com");

        // Setup test document
        testGDocument = new GDocument();
        testGDocument.setDoc_id(1L);
        testGDocument.setName("TestDoc");
        testGDocument. setLocation("https://example.com/doc");
        testGDocument.setType("PDF");
        testGDocument.setYear("2025");
        testGDocument.setValidity(new LocalDate[]{LocalDate.now(), LocalDate.now().plusYears(1)});
        testGDocument.setUserid(1);

        testGDocuments = Arrays.asList(testGDocument);
    }

    @Test
    void testGetDocumentsByName_WithSpecificName() {
        // Arrange
        String documentName = "TestDoc";
        when(jwt.getClaims()).thenReturn(Map.of("name", "testUser"));
        when(userRepository.findUsersByUserName("testUser")).thenReturn(Arrays.asList(testUser));
        when(gDocumentRepository.findGDocumentByUseridAndName(1L, documentName)).thenReturn(testGDocuments);

        // Act
        List<Document> result = documentsService.getDocumentsByName(documentName, jwt);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestDoc", result. get(0).getDocName());
        assertEquals("PDF", result.get(0).getDocType());
        verify(gDocumentRepository).findGDocumentByUseridAndName(1L, documentName);
        verify(userRepository).findUsersByUserName("testUser");
    }

    @Test
    void testGetDocumentsByName_GetAllDocuments() {
        // Arrange
        when(jwt.getClaims()).thenReturn(Map.of("name", "testUser"));
        when(userRepository.findUsersByUserName("testUser")).thenReturn(Arrays.asList(testUser));
        when(gDocumentRepository.findGdocumentByUserid(1L)).thenReturn(testGDocuments);

        // Act
        List<Document> result = documentsService.getDocumentsByName("get all", jwt);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gDocumentRepository).findGdocumentByUserid(1L);
    }

    @Test
    void testGetDocumentsByName_EmptyString() {
        // Arrange
        when(jwt.getClaims()).thenReturn(Map.of("name", "testUser"));
        when(userRepository.findUsersByUserName("testUser")).thenReturn(Arrays.asList(testUser));
        when(gDocumentRepository.findGdocumentByUserid(1L)).thenReturn(testGDocuments);

        // Act
        List<Document> result = documentsService.getDocumentsByName("", jwt);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gDocumentRepository).findGdocumentByUserid(1L);
    }

    @Test
    void testGetDocumentsByType_WithSpecificType() {
        // Arrange
        Collection<String> documentTypes = Arrays.asList("PDF");
        Map<String, Object> claims = new HashMap<>();
        claims.put("given_name", "testUser");
        claims.put("email", "test@example.com");

        Jwt mockJwt = mock(Jwt.class);
        when(authentication.getPrincipal()).thenReturn(mockJwt);
        when(mockJwt.getSubject()).thenReturn("testUser");
        when(mockJwt.getClaims()).thenReturn(claims);

        when(userRepository.findUsersByEmail("test@example.com")).thenReturn(Arrays.asList(testUser));
        when(gDocumentRepository.findGDocumentByUseridAndTypeIn(1L, documentTypes)).thenReturn(testGDocuments);

        // Act
        List<Document> result = documentsService.getDocumentsByType(documentTypes, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestDoc", result.get(0).getDocName());
        verify(gDocumentRepository).findGDocumentByUseridAndTypeIn(1L, documentTypes);
    }

    @Test
    void testGetDocumentsByType_GetAll() {
        // Arrange
        Collection<String> documentTypes = Arrays.asList("Get All");
        Map<String, Object> claims = new HashMap<>();
        claims.put("given_name", "testUser");
        claims.put("email", "test@example.com");

        Jwt mockJwt = mock(Jwt.class);
        when(authentication.getPrincipal()).thenReturn(mockJwt);
        when(mockJwt.getSubject()).thenReturn("testUser");
        when(mockJwt.getClaims()).thenReturn(claims);

        when(userRepository.findUsersByEmail("test@example.com")).thenReturn(Arrays.asList(testUser));
        when(gDocumentRepository.findGdocumentByUserid(1L)).thenReturn(testGDocuments);

        // Act
        List<Document> result = documentsService.getDocumentsByType(documentTypes, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gDocumentRepository).findGdocumentByUserid(1L);
    }

    @Test
    void testGetDocumentsByType_NullType() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("given_name", "testUser");
        claims.put("email", "test@example.com");

        Jwt mockJwt = mock(Jwt.class);
        when(authentication.getPrincipal()).thenReturn(mockJwt);
        when(mockJwt.getSubject()).thenReturn("testUser");
        when(mockJwt.getClaims()).thenReturn(claims);

        when(userRepository.findUsersByEmail("test@example.com")).thenReturn(Arrays.asList(testUser));
        when(gDocumentRepository.findGdocumentByUserid(1L)).thenReturn(testGDocuments);

        // Act
        List<Document> result = documentsService.getDocumentsByType(null, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(1, result. size());
        verify(gDocumentRepository).findGdocumentByUserid(1L);
    }

    @Test
    void testDocumentsMapping() {
        // Act
        List<Document> result = documentsService.documentsMapping(testGDocuments);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        Document doc = result.get(0);
        assertEquals("TestDoc", doc.getDocName());
        assertEquals("https://example.com/doc", doc.getDocLocation());
        assertEquals("PDF", doc.getDocType());
        assertEquals("2025", doc.getDocYear());
        assertNotNull(doc.getDocValidity());
    }

    @Test
    void testDocumentsMapping_EmptyList() {
        // Act
        List<Document> result = documentsService.documentsMapping(Collections.emptyList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserDetails_UserFound() {
        // Arrange
        when(userRepository.findUsersByUserName("testUser")).thenReturn(Arrays.asList(testUser));

        // Act
        UsersDomain result = documentsService.getUserDetails("testUser");

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getUserName());
        assertEquals(1L, result.getUserID());
        verify(userRepository).findUsersByUserName("testUser");
    }

    @Test
    void testGetUserDetails_UserNotFound() {
        // Arrange
        when(userRepository.findUsersByUserName("nonexistent")).thenReturn(Collections.emptyList());

        // Act
        UsersDomain result = documentsService.getUserDetails("nonexistent");

        // Assert
        assertNull(result);
        verify(userRepository).findUsersByUserName("nonexistent");
    }

    @Test
    void testGetUserDetailsByEmail_UserFound() {
        // Arrange
        when(userRepository.findUsersByEmail("test@example.com")).thenReturn(Arrays.asList(testUser));

        // Act
        UsersDomain result = documentsService.getUserDetailsbyEmail("test@example.com");

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getUserName());
        assertEquals(1L, result.getUserID());
        verify(userRepository).findUsersByEmail("test@example.com");
    }

    @Test
    void testGetUserDetailsByEmail_UserNotFound() {
        // Arrange
        when(userRepository.findUsersByEmail("nonexistent@example.com")).thenReturn(Collections.emptyList());

        // Act
        UsersDomain result = documentsService.getUserDetailsbyEmail("nonexistent@example.com");

        // Assert
        assertNull(result);
        verify(userRepository).findUsersByEmail("nonexistent@example.com");
    }

    @Test
    void testGetDocumentsByType_UserFoundByGivenName() {
        // Arrange
        Collection<String> documentTypes = Arrays. asList("PDF");
        Map<String, Object> claims = new HashMap<>();
        claims.put("given_name", "testUser");
        claims.put("email", "test@example.com");

        Jwt mockJwt = mock(Jwt.class);
        when(authentication.getPrincipal()).thenReturn(mockJwt);
        when(mockJwt.getSubject()).thenReturn("testUser");
        when(mockJwt.getClaims()).thenReturn(claims);

        when(userRepository.findUsersByEmail("test@example.com")).thenReturn(Collections.emptyList());
        when(userRepository.findUsersByUserName("testUser")).thenReturn(Arrays.asList(testUser));
        when(gDocumentRepository. findGDocumentByUseridAndTypeIn(1L, documentTypes)).thenReturn(testGDocuments);

        // Act
        List<Document> result = documentsService.getDocumentsByType(documentTypes, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findUsersByEmail("test@example.com");
        verify(userRepository).findUsersByUserName("testUser");
    }
}