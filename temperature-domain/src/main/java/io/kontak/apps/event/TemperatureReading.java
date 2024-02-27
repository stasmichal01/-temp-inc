package io.kontak.apps.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemperatureReading {
    private double temperature;
    private String roomId;
    private String thermometerId;
    private Instant timestamp;
}
