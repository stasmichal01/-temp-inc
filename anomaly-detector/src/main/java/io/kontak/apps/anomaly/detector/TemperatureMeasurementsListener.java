package io.kontak.apps.anomaly.detector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.WindowStore;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static org.apache.kafka.streams.kstream.TimeWindows.ofSizeWithNoGrace;

@NoArgsConstructor
@AllArgsConstructor
public class TemperatureMeasurementsListener implements Function<KStream<String, TemperatureReading>, KStream<String, Anomaly>> {
    private AnomalyDetector anomalyDetector;
    private ObjectMapper objectMapper;
    private int window;

    @Override
    public KStream<String, Anomaly> apply(KStream<String, TemperatureReading> events) {
        return events
                .groupByKey()
                .windowedBy(ofSizeWithNoGrace(Duration.ofSeconds(window)))
                .aggregate(LinkedList::new, (k, v, acc) -> {
                    acc.add(v);
                    return acc;
                }, Materialized.<String, List<TemperatureReading>, WindowStore<Bytes, byte[]>>as("temperature-event-window").withValueSerde(new Serdes.WrapperSerde<>(new TemperatureReadingsSerializer(), new TemperatureReadingsDeserializer())))
                .toStream()
                .selectKey((s, list) -> s.key())
                .flatMapValues((s, list) -> list == null || list.isEmpty() ?
                        List.of() :
                        anomalyDetector.apply(list)
                );
    }

    public class TemperatureReadingsSerializer implements Serializer<List<TemperatureReading>> {
        @Override
        public byte[] serialize(String var1, List<TemperatureReading> var2) {
            if (var2 == null) {
                return new byte[0];
            }
            try {
                return objectMapper.writeValueAsBytes(var2);
            } catch (Exception e) {
                throw new ListenerSerializationError("Error on serialize Kafka event with TemperatureReading list", e);
            }
        }
    }

    public class TemperatureReadingsDeserializer implements Deserializer<List<TemperatureReading>> {
        @Override
        public List<TemperatureReading> deserialize(String var1, byte[] var2) {
            if (var2 == null) {
                return Collections.emptyList();
            }
            try {
                return objectMapper.readValue(var2, new TypeReference<>() {
                });
            } catch (Exception e) {
                throw new ListenerSerializationError("Error on deserialize Kafka event with temperature list", e);
            }
        }
    }

    public static class ListenerSerializationError extends RuntimeException {
        public ListenerSerializationError(String message, Throwable cause) {
            super(message, cause);
        }

    }

}
