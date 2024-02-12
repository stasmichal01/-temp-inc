package io.kontak.temperature.analytics;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.kontak.apps.event.TemperatureCountAnomalies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TemperatureAnalyticsServiceTest {


    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
    private static final String THERMOMETER_ID = "temp-1";

    private TemperatureAnalyticsRepository temperatureAnalyticsRepository;
    private TemperatureAnalyticsService temperatureAnalyticsService;

    @BeforeEach
    void setUp() {
        temperatureAnalyticsRepository = mock(TemperatureAnalyticsRepository.class);
        temperatureAnalyticsService = new TemperatureAnalyticsService(mapper, temperatureAnalyticsRepository);
    }

    @Test
    void shouldReturnAnomaliesPerThermometer() {
        var roomId = "room-2";
        List<AnomalyDomain> mocked = List.of(new AnomalyDomain(1L, 20, roomId, THERMOMETER_ID, Instant.now().minusSeconds(5)));
        when(temperatureAnalyticsRepository.findByThermometerId(THERMOMETER_ID)).thenReturn(mocked);

        var response = temperatureAnalyticsService.findAnomaliesByThermometer(THERMOMETER_ID);

        assertEquals(1, response.size());
        assertEquals(THERMOMETER_ID, response.get(0).getThermometerId());
        verify(temperatureAnalyticsRepository, times(1)).findByThermometerId(THERMOMETER_ID);
    }

    @Test
    void shouldReturnAnomaliesInRoom() {
        var roomId = "room-2";
        List<AnomalyDomain> mocked = List.of(new AnomalyDomain(1L, 20, roomId, THERMOMETER_ID, Instant.now().minusSeconds(5)));
        when(temperatureAnalyticsRepository.findByRoomId(roomId)).thenReturn(mocked);

        var response = temperatureAnalyticsService.findAnomaliesByRoom(roomId);

        assertEquals(1, response.size());
        assertEquals(THERMOMETER_ID, response.get(0).getThermometerId());
        verify(temperatureAnalyticsRepository, times(1)).findByRoomId(roomId);
    }

    @Test
    void thermometersWithNumberOfAnomalies() {
        var thermometerId = "temp-1";
        var threshold = 5;
        TemperatureCountAnomalies expected = new TemperatureCountAnomalies(20, thermometerId);
        TemperatureCountAnomalies notExpected = new TemperatureCountAnomalies(2, thermometerId);
        when(temperatureAnalyticsRepository.findIdAndCountAnomaliesByThreshold()).thenReturn(List.of(expected, notExpected));

        var response = temperatureAnalyticsService.findIdAndCountAnomaliesByThreshold(threshold);

        assertEquals(expected, response.get(0));
        verify(temperatureAnalyticsRepository, times(1)).findIdAndCountAnomaliesByThreshold();
    }
}