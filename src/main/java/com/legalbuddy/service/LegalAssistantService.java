package com.legalbuddy.service;

import com.openai.openai.client.OpenAI;
import com.openai.openai.exceptions.OpenAIException;
import com.openai.openai.models.chat.ChatCompletion;
import com.openai.openai.models.chat.ChatCompletionRequest;
import com.openai.openai.models.chat.ChatMessage;
import com.openai.openai.models.chat.ChatRole;
import com.legalbuddy.dto.LegalDocumentRequest;
import com.legalbuddy.utils.DocumentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.annotation.PostConstruct;

@Service
public class LegalAssistantService {
    private static final Logger logger = LoggerFactory.getLogger(LegalAssistantService.class);
    
    @Value("${openai.api.key}")
    private String openAiApiKey;
    
    private Map<String, String> languageTemplates;
    private OpenAI openAiClient;
    
    @PostConstruct
    private void init() {
        this.openAiClient = OpenAI.builder()
                .setApiKey(openAiApiKey)
                .build();
        this.languageTemplates = new HashMap<>();
        languageTemplates.put("en", "Generate a professional contract based on this template and business details:");
        languageTemplates.put("es", "Generar un contrato profesional basado en esta plantilla y detalles de negocio:");
        languageTemplates.put("fr", "Générer un contrat professionnel basé sur cette modèle et détails de l'entreprise:");
    }
    
    public String generateContract(String template, String businessDetails, String language, String documentType, String jurisdiction) {
        if (template == null || businessDetails == null) {
            throw new IllegalArgumentException("Template and business details are required");
        }
        
        String langTemplate = languageTemplates.getOrDefault(language, languageTemplates.get("en"));
        String prompt = String.format("%s\nTemplate: %s\nBusiness Details: %s\n\nFormat the contract in legal language and ensure all necessary clauses are included.\n\nAdditional Information:\n- Document Type: %s\n- Jurisdiction: %s\n- Language: %s", 
            langTemplate, template, businessDetails, documentType, jurisdiction, language);
        
        String response = getAiResponse(prompt);
        
        // Add metadata for PDF generation
        DocumentUtils.DocumentMetadata metadata = new DocumentUtils.DocumentMetadata();
        metadata.setTitle("Legal Contract - " + documentType);
        metadata.setLanguage(language);
        metadata.setSubject("Legal Contract Document");
        metadata.setKeywords("contract, legal, " + jurisdiction);
        
        return response;
    }
    
    public String analyzeLegalNotice(String notice, String language, String jurisdiction) {
        if (notice == null) {
            throw new IllegalArgumentException("Notice text is required");
        }
        
        String langTemplate = languageTemplates.getOrDefault(language, languageTemplates.get("en"));
        String prompt = String.format("%s\nAnalyze this legal notice and provide a summary of key points and recommended actions:\nNotice: %s\n\nInclude:\n1. Type of notice\n2. Key obligations\n3. Deadlines\n4. Recommended next steps\n\nAdditional Information:\n- Jurisdiction: %s\n- Language: %s", 
            langTemplate, notice, jurisdiction, language);
        
        return getAiResponse(prompt);
    }
    
    public String prepareFiling(String filingType, String businessDetails, String language, String jurisdiction) {
        if (filingType == null || businessDetails == null) {
            throw new IllegalArgumentException("Filing type and business details are required");
        }
        
        String langTemplate = languageTemplates.getOrDefault(language, languageTemplates.get("en"));
        String prompt = String.format("%s\nPrepare a %s filing document based on these business details:\nDetails: %s\n\nInclude all required information in the correct format.\n\nAdditional Information:\n- Jurisdiction: %s\n- Language: %s", 
            langTemplate, filingType, businessDetails, jurisdiction, language);
        
        return getAiResponse(prompt);
    }
    
    private String getAiResponse(String prompt) {
        try {
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-4")
                    .messages(List.of(
                        ChatMessage.builder()
                                .role(ChatRole.USER)
                                .content(prompt)
                                .build()
                    ))
                    .build();
            
            ChatCompletion response = openAiClient.chatCompletion(request);
            return response.getChoices().get(0).getMessage().getContent();
        } catch (OpenAIException e) {
            logger.error("Error processing AI request", e);
            throw new RuntimeException("Failed to process AI request: " + e.getMessage(), e);
        }
    }
}
