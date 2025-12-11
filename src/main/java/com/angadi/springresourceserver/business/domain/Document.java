package com.angadi.springresourceserver.business.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
public class Document {
    private String docName;
    private String docLocation;
    private String docType;
    private String docYear;
    private LocalDate[] docValidity;
}
