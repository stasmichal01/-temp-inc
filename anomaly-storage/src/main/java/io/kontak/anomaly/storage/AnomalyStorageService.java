package io.kontak.anomaly.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kontak.apps.event.Anomaly;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class AnomalyStorageService {
    private final ObjectMapper mapper;
    private final AnomalyRepository temperatureAnalyticsRepository;

    @KafkaListener(topics = {"temperature-anomalies"})
    public void storeAnomaly(Anomaly anomaly) {
        temperatureAnalyticsRepository.save(mapper.convertValue(anomaly, AnomalyDomain.class));
    }

}
