package io.kontak.apps.anomaly.detector.algorithms;


import io.kontak.apps.event.TemperatureReading;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Queue;

@RequiredArgsConstructor
class Window {
    private final Queue<TemperatureReading> records;
    private final int threshold;
    @Getter @Setter private int windowEnd;
    @Getter @Setter private double sum;

    void add(TemperatureReading temperatureReading) {
        records.add(temperatureReading);
        setSum(getSum() + temperatureReading.getTemperature());
    }
    TemperatureReading getFirst() {
        return records.peek();
    }

    void removeFirst() {
        if (!records.isEmpty()) {
            var removed = records.poll();
            setSum(getSum() - removed.getTemperature());
        }
    }

    boolean isNotEmpty() {
        return !records.isEmpty();
    }

    boolean isAnomaly(TemperatureReading temperatureReading) {
        return temperatureReading.getTemperature() > (getAverage() + threshold);
    }

    double getAverage() {
        return sum / records.size();
    }
}

