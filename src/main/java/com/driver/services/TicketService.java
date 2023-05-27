package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db


        //checking if seats are available or not
        Train train = trainRepository.findById(bookTicketEntryDto.getTrainId()).get(); //getting train object

        int availableSeats = train.getNoOfSeats() - train.getBookedTickets().size();

        if(availableSeats < bookTicketEntryDto.getNoOfSeats()){
            throw new Exception("Less tickets are available");
        }

        //checking if stations area valid or not

        String routes = train.getRoute();

        String[] str = routes.split(",");

        String frmStation = String.valueOf(bookTicketEntryDto.getFromStation());
        String toStation = String.valueOf(bookTicketEntryDto.getToStation());

        int x = -1;
        int y = -1;

        for(int i = 0; i < str.length; i++){
            if(str[i]==frmStation){
                x = i;
                break;
            }
        }

        for(int i = 0; i < str.length; i++){
            if(str[i]==toStation){
                y = i;
                break;
            }
        }

        if(x==-1 || y==-1 || y-x < 0){
            throw new Exception("Invalid stations");
        }

        //if station is valid and seats area available book the tickets

        Ticket ticket = new Ticket();
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());

        List<Passenger> passengerList = new ArrayList<>();

        for(Integer id : bookTicketEntryDto.getPassengerIds()){
            Passenger passenger = passengerRepository.findById(id).get();
            passengerList.add(passenger);
        }

        passengerList.add(passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get());
        ticket.setPassengersList(passengerList);

        int fare = bookTicketEntryDto.getNoOfSeats()*(y-x)*300;

        ticket.setTotalFare(fare);

        train.getBookedTickets().add(ticket);
        train.setNoOfSeats(train.getNoOfSeats() - train.getBookedTickets().size());

        Train savedTrain = trainRepository.save(train);

        Ticket t = savedTrain.getBookedTickets().get(savedTrain.getBookedTickets().size()-1);

       return t.getTicketId();

    }
}
