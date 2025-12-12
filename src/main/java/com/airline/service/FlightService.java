package com.airline.service;

import com.airline.model.Flight;
import com.airline.model.Reservation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing flights and reservations.
 */
@Service
public class FlightService {
    private List<Flight> flights;
    private List<Reservation> reservations;

    public FlightService() {
        this.flights = new ArrayList<>();
        this.reservations = new ArrayList<>();
    }

    /**
     * Searches for available flights to a given destination on a specified date.
     * 
     * @param destination the destination city
     * @param date the departure date
     * @return a list of available flights matching the criteria
     */
    public List<Flight> searchFlights(String destination, LocalDateTime date) {
        LocalDate searchDate = date.toLocalDate();
        
        return flights.stream()
                .filter(flight -> flight.getDestination().equalsIgnoreCase(destination))
                .filter(flight -> flight.getDepartureTime().toLocalDate().equals(searchDate))
                .filter(flight -> flight.getAvailableSeats() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Books a flight for a customer with the specified number of seats.
     * 
     * @param customerName the name of the customer
     * @param flight the flight to book
     * @param seats the number of seats to book
     * @return the created reservation, or null if booking failed
     * @throws IllegalArgumentException if seats requested exceed available seats
     */
    public Reservation bookFlight(String customerName, Flight flight, int seats) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null");
        }
        
        if (seats <= 0) {
            throw new IllegalArgumentException("Number of seats must be greater than zero");
        }

        // Check if the flight exists in our system
        Flight existingFlight = flights.stream()
                .filter(f -> f.equals(flight))
                .findFirst()
                .orElse(null);

        if (existingFlight == null) {
            throw new IllegalArgumentException("Flight not found in the system");
        }

        // Check if there are enough available seats
        if (existingFlight.getAvailableSeats() < seats) {
            throw new IllegalArgumentException(
                String.format("Not enough seats available. Requested: %d, Available: %d", 
                    seats, existingFlight.getAvailableSeats())
            );
        }

        // Create reservation and update available seats
        Reservation reservation = new Reservation(customerName, existingFlight, seats);
        existingFlight.reduceAvailableSeats(seats);
        reservations.add(reservation);

        return reservation;
    }

    /**
     * Gets all reservations for a specific customer.
     * 
     * @param customerName the name of the customer
     * @return a list of reservations for the customer
     */
    public List<Reservation> getReservationsByCustomer(String customerName) {
        return reservations.stream()
                .filter(reservation -> reservation.getCustomerName().equalsIgnoreCase(customerName))
                .collect(Collectors.toList());
    }

    /**
     * Adds a flight to the system.
     * 
     * @param flight the flight to add
     */
    public void addFlight(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null");
        }
        flights.add(flight);
    }

    /**
     * Gets all flights in the system.
     * 
     * @return a list of all flights
     */
    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights);
    }

    /**
     * Gets all reservations in the system.
     * 
     * @return a list of all reservations
     */
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }
}



