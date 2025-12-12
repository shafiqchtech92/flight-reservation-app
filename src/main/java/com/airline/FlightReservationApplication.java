package com.airline;

import com.airline.model.Flight;
import com.airline.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

/**
 * Spring Boot application entry point for the Flight Reservation System.
 */
@SpringBootApplication
public class FlightReservationApplication {

    @Autowired
    private FlightService flightService;

    public static void main(String[] args) {
        SpringApplication.run(FlightReservationApplication.class, args);
    }

    @Bean
    public CommandLineRunner initializeSampleFlights() {
        return args -> {
            LocalDateTime now = LocalDateTime.now();
            
            flightService.addFlight(new Flight("AA101", "New York", now.plusDays(1).withHour(10).withMinute(0), 50));
            flightService.addFlight(new Flight("AA102", "New York", now.plusDays(1).withHour(15).withMinute(30), 30));
            flightService.addFlight(new Flight("UA201", "Los Angeles", now.plusDays(2).withHour(8).withMinute(0), 40));
            flightService.addFlight(new Flight("UA202", "Los Angeles", now.plusDays(2).withHour(18).withMinute(45), 25));
            flightService.addFlight(new Flight("DL301", "Chicago", now.plusDays(3).withHour(12).withMinute(0), 60));
            flightService.addFlight(new Flight("SW401", "Miami", now.plusDays(1).withHour(14).withMinute(0), 20));
        };
    }
}
