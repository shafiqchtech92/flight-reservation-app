package com.airline.service;

import com.airline.model.Flight;
import com.airline.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FlightService class.
 */
class FlightServiceTest {
    private FlightService flightService;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        flightService = new FlightService();
        testDateTime = LocalDateTime.of(2024, 12, 25, 14, 30);
    }

    @Test
    void testSearchFlights_WithMatchingDestinationAndDate_ReturnsFlights() {
        // Arrange
        Flight flight1 = new Flight("AA101", "New York", testDateTime, 50);
        Flight flight2 = new Flight("AA102", "New York", testDateTime.plusHours(2), 30);
        Flight flight3 = new Flight("UA201", "Los Angeles", testDateTime, 40);
        
        flightService.addFlight(flight1);
        flightService.addFlight(flight2);
        flightService.addFlight(flight3);

        // Act
        List<Flight> results = flightService.searchFlights("New York", testDateTime);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(f -> f.getFlightNumber().equals("AA101")));
        assertTrue(results.stream().anyMatch(f -> f.getFlightNumber().equals("AA102")));
    }

    @Test
    void testSearchFlights_WithNoMatchingFlights_ReturnsEmptyList() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 50);
        flightService.addFlight(flight);

        // Act
        List<Flight> results = flightService.searchFlights("Tokyo", testDateTime);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchFlights_WithNoAvailableSeats_ExcludesFlight() {
        // Arrange
        Flight flight1 = new Flight("AA101", "New York", testDateTime, 0);
        Flight flight2 = new Flight("AA102", "New York", testDateTime, 50);
        
        flightService.addFlight(flight1);
        flightService.addFlight(flight2);

        // Act
        List<Flight> results = flightService.searchFlights("New York", testDateTime);

        // Assert
        assertEquals(1, results.size());
        assertEquals("AA102", results.get(0).getFlightNumber());
    }

    @Test
    void testSearchFlights_CaseInsensitiveDestination() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 50);
        flightService.addFlight(flight);

        // Act
        List<Flight> results = flightService.searchFlights("new york", testDateTime);

        // Assert
        assertEquals(1, results.size());
    }

    @Test
    void testBookFlight_WithValidBooking_CreatesReservation() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 50);
        flightService.addFlight(flight);

        // Act
        Reservation reservation = flightService.bookFlight("John Doe", flight, 3);

        // Assert
        assertNotNull(reservation);
        assertEquals("John Doe", reservation.getCustomerName());
        assertEquals(flight, reservation.getFlight());
        assertEquals(3, reservation.getSeatsBooked());
        assertEquals(47, flight.getAvailableSeats());
    }

    @Test
    void testBookFlight_WithExactAvailableSeats_Succeeds() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 5);
        flightService.addFlight(flight);

        // Act
        Reservation reservation = flightService.bookFlight("John Doe", flight, 5);

        // Assert
        assertNotNull(reservation);
        assertEquals(0, flight.getAvailableSeats());
    }

    @Test
    void testBookFlight_WithMoreSeatsThanAvailable_ThrowsException() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 5);
        flightService.addFlight(flight);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.bookFlight("John Doe", flight, 10)
        );
        
        assertTrue(exception.getMessage().contains("Not enough seats available"));
        assertEquals(5, flight.getAvailableSeats()); // Seats should not be reduced
    }

    @Test
    void testBookFlight_WithZeroSeats_ThrowsException() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 50);
        flightService.addFlight(flight);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.bookFlight("John Doe", flight, 0)
        );
        
        assertTrue(exception.getMessage().contains("greater than zero"));
    }

    @Test
    void testBookFlight_WithNegativeSeats_ThrowsException() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 50);
        flightService.addFlight(flight);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.bookFlight("John Doe", flight, -1)
        );
        
        assertTrue(exception.getMessage().contains("greater than zero"));
    }

    @Test
    void testBookFlight_WithNullCustomerName_ThrowsException() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 50);
        flightService.addFlight(flight);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            flightService.bookFlight(null, flight, 3)
        );
    }

    @Test
    void testBookFlight_WithEmptyCustomerName_ThrowsException() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 50);
        flightService.addFlight(flight);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            flightService.bookFlight("", flight, 3)
        );
    }

    @Test
    void testBookFlight_WithNullFlight_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            flightService.bookFlight("John Doe", null, 3)
        );
    }

    @Test
    void testBookFlight_WithFlightNotInSystem_ThrowsException() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 50);
        // Note: flight is NOT added to the service

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> flightService.bookFlight("John Doe", flight, 3)
        );
        
        assertTrue(exception.getMessage().contains("Flight not found"));
    }

    @Test
    void testBookFlight_MultipleBookings_ReducesSeatsCorrectly() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 50);
        flightService.addFlight(flight);

        // Act
        flightService.bookFlight("John Doe", flight, 10);
        flightService.bookFlight("Jane Smith", flight, 5);
        Reservation reservation3 = flightService.bookFlight("Bob Johnson", flight, 15);

        // Assert
        assertEquals(20, flight.getAvailableSeats());
        assertNotNull(reservation3);
        assertEquals(3, flightService.getAllReservations().size());
    }

    @Test
    void testGetReservationsByCustomer_ReturnsCorrectReservations() {
        // Arrange
        Flight flight1 = new Flight("AA101", "New York", testDateTime, 50);
        Flight flight2 = new Flight("UA201", "Los Angeles", testDateTime.plusDays(1), 40);
        flightService.addFlight(flight1);
        flightService.addFlight(flight2);

        flightService.bookFlight("John Doe", flight1, 2);
        flightService.bookFlight("John Doe", flight2, 3);
        flightService.bookFlight("Jane Smith", flight1, 1);

        // Act
        List<Reservation> johnReservations = flightService.getReservationsByCustomer("John Doe");

        // Assert
        assertEquals(2, johnReservations.size());
    }

    @Test
    void testGetReservationsByCustomer_CaseInsensitive() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 50);
        flightService.addFlight(flight);
        flightService.bookFlight("John Doe", flight, 2);

        // Act
        List<Reservation> reservations = flightService.getReservationsByCustomer("john doe");

        // Assert
        assertEquals(1, reservations.size());
    }

    @Test
    void testGetReservationsByCustomer_WithNoReservations_ReturnsEmptyList() {
        // Act
        List<Reservation> reservations = flightService.getReservationsByCustomer("John Doe");

        // Assert
        assertTrue(reservations.isEmpty());
    }

    @Test
    void testSearchFlights_OnDateWithoutFlights_ReturnsEmptyList() {
        // Arrange
        Flight flight = new Flight("AA101", "New York", testDateTime, 50);
        flightService.addFlight(flight);

        LocalDateTime differentDate = testDateTime.plusDays(1);

        // Act
        List<Flight> results = flightService.searchFlights("New York", differentDate);

        // Assert
        assertTrue(results.isEmpty());
    }
}



