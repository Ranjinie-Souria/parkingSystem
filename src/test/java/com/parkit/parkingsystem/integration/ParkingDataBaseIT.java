package com.parkit.parkingsystem.integration;

import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){
    	dataBasePrepareService.clearDataBaseEntries();
    }
    
    @Test
    @Tag("ParkingDataBaseIT")
    @DisplayName("Checking that a ticket is actually saved in DB and Parking table is updated with availability")
    public void testParkingACar(){
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    	// Verifying that the ticket does not exist
        Assertions.assertNull(ticketDAO.getTicket("ABCDEF"));
        parkingService.processIncomingVehicle(); //Creating a ticket for a car named ABCDEF
        Assertions.assertNotNull(ticketDAO.getTicket("ABCDEF"));
        Assertions.assertFalse(ticketDAO.getTicket("ABCDEF").getParkingSpot().isAvailable());
    }

    
    
    @Test
    @Tag("ParkingDataBaseIT")
    @DisplayName("Checking that the fare generated and out time are populated correctly in the database")
    public void testParkingLotExit(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        Date dateExpected = new Date();
        parkingService.processExitingVehicle();
        Ticket theTicket = new Ticket();
        theTicket = ticketDAO.getTicket("ABCDEF");
        Date dateGot = ticketDAO.getOutTime(theTicket);
        
        Assertions.assertEquals(0.0,ticketDAO.getPrice(theTicket),0.0001);
        Assertions.assertNotNull(ticketDAO.getOutTime(theTicket));
        //getTime() gives the nb of milliseconds between the 01/01/1970 and the date
        Assertions.assertTrue((dateGot.getTime() - dateExpected.getTime()) < 1000);
    }
    
    @Test
    @Tag("ParkingDataBaseIT")
    @DisplayName("Checking that a new ticket is created for a known user")
    public void testKnownUser(){
    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        parkingService.processExitingVehicle();
        
        Ticket theTicket = new Ticket();
        theTicket.setVehicleRegNumber("ABCDEF");
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  45 * 60 * 1000) );
        Date outTime = new Date();
        outTime.setTime( System.currentTimeMillis() + (  45 * 60 * 1000) );
        theTicket.setInTime(inTime);
        theTicket.setOutTime(outTime);
        theTicket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR,false));
        ticketDAO.saveTicket(theTicket);
        
        Assertions.assertTrue(ticketDAO.isKnownUser(theTicket));
    }
    
    

}