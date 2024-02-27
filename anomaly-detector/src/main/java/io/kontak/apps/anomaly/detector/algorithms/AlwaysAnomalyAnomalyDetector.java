package io.kontak.apps.anomaly.detector.algorithms;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kontak.apps.anomaly.detector.AnomalyDetector;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Component("AlwaysAnomalyAnomalyDetector")
public class AlwaysAnomalyAnomalyDetector implements AnomalyDetector {
    private ObjectMapper mapper;

    @Override
    public List<Anomaly> apply(List<TemperatureReading> temperatureReadings) {
        if (temperatureReadings.isEmpty()) {
            return List.of();
        } else {
            return List.of(mapper.convertValue(temperatureReadings.get(0), Anomaly.class));
        }
    }
}
