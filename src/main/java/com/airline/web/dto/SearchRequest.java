package com.airline.web.dto;

/**
 * Data Transfer Object for flight search requests.
 */
public class SearchRequest {
    private String destination;
    private String dateTime;

    public SearchRequest() {
    }

    public SearchRequest(String destination, String dateTime) {
        this.destination = destination;
        this.dateTime = dateTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
