package com.airline;

import com.airline.model.Flight;
import com.airline.model.Reservation;
import com.airline.service.FlightService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * GUI application for the Flight Reservation System using Java Swing.
 */
public class MainGUI extends JFrame {
    private FlightService flightService;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // Search panel components
    private JTextField destinationField;
    private JTextField dateTimeField;
    private JTable flightResultsTable;
    private DefaultTableModel flightTableModel;
    
    // Booking panel components
    private JTextField bookingNameField;
    private JTextField bookingFlightNumberField;
    private JSpinner seatsSpinner;
    private JTextArea bookingResultArea;
    
    // Reservations panel components
    private JTextField reservationNameField;
    private JTable reservationTable;
    private DefaultTableModel reservationTableModel;

    public MainGUI() {
        flightService = new FlightService();
        initializeSampleFlights();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Flight Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Create main tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Create panels
        tabbedPane.addTab("Search Flights", createSearchPanel());
        tabbedPane.addTab("Book Flight", createBookingPanel());
        tabbedPane.addTab("View Reservations", createReservationsPanel());
        
        add(tabbedPane);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Search input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new TitledBorder("Search Criteria"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Destination field
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        destinationField = new JTextField(20);
        inputPanel.add(destinationField, gbc);
        
        // Date/Time field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Date/Time (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        dateTimeField = new JTextField(20);
        inputPanel.add(dateTimeField, gbc);
        
        // Search button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton searchButton = new JButton("Search Flights");
        searchButton.setPreferredSize(new Dimension(150, 30));
        searchButton.addActionListener(e -> performSearch());
        inputPanel.add(searchButton, gbc);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        // Results table
        String[] columnNames = {"Flight Number", "Destination", "Departure", "Available Seats"};
        flightTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightResultsTable = new JTable(flightTableModel);
        flightResultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightResultsTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(flightResultsTable);
        scrollPane.setBorder(new TitledBorder("Search Results"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Booking input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new TitledBorder("Booking Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Customer name
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        bookingNameField = new JTextField(20);
        inputPanel.add(bookingNameField, gbc);
        
        // Flight number
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Flight Number:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        bookingFlightNumberField = new JTextField(20);
        inputPanel.add(bookingFlightNumberField, gbc);
        
        // Number of seats
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Number of Seats:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        seatsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        inputPanel.add(seatsSpinner, gbc);
        
        // Book button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton bookButton = new JButton("Book Flight");
        bookButton.setPreferredSize(new Dimension(150, 30));
        bookButton.addActionListener(e -> performBooking());
        inputPanel.add(bookButton, gbc);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        // Result area
        bookingResultArea = new JTextArea(10, 40);
        bookingResultArea.setEditable(false);
        bookingResultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        bookingResultArea.setBorder(new TitledBorder("Booking Result"));
        JScrollPane resultScrollPane = new JScrollPane(bookingResultArea);
        panel.add(resultScrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Search input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBorder(new TitledBorder("Customer Information"));
        inputPanel.add(new JLabel("Customer Name:"));
        reservationNameField = new JTextField(20);
        inputPanel.add(reservationNameField);
        
        JButton viewButton = new JButton("View Reservations");
        viewButton.addActionListener(e -> loadReservations());
        inputPanel.add(viewButton);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        // Reservations table
        String[] columnNames = {"Flight Number", "Destination", "Departure", "Seats Booked"};
        reservationTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(reservationTableModel);
        reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        scrollPane.setBorder(new TitledBorder("Reservations"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void performSearch() {
        String destination = destinationField.getText().trim();
        String dateTimeString = dateTimeField.getText().trim();
        
        if (destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a destination.", "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (dateTimeString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a date and time.", "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, 
                    "Invalid date format. Please use yyyy-MM-dd HH:mm (e.g., 2024-12-25 14:30)", 
                    "Invalid Format", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Flight> flights = flightService.searchFlights(destination, dateTime);
        
        // Clear existing results
        flightTableModel.setRowCount(0);
        
        if (flights.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "No available flights found for " + destination + " on " + dateTime.toLocalDate() + ".", 
                    "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Flight flight : flights) {
                flightTableModel.addRow(new Object[]{
                    flight.getFlightNumber(),
                    flight.getDestination(),
                    flight.getDepartureTime().format(DATE_TIME_FORMATTER),
                    flight.getAvailableSeats()
                });
            }
        }
    }

    private void performBooking() {
        String customerName = bookingNameField.getText().trim();
        String flightNumber = bookingFlightNumberField.getText().trim();
        int seats = (Integer) seatsSpinner.getValue();
        
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name.", "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (flightNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a flight number.", "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Find the flight
        Flight selectedFlight = flightService.getAllFlights().stream()
                .filter(f -> f.getFlightNumber().equalsIgnoreCase(flightNumber))
                .findFirst()
                .orElse(null);
        
        if (selectedFlight == null) {
            bookingResultArea.setText("Error: Flight not found. Please search for flights first.");
            return;
        }
        
        try {
            Reservation reservation = flightService.bookFlight(customerName, selectedFlight, seats);
            
            StringBuilder result = new StringBuilder();
            result.append("✓ Booking Successful!\n\n");
            result.append("Reservation Details:\n");
            result.append("  Customer: ").append(reservation.getCustomerName()).append("\n");
            result.append("  Flight: ").append(reservation.getFlight().getFlightNumber()).append("\n");
            result.append("  Destination: ").append(reservation.getFlight().getDestination()).append("\n");
            result.append("  Departure: ").append(reservation.getFlight().getDepartureTime().format(DATE_TIME_FORMATTER)).append("\n");
            result.append("  Seats: ").append(reservation.getSeatsBooked()).append("\n");
            result.append("  Remaining seats on flight: ").append(reservation.getFlight().getAvailableSeats()).append("\n");
            
            bookingResultArea.setText(result.toString());
            
            // Clear fields
            bookingNameField.setText("");
            bookingFlightNumberField.setText("");
            seatsSpinner.setValue(1);
            
        } catch (IllegalArgumentException e) {
            bookingResultArea.setText("Booking Failed: " + e.getMessage());
        }
    }

    private void loadReservations() {
        String customerName = reservationNameField.getText().trim();
        
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a customer name.", "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Reservation> reservations = flightService.getReservationsByCustomer(customerName);
        
        // Clear existing results
        reservationTableModel.setRowCount(0);
        
        if (reservations.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "No reservations found for " + customerName + ".", 
                    "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Reservation reservation : reservations) {
                reservationTableModel.addRow(new Object[]{
                    reservation.getFlight().getFlightNumber(),
                    reservation.getFlight().getDestination(),
                    reservation.getFlight().getDepartureTime().format(DATE_TIME_FORMATTER),
                    reservation.getSeatsBooked()
                });
            }
        }
    }

    private void initializeSampleFlights() {
        LocalDateTime now = LocalDateTime.now();
        
        flightService.addFlight(new Flight("AA101", "New York", now.plusDays(1).withHour(10).withMinute(0), 50));
        flightService.addFlight(new Flight("AA102", "New York", now.plusDays(1).withHour(15).withMinute(30), 30));
        flightService.addFlight(new Flight("UA201", "Los Angeles", now.plusDays(2).withHour(8).withMinute(0), 40));
        flightService.addFlight(new Flight("UA202", "Los Angeles", now.plusDays(2).withHour(18).withMinute(45), 25));
        flightService.addFlight(new Flight("DL301", "Chicago", now.plusDays(3).withHour(12).withMinute(0), 60));
        flightService.addFlight(new Flight("SW401", "Miami", now.plusDays(1).withHour(14).withMinute(0), 20));
    }

    public static void main(String[] args) {
        // Set look and feel to system default for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}
