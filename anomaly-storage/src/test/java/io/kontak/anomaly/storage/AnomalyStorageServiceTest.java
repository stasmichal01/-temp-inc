package io.kontak.anomaly.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.kontak.apps.event.Anomaly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AnomalyStorageServiceTest {
    private static final String THERMOMETER_ID = "temp-1";
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private AnomalyRepository anomalyRepository;
    private AnomalyStorageService anomalyStorageService;

    @BeforeEach
    void setUp() {
        anomalyRepository = mock(AnomalyRepository.class);
        anomalyStorageService = new AnomalyStorageService(mapper, anomalyRepository);
    }

    @Test
    void storeAnomaly() {
        var roomId = "room-2";
        var expected = new Anomaly(20, roomId, THERMOMETER_ID, Instant.now().minusSeconds(5));

        anomalyStorageService.storeAnomaly(expected);

        verify(anomalyRepository, times(1)).save(any());
    }
}