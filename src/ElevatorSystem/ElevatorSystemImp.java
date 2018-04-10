package ElevatorSystem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import Elevator.Elevator;
import Elevator.MovingState;
import Elevator.SortFloor;
import ElevatorSystem.ElevatorSystem;

/**
 *
 * Implementation of ElevatorSytem, controls 4 elevators
 * @author Chenxiao Cui
 * @version March 26, 2018
 */
public class ElevatorSystemImp implements ElevatorPanel, ElevatorSystem {

	private final Object REQUEST_LOCK = new Object();
	private final int MAX_FLOOR;
	private final int MIN_FLOOR;	
	private Map<Elevator, List<Integer>> stops;
	private ExecutorService service;
	private AtomicBoolean shutDown = new AtomicBoolean(false);
	private MovingState callDirection;

	public ElevatorSystemImp(int MIN_FLOOR, int MAX_FLOOR) {
		this.MAX_FLOOR = MAX_FLOOR;
		this.MIN_FLOOR = MIN_FLOOR;
		stops = new HashMap<>();
		service = Executors.newCachedThreadPool();
	}

	private Runnable run = () -> {
		AtomicInteger[] ai = new AtomicInteger[stops.size()];

		for (int i = 0; i < ai.length; i++) {
			ai[i] = new AtomicInteger(0);
		}
		while (!shutDown.get()) {
			for (Elevator e : stops.keySet()) {
				List<Integer> stopList = stops.get(e);
				if (!e.isIdle() || stopList.isEmpty() || ai[e.id()].get() != 0) {
					continue;
				}
				synchronized (REQUEST_LOCK) {					
					int floor = stopList.remove(0);					
					ai[e.id()].incrementAndGet();
					service.submit(() -> {
						e.moveTo(floor);						
						ai[e.id()].decrementAndGet();
					});
				}
			}
		}

	};

	/**
	 * Calculate every elevator's steps to the (target) floor and return the closet elevator	 * 
	 * @param floor -current floor	 *           
	 * @return  best Elevator
	 */
	synchronized public Elevator GAE(int floor) {
		
		Elevator best = null;
		int smallest = Integer.MAX_VALUE;		

		// Get best elevator
		for (Elevator e : stops.keySet()) {
			if (e.isIdle() && stops.get(e).isEmpty()&&Math.abs((e.getFloor() - floor)) + 1 < smallest) {				
					best = e;
					smallest = Math.abs((e.getFloor() - floor)) + 1;				
			}
		}
		return best;
	}

	/**	 
	 * @param floor -target floor	 *           
	 * @param direction -the elevator calling direction           
	 * @return e Elevator
	 */
	private Elevator call(int floor, MovingState direction) {
		callDirection = direction;
		Elevator e = GAE(floor);
		e.moveTo(floor);
		return e;

	}

	/**
	 * when calling up it means the passenger intends to travel to a higher floor.
	 * 
	 * @param floor
	 *            - passengers current floor when calling for an {@link Elevator}
	 * @return an {@link ElevatorSystem} that has reach the requested floor
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Override
	public Elevator callUp(int floor) {
		if (floor < 0 || floor > 20) {
			throw new IllegalArgumentException();
		}
		return call(floor, MovingState.Up);

	}

	/**
	 * when calling down it means the passenger intends to travel to a lower floor.
	 * 
	 * @param floor
	 *            - passengers current floor when calling for an {@link Elevator}
	 * @return an {@link ElevatorSystem} that has reach the requested floor
	 */
	@Override
	public Elevator callDown(int floor) {
		if (floor < 0 || floor > 20) {
			throw new IllegalArgumentException();
		}

		return call(floor, MovingState.Down);
	}

	/**
	 * add an {@link Elevator} to {@link ElevatorSystem}, if implemented multiple
	 * {@link Elevator} can be added
	 * 
	 * @param elevator
	 *            - {@link Elevator} object to be added to {@link ElevatorSystem}
	 */
	@Override
	public void addElevator(Elevator elevator) {
		if (elevator == null)
			throw new NullPointerException();

		stops.put(elevator, new LinkedList<>());
	}

	/**
	 * get maximum floor for this {@link ElevatorSystem}
	 * 
	 * @return maximum floor for this {@link ElevatorSystem}
	 */
	@Override
	public int getMaxFloor() {
		return MAX_FLOOR;
	}

	/**
	 * get minimum floor for this {@link ElevatorSystem}
	 * 
	 * @return minimum floor for this {@link ElevatorSystem}
	 */
	@Override
	public int getMinFloor() {
		return MIN_FLOOR;
	}

	/**
	 * get total floors to which {@link ElevatorSystem} can send an
	 * {@link Elevator}. behavior and definition of this method will likely change
	 * when more elevators are introduced.
	 * 
	 * @return total floors
	 */
	@Override
	public int getFloorCount() {
		return MAX_FLOOR-MIN_FLOOR+1;
	}

	/**
	 * total number of elevators regardless of their states
	 * 
	 * @return total number of elevators
	 */
	@Override
	public int getElevatorCount() {
		return stops.size();
	}

	/**
	 * return total power consumed by all {@link Elevator} in the
	 * {@link ElevatorSystem}
	 * 
	 * @return total power consumed
	 */
	@Override
	public double getPowerConsumed() {
		double result = 0;
		for (Elevator e : stops.keySet()) {
			result += e.getPowerConsumed();

		}
		return result;
	}

	@Override
	public void addObserver(Observer observer) {
		if (observer == null) {
			throw new NullPointerException();

		}
		for (Elevator e : stops.keySet()) {
			e.addObserver(observer);
		}
	}

	/**
	 * Shutdown the ExecutorService
	 * shutdown {@link ExecutorService} which handles are threads
	 */
	@Override
	public void shutdown() {
		shutDown.set(true);

		service.shutdown();
	}

	/**
	 * start the main thread controlling {@link ElevatorSystem}
	 */
	@Override
	public void start() {
		try {
			service.submit(run);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * called from {@link Elevator} to inform {@link ElevatorSystem} of request stop
	 * to new floor.
	 * 
	 * @param elevator
	 *            - reference to the calling elevator.
	 * @param floor
	 *            - new floor to which {@link Elevator} will travel.
	 */
	@Override
	public void requestStop(Elevator elevator, int floor) {
		requestStops(elevator, floor);
	}

	/**
	 * called from {@link Elevator} to inform {@link ElevatorSystem} of multiple
	 * stop requests.
	 * 
	 * @param elevator
	 *            - reference to the calling elevator.
	 * @param floors
	 *            - new stops to which {@link Elevator} will travel.
	 */
	
	@Override
	public void requestStops(Elevator elevator, int... floors) {
		if (floors == null || elevator == null) {
			throw new NullPointerException();
		}

		if(callDirection == MovingState.Up)
			floors = SortFloor.ascSort(floors);
		else
			floors = SortFloor.descSort(floors);

		synchronized (REQUEST_LOCK) {
			List<Integer> list = stops.get(elevator);
			for (int floor : floors) {
				list.add(floor);
			}
		}
	}

	/**
	 * Return the HashMap
	 * @return stops
	 */
	public Map<Elevator, List<Integer>> getStops(){
		return stops;
	}
	
	/**
	 * Set the MovingState, used for JUnit test
	 * @param state MovingState
	 */
	public void setCallDirection(MovingState state) {
		callDirection = state;
	}

	/**
	 * return current floor of {@link Elevator} in {@link ElevatorSystem} when only
	 * have one elevator but should not be used anymore since there are more than
	 * one elevator, because different elevator will have the different the current
	 * floor
	 */
	@Override
	public int getCurrentFloor() {		
		return 0;
	}
}
