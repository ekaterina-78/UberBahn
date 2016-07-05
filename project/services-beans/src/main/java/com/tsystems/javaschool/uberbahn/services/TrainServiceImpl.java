package com.tsystems.javaschool.uberbahn.services;


import com.tsystems.javaschool.uberbahn.entities.*;
import com.tsystems.javaschool.uberbahn.repositories.*;
import com.tsystems.javaschool.uberbahn.services.TrainService;
import com.tsystems.javaschool.uberbahn.transports.PassengerInfo;
import com.tsystems.javaschool.uberbahn.transports.TrainInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainServiceImpl implements TrainService {

    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;
    private final SpotRepository spotRepository;
    private final TicketRepository ticketRepository;
    private final TrainRepository trainRepository;
    private final PresenceRepository presenceRepository;

    @Autowired
    public TrainServiceImpl(RouteRepository routeRepository, StationRepository stationRepository, SpotRepository spotRepository, TicketRepository ticketRepository, TrainRepository trainRepository, PresenceRepository presenceRepositary) {
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
        this.spotRepository = spotRepository;
        this.ticketRepository = ticketRepository;
        this.trainRepository = trainRepository;
        this.presenceRepository = presenceRepositary;
    }


    @Override
    public Collection<TrainInfo> getAll(int stationOfDepartureId, int stationOfArrivalId, LocalDateTime since, LocalDateTime until) {

        Station stationOfDeparture = stationRepository.findOne(stationOfDepartureId);
        Station stationOfArrival = stationRepository.findOne(stationOfArrivalId);
        Instant sinceDateTime = since.toInstant(ZoneOffset.ofHours(stationOfDeparture.getTimezone()));
        Instant untilDateTime = until.toInstant(ZoneOffset.ofHours(stationOfDeparture.getTimezone()));

        Collection<Train> trains = trainRepository.findByDepartureArrivalAndTime(
                stationOfDepartureId, stationOfDepartureId, sinceDateTime, untilDateTime);
        Collection<TrainInfo> trainInfos = new ArrayList<>();
        trains.forEach(train -> {
            TrainInfo trainInfo = new TrainInfo();
            trainInfo.setTrainId(train.getId());
            trainInfo.setRouteTitle(train.getRoute().getTitle());
            trainInfo.setStationOfDeparture(stationOfDeparture.getTitle());
            trainInfo.setStationOfArrival(stationOfArrival.getTitle());

            Collection<Presence> presences = presenceRepository.findByTrainId(train.getId());
            boolean isDeparturePassed = false;
            boolean isArrivalNotPassed = true;
            int minutesDeparture = 0;
            int minutesArrival = 0;

            int ticketsAvailable = train.getNumberOfSeats();

            for (Presence presence : presences) {
                if (presence.getSpot().getStation().getId() == stationOfDepartureId) {
                    isDeparturePassed = true;

                    OffsetDateTime datetimeDeparture = presence.getInstant().atOffset(ZoneOffset.ofHours(stationOfDeparture.getTimezone()));
                    trainInfo.setDateOfDeparture(datetimeDeparture.toLocalDate());
                    trainInfo.setTimeOfDeparture(datetimeDeparture.toLocalTime());
                    minutesDeparture = presence.getSpot().getMinutesSinceDeparture();
                }
                if (presence.getSpot().getStation().getId() == stationOfArrivalId) {
                    isArrivalNotPassed = false;
                    OffsetDateTime datetimeArrival = presence.getInstant().atOffset(ZoneOffset.ofHours(stationOfArrival.getTimezone()));

                    trainInfo.setDateOfArrival(datetimeArrival.toLocalDate());
                    trainInfo.setTimeOfArrival(datetimeArrival.toLocalTime());
                    minutesArrival = presence.getSpot().getMinutesSinceDeparture();

                }
                if (isDeparturePassed && isArrivalNotPassed) {
                    ticketsAvailable = Math.min((train.getNumberOfSeats()-presence.getNumberOfTicketsPurchased()), ticketsAvailable);
                }
            }
            trainInfo.setNumberOfSeatsAvailable(ticketsAvailable);

            trainInfo.setTicketPrice(((new BigDecimal(minutesArrival-minutesDeparture)).multiply(new BigDecimal(train.getPriceCoefficient())).multiply(train.getRoute().getPricePerMinute())).setScale(2, BigDecimal.ROUND_HALF_UP));


            if (trainInfo.getTicketPrice().compareTo(BigDecimal.ZERO) > 0){
                trainInfos.add(trainInfo);
            }
        });
        return trainInfos;
        /*return trains.stream().map(train -> {

            TrainInfo trainInfo = new TrainInfo();
            trainInfo.setTrainId(train.getId());
            trainInfo.setRouteTitle(train.getRoute().getTitle());
            trainInfo.setStationOfDeparture(stationRepository.findOne(stationOfDepartureId).getTitle());
            trainInfo.setStationOfArrival(stationRepository.findOne(stationOfArrivalId).getTitle());

            Collection<Presence> presences = train.getPresences();
            boolean isDeparturePassed = false;
            boolean isArrivalNotPassed = true;
            int minutesDeparture = 0;
            int minutesArrival = 0;

            int ticketsAvailable = train.getNumberOfSeats();

            for (Presence presence : presences) {
                if (presence.getSpot().getStation().getId() == stationOfDepartureId) {
                    isDeparturePassed = true;
                    LocalDateTime datetimeDeparture = train.getDateOfDeparture()
                            .atTime(train.getRoute().getTimeOfDeparture())
                            .plus(presence.getSpot().getMinutesSinceDeparture(), ChronoUnit.MINUTES);
                    trainInfo.setDateOfDeparture(datetimeDeparture.toLocalDate());
                    trainInfo.setTimeOfDeparture(datetimeDeparture.toLocalTime());
                    minutesDeparture = presence.getSpot().getMinutesSinceDeparture();
                }
                if (presence.getSpot().getStation().getId() == stationOfArrivalId) {
                    isArrivalNotPassed = false;
                    LocalDateTime datetimeArrival = train.getDateOfDeparture()
                            .atTime(train.getRoute().getTimeOfDeparture())
                            .plus(presence.getSpot().getMinutesSinceDeparture(), ChronoUnit.MINUTES);
                    trainInfo.setDateOfArrival(datetimeArrival.toLocalDate());
                    trainInfo.setTimeOfArrival(datetimeArrival.toLocalTime());
                    minutesArrival = presence.getSpot().getMinutesSinceDeparture();

                }
                if (isDeparturePassed && isArrivalNotPassed) {
                    ticketsAvailable = Math.min(presence.getNumberOfTicketsPurchased(), ticketsAvailable);
                }
            }
            trainInfo.setNumberOfSeatsAvailable(ticketsAvailable);

            trainInfo.setTicketPrice((new BigDecimal(minutesArrival-minutesDeparture)).multiply(new BigDecimal(train.getPriceCoefficient())).multiply(train.getRoute().getPricePerMinute()));

            return trainInfo;
        }).collect(Collectors.toList());*/
    }

    @Override
    public TrainInfo create(int routeId, LocalDate dateOfDeparture, int numberOfSeats, double priceCoefficient) {

        Train findTrain = trainRepository.findByRouteIdAndDateOfDeparture(routeId, dateOfDeparture);
        if (findTrain != null) {
            throw new RuntimeException("Train already exists");
        }

        Train train = new Train();
        Route route = routeRepository.findOne(routeId);
        train.setRoute(route);
        train.setDateOfDeparture(dateOfDeparture);
        train.setNumberOfSeats(numberOfSeats);
        train.setPriceCoefficient(priceCoefficient);
        train.setArchived(false);

        int trainId = trainRepository.save(train).getId();

        Train addedTrain = trainRepository.findOne(trainId);
        Collection<Spot> spots = spotRepository.findByRouteId(routeId);
        Instant datetimeDeparture = addedTrain.getDateOfDeparture()
                .atTime(addedTrain.getRoute().getTimeOfDeparture())
                .toInstant(ZoneOffset.ofHours(route.getSpots().get(0).getStation().getTimezone()));
        spots.forEach(spot -> {
            Presence presence = new Presence();
            presence.setTrain(addedTrain);
            presence.setSpot(spot);
            Instant instant = datetimeDeparture
                    .plus(spot.getMinutesSinceDeparture(), ChronoUnit.MINUTES);
            presence.setInstant(instant);
            presence.setNumberOfTicketsPurchased(0);
            presenceRepository.save(presence);
        });

        TrainInfo trainInfo = new TrainInfo();
        trainInfo.setTrainId(trainId);
        trainInfo.setRouteTitle(routeRepository.findOne(routeId).getTitle());
        trainInfo.setDateOfDeparture(dateOfDeparture);
        trainInfo.setNumberOfSeats(numberOfSeats);

        return trainInfo;
    }

    @Override
    public Collection<TrainInfo> getTrainInfos(int routeId) {

        Collection<TrainInfo> trainInfos = new ArrayList<>();
        Collection<Train> trains = trainRepository.findByRouteId(routeId);
        trains.forEach(train -> {
            if (!train.isArchived()) {
                TrainInfo trainInfo = new TrainInfo();
                trainInfo.setTrainId(train.getId());
                trainInfo.setRouteTitle(train.getRoute().getTitle());
                trainInfo.setDateOfDeparture(train.getDateOfDeparture());
                trainInfos.add(trainInfo);
            }
        });

        return trainInfos;
    }


    @Override
    public Collection<PassengerInfo> getPassengerInfo(int trainId) {
        return trainRepository.getTicketsByTrainId(trainId).stream().map(ticket -> {
            PassengerInfo passengerInfo = new PassengerInfo();
            passengerInfo.setFirstName(ticket.getFirstName());
            passengerInfo.setLastName(ticket.getLastName());
            passengerInfo.setDateOfBirth(ticket.getDateOfBirth());
            passengerInfo.setStationOfDeparture(ticket.getStationOfDeparture().getTitle());
            passengerInfo.setStationOfArrival(ticket.getStationOfArrival().getTitle());
            return passengerInfo;
        }).collect(Collectors.toList());
    }
}
