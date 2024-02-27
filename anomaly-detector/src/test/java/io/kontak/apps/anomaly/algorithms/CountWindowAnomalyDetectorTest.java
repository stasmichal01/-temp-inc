package io.kontak.apps.anomaly.algorithms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.kontak.apps.anomaly.detector.algorithms.CountWindowAnomalyDetector;
import io.kontak.apps.event.TemperatureReading;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CountWindowAnomalyDetectorTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private CountWindowAnomalyDetector countWindowAnomalyDetector;

    @BeforeEach
    void setUp() {
        countWindowAnomalyDetector = new CountWindowAnomalyDetector(mapper, 10, 5);
    }

    @Test
    void shouldFindAnomalyDocExample() {
        var temperatures = getTemperatureReadingList(List.of(20.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 27.1, 23.1));
        var response = countWindowAnomalyDetector.apply(temperatures);
        assertFalse(response.isEmpty());
        assertEquals(27.1, response.get(0).getTemperature());
    }

    @Test
    void shouldFindAnomalyWhenOnFirstPlace() {
        var temperatures = getTemperatureReadingList(List.of(27.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 20.1, 23.1));
        var response = countWindowAnomalyDetector.apply(temperatures);
        assertFalse(response.isEmpty());
        assertEquals(27.1, response.get(0).getTemperature());
    }

    @Test
    void shouldFindAnomalyWhenOnTheLastPlace() {
        var temperatures = getTemperatureReadingList(List.of(23.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 20.1, 27.1));
        var response = countWindowAnomalyDetector.apply(temperatures);
        assertFalse(response.isEmpty());
        assertEquals(27.1, response.get(0).getTemperature());
    }

    @Test
    void shouldFindThreeManyAnomalies() {
        var temperatures = getTemperatureReadingList(List.of(17.4, 17.4, 17.4, 27.5, 17.4, 17.4, 17.4, 30.9, 17.4, 17.4, 17.4, 25.4));
        var response = countWindowAnomalyDetector.apply(temperatures);
        assertFalse(response.isEmpty());
        assertEquals(3, response.size());
    }

    @Test
    void shouldNotFindAnomaly() {
        List<TemperatureReading> temperatures = List.of();
        assertTrue(countWindowAnomalyDetector.apply(temperatures).isEmpty());
    }

    @NotNull
    private static List<TemperatureReading> getTemperatureReadingList(List<Double> list) {
        return list.stream()
                .map((i) -> new TemperatureReading(i, "", "", Instant.now()))
                .toList();
    }
}