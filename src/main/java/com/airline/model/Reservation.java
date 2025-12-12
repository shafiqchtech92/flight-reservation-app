package com.airline.model;

import java.util.Objects;

/**
 * Represents a customer's flight reservation.
 */
public class Reservation {
    private String customerName;
    private Flight flight;
    private int seatsBooked;

    public Reservation(String customerName, Flight flight, int seatsBooked) {
        this.customerName = customerName;
        this.flight = flight;
        this.seatsBooked = seatsBooked;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(customerName, that.customerName) &&
               Objects.equals(flight, that.flight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerName, flight);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "customerName='" + customerName + '\'' +
                ", flight=" + flight.getFlightNumber() +
                ", destination='" + flight.getDestination() + '\'' +
                ", departureTime=" + flight.getDepartureTime() +
                ", seatsBooked=" + seatsBooked +
                '}';
    }
}



