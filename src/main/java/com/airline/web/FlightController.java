package com.airline.web;

import com.airline.model.Flight;
import com.airline.model.Reservation;
import com.airline.service.FlightService;
import com.airline.web.dto.BookingRequest;
import com.airline.web.dto.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Web controller for handling flight reservation operations.
 */
@Controller
public class FlightController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private FlightService flightService;

    @GetMapping("/")
    public String index() {
        return "redirect:/search";
    }

    @GetMapping("/search")
    public String searchPage(Model model) {
        model.addAttribute("searchRequest", new SearchRequest());
        return "search";
    }

    @PostMapping("/search")
    public String performSearch(@ModelAttribute SearchRequest searchRequest, Model model) {
        String destination = searchRequest.getDestination();
        String dateTimeString = searchRequest.getDateTime();

        if (destination == null || destination.trim().isEmpty()) {
            model.addAttribute("error", "Please enter a destination.");
            return "search";
        }

        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            model.addAttribute("error", "Please enter a date and time.");
            return "search";
        }

        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "Invalid date format. Please use yyyy-MM-dd HH:mm (e.g., 2024-12-25 14:30)");
            return "search";
        }

        List<Flight> flights = flightService.searchFlights(destination, dateTime);
        model.addAttribute("flights", flights);
        model.addAttribute("searchRequest", searchRequest);

        if (flights.isEmpty()) {
            model.addAttribute("message", "No available flights found for " + destination + " on " + dateTime.toLocalDate() + ".");
        }

        return "search";
    }

    @GetMapping("/book")
    public String bookPage(Model model) {
        model.addAttribute("bookingRequest", new BookingRequest());
        model.addAttribute("allFlights", flightService.getAllFlights());
        return "book";
    }

    @PostMapping("/book")
    public String performBooking(@ModelAttribute BookingRequest bookingRequest, Model model, RedirectAttributes redirectAttributes) {
        String customerName = bookingRequest.getCustomerName();
        String flightNumber = bookingRequest.getFlightNumber();
        Integer seats = bookingRequest.getSeats();

        if (customerName == null || customerName.trim().isEmpty()) {
            model.addAttribute("error", "Please enter your name.");
            model.addAttribute("allFlights", flightService.getAllFlights());
            return "book";
        }

        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            model.addAttribute("error", "Please enter a flight number.");
            model.addAttribute("allFlights", flightService.getAllFlights());
            return "book";
        }

        if (seats == null || seats <= 0) {
            model.addAttribute("error", "Please enter a valid number of seats (greater than 0).");
            model.addAttribute("allFlights", flightService.getAllFlights());
            return "book";
        }

        // Find the flight
        Flight selectedFlight = flightService.getAllFlights().stream()
                .filter(f -> f.getFlightNumber().equalsIgnoreCase(flightNumber))
                .findFirst()
                .orElse(null);

        if (selectedFlight == null) {
            model.addAttribute("error", "Flight not found. Please search for flights first.");
            model.addAttribute("allFlights", flightService.getAllFlights());
            return "book";
        }

        try {
            Reservation reservation = flightService.bookFlight(customerName, selectedFlight, seats);
            redirectAttributes.addFlashAttribute("success", "Booking successful! Reservation details have been saved.");
            redirectAttributes.addFlashAttribute("reservation", reservation);
            return "redirect:/book";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Booking failed: " + e.getMessage());
            model.addAttribute("allFlights", flightService.getAllFlights());
            return "book";
        }
    }

    @GetMapping("/reservations")
    public String reservationsPage(Model model) {
        model.addAttribute("customerName", "");
        return "reservations";
    }

    @PostMapping("/reservations")
    public String viewReservations(String customerName, Model model) {
        if (customerName == null || customerName.trim().isEmpty()) {
            model.addAttribute("error", "Please enter a customer name.");
            return "reservations";
        }

        List<Reservation> reservations = flightService.getReservationsByCustomer(customerName);
        model.addAttribute("reservations", reservations);
        model.addAttribute("customerName", customerName);

        if (reservations.isEmpty()) {
            model.addAttribute("message", "No reservations found for " + customerName + ".");
        }

        return "reservations";
    }
}
