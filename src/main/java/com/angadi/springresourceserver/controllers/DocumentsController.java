package com.angadi.springresourceserver.controllers;


import com.angadi.springresourceserver.business.domain.Document;
import com.angadi.springresourceserver.business.requests.DocumentsRequest;
import com.angadi.springresourceserver.business.service.DocumentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DocumentsController {

    public final DocumentsService documentsService;

    @Autowired
    DocumentsController(DocumentsService documentsService){
        this.documentsService = documentsService;
    }

    @Operation(summary = "Get Documents by Name", description = "Returns document details and links")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/getdocuments")
    @ResponseStatus(HttpStatus.OK)
    public List<Document> getDocuments(@RequestBody(required = true) DocumentsRequest request, @Parameter(hidden=true) Authentication authentication
                                       ){


        return this.documentsService.getDocumentsByType(request.getDocName(),authentication);
    }
}
