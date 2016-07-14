package com.tsystems.javaschool.uberbahn.services;


import com.tsystems.javaschool.uberbahn.entities.Route;
import com.tsystems.javaschool.uberbahn.entities.Spot;
import com.tsystems.javaschool.uberbahn.repositories.RouteRepository;
import com.tsystems.javaschool.uberbahn.repositories.SpotRepository;
import com.tsystems.javaschool.uberbahn.repositories.StationRepository;
import com.tsystems.javaschool.uberbahn.services.errors.BusinessLogicException;
import com.tsystems.javaschool.uberbahn.services.errors.DatabaseException;
import com.tsystems.javaschool.uberbahn.transports.RouteInfo;
import com.tsystems.javaschool.uberbahn.transports.RouteSpotInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class  RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;
    private final SpotRepository spotRepository;

    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository, StationRepository stationRepository, SpotRepository  spotRepository) {
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
        this.spotRepository = spotRepository;
    }

    @Override
    public RouteInfo getById(int id) {
        Route route = routeRepository.findOne(id);
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setId(id);
        routeInfo.setTitle(route.getTitle());
        routeInfo.setTimeOfDeparture(route.getTimeOfDeparture());
        Collection<RouteSpotInfo> routeSpotInfos = route.getSpots().stream().map(spot -> {
            RouteSpotInfo spotInfo = new RouteSpotInfo();
            spotInfo.setStationTitle(spot.getStation().getTitle());
            spotInfo.setMinutesSinceDeparture(spot.getMinutesSinceDeparture());
            return spotInfo;
        }).collect(Collectors.toList());
        routeInfo.setSpots(routeSpotInfos);
        routeInfo.setPricePerMinute(route.getPricePerMinute());
        return routeInfo;
    }

    @Override
    public RouteInfo create(String title, LocalTime timeOfDeparture, List<Integer> stationIds, List<Integer> minutesSinceDepartures, BigDecimal pricePerMinute) {
        if (existsRoute(title)){
            throw new BusinessLogicException(String.format("Route %s already exists", title));
        }
        Route route = new Route();
        route.setTitle(title);
        route.setTimeOfDeparture(timeOfDeparture);
        route.setPricePerMinute(pricePerMinute);

        int routeId;
        try {
            routeId = routeRepository.save(route).getId();
        } catch (PersistenceException | NullPointerException ex) {
            throw new DatabaseException("Database writing error", ex);
        }

        Collection<RouteSpotInfo> routeSpotInfos = new ArrayList<>();

        for (int i=0; i<stationIds.size(); i++) {
            Spot spot = new Spot();
            spot.setRoute(route);
            spot.setMinutesSinceDeparture(minutesSinceDepartures.get(i));
            spot.setStation(stationRepository.findOne(stationIds.get(i)));
            try {
                spotRepository.save(spot);
            } catch (PersistenceException | NullPointerException ex) {
                throw new DatabaseException("Database writing error", ex);
            }
            RouteSpotInfo spotInfo = new RouteSpotInfo();
            spotInfo.setStationTitle(spot.getStation().getTitle());
            spotInfo.setMinutesSinceDeparture(spot.getMinutesSinceDeparture());
            routeSpotInfos.add(spotInfo);
        }
        RouteInfo routeInfo = new RouteInfo();
        routeInfo.setId(routeId);
        routeInfo.setTitle(title);
        routeInfo.setTimeOfDeparture(timeOfDeparture);
        routeInfo.setSpots(routeSpotInfos);
        routeInfo.setPricePerMinute(route.getPricePerMinute());
        return routeInfo;

    }

    @Override
    public boolean existsRoute(String title) {
        Route route = routeRepository.findByTitle(title);
        if (route != null) {
            return true;
        }
        return false;
    }

    @Override
    public Collection<RouteInfo> getAll() {

        return routeRepository.findAllByOrderByTitleAsc().stream().map(route -> {
            RouteInfo info = new RouteInfo();
            info.setId(route.getId());
            info.setTitle(route.getTitle());
            return info;
        }).collect(Collectors.toList());

    }

}
