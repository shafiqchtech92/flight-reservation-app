package com.airline;

import com.airline.model.Flight;
import com.airline.model.Reservation;
import com.airline.service.FlightService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class providing a console-based interface for the Flight Reservation System.
 */
public class Main {
    private static FlightService flightService;
    private static Scanner scanner;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        // Check if user wants console mode (--console flag) or GUI mode (default)
        boolean consoleMode = args.length > 0 && args[0].equals("--console");
        
        if (consoleMode) {
            runConsoleMode();
        } else {
            // Launch GUI mode
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            javax.swing.SwingUtilities.invokeLater(() -> {
                MainGUI gui = new MainGUI();
                gui.setVisible(true);
            });
        }
    }
    
    private static void runConsoleMode() {
        flightService = new FlightService();
        scanner = new Scanner(System.in);
        
        // Initialize with sample flights
        initializeSampleFlights();
        
        System.out.println("=== Flight Reservation System ===");
        System.out.println("Welcome! Please select an option:\n");
        
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1":
                        searchFlights();
                        break;
                    case "2":
                        bookFlight();
                        break;
                    case "3":
                        viewReservations();
                        break;
                    case "4":
                        running = false;
                        System.out.println("Thank you for using the Flight Reservation System. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.\n");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            }
        }
        
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("1. Search for flights");
        System.out.println("2. Book a flight");
        System.out.println("3. View my reservations");
        System.out.println("4. Exit");
        System.out.print("\nEnter your choice: ");
    }

    private static void searchFlights() {
        System.out.println("\n=== Search Flights ===");
        System.out.print("Enter destination: ");
        String destination = scanner.nextLine().trim();
        
        if (destination.isEmpty()) {
            System.out.println("Destination cannot be empty.\n");
            return;
        }
        
        System.out.print("Enter departure date and time (yyyy-MM-dd HH:mm): ");
        String dateTimeString = scanner.nextLine().trim();
        
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm (e.g., 2024-12-25 14:30)\n");
            return;
        }
        
        List<Flight> flights = flightService.searchFlights(destination, dateTime);
        
        if (flights.isEmpty()) {
            System.out.println("\nNo available flights found for " + destination + " on " + 
                             dateTime.toLocalDate() + ".\n");
        } else {
            System.out.println("\nAvailable flights:");
            System.out.println("------------------------------------------------------------");
            for (int i = 0; i < flights.size(); i++) {
                Flight flight = flights.get(i);
                System.out.printf("%d. Flight: %s | Destination: %s | Departure: %s | Available Seats: %d%n",
                    i + 1,
                    flight.getFlightNumber(),
                    flight.getDestination(),
                    flight.getDepartureTime().format(DATE_TIME_FORMATTER),
                    flight.getAvailableSeats());
            }
            System.out.println("------------------------------------------------------------\n");
        }
    }

    private static void bookFlight() {
        System.out.println("\n=== Book a Flight ===");
        System.out.print("Enter your name: ");
        String customerName = scanner.nextLine().trim();
        
        if (customerName.isEmpty()) {
            System.out.println("Customer name cannot be empty.\n");
            return;
        }
        
        System.out.print("Enter flight number: ");
        String flightNumber = scanner.nextLine().trim();
        
        // Find the flight
        Flight selectedFlight = flightService.getAllFlights().stream()
                .filter(f -> f.getFlightNumber().equalsIgnoreCase(flightNumber))
                .findFirst()
                .orElse(null);
        
        if (selectedFlight == null) {
            System.out.println("Flight not found. Please search for flights first.\n");
            return;
        }
        
        System.out.print("Enter number of seats to book: ");
        String seatsInput = scanner.nextLine().trim();
        
        int seats;
        try {
            seats = Integer.parseInt(seatsInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number of seats.\n");
            return;
        }
        
        try {
            Reservation reservation = flightService.bookFlight(customerName, selectedFlight, seats);
            System.out.println("\n✓ Booking successful!");
            System.out.println("Reservation details:");
            System.out.println("  Customer: " + reservation.getCustomerName());
            System.out.println("  Flight: " + reservation.getFlight().getFlightNumber());
            System.out.println("  Destination: " + reservation.getFlight().getDestination());
            System.out.println("  Departure: " + reservation.getFlight().getDepartureTime().format(DATE_TIME_FORMATTER));
            System.out.println("  Seats: " + reservation.getSeatsBooked());
            System.out.println("  Remaining seats on flight: " + reservation.getFlight().getAvailableSeats());
            System.out.println();
        } catch (IllegalArgumentException e) {
            System.out.println("Booking failed: " + e.getMessage() + "\n");
        }
    }

    private static void viewReservations() {
        System.out.println("\n=== View Reservations ===");
        System.out.print("Enter your name: ");
        String customerName = scanner.nextLine().trim();
        
        if (customerName.isEmpty()) {
            System.out.println("Customer name cannot be empty.\n");
            return;
        }
        
        List<Reservation> reservations = flightService.getReservationsByCustomer(customerName);
        
        if (reservations.isEmpty()) {
            System.out.println("\nNo reservations found for " + customerName + ".\n");
        } else {
            System.out.println("\nYour reservations:");
            System.out.println("------------------------------------------------------------");
            for (int i = 0; i < reservations.size(); i++) {
                Reservation reservation = reservations.get(i);
                System.out.printf("%d. Flight: %s | Destination: %s | Departure: %s | Seats: %d%n",
                    i + 1,
                    reservation.getFlight().getFlightNumber(),
                    reservation.getFlight().getDestination(),
                    reservation.getFlight().getDepartureTime().format(DATE_TIME_FORMATTER),
                    reservation.getSeatsBooked());
            }
            System.out.println("------------------------------------------------------------\n");
        }
    }

    private static void initializeSampleFlights() {
        LocalDateTime now = LocalDateTime.now();
        
        flightService.addFlight(new Flight("AA101", "New York", now.plusDays(1).withHour(10).withMinute(0), 50));
        flightService.addFlight(new Flight("AA102", "New York", now.plusDays(1).withHour(15).withMinute(30), 30));
        flightService.addFlight(new Flight("UA201", "Los Angeles", now.plusDays(2).withHour(8).withMinute(0), 40));
        flightService.addFlight(new Flight("UA202", "Los Angeles", now.plusDays(2).withHour(18).withMinute(45), 25));
        flightService.addFlight(new Flight("DL301", "Chicago", now.plusDays(3).withHour(12).withMinute(0), 60));
        flightService.addFlight(new Flight("SW401", "Miami", now.plusDays(1).withHour(14).withMinute(0), 20));
    }
}



