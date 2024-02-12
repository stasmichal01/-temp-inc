package io.kontak.apps.anomaly.detector.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kontak.apps.anomaly.detector.AnomalyDetector;
import io.kontak.apps.anomaly.detector.algorithms.AlwaysAnomalyAnomalyDetector;
import io.kontak.apps.anomaly.detector.algorithms.CountWindowAnomalyDetector;
import io.kontak.apps.anomaly.detector.algorithms.TimeWindowAnomalyDetector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AnomalyConfig {

    @Bean
    @ConditionalOnProperty(name = "anomaly.detector.algorithm", havingValue = "AlwaysAnomalyAnomalyDetector")
    public AnomalyDetector alwaysAnomalyAnomalyDetector(ObjectMapper mapper) {
        return new AlwaysAnomalyAnomalyDetector(mapper);
    }

    @Bean
    @ConditionalOnProperty(name = "anomaly.detector.algorithm", havingValue = "CountWindowAnomalyDetector")
    public AnomalyDetector countWindowAnomalyDetector(ObjectMapper mapper,
                                                      @Value("${anomaly.detector.count.threshold:5}") Integer threshold,
                                                      @Value("${anomaly.detector.count.window:10}") Integer window) {
        return new CountWindowAnomalyDetector(mapper, window, threshold);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "anomaly.detector.algorithm", havingValue = "TimeWindowAnomalyDetector")
    public AnomalyDetector timeWindowAnomalyDetector(ObjectMapper mapper,
                                                     @Value("${anomaly.detector.time.threshold:5}") Integer threshold,
                                                     @Value("${anomaly.detector.time.window:10}") Integer window) {
        return new TimeWindowAnomalyDetector(mapper, window, threshold);
    }

}
