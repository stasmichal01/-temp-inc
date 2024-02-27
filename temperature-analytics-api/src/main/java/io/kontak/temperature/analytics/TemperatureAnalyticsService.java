package io.kontak.temperature.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureCountAnomalies;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TemperatureAnalyticsService {
    private final ObjectMapper mapper;

    private final TemperatureAnalyticsRepository temperatureAnalyticsRepository;

    public List<Anomaly> findAnomaliesByThermometer(String thermometerId) {
        return temperatureAnalyticsRepository.findByThermometerId(thermometerId).stream()
                .map(temperatureReading -> mapper.convertValue(temperatureReading, Anomaly.class))
                .toList();
    }

    public List<Anomaly> findAnomaliesByRoom(String roomId) {
        return temperatureAnalyticsRepository.findByRoomId(roomId)
                .stream()
                .map(temperatureReading -> mapper.convertValue(temperatureReading, Anomaly.class))
                .toList();
    }

    public List<TemperatureCountAnomalies> findIdAndCountAnomaliesByThreshold(long threshold) {
        return temperatureAnalyticsRepository.findIdAndCountAnomaliesByThreshold().stream().filter(i -> i.getCount() > threshold).toList();
    }
}
