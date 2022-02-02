package com.parkit.parkingsystem.service;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
        double duration = TimeUnit.MILLISECONDS.toHours((long) durationMilliseconds);

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