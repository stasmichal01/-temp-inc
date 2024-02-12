package io.kontak.apps.anomaly.detector.algorithms;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kontak.apps.anomaly.detector.AnomalyDetector;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Component("TimeWindowAnomalyDetector")
public class TimeWindowAnomalyDetector implements AnomalyDetector {
    private ObjectMapper mapper;
    private int windowSize;
    private int threshold;

    @Override
    public List<Anomaly> apply(List<TemperatureReading> temperatureReadings) {
        if (temperatureReadings.isEmpty()) {
            return List.of();
        } else {
            var sorted = temperatureReadings.stream()
                    .sorted(Comparator.comparing(TemperatureReading::getTimestamp))
                    .toList();
            return findAnomalyTemperatures(sorted).stream()
                    .map(temperatureReading -> mapper.convertValue(temperatureReading, Anomaly.class))
                    .toList();
        }
    }

    private List<TemperatureReading> findAnomalyTemperatures(List<TemperatureReading> temperatureReadings) {
        var result = new ArrayList<TemperatureReading>();
        var window = getWindow(temperatureReadings);
        for (int i = 0; i < temperatureReadings.size(); i++) {
            var current = temperatureReadings.get(i);
            if (i > window.getWindowEnd()) {
                window.add(current);
            }
            if (window.isAnomaly(current)) {
                result.add(current);
            }
            while (isFirstBeforeWindow(current, window)) {
                window.removeFirst();
            }
        }
        return result;
    }

    private boolean isFirstBeforeWindow(TemperatureReading current, Window window) {
        var windowStart = current.getTimestamp().minusSeconds(windowSize);
        return window.isNotEmpty() && window.getFirst().getTimestamp().isBefore(windowStart);
    }


    private Window getWindow(List<TemperatureReading> temperatureReadings) {
        var window = new Window(new LinkedList<>(), threshold);
        var windowEnd = temperatureReadings.get(0).getTimestamp().plusSeconds(windowSize);
        for (int i = 0; i < temperatureReadings.size(); i++) {
            var current = temperatureReadings.get(i);
            if (current.getTimestamp().isBefore(windowEnd)) {
                window.add(current);
            } else {
                window.setWindowEnd(i);
                break;
            }
        }
        return window;
    }
}



