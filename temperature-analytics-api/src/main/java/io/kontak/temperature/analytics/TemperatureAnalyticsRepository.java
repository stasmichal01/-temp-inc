package io.kontak.temperature.analytics;

import io.kontak.apps.event.TemperatureCountAnomalies;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;


public interface TemperatureAnalyticsRepository extends Repository<AnomalyDomain, String> {

    List<AnomalyDomain> findByThermometerId(String thermometerId);

    List<AnomalyDomain> findByRoomId(String roomId);

    @Query("select new io.kontak.apps.event.TemperatureCountAnomalies(count(t), t.thermometerId) from AnomalyDomain t group by t.thermometerId")
    List<TemperatureCountAnomalies> findIdAndCountAnomaliesByThreshold();
}
