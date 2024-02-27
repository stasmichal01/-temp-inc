package io.kontak.apps.anomaly.algorithms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.kontak.apps.anomaly.detector.algorithms.TimeWindowAnomalyDetector;
import io.kontak.apps.event.TemperatureReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeWindowAnomalyDetectorTest {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private TimeWindowAnomalyDetector timeWindowAnomalyDetector;

    @BeforeEach
    void setUp() {
        timeWindowAnomalyDetector = new TimeWindowAnomalyDetector(mapper, 10, 5);
    }

    @Test
    void shouldNotReturnAnomaly() {
        var result = timeWindowAnomalyDetector.apply(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnTwoAnomalies() {
        var list = List.of(
                new TemperatureReading(19.1, "", "", Instant.ofEpochSecond(1684945005)),
                new TemperatureReading(19.2, "", "", Instant.ofEpochSecond(1684945006)),
                new TemperatureReading(19.5, "", "", Instant.ofEpochSecond(1684945007)),
                new TemperatureReading(19.7, "", "", Instant.ofEpochSecond(1684945008)),
                new TemperatureReading(19.3, "", "", Instant.ofEpochSecond(1684945009)),
                new TemperatureReading(25.1, "", "", Instant.ofEpochSecond(1684945010)),
                new TemperatureReading(18.2, "", "", Instant.ofEpochSecond(1684945011)),
                new TemperatureReading(19.1, "", "", Instant.ofEpochSecond(1684945012)),
                new TemperatureReading(19.2, "", "", Instant.ofEpochSecond(1684945013)),
                new TemperatureReading(25.4, "", "", Instant.ofEpochSecond(1684945015))
        );

        var result = timeWindowAnomalyDetector.apply(list);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnOneAnomaly() {
        var list = List.of(
                new TemperatureReading(19.1, "", "", Instant.ofEpochSecond(1684945005)),
                new TemperatureReading(19.2, "", "", Instant.ofEpochSecond(1684945006)),
                new TemperatureReading(19.5, "", "", Instant.ofEpochSecond(1684945007)),
                new TemperatureReading(19.3, "", "", Instant.ofEpochSecond(1707495743)),
                new TemperatureReading(19.3, "", "", Instant.ofEpochSecond(1707495744)),
                new TemperatureReading(19.7, "", "", Instant.ofEpochSecond(1684945008)),
                new TemperatureReading(19.3, "", "", Instant.ofEpochSecond(1684945009)),
                new TemperatureReading(30.1, "", "", Instant.ofEpochSecond(1684945010)),
                new TemperatureReading(18.2, "", "", Instant.ofEpochSecond(1684945011)),
                new TemperatureReading(19.1, "", "", Instant.ofEpochSecond(1684945012)),
                new TemperatureReading(19.2, "", "", Instant.ofEpochSecond(1684945013)),
                new TemperatureReading(19.3, "", "", Instant.ofEpochSecond(1684945015))
        );

        var result = timeWindowAnomalyDetector.apply(list);
        assertFalse(result.isEmpty());
        assertEquals(30.1, result.get(0).getTemperature());
    }

    @Test
    void shouldReturnOneAnomalyWhenUnorderedEventsByTime() {
        var list = List.of(
                new TemperatureReading(19.1, "", "", Instant.ofEpochSecond(1684945005)),
                new TemperatureReading(19.2, "", "", Instant.ofEpochSecond(1684945006)),
                new TemperatureReading(19.5, "", "", Instant.ofEpochSecond(1684945007)),
                new TemperatureReading(19.7, "", "", Instant.ofEpochSecond(1684945008)),
                new TemperatureReading(19.3, "", "", Instant.ofEpochSecond(1707495743)),
                new TemperatureReading(19.3, "", "", Instant.ofEpochSecond(1707495744)),
                new TemperatureReading(19.3, "", "", Instant.ofEpochSecond(1684945009)),
                new TemperatureReading(30.1, "", "", Instant.ofEpochSecond(1684945010)),
                new TemperatureReading(18.2, "", "", Instant.ofEpochSecond(1684945011)),
                new TemperatureReading(19.1, "", "", Instant.ofEpochSecond(1684945012)),
                new TemperatureReading(19.2, "", "", Instant.ofEpochSecond(1684945013)),
                new TemperatureReading(19.3, "", "", Instant.ofEpochSecond(1684945015))
        );

        var result = timeWindowAnomalyDetector.apply(list);
        assertFalse(result.isEmpty());
        assertEquals(30.1, result.get(0).getTemperature());
    }
}