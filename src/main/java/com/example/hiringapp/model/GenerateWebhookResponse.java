package com.example.hiringapp.model;

public class GenerateWebhookResponse {
    private String webhook; // URL to submit final query
    private String accessToken; // JWT token for Authorization header

    public String getWebhook() { return webhook; }
    public void setWebhook(String webhook) { this.webhook = webhook; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
