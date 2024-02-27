package io.kontak.apps.anomaly.detector.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kontak.apps.anomaly.detector.AnomalyDetector;
import io.kontak.apps.anomaly.detector.TemperatureMeasurementsListener;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class KafkaConfig {

    @Bean
    public Function<KStream<String, TemperatureReading>, KStream<String, Anomaly>> anomalyDetectorProcessor(
            AnomalyDetector anomalyDetector,
            ObjectMapper objectMapper,
            @Value("${anomaly.kafka.window:10}") Integer window) {
        return new TemperatureMeasurementsListener(anomalyDetector, objectMapper, window);
    }

}
