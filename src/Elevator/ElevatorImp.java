package Elevator;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import ElevatorSystem.ElevatorPanel;

/**
 * Extends Observable and implements Elevator, contains features and functions of elevators.
 * @author Chenxiao
 * @version March 26, 2018
 */
public class ElevatorImp extends Observable implements Elevator {
	
	public final static int POWER_START_STOP=2;
	public final static int POWER_CONTINOUS=1;
	public final static long SLEEP_START_STOP=500;
	public final static long SLEEP_CONTINOUS=250;
	
	private final int MAX_CAPACITY_PERSONS;
	private final boolean delay;
	private final int ID;
		
	private int powerUsed;
	private int currentFloor;
	private int capacity;
	private ElevatorPanel panel;
	private volatile MovingState state = MovingState.Idle;
	private int step;

	/**
	 * Constructor, initialize a new elevator
	 * @param CAPACITY_PERSONS
	 * @param panel
	 * @param ID
	 * @param delay
	 */
	public ElevatorImp(int CAPACITY_PERSONS,ElevatorPanel panel, int ID, boolean delay) {
		MAX_CAPACITY_PERSONS = CAPACITY_PERSONS;
		this.panel = panel;	
		this.delay = delay;
		this.ID = ID;
	}
	
	public ElevatorImp(int CAPACITY_PERSONS, ElevatorPanel panel, int ID) {
		this(CAPACITY_PERSONS,panel,ID,true);
	}
	
	/**
	 * move {@link Elevator} to a specific floor
	 * @param floor - target floor
	 */
	@Override
	public void moveTo(int floor) {		
		if(floor<0||floor>20) {			
			throw new IllegalArgumentException("The floor should be 0-20") ; 
		}
		while(floor!=currentFloor) {		
			switch (getState()) {
				case Idle:
					step = floor-currentFloor<0?-1:1;
					state = step==1?MovingState.SlowUp:MovingState.SlowDown;					
					break;
				case SlowUp:					
					currentFloor += 1;
					powerUsed +=2;
					step = floor-currentFloor;
					if(step==1)
						state = MovingState.SlowUp;
					else if(step>1)
						state = MovingState.Up;
					else
						state = MovingState.Idle;						
					break;					
				case SlowDown:
					currentFloor -= 1;
					powerUsed +=2;
					step = currentFloor - floor;
					if(step==1)
						state = MovingState.SlowDown;
					else if(step>1)
						state = MovingState.Down;
					else
						state = MovingState.Idle;					
					break;
				case Up:
					currentFloor += 1; 
					powerUsed +=1;
					step = floor - currentFloor;
					state = step>1?MovingState.Up:MovingState.SlowUp;										
					break;
				case Down:
					currentFloor -= 1;
					powerUsed +=1;
					step = currentFloor - floor;
					state = step>1?MovingState.Down:MovingState.SlowDown;					
					break;					
			}
			setChanged();
			notifyObservers(Arrays.asList(ID, currentFloor,floor, powerUsed));	
			
			try {				
				Thread.sleep(SLEEP_CONTINOUS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * add number of persons to {@link Elevator}
	 * @param persons - number of passengers getting on at current floor
	 */
	@Override
	public void addPersons(int persons){
		if(persons<0||persons+capacity > MAX_CAPACITY_PERSONS) {
			throw new IllegalArgumentException();
		}
		capacity += persons;
	}
	
	/**
	 * represent the request made by one passenger inside of {@link Elevator}
	 * @param floor - target floor
	 */
	@Override
	public void requestStop(int floor) {		
		requestStops(floor);
	}
	
	/**
	 * represent the request made by multiple passenger inside of an {@link Elevator} object
	 * @param floors - target floors
	 */
	@Override
	public void requestStops(int... floors) {
		if (floors == null) {
			throw new NullPointerException("The floors is not valid, please re-enter");
		}
		panel.requestStops(this, floors);
	}
	
	/**
	 * get current capacity of the elevator not the maximum capacity.
	 * @return integer for total capacity currently in the {@link Elevator}
	 */
	@Override
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * get current floor of {@link Elevator} at this point
	 * @return current floor
	 */
	@Override
	public int getFloor() {		
		return currentFloor;
	}
	
	/**
	 * return total amount of power consumed to this point
	 * @return power consumed
	 */
	@Override
	public double getPowerConsumed() {		
		return powerUsed;	
	}
	
	/**
	 * get current {@link MovingState} of the {@link Elevator}
	 * @return current {@link MovingState}
	 */
	@Override
	public MovingState getState() {
		return state;
	}

	/**
	 * Compare max capacity with current capacity
	 * @return boolean, true for full
	 */
	@Override
	public boolean isFull() {		
		return MAX_CAPACITY_PERSONS-capacity<=0?true:false;
	}

	/**
	 * Check if this elevator empty
	 * @return boolean, true for empty
	 */
	@Override
	public boolean isEmpty() {		
		return capacity==0?true:false;
	}

	@Override
	public boolean isIdle() {
		return state.isIdle();
	}

	/**
	 * Unique integer that identifies this {@link Elevator} object
	 * @return unique identifier integer
	 */
	@Override
	public int id() {
		return ID;
	}

	
	/**
	 * add an {@link Observer} to this {@link Elevator}
	 * @param observer - add to this {@link Elevator}, cannot be null
	 */
	@Override
	public void addObserver( Observer observer) {
		if (observer == null) {
			throw new NullPointerException();
		}
		super.addObserver(observer);		
	};
	
	public boolean equals(Object obj) {
		if (obj == null) {
			throw new NullPointerException();
		}
		ElevatorImp elevator = (ElevatorImp) obj;
		return this.ID == elevator.ID;
	}
	
}
