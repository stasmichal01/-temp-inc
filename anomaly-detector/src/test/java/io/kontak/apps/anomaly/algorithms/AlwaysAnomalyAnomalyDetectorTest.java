package io.kontak.apps.anomaly.algorithms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.kontak.apps.anomaly.detector.algorithms.AlwaysAnomalyAnomalyDetector;
import io.kontak.apps.event.TemperatureReading;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlwaysAnomalyAnomalyDetectorTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void shouldNotReturnWhenNoEvents() {
        List<TemperatureReading> temperatures = List.of();
        AlwaysAnomalyAnomalyDetector anomalyDetectorTest = new AlwaysAnomalyAnomalyDetector(mapper);
        var response = anomalyDetectorTest.apply(temperatures);
        assertTrue(response.isEmpty());
    }

    @Test
    void shouldReturnAllAsAnomaly() {
        var temperatures = List.of(new TemperatureReading(20, "", "", Instant.now()),
                new TemperatureReading(20, "", "", Instant.now()));
        AlwaysAnomalyAnomalyDetector anomalyDetectorTest = new AlwaysAnomalyAnomalyDetector(mapper);
        var response = anomalyDetectorTest.apply(temperatures);
        assertFalse(response.isEmpty());
        assertEquals(20d, response.get(0).getTemperature());
    }
}