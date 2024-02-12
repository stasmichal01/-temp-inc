package io.kontak.apps.temperature.generator;

import io.kontak.apps.event.TemperatureReading;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
public class SimpleTemperatureGenerator implements TemperatureGenerator {
    private final int rooms;
    private final int thermometers;

    public SimpleTemperatureGenerator(@Value("${temperature-generator.rooms:3}")
                                      int rooms,
                                      @Value("${temperature-generator.room.thermometers:3}")
                                      int thermometers) {
        this.rooms = rooms;
        this.thermometers = thermometers;

    }

    private final Random random = new Random();

    @Override
    public List<TemperatureReading> generate() {
        return IntStream.range(0, rooms)
                .mapToObj(this::generate)
                .flatMap(Collection::stream)
                .toList();
    }

    private List<TemperatureReading> generate(int roomId) {
        return IntStream.range(0, thermometers)
                .mapToObj(i -> generateSingleReading(roomId, i))
                .toList();
    }

    private TemperatureReading generateSingleReading(int roomId, int thermometerId) {
        return new TemperatureReading(
                thermometerId == 0 && roomId == 0 ? 50d : random.nextDouble(18d, 25d), // first thermometer is broken
                "room-" + roomId,
                "thermometer-" + thermometerId,
                Instant.now()
        );
    }
}
