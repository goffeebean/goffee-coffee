package com.goffeebean;

import com.goffeebean.dto.ApiError;
import com.goffeebean.dto.RoastRequest;
import com.goffeebean.dto.RoastResponse;
import com.goffeebean.entity.RoastLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class RoastIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/roasts";
    }

    @Test
    void fullLifecycle_createGetUpdateDelete() {
        RoastRequest createRequest = new RoastRequest("Ethiopia Yirgacheffe", "Ethiopia", RoastLevel.LIGHT,
                new BigDecimal("14.99"), "Floral, citrus");

        ResponseEntity<RoastResponse> createResponse =
                restTemplate.postForEntity(baseUrl(), createRequest, RoastResponse.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        RoastResponse created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals("Ethiopia Yirgacheffe", created.name());
        assertNotNull(createResponse.getHeaders().getLocation());
        assertTrue(createResponse.getHeaders().getLocation().toString().endsWith("/api/v1/roasts/" + created.id()));

        Long id = created.id();

        ResponseEntity<RoastResponse> getResponse =
                restTemplate.getForEntity(baseUrl() + "/" + id, RoastResponse.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(id, getResponse.getBody().id());
        assertEquals("Ethiopia Yirgacheffe", getResponse.getBody().name());

        RoastRequest updateRequest = new RoastRequest("Colombia Supremo", "Colombia", RoastLevel.MEDIUM,
                new BigDecimal("12.50"), "Nutty, chocolate");
        restTemplate.put(baseUrl() + "/" + id, updateRequest);

        ResponseEntity<RoastResponse> getAfterUpdate =
                restTemplate.getForEntity(baseUrl() + "/" + id, RoastResponse.class);
        assertEquals(HttpStatus.OK, getAfterUpdate.getStatusCode());
        assertEquals("Colombia Supremo", getAfterUpdate.getBody().name());
        assertEquals("Colombia", getAfterUpdate.getBody().origin());
        assertEquals(RoastLevel.MEDIUM, getAfterUpdate.getBody().roastLevel());

        restTemplate.delete(baseUrl() + "/" + id);

        ResponseEntity<ApiError> getAfterDelete =
                restTemplate.getForEntity(baseUrl() + "/" + id, ApiError.class);
        assertEquals(HttpStatus.NOT_FOUND, getAfterDelete.getStatusCode());
        assertEquals("Not Found", getAfterDelete.getBody().error());
    }

    @Test
    void create_returns400WhenBodyInvalid() {
        RoastRequest invalidRequest = new RoastRequest("", "Ethiopia", null, new BigDecimal("-5"), null);

        ResponseEntity<ApiError> response =
                restTemplate.postForEntity(baseUrl(), invalidRequest, ApiError.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().error());
        assertTrue(response.getBody().details().size() > 0);
    }
}
