package com.example.apnisavari;

public class History {
    private String Destination,PickUpPoint,Distance,Date,BookingId,DriverId;
    Double TotalFare;
    public History()
    {

    }

    public History(String destination, String pickUpPoint, String distance, String date, String bookingId, String driverId, Double totalFare) {
        Destination = destination;
        PickUpPoint = pickUpPoint;
        Distance = distance;
        Date = date;
        BookingId = bookingId;
        DriverId = driverId;
        TotalFare = totalFare;
    }

    public String getDriverId() {
        return DriverId;
    }

    public void setDriverId(String driverId) {
        DriverId = driverId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public Double getTotalFare() {
        return TotalFare;
    }

    public void setTotalFare(Double totalFare) {
        TotalFare = totalFare;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
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

public String toString() {
        return this.Destination+".";

}
}
