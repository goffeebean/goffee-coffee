package com.goffeebean.service;

import com.goffeebean.dto.RoastRequest;
import com.goffeebean.dto.RoastResponse;
import com.goffeebean.entity.Roast;
import com.goffeebean.entity.RoastLevel;
import com.goffeebean.exception.RoastNotFoundException;
import com.goffeebean.mapper.RoastMapper;
import com.goffeebean.repository.RoastRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoastServiceTest {

    @Mock
    private RoastRepository roastRepository;

    private final RoastMapper roastMapper = new RoastMapper();

    private RoastService roastService;

    @BeforeEach
    void setUp() {
        roastService = new RoastService(roastRepository, roastMapper);
    }

    private Roast sampleRoast(Long id) {
        return new Roast(id, "Ethiopia Yirgacheffe", "Ethiopia", RoastLevel.LIGHT,
                new BigDecimal("14.99"), "Floral, citrus");
    }

    private RoastRequest sampleRequest() {
        return new RoastRequest("Ethiopia Yirgacheffe", "Ethiopia", RoastLevel.LIGHT,
                new BigDecimal("14.99"), "Floral, citrus");
    }

    @Test
    void findAll_returnsAllRoastsMappedToResponses() {
        Roast roast1 = sampleRoast(1L);
        Roast roast2 = sampleRoast(2L);
        when(roastRepository.findAll()).thenReturn(List.of(roast1, roast2));

        List<RoastResponse> result = roastService.findAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }

    @Test
    void findAll_returnsEmptyListWhenNoRoastsExist() {
        when(roastRepository.findAll()).thenReturn(List.of());

        List<RoastResponse> result = roastService.findAll();

        assertEquals(0, result.size());
    }

    @Test
    void findById_returnsMappedResponseWhenFound() {
        Roast roast = sampleRoast(1L);
        when(roastRepository.findById(1L)).thenReturn(Optional.of(roast));

        RoastResponse result = roastService.findById(1L);

        assertEquals(1L, result.id());
        assertEquals("Ethiopia Yirgacheffe", result.name());
    }

    @Test
    void findById_throwsRoastNotFoundExceptionWhenMissing() {
        when(roastRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RoastNotFoundException.class, () -> roastService.findById(99L));
    }

    @Test
    void create_savesEntityAndReturnsResponseWithGeneratedId() {
        RoastRequest request = sampleRequest();
        Roast saved = sampleRoast(1L);
        when(roastRepository.save(any(Roast.class))).thenReturn(saved);

        RoastResponse result = roastService.create(request);

        assertEquals(1L, result.id());
        assertEquals(request.name(), result.name());
        verify(roastRepository, times(1)).save(any(Roast.class));
    }

    @Test
    void update_updatesFieldsAndReturnsResponseWhenFound() {
        Roast existing = sampleRoast(1L);
        RoastRequest request = new RoastRequest("Colombia Supremo", "Colombia", RoastLevel.MEDIUM,
                new BigDecimal("12.50"), "Nutty, chocolate");
        when(roastRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roastRepository.save(existing)).thenReturn(existing);

        RoastResponse result = roastService.update(1L, request);

        assertEquals(1L, result.id());
        assertEquals("Colombia Supremo", result.name());
        assertEquals("Colombia", result.origin());
        assertEquals(RoastLevel.MEDIUM, result.roastLevel());
        verify(roastRepository, times(1)).save(existing);
    }

    @Test
    void update_throwsRoastNotFoundExceptionWhenMissing() {
        RoastRequest request = sampleRequest();
        when(roastRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RoastNotFoundException.class, () -> roastService.update(99L, request));
        verify(roastRepository, never()).save(any(Roast.class));
    }

    @Test
    void delete_deletesWhenRoastExists() {
        when(roastRepository.existsById(1L)).thenReturn(true);

        roastService.delete(1L);

        verify(roastRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_throwsRoastNotFoundExceptionWhenMissing() {
        when(roastRepository.existsById(99L)).thenReturn(false);

        assertThrows(RoastNotFoundException.class, () -> roastService.delete(99L));
        verify(roastRepository, never()).deleteById(any());
    }
}
