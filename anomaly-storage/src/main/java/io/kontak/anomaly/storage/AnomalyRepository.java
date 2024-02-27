package io.kontak.anomaly.storage;

import org.springframework.data.jpa.repository.JpaRepository;

interface AnomalyRepository extends JpaRepository<AnomalyDomain, Long> {

}