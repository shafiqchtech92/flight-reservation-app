package com.airline.web.dto;

/**
 * Data Transfer Object for flight booking requests.
 */
public class BookingRequest {
    private String customerName;
    private String flightNumber;
    private Integer seats;

    public BookingRequest() {
    }

    public BookingRequest(String customerName, String flightNumber, Integer seats) {
        this.customerName = customerName;
        this.flightNumber = flightNumber;
        this.seats = seats;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }
}
