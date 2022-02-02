package com.parkit.parkingsystem.service;
import java.util.Date;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }


        Date inHour = ticket.getInTime();
        Date outHour =  ticket.getOutTime();
        
        //converting the duration in milliseconds to hours
        double durationMilliseconds = outHour.getTime() - inHour.getTime();
        double duration = ((durationMilliseconds / (1000*60*60)) % 24);
        
        //TODO: Some tests are failing here. Need to check if this logic is correct

    System.out.println("heures : "+duration+" millisecondes : "+durationMilliseconds);
    if(duration>0.50) {
    	switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
        
    }
}