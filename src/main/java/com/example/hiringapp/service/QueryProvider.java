package com.example.hiringapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class QueryProvider {
    private static final Logger log = LoggerFactory.getLogger(QueryProvider.class);

    public String getFinalQueryByRegNo(String regNo) {
        boolean isOdd = isLastTwoDigitsOdd(regNo);
        String path = isOdd ? "queries/question1.sql" : "queries/question2.sql";
        try {
            ClassPathResource resource = new ClassPathResource(path);
            byte[] bytes = resource.getContentAsByteArray();
            String sql = new String(bytes, StandardCharsets.UTF_8).trim();
            if (sql.isEmpty()) {
                log.warn("The SQL file '{}' is empty. Please fill it with your final query.", path);
            }
            return sql;
        } catch (IOException e) {
            throw new RuntimeException("Unable to load SQL file from classpath: " + path, e);
        }
    }

    private boolean isLastTwoDigitsOdd(String regNo) {
        String digits = regNo.replaceAll("[^0-9]", "");
        if (digits.length() < 2) {
            // If less than two digits, use last digit
            int last = Character.getNumericValue(digits.charAt(digits.length() - 1));
            return last % 2 == 1;
        }
        int lastTwo = Integer.parseInt(digits.substring(digits.length() - 2));
        return lastTwo % 2 == 1;
    }
}
