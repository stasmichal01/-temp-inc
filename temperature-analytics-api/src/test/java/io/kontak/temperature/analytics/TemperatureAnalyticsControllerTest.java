package io.kontak.temperature.analytics;

import io.kontak.apps.event.Anomaly;
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

class TemperatureAnalyticsControllerTest {
    private static final String THERMOMETER_ID = "temp-1";
    private TemperatureAnalyticsService temperatureAnalyticsService;
    private TemperatureAnalyticsController temperatureAnalyticsController;

    @BeforeEach
    void setUp() {
        temperatureAnalyticsService = mock(TemperatureAnalyticsService.class);
        temperatureAnalyticsController = new TemperatureAnalyticsController(temperatureAnalyticsService);
    }

    @Test
    void shouldReturnAnomaliesPerThermometer() {
        var roomId = "room-2";
        List<Anomaly> expected = List.of(new Anomaly(20, roomId, THERMOMETER_ID, Instant.now().minusSeconds(5)));
        when(temperatureAnalyticsController.getTemperatureAnomaliesByThermometer(THERMOMETER_ID)).thenReturn(expected);

        var response = temperatureAnalyticsService.findAnomaliesByThermometer(THERMOMETER_ID);

        assertEquals(expected, response);
        verify(temperatureAnalyticsService, times(1)).findAnomaliesByThermometer(THERMOMETER_ID);
    }

    @Test
    void shouldReturnAnomaliesByRoom() {
        var roomId = "room-2";
        List<Anomaly> expected = List.of(new Anomaly(20, roomId,  THERMOMETER_ID, Instant.now().minusSeconds(5)));
        when(temperatureAnalyticsController.getTemperatureAnomaliesByRoom(roomId)).thenReturn(expected);

        var response = temperatureAnalyticsService.findAnomaliesByRoom(roomId);

        assertEquals(expected, response);
        verify(temperatureAnalyticsService, times(1)).findAnomaliesByRoom(roomId);
    }

    @Test
    void shouldReturnAnomaliesByThreshold() {
        var thermometerId = "temp-1";
        var threshold = 5;
        TemperatureCountAnomalies expected = new TemperatureCountAnomalies(20, thermometerId);
        when(temperatureAnalyticsController.getTemperatureAnomaliesByThreshold(threshold)).thenReturn(List.of(expected));

        var response = temperatureAnalyticsService.findIdAndCountAnomaliesByThreshold(threshold);

        assertEquals(expected, response.get(0));
        verify(temperatureAnalyticsService, times(1)).findIdAndCountAnomaliesByThreshold(threshold);
    }
}