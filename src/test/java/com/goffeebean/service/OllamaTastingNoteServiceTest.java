package com.goffeebean.service;

import com.goffeebean.dto.TastingNoteResponse;
import com.goffeebean.entity.Roast;
import com.goffeebean.entity.RoastLevel;
import com.goffeebean.exception.OllamaUnavailableException;
import com.goffeebean.exception.RoastNotFoundException;
import com.goffeebean.repository.RoastRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class OllamaTastingNoteServiceTest {

    @Mock
    private RoastRepository roastRepository;

    private MockRestServiceServer mockServer;
    private OllamaTastingNoteService ollamaTastingNoteService;

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        ollamaTastingNoteService = new OllamaTastingNoteService(
                roastRepository, restClientBuilder, "http://localhost:11434", "llama3.2:1b");
    }

    private Roast sampleRoast() {
        return new Roast(1L, "Ethiopia Yirgacheffe", "Ethiopia", RoastLevel.LIGHT,
                new BigDecimal("14.99"), null);
    }

    @Test
    void generateTastingNotes_returnsNotesFromOllamaWhenRoastExists() {
        when(roastRepository.findById(1L)).thenReturn(Optional.of(sampleRoast()));

        mockServer.expect(requestTo("http://localhost:11434/api/generate"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        "{\"response\": \"Bright and floral, with notes of citrus and jasmine.\"}",
                        MediaType.APPLICATION_JSON));

        TastingNoteResponse result = ollamaTastingNoteService.generateTastingNotes(1L);

        assertEquals(1L, result.roastId());
        assertEquals("Bright and floral, with notes of citrus and jasmine.", result.tastingNotes());
        mockServer.verify();
    }

    @Test
    void generateTastingNotes_throwsRoastNotFoundExceptionWhenRoastMissing() {
        when(roastRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RoastNotFoundException.class, () -> ollamaTastingNoteService.generateTastingNotes(99L));
    }

    @Test
    void generateTastingNotes_throwsOllamaUnavailableExceptionWhenOllamaErrors() {
        when(roastRepository.findById(1L)).thenReturn(Optional.of(sampleRoast()));

        mockServer.expect(requestTo("http://localhost:11434/api/generate"))
                .andRespond(withServerError());

        assertThrows(OllamaUnavailableException.class, () -> ollamaTastingNoteService.generateTastingNotes(1L));
    }
}
