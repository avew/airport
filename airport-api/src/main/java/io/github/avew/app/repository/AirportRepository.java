package io.github.avew.app.repository;

import io.github.avew.app.domain.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends
        JpaRepository<Airport, String>,
        JpaSpecificationExecutor<Airport>{



}
