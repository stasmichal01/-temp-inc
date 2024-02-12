package io.kontakt.apps.anomaly.detector;

import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TemperatureMeasurementsListenerTest extends AbstractIntegrationTest {

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-in-0.destination}")
    private String inputTopic;

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-out-0.destination}")
    private String outputTopic;

    @Test
    void shouldNotReturnAnomaliesWhenNoEvent() {
        try (TestKafkaConsumer<Anomaly> consumer = consumer()) {
            consumer.assertNoMoreRecords();
        }
    }

    @Test
    void shouldNotReturnAnomaliesWhenOneEvent() {
        try (TestKafkaConsumer<Anomaly> consumer = consumer(); TestKafkaProducer<TemperatureReading> producer = producer()) {
            var list = List.of(new TemperatureReading(19.1, "", "", Instant.ofEpochSecond(1684945005)));
            produceEvents(list, producer);

            var results = consumer.drain(0, Duration.ofSeconds(11));
            assertNotNull(results);
        }
    }
    @Test
    void shouldReturnAnomaliesWhenManyEvents() {
        try (TestKafkaConsumer<Anomaly> consumer = consumer(); TestKafkaProducer<TemperatureReading> producer = producer()) {
            var now = Instant.now();
            var list = getTemperatureReadings(now);
            produceEvents(list, producer);

            var results = consumer.drain(
                    consumerRecords -> consumerRecords.stream().anyMatch(r -> r.value().getThermometerId().equals(list.get(list.size() - 1
                    ).getThermometerId())),
                    Duration.ofSeconds(11)
            );
            assertNotNull(results);
            assertEquals(2, results.size());
        }
    }

    private static void produceEvents(List<TemperatureReading> list, TestKafkaProducer<TemperatureReading> producer) {
        list.forEach(temperatureReading -> producer.produce(temperatureReading.getRoomId(), temperatureReading));
    }

    private static List<TemperatureReading> getTemperatureReadings(Instant now) {
        return List.of(
                new TemperatureReading(19.1, "", "", now),
                new TemperatureReading(18.0, "", "", now.plusSeconds(1)),
                new TemperatureReading(29, "", "", now.plusSeconds(2)),
                new TemperatureReading(18.2, "", "", now.plusSeconds(2)),
                new TemperatureReading(19.2, "", "", now.plusSeconds(3)),
                new TemperatureReading(30, "", "", now.plusSeconds(3))
        );
    }


    private TestKafkaConsumer<Anomaly> consumer() {
        return new TestKafkaConsumer<>(
                kafkaContainer.getBootstrapServers(),
                outputTopic,
                Anomaly.class
        );
    }

    private TestKafkaProducer<TemperatureReading> producer() {
        return new TestKafkaProducer<>(kafkaContainer.getBootstrapServers(), inputTopic);
    }
}
