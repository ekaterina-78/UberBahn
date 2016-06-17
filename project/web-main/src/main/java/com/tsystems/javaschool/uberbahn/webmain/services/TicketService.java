package com.tsystems.javaschool.uberbahn.webmain.services;


import com.tsystems.javaschool.uberbahn.webmain.transports.TicketInfo;

import java.time.LocalDate;

public interface TicketService {

    TicketInfo getTicketInfo (int trainId, int stationOfDepartureId, int stationOfArrivalId, String firstName, String lastName, LocalDate dateOfBirth, int accountId);
}
