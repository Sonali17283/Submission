package com.example.hiringapp.service;

import com.example.hiringapp.model.GenerateWebhookRequest;
import com.example.hiringapp.model.GenerateWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class HiringClient {
    private static final Logger log = LoggerFactory.getLogger(HiringClient.class);

    private final RestTemplate restTemplate;

    @Value("${hiring.baseUrl:https://bfhldevapigw.healthrx.co.in}")
    private String baseUrl;

    @Value("${candidate.name}")
    private String candidateName;

    @Value("${candidate.regNo}")
    private String candidateRegNo;

    @Value("${candidate.email}")
    private String candidateEmail;

    public HiringClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GenerateWebhookResponse generateWebhook() {
        String url = baseUrl + "/hiring/generateWebhook/JAVA";
        GenerateWebhookRequest payload = new GenerateWebhookRequest(candidateName, candidateRegNo, candidateEmail);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GenerateWebhookRequest> entity = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<GenerateWebhookResponse> response = restTemplate.postForEntity(url, entity, GenerateWebhookResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Generated webhook successfully");
                return response.getBody();
            }
            throw new RestClientException("Non-200 from generateWebhook: " + response.getStatusCode());
        } catch (RestClientException ex) {
            log.error("Failed to generate webhook", ex);
            throw ex;
        }
    }

    public void submitFinalQuery(String webhookUrl, String jwtToken, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);
        String body = String.format("{\"finalQuery\":\"%s\"}", escapeJson(finalQuery));
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Final query submitted successfully. Response: {}", response.getBody());
            } else {
                log.warn("Submission returned status: {} body: {}", response.getStatusCode(), response.getBody());
            }
        } catch (RestClientException ex) {
            log.error("Failed to submit final query", ex);
            throw ex;
        }
    }

    private String escapeJson(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    public String getCandidateRegNo() {
        return candidateRegNo;
    }
}
