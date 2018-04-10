package Test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Elevator.Elevator;
import Elevator.ElevatorImp;
import Elevator.MovingState;
import ElevatorSystem.ElevatorPanel;
import ElevatorSystem.ElevatorSystemImp;

public class ElevatorSystemImpTest {

	private ElevatorSystemImp system;
	private ElevatorImp elevator1;
	private ElevatorImp elevator2;
	private boolean excep;

	@Before
	public void setUp() throws Exception {

		elevator1 = new ElevatorImp(1, (ElevatorPanel) system, 0);
		elevator2 = new ElevatorImp(1, (ElevatorPanel) system, 1);
		system = new ElevatorSystemImp(0, 20);
		system.addElevator(elevator1);
		system.addElevator(elevator2);
	}

	@After
	public void tearDown() throws Exception {

		elevator1 = null;
		elevator1 = null;
		system = null;
	}

	@Test
	public void testCallUp() {
		// happy path
		Elevator result = system.callUp(10);
		assertEquals("callUp() happy path", 10, result.getFloor());

		// sad path
		try {
			system.callUp(21);
		} catch (Exception e) {
			excep = true;
		}
		assertTrue("callUp() sad path",excep);
	}

	@Test
	public void testCallDown() {
		// happy path
		elevator1.moveTo(20);
		Elevator result = system.callDown(15);
		assertEquals("callDown() happy path", 15, result.getFloor());

		// sad path
		try {
			system.callDown(-1);
		} catch (Exception e) {
			excep = true;
		}
		assertTrue("callDown() sad path",excep);
	}

	@Test
	public void testGAE() {	
		elevator1.moveTo(15);
		elevator2.moveTo(16);
		Elevator result = system.GAE(17);
		assertEquals("GAE() happy path",1,result.id());	
	}	
	
	@Test
	public void testAddElevator() {
		//happy path
		Elevator elevator3 = new ElevatorImp(1, (ElevatorPanel) system, 2);
		system.addElevator(elevator3);
		Map<Elevator, List<Integer>> elevators = system.getStops();		
		assertEquals("addElevator() happy path",3,elevators.size());
		
		//sad path
		Elevator elevator4 = null;
		try {
			system.addElevator(elevator4);
		}catch(NullPointerException e) {
			excep = true;
		}
		assertTrue("addElevator() sad path",excep);
		
	}	

	@Test
	public void testShutdown() {
		
	}

	@Test
	public void testStart() {
		
	}

	@Test
	public void testRequestStops() {
		
		int[] floors = {2,6,10,8};
		system.setCallDirection(MovingState.Up);
		system.requestStops(elevator1, floors);
		Map<Elevator, List<Integer>> stops = system.getStops();
		List<Integer> list = stops.get(elevator1);
		assertEquals("requestStops() happy path",(Integer)10,list.get(3));
		
		//sad path
		try {
			system.requestStop(null, 0);
		}catch(NullPointerException e) {
			excep = true;
		}
		assertTrue("requestStops() sad path",excep);
	}
	
	

}
