# Flight Reservation System

A Java-based flight reservation system that allows users to search for available flights, book flights, and manage their reservations. The application features a modern web-based interface built with Spring Boot and Thymeleaf. Uses in-memory data storage.

## Features

- **Flight Search**: Search for available flights by destination and date
- **Flight Booking**: Book seats on available flights with validation
- **Reservation Management**: View all reservations for a customer
- **Data Validation**: Prevents overbooking and validates input data

## Project Structure

```
flight-reservation-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── airline/
│   │   │           ├── FlightReservationApplication.java  # Spring Boot application
│   │   │           ├── Main.java              # Console application entry point
│   │   │           ├── MainGUI.java           # Swing GUI interface
│   │   │           ├── model/
│   │   │           │   ├── Flight.java        # Flight entity
│   │   │           │   └── Reservation.java   # Reservation entity
│   │   │           ├── service/
│   │   │           │   └── FlightService.java # Business logic service
│   │   │           └── web/
│   │   │               ├── FlightController.java  # Web controller
│   │   │               └── dto/
│   │   │                   ├── BookingRequest.java
│   │   │                   └── SearchRequest.java
│   │   └── resources/
│   │       └── templates/
│   │           ├── search.html        # Flight search page
│   │           ├── book.html          # Booking page
│   │           └── reservations.html  # Reservations page
│   └── test/
│       └── java/
│           └── com/
│               └── airline/
│                   └── service/
│                       └── FlightServiceTest.java # Unit tests
├── pom.xml                                    # Maven configuration
└── README.md                                  # This file
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## How to Run

### 1. Compile the Project

```bash
mvn clean compile
```

### 2. Run the Web Application

**Web Application (Default - Recommended):**
```bash
mvn spring-boot:run
```

Or using the compiled JAR:
```bash
mvn clean package
java -jar target/flight-reservation-system-1.0.0.jar
```

The web application will start on `http://localhost:8080`

**Console Mode:**
```bash
mvn exec:java -Dexec.mainClass="com.airline.Main" -Dexec.args="--console"
```

**Swing GUI Mode:**
```bash
mvn exec:java -Dexec.mainClass="com.airline.MainGUI"
```

**Note:** The application now features a modern web-based interface built with Spring Boot. Access it at `http://localhost:8080` after starting the application.

### 3. Run Tests

```bash
mvn test
```

## Usage

### Web Application

When you start the web application, open your browser and navigate to `http://localhost:8080`. You'll see a navigation menu with three main sections:

1. **Search Flights** (`/search`): Enter a destination and departure date/time to find available flights
   - Destination: `New York`
   - Date/Time: `2024-12-26 10:00` (format: yyyy-MM-dd HH:mm)

2. **Book Flight** (`/book`): Book seats on a flight by providing:
   - Customer Name: `John Doe`
   - Flight Number: `AA101`
   - Number of Seats: `2`

3. **My Reservations** (`/reservations`): View all reservations for a specific customer
   - Enter customer name: `John Doe`

### Console Mode

When you run the application in console mode, you'll see a menu with the following options:

1. **Search for flights**: Enter a destination and departure date/time to find available flights
2. **Book a flight**: Book seats on a flight by providing your name, flight number, and number of seats
3. **View my reservations**: View all reservations for a specific customer
4. **Exit**: Close the application

## Design Decisions

### 1. **Service Layer Pattern**
   - Implemented a `FlightService` class to encapsulate all business logic
   - Separates concerns between data models and business operations
   - Makes the code more testable and maintainable

### 2. **In-Memory Data Storage**
   - Used `ArrayList` for storing flights and reservations
   - Simple and efficient for the scope of this exercise
   - Data is lost when the application stops (as per requirements)

### 3. **Input Validation**
   - Comprehensive validation in `FlightService.bookFlight()`:
     - Checks for null/empty customer names
     - Validates seat count (must be positive)
     - Prevents overbooking (throws exception if insufficient seats)
     - Verifies flight exists in the system
   - Prevents booking when available seats would go below zero

### 4. **Case-Insensitive Search**
   - Destination search is case-insensitive for better user experience
   - Customer name matching is also case-insensitive

### 5. **Date-Based Search**
   - Search compares only the date portion (ignores time)
   - Allows users to find all flights on a specific day
   - Time is still displayed and stored for booking purposes

### 6. **Exception Handling**
   - Uses `IllegalArgumentException` for invalid inputs
   - Provides clear, descriptive error messages
   - Prevents state corruption (e.g., seats aren't reduced if booking fails)

### 7. **Sample Data Initialization**
   - Application pre-populates with sample flights for demonstration
   - Flights are scheduled relative to current date/time for realistic testing

## Real-Life Considerations

### 1. **Concurrency**
   - **Current**: Single-threaded console application
   - **Real-world**: Would need thread-safe collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`) or database transactions with locking mechanisms to handle concurrent bookings

### 2. **Data Persistence**
   - **Current**: In-memory storage (data lost on restart)
   - **Real-world**: Would use a database (PostgreSQL, MySQL) with proper schema design, indexes for fast searches, and backup strategies

### 3. **Transaction Management**
   - **Current**: Simple method calls
   - **Real-world**: Would implement ACID transactions to ensure atomicity (e.g., if payment fails, booking should be rolled back)

### 4. **Reservation IDs**
   - **Current**: No unique identifier for reservations
   - **Real-world**: Each reservation would have a unique confirmation number/code for customer reference

### 5. **Seat Selection**
   - **Current**: Only tracks number of seats, not specific seat assignments
   - **Real-world**: Would track individual seat numbers (e.g., "12A", "12B") and seat preferences (window, aisle, etc.)

### 6. **Customer Management**
   - **Current**: Only stores customer name
   - **Real-world**: Would have a `Customer` entity with contact information, frequent flyer number, payment methods, etc.

### 7. **Flight Status**
   - **Current**: No tracking of flight status (on-time, delayed, cancelled)
   - **Real-world**: Would need to handle cancellations, delays, and rebooking scenarios

### 8. **Pricing**
   - **Current**: No pricing information
   - **Real-world**: Would include dynamic pricing, seat classes (economy, business, first), and payment processing

### 9. **Search Enhancements**
   - **Current**: Basic destination and date search
   - **Real-world**: Would support origin airport, flexible dates, price range, airline filters, sorting options

### 10. **Validation & Security**
   - **Current**: Basic input validation
   - **Real-world**: Would include authentication, authorization, rate limiting, input sanitization, and protection against SQL injection (if using databases)

### 11. **Error Handling & Logging**
   - **Current**: Basic console error messages
   - **Real-world**: Comprehensive logging framework (Log4j, SLF4J), structured error handling, monitoring, and alerting

### 12. **API Design**
   - **Current**: Console interface
   - **Real-world**: RESTful API with proper HTTP status codes, request/response DTOs, API versioning, and documentation (OpenAPI/Swagger)

## Testing

The project includes comprehensive unit tests covering:

- Flight search functionality (matching, case-insensitivity, date filtering)
- Flight booking (successful bookings, edge cases)
- Overbooking prevention
- Input validation (null checks, empty strings, invalid seat counts)
- Multiple bookings on the same flight
- Reservation retrieval by customer

Run tests with:
```bash
mvn test
```

## Future Enhancements

If this were a production system, I would consider:

1. Database integration with JPA/Hibernate
2. REST API with Spring Boot
3. User authentication and authorization
4. Payment processing integration
5. Email notifications for bookings
6. Cancellation and modification features
7. Seat selection interface
8. Flight status updates
9. Multi-currency support
10. Comprehensive logging and monitoring

## License

This project is created for educational/demonstration purposes.



"# flight-reservation-app" 
"# flight-reservation-app" 
