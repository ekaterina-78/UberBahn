package com.tsystems.javaschool.uberbahn.webmain.repositories;

import com.tsystems.javaschool.uberbahn.webmain.entities.Station;
import com.tsystems.javaschool.uberbahn.webmain.entities.Ticket;
import com.tsystems.javaschool.uberbahn.webmain.entities.Train;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;


public interface TrainRepository extends BaseRepository<Train> {

    Collection<Ticket> getTicketsByTrainId(int trainId);
    Train findByRouteIdAndDateOfDeparture(int routeId, LocalDate dateOfDeparture);
    Collection<Train> findByRouteId(int routeId);

}

