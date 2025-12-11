package com.angadi.springresourceserver.business.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;


@Getter @Setter @NoArgsConstructor
public class DocumentsRequest {

    @NotBlank(message = "Username is required")
    private Collection<String> docName;
}
