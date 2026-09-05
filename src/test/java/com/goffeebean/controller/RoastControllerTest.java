package com.goffeebean.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goffeebean.dto.RoastRequest;
import com.goffeebean.dto.RoastResponse;
import com.goffeebean.entity.RoastLevel;
import com.goffeebean.exception.RoastNotFoundException;
import com.goffeebean.service.OllamaTastingNoteService;
import com.goffeebean.service.RoastService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoastController.class)
class RoastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoastService roastService;

    @MockitoBean
    private OllamaTastingNoteService ollamaTastingNoteService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RoastResponse sampleResponse(Long id) {
        return new RoastResponse(id, "Ethiopia Yirgacheffe", "Ethiopia", RoastLevel.LIGHT,
                new BigDecimal("14.99"), "Floral, citrus");
    }

    private RoastRequest sampleRequest() {
        return new RoastRequest("Ethiopia Yirgacheffe", "Ethiopia", RoastLevel.LIGHT,
                new BigDecimal("14.99"), "Floral, citrus");
    }

    @Test
    void getListOfRoasts_returns200AndList() throws Exception {
        when(roastService.findAll()).thenReturn(List.of(sampleResponse(1L), sampleResponse(2L)));

        mockMvc.perform(get("/api/v1/roasts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getRoastById_returns200WhenFound() throws Exception {
        when(roastService.findById(1L)).thenReturn(sampleResponse(1L));

        mockMvc.perform(get("/api/v1/roasts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ethiopia Yirgacheffe"));
    }

    @Test
    void getRoastById_returns404WhenMissing() throws Exception {
        when(roastService.findById(99L)).thenThrow(new RoastNotFoundException(99L));

        mockMvc.perform(get("/api/v1/roasts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void createRoast_returns201WithLocationHeader() throws Exception {
        RoastRequest request = sampleRequest();
        when(roastService.create(any(RoastRequest.class))).thenReturn(sampleResponse(1L));

        mockMvc.perform(post("/api/v1/roasts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/roasts/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createRoast_returns400WhenBodyInvalid() throws Exception {
        RoastRequest invalidRequest = new RoastRequest("", "Ethiopia", null, new BigDecimal("-5"), null);

        mockMvc.perform(post("/api/v1/roasts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details").isNotEmpty());
    }

    @Test
    void updateRoastById_returns200WhenFound() throws Exception {
        RoastRequest request = sampleRequest();
        when(roastService.update(eq(1L), any(RoastRequest.class))).thenReturn(sampleResponse(1L));

        mockMvc.perform(put("/api/v1/roasts/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateRoastById_returns404WhenMissing() throws Exception {
        RoastRequest request = sampleRequest();
        when(roastService.update(eq(99L), any(RoastRequest.class))).thenThrow(new RoastNotFoundException(99L));

        mockMvc.perform(put("/api/v1/roasts/99")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRoastById_returns204WhenFound() throws Exception {
        mockMvc.perform(delete("/api/v1/roasts/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteRoastById_returns404WhenMissing() throws Exception {
        org.mockito.Mockito.doThrow(new RoastNotFoundException(99L)).when(roastService).delete(99L);

        mockMvc.perform(delete("/api/v1/roasts/99"))
                .andExpect(status().isNotFound());
    }
}
