package com.example.apnisavari;

public class CancelledHistory {
    private String Destination,PickUpPoint,Date,DriverId,BookingId;
    public CancelledHistory()
    {

    }

    public CancelledHistory(String destination, String pickUpPoint, String date, String driverId, String bookingId) {
        Destination = destination;
        PickUpPoint = pickUpPoint;
        Date = date;
        DriverId = driverId;
        BookingId = bookingId;
    }

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

    public String getPickUpPoint() {
        return PickUpPoint;
    }

    public void setPickUpPoint(String pickUpPoint) {
        PickUpPoint = pickUpPoint;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDriverId() {
        return DriverId;
    }

    public void setDriverId(String driverId) {
        DriverId = driverId;
    }
}
