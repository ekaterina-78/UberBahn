package com.tsystems.javaschool.uberbahn.repositories;


import com.tsystems.javaschool.uberbahn.entities.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface SpotRepository extends JpaRepository<Spot, Integer> {

    @Query("SELECT s FROM Spot AS s WHERE s.station.id = :stationId AND s.route.id = :routeId")
    Spot findByStationIdAndRouteId (@Param("stationId") int stationId, @Param("routeId") int routeId);

    @Query("SELECT s FROM Spot AS s WHERE s.station.id = :stationId")
    Collection<Spot> findByStationId (@Param("stationId") int stationId);

    @Query("SELECT s FROM Spot AS s WHERE s.route.id = :routeId")
    Collection<Spot> findByRouteId (@Param("routeId") int routeId);

}

