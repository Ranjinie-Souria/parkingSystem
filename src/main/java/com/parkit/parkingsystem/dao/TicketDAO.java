package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public boolean saveTicket(Ticket ticket){
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            //ps.setInt(1,ticket.getId());
            ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null)?null: (new Timestamp(ticket.getOutTime().getTime())) );
            logger.info("Ticket saved");
            return ps.execute();
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
		return false;
    }

    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1,vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)),false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);   
        }
		return ticket;
    }

    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            ps.setInt(3,ticket.getId());
            ps.execute();
            return true;
        }catch (Exception ex){
            logger.error("Error saving ticket info",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }
    
    public boolean isKnownUser(Ticket ticket) {
    	Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_KNOWN_USER);
            ps.setString(1,ticket.getVehicleRegNumber());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
	            if(rs.getInt(1)>1) {
		            System.out.println("You are a reccuring user, you parked here "+rs.getInt(1)+" times.");
	            	dataBaseConfig.closeResultSet(rs);
	                dataBaseConfig.closePreparedStatement(ps);
	            	return true;            	
	            }
	            else {
	            	dataBaseConfig.closeResultSet(rs);
	                dataBaseConfig.closePreparedStatement(ps);
	            	return false;
	            }
            }
        }catch (Exception ex){
            logger.error("Error fetching user",ex);
        }finally {
            dataBaseConfig.closeConnection(con);   
        }
		return false;
    }
    
    public double getPrice(Ticket ticket) {
    	Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_PRICE);
            ps.setString(1,ticket.getVehicleRegNumber());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
	            	return rs.getDouble(1);
            }
        }catch (Exception ex){
            logger.error("Error : ",ex);
        }finally {
            dataBaseConfig.closeConnection(con);   
        }
		return 0;
    }
    
    public java.util.Date getOutTime(Ticket ticket) {
    	Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_OUT_TIME);
            ps.setString(1,ticket.getVehicleRegNumber());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
            	SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH);
            	String dateFormated = dateFormat.format( rs.getTimestamp("OUT_TIME") );
            	java.util.Date date = dateFormat.parse(dateFormated);
            	logger.info("Ticket Out time is : ",dateFormated);
            	
            	return date;
            }
        }catch (Exception ex){
            logger.error("Error : ",ex);
        }finally {
            dataBaseConfig.closeConnection(con);   
        }
		return null;
    }
    
}
