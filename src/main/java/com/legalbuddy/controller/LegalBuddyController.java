package com.legalbuddy.controller;

import com.legalbuddy.dto.LegalDocumentRequest;
import com.legalbuddy.service.LegalAssistantService;
import com.legalbuddy.utils.DocumentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/legal-buddy")
public class LegalBuddyController {
    
    private static final Logger logger = LoggerFactory.getLogger(LegalBuddyController.class);
    
    @Autowired
    private LegalAssistantService legalAssistantService;
    
    @PostMapping("/generate-contract")
    public ResponseEntity<?> generateContract(@RequestBody LegalDocumentRequest request) {
        try {
            if (request.getTemplate() == null || request.getBusinessDetails() == null) {
                return ResponseEntity.badRequest().body("Template and business details are required");
            }
            
            String contract = legalAssistantService.generateContract(
                request.getTemplate(), 
                request.getBusinessDetails(),
                request.getLanguage(),
                request.getDocumentType(),
                request.getJurisdiction()
            );
            
            if (request.isGeneratePdf()) {
                DocumentUtils.DocumentMetadata metadata = new DocumentUtils.DocumentMetadata();
                metadata.setTitle("Legal Contract - " + request.getDocumentType());
                metadata.setLanguage(request.getLanguage());
                metadata.setSubject("Legal Contract Document");
                metadata.setKeywords("contract, legal, " + request.getJurisdiction());
                
                byte[] pdfBytes = DocumentUtils.createPdfFromText(contract, metadata);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "contract.pdf");
                return ResponseEntity.ok().headers(headers).body(pdfBytes);
            }
            
            return ResponseEntity.ok(contract);
        } catch (Exception e) {
            logger.error("Error generating contract", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate contract: " + e.getMessage());
        }
    }
    
    @PostMapping("/analyze-notice")
    public ResponseEntity<?> analyzeLegalNotice(@RequestBody LegalDocumentRequest request) {
        try {
            if (request.getNotice() == null) {
                return ResponseEntity.badRequest().body("Notice text is required");
            }
            
            String analysis = legalAssistantService.analyzeLegalNotice(
                request.getNotice(),
                request.getLanguage(),
                request.getJurisdiction()
            );
            
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            logger.error("Error analyzing notice", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to analyze notice: " + e.getMessage());
        }
    }
    
    @PostMapping("/prepare-filing")
    public ResponseEntity<?> prepareFiling(@RequestBody LegalDocumentRequest request) {
        try {
            if (request.getFilingType() == null || request.getBusinessDetails() == null) {
                return ResponseEntity.badRequest().body("Filing type and business details are required");
            }
            
            String filing = legalAssistantService.prepareFiling(
                request.getFilingType(),
                request.getBusinessDetails(),
                request.getLanguage(),
                request.getJurisdiction()
            );
            
            if (request.isGeneratePdf()) {
                DocumentUtils.DocumentMetadata metadata = new DocumentUtils.DocumentMetadata();
                metadata.setTitle("Filing Document - " + request.getFilingType());
                metadata.setLanguage(request.getLanguage());
                metadata.setSubject("Filing Document");
                metadata.setKeywords("filing, legal, " + request.getJurisdiction());
                
                byte[] pdfBytes = DocumentUtils.createPdfFromText(filing, metadata);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "filing.pdf");
                return ResponseEntity.ok().headers(headers).body(pdfBytes);
            }
            
            return ResponseEntity.ok(filing);
        } catch (Exception e) {
            logger.error("Error preparing filing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to prepare filing: " + e.getMessage());
        }
    }
}
