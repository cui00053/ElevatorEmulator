package Test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Elevator.Elevator;
import Elevator.ElevatorImp;
import ElevatorSystem.ElevatorPanel;
import ElevatorSystem.ElevatorSystemImp;

public class ElevatorImpTest {

	private ElevatorImp elevator;	
	private ElevatorPanel panel = new ElevatorSystemImp(0,20);
	private boolean excep;
	
	@Before
	public void setUp() throws Exception {			
		elevator = new ElevatorImp(10, panel,0);
	}

	@After
	public void tearDown() throws Exception {
		elevator = null;
	}	
	@Test
	public void testMoveTo() {
		// happy path
		elevator.moveTo(10);
		assertEquals(12,elevator.getPowerConsumed(),0);	
		
		//sad path
		try {
			elevator.moveTo(21);
		}catch(IllegalArgumentException e) {
			excep = true;
		}
		assertTrue(excep);
	}

	@Test
	public void testAddPersons() {
		//happy path
		elevator.addPersons(2);		
		assertEquals("Added right person number.", 2, elevator.getCapacity());
		
		//sad path
		try {
			elevator.addPersons(21);
		}catch(IllegalArgumentException e) {
			excep = true;
		}
		assertTrue(excep);
	}
	

	@Test
	public void testIsFull() {
		elevator.addPersons(10);		
		assertTrue( elevator.isFull());
	}

	@Test
	public void testIsEmpty() {
		assertTrue(elevator.isEmpty());
	}

	@Test
	public void testIsIdle() {
		assertTrue(elevator.isIdle());
	}

	@Test
	public void testId() {
		assertEquals(0,elevator.id());
	}

	@Test
	public void testEquals() {
		//happy path
		Elevator elevator2 = new ElevatorImp(10,panel,1);
		assertFalse(elevator.equals(elevator2));
		//sad path
		try {
			Elevator elevator3=null;
			elevator.equals(elevator3);
		}catch(NullPointerException e) {
			excep = true;
		}
		assertTrue(excep);
	}

}
