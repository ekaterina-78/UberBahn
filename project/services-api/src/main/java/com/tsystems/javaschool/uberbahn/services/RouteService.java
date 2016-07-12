package com.tsystems.javaschool.uberbahn.services;



import com.tsystems.javaschool.uberbahn.transports.RouteInfo;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface RouteService {

    RouteInfo getById(int id);

    RouteInfo create (String title, LocalTime timeOfDeparture, List<Integer> stationIds, List<Integer> minutesSinceDepartures, BigDecimal pricePerMinute);

    boolean existsRoute (String title);

    Collection<RouteInfo> getAll ();

}
