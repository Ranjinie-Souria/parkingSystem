package com.parkit.parkingsystem.service;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
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
        //in the case the duration is inferior to 1 hour
        if(duration<1) {
        	duration = TimeUnit.MILLISECONDS.toMinutes((long) durationMilliseconds);
        	duration = duration / 60;
        }
        
        double tauxHoraire = 1;
        double reduction = 1;
	    if(duration>0.50) {
	    	switch (ticket.getParkingSpot().getParkingType()){
	            case CAR: {
	            	tauxHoraire = Fare.CAR_RATE_PER_HOUR;
	                break;
	            }
	            case BIKE: {
	            	tauxHoraire = Fare.BIKE_RATE_PER_HOUR;
	                break;
	            }
	            default: throw new IllegalArgumentException("Unknown Parking Type");
	        }
	    	
	    	TicketDAO dao = new TicketDAO();
	    	System.out.println("VALEUR DE ISKNOWNUSER : "+dao.isKnownUser(ticket));
	    	if(dao.isKnownUser(ticket)) {
	    		//Known users get a 5% reduction
	    		reduction = 0.95;
	    	}
	    	
	    	double prix = duration * tauxHoraire * reduction;
	    	ticket.setPrice(prix);
	    }
	    else {
	    	ticket.setPrice(0);
	    }
    }
    
}