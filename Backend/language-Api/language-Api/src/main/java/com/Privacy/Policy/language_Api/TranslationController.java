package com.Privacy.Policy.language_Api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@RestController
public class TranslationController {

    @Value("${huggingface.api.token}")
    private String hfApiToken;


    private static final Map<String, String> languageModelMap = Map.ofEntries(
            Map.entry("es", "Helsinki-NLP/opus-mt-en-es"),
            Map.entry("fr", "Helsinki-NLP/opus-mt-en-fr"),
            Map.entry("de", "Helsinki-NLP/opus-mt-en-de"),
            Map.entry("hi", "Helsinki-NLP/opus-mt-en-hi"),
            Map.entry("it", "Helsinki-NLP/opus-mt-en-it"),
            Map.entry("pt", "Helsinki-NLP/opus-mt-en-pt"),
            Map.entry("zh", "Helsinki-NLP/opus-mt-en-zh"),
            Map.entry("ja", "Helsinki-NLP/opus-mt-en-ja"),
            Map.entry("ar", "Helsinki-NLP/opus-mt-en-ar")
            // Add more language mappings as needed
    );

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/translate")
    public TranslationResponse translateText(@RequestBody TranslationRequest request) throws IOException, InterruptedException {
        try {
            System.out.println("Loaded Hugging Face token: " + hfApiToken);
            String model = languageModelMap.getOrDefault(request.getTargetLang(), "Helsinki-NLP/opus-mt-en-en");
            String huggingFaceUrl = "https://api-inference.huggingface.co/models/" + model;

            String requestBody = objectMapper.writeValueAsString(Collections.singletonMap("inputs", request.getText()));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(huggingFaceUrl))
//                    .header("Authorization", "Bearer " + System.getenv("HF_API_TOKEN")) // or use @Value if preferred
                    .header("Authorization", "Bearer " + hfApiToken)

                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<TranslationResult> results = objectMapper.readValue(
                        response.body(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, TranslationResult.class)
                );
                if (!results.isEmpty()) {
                    return new TranslationResponse(results.get(0).getTranslationText());
                } else {
                    return new TranslationResponse("Translation failed: No result from API.");
                }
            } else {
                return new TranslationResponse("Translation failed with status: " + response.statusCode() + ", response: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new TranslationResponse("Translation failed due to an exception: " + e.getMessage());
        }
    }
}
