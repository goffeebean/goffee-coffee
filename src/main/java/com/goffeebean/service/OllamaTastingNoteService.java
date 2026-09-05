package com.goffeebean.service;

import com.goffeebean.dto.TastingNoteResponse;
import com.goffeebean.entity.Roast;
import com.goffeebean.exception.OllamaUnavailableException;
import com.goffeebean.exception.RoastNotFoundException;
import com.goffeebean.repository.RoastRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class OllamaTastingNoteService {

    private final RoastRepository roastRepository;
    private final RestClient restClient;
    private final String model;

    public OllamaTastingNoteService(RoastRepository roastRepository,
                                     RestClient.Builder restClientBuilder,
                                     @Value("${ollama.base-url}") String baseUrl,
                                     @Value("${ollama.model}") String model) {
        this.roastRepository = roastRepository;
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.model = model;
    }

    public TastingNoteResponse generateTastingNotes(Long id) {
        Roast roast = roastRepository.findById(id).orElseThrow(() -> new RoastNotFoundException(id));
        String tastingNotes = callOllama(buildPrompt(roast));
        return new TastingNoteResponse(roast.getId(), tastingNotes);
    }

    private String buildPrompt(Roast roast) {
        return """
                Write two sentences of tasting notes for a coffee roast with these details:
                Name: %s
                Origin: %s
                Roast level: %s
                Respond with only the tasting notes, no preamble.
                """.formatted(roast.getName(), roast.getOrigin(), roast.getRoastLevel());
    }

    private String callOllama(String prompt) {
        try {
            OllamaGenerateResponse response = restClient.post()
                    .uri("/api/generate")
                    .body(new OllamaGenerateRequest(model, prompt, false))
                    .retrieve()
                    .body(OllamaGenerateResponse.class);
            return response.response();
        } catch (RestClientException e) {
            throw new OllamaUnavailableException("Unable to reach Ollama at configured base URL", e);
        }
    }

    private record OllamaGenerateRequest(String model, String prompt, boolean stream) {
    }

    private record OllamaGenerateResponse(String response) {
    }
}
