package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Date;

public class FareCalculatorServiceTest {
	private static final Logger logger = LogManager.getLogger("FareCalculatorServiceTest");
    private static FareCalculatorService fareCalculatorService;
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private Ticket ticket;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    
    @BeforeAll
    private static void setUp() {
    	logger.info("Setting up the Fare Calculator Service");
        fareCalculatorService = new FareCalculatorService();
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }
    @BeforeEach
    private void setUpPerTest(){
    	logger.info("Creating a new Ticket");
        ticket = new Ticket();
    }
    
    
    @Test
    @Tag("FareCalculatorService")
    @DisplayName("Calculating the fare for a car")
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @Tag("FareCalculatorService")
    @DisplayName("Calculating the fare for a bike")
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    @Tag("FareCalculatorService")
    @DisplayName("Checking if we correctly get an exception when the vehicle type is unknown")
    public void calculateFareUnknownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    @Tag("FareCalculatorService")
    @DisplayName("Checking if we correctly get an exception when the bike parks in the future")
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    @Tag("FareCalculatorService")
    @DisplayName("Calculating the Fare when parking a bike for less than a hour")
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    @Tag("FareCalculatorService")
    @DisplayName("Calculating the Fare when parking a car for less than a hour")
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    @Tag("FareCalculatorService")
    @DisplayName("Calculating the Fare when parking for more than a day")
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    @Test
    @Tag("FareCalculatorService")
    @DisplayName("Checking if the users are correctly having free parking under 30 min ")
    public void calculateFree30MinParking() {
       Date inTime = new Date();
       inTime.setTime( System.currentTimeMillis() - (  30 * 60 * 1000) );//30 minutes parking time should give free parking fare
       Date outTime = new Date();
       ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

       ticket.setInTime(inTime);
       ticket.setOutTime(outTime);                                  
       ticket.setParkingSpot(parkingSpot);
       fareCalculatorService.calculateFare(ticket);
       assertEquals((0 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    
 
       @Test
       @Tag("FareCalculatorService")
       @DisplayName("Checking if the users are correctly getting a discount if they already parked here before")
       public void checkDiscountRecurringUsersBike() throws Exception{

           
       	/*Old ticket*/
       	//A bike parked 1h ago for 30min
           Date inTime1 = new Date();
           inTime1.setTime( (System.currentTimeMillis() - (  30 * 60 * 1000)) - (  30 * 60 * 1000) );
           Date outTime1 = new Date();
           outTime1.setTime( System.currentTimeMillis() - (  30 * 60 * 1000) );
           ParkingSpot parkingSpot1 = new ParkingSpot(1, ParkingType.BIKE,false);
           ticket.setInTime(inTime1);
           ticket.setOutTime(outTime1);
           ticket.setParkingSpot(parkingSpot1);
           ticket.setVehicleRegNumber("ABCDEF");
           fareCalculatorService.calculateFare(ticket);
           ticketDAO.saveTicket(ticket);
           
           /*New ticket*/
           //The bike parks again for 30min
           Date inTime = new Date();
           inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time
           Date outTime = new Date();
           ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
           Ticket newTicket = new Ticket();
           newTicket.setInTime(inTime1);
           newTicket.setOutTime(outTime1);
           newTicket.setParkingSpot(parkingSpot1);
           newTicket.setVehicleRegNumber("ABCDEF");
           newTicket.setInTime(inTime);
           newTicket.setOutTime(outTime);
           newTicket.setParkingSpot(parkingSpot);
           fareCalculatorService.calculateFare(newTicket);
           
           assertEquals(((0.75 * Fare.BIKE_RATE_PER_HOUR)*0.95), newTicket.getPrice() );
       }
        
    

}
