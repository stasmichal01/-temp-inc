package io.kontak.apps.anomaly.detector.algorithms;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kontak.apps.anomaly.detector.AnomalyDetector;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Component("CountWindowAnomalyDetector")
public class CountWindowAnomalyDetector implements AnomalyDetector {
    private ObjectMapper mapper;
    private int windowSize;
    private int threshold;

    @Override
    public List<Anomaly> apply(List<TemperatureReading> temperatureReadings) {
        if (temperatureReadings.isEmpty()) {
            return List.of();
        } else {
            return findAnomaly(temperatureReadings).stream()
                    .map(temperatureReading -> mapper.convertValue(temperatureReading, Anomaly.class))
                    .toList();
        }
    }

    private List<TemperatureReading> findAnomaly(List<TemperatureReading> temperatureReadings) {
        var result = new ArrayList<TemperatureReading>();
        var window = getWindow(temperatureReadings);
        for (var current : temperatureReadings) {
            window.removeFirst();
            if (window.isAnomaly(current)) {
                result.add(current);
            }
            window.add(current);
        }
        return result;
    }

    private Window getWindow(List<TemperatureReading> temperatureReadings) {
        var window = new Window(new LinkedList<>(), threshold);
        for (int i = 0; i < windowSize && i < temperatureReadings.size(); i++) {
            var current = temperatureReadings.get(i);
            window.add(current);
            window.setWindowEnd(i);
        }
        return window;
    }
}
