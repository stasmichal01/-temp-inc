package io.kontak.apps.temperature.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleTemperatureGeneratorTest {

    @Test
    void shouldGenerateTestData() {
        var room = 4;
        var thermometersPerRoom = 5;
        var result = new SimpleTemperatureGenerator(room, thermometersPerRoom).generate();
        assertFalse(result.isEmpty());
        assertEquals(room * thermometersPerRoom, result.size());
    }

    @Test
    void shouldNotGenerateTestData() {
        var room = 0;
        var thermometersPerRoom = 5;
        var result = new SimpleTemperatureGenerator(room, thermometersPerRoom).generate();
        assertTrue(result.isEmpty());
    }
}