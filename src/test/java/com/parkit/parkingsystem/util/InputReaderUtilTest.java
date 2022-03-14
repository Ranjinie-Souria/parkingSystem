package com.parkit.parkingsystem.util;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class InputReaderUtilTest {
	
	private static InputStream stdin;
	
	@BeforeEach
	private void setUpPerTest(){
		stdin = System.in;
    }
	/*
	@Test
	@DisplayName("Returns correctly the given input")
	public void testReadSelection(){
		System.setIn(new ByteArrayInputStream("1".getBytes()));
		InputReaderUtil inputReaderUtil = new InputReaderUtil();
		assertEquals(1,inputReaderUtil.readSelection());
		System.setIn(stdin);
		
	}
		
	@Test
	@DisplayName("Returns correctly an error if an incorrect input is given")
	public void testReadIncorrectSelection(){
		System.setIn(new ByteArrayInputStream("abcdef".getBytes()));
		InputReaderUtil inputReaderUtil = new InputReaderUtil();
		assertEquals(-1,inputReaderUtil.readSelection());
		System.setIn(stdin);
		
	}
	

	@Test
	@DisplayName("Returns correctly an error if the string is too long")
	public void testReadVehicleRegistrationNumberTooLong() throws Exception {
		System.setIn(new ByteArrayInputStream("abcdefghijklmnopqrstuvwxyz".getBytes()));
		InputReaderUtil inputReaderUtil = new InputReaderUtil();
		    assertThrows(Exception.class,
		            ()->{
		            	inputReaderUtil.readVehicleRegistrationNumber();
		            });
		System.setIn(stdin);
	}
	
*/
}
