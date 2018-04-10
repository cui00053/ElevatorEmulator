package Test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import Elevator.SortFloor;

public class SortFloorTest {

	private int[] toBeSorted;
	private boolean excep = false;

	@Before
	public void setUp() throws Exception {
		toBeSorted = new int[] { 3, 2, 4, 6, 20, 8, 7 };
	}

	@After
	public void tearDown() throws Exception {
		toBeSorted = null;
	}

	@Test
	public void testAscSort() {
		// happy path
		int[] result = SortFloor.ascSort(toBeSorted);
		assertEquals("ascSort happy path", 2, result[0]);

		// sad path
		try {
			SortFloor.ascSort(null);
		} catch (NullPointerException e) {
			excep = true;
		}
		assertTrue("ascSort sad path", excep);
	}

	@Test
	public void testDescSort() {
		// happy path
		int[] result = SortFloor.descSort(toBeSorted);
		assertEquals("descSort happy path", 20, result[0]);

		// sad path
		try {
			SortFloor.ascSort(null);
		} catch (NullPointerException e) {
			excep = true;
		}
		assertTrue("descSort sad path", excep);
	}

}
