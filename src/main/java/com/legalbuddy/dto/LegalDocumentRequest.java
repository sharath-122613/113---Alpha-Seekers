package com.legalbuddy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LegalDocumentRequest {
    private String template;
    private String businessDetails;
    private String filingType;
    private String notice;
    private boolean generatePdf;
    private String language = "en"; // Default to English
    private String documentType;    // Optional document type
    private String jurisdiction;    // Optional jurisdiction
    private String clientName;      // Optional client name
    private String clientEmail;     // Optional client email
    private String clientPhone;     // Optional client phone
    private boolean includeMetadata = true; // Include metadata in response
}
