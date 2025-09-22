package com.example.hiringapp.runner;

import com.example.hiringapp.model.GenerateWebhookResponse;
import com.example.hiringapp.service.HiringClient;
import com.example.hiringapp.service.QueryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);

    private final HiringClient hiringClient;
    private final QueryProvider queryProvider;

    public StartupRunner(HiringClient hiringClient, QueryProvider queryProvider) {
        this.hiringClient = hiringClient;
        this.queryProvider = queryProvider;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting hiring flow...");
        GenerateWebhookResponse resp = hiringClient.generateWebhook();
        String webhookUrl = resp.getWebhook();
        String jwt = resp.getAccessToken();

        String regNo = hiringClient.getCandidateRegNo();
        String finalQuery = queryProvider.getFinalQueryByRegNo(regNo);

        if (finalQuery == null || finalQuery.isBlank()) {
            log.error("Final SQL query is empty. Please fill the appropriate file in resources/queries.");
            return;
        }

        log.info("Submitting final SQL query to webhook: {}", webhookUrl);
        hiringClient.submitFinalQuery(webhookUrl, jwt, finalQuery);
        log.info("Flow complete.");
    }
}
