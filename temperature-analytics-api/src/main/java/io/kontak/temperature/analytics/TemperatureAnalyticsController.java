package io.kontak.temperature.analytics;

import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureCountAnomalies;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics/anomalies/")
@AllArgsConstructor
public class TemperatureAnalyticsController {
    private final TemperatureAnalyticsService temperatureAnalyticsService;

    @GetMapping("/thermometer/{thermometerId}")
    public List<Anomaly> getTemperatureAnomaliesByThermometer(@PathVariable String thermometerId) {
        return temperatureAnalyticsService.findAnomaliesByThermometer(thermometerId);
    }


    @GetMapping("/room/{roomId}")
    public List<Anomaly> getTemperatureAnomaliesByRoom(@PathVariable String roomId) {
        return temperatureAnalyticsService.findAnomaliesByRoom(roomId);
    }

    @GetMapping("/thermometer")
    public List<TemperatureCountAnomalies> getTemperatureAnomaliesByThreshold(@RequestParam long threshold) {
        return temperatureAnalyticsService.findIdAndCountAnomaliesByThreshold(threshold);
    }
}
