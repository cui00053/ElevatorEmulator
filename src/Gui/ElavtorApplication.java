package Gui;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import Simulator.Simulator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * An application simulates elevators' movement.
 * 
 * @author Chenxiao Cui
 * @version March 26, 2018
 */
public class ElavtorApplication extends Application implements Observer {
	private ElevatorAnime ea;
	private Simulator simulator = new Simulator(this);
	private int FLOOR_COUNT = 21;
	private int ELEVATOR_COUNT = 4;
	private int[] targetFloor = new int[ELEVATOR_COUNT];
	private int[] currentFloor = new int[ELEVATOR_COUNT];
	private double[] powerUsed = new double[ELEVATOR_COUNT];
	private TextField[] id = new TextField[ELEVATOR_COUNT];
	private TextField[] cFloor = new TextField[ELEVATOR_COUNT];
	private TextField[] tFloor = new TextField[ELEVATOR_COUNT];
	private TextField[] tEnergy = new TextField[ELEVATOR_COUNT];
	
	Label floors[][] = new Label[ELEVATOR_COUNT][FLOOR_COUNT];

	/**
	 * Create floor labels as elevator GUI
	 */
	@Override
	public void init() throws Exception {
		super.init();
		
		for (int i = 0; i < ELEVATOR_COUNT; i++) {
			for (int j = 0; j < FLOOR_COUNT; j++) {
				floors[i][j] = new Label();
				floors[i][j].setId("empty");
			}
			floors[i][0].setId("elevator");
		}
		
		ea = new ElevatorAnime();
	}

	/**
	 * Receive List<Number> transfered from notifyOberserver()
	 * 
	 * @param Observable ElevatorImp, Object List<Number>
	 */
	@Override
	public void update(Observable observable, Object obj) {
		if (obj == null) {
			throw new NullPointerException("the obj should not be null");
		}
		List<Number> pair = (List<Number>) obj;
		ea.addPair(pair);
	}

	/**
	 * Start the application, start simulator
	 * 
	 * @param Stage
	 */
	@Override
	public void start(Stage stage) throws Exception {
		GridPane elevator1 = new GridPane();
		GridPane elevator2 = new GridPane();
		GridPane elevator3 = new GridPane();
		GridPane elevator4 = new GridPane();
		for (int i = FLOOR_COUNT-1; i >=0; i--) {
			elevator1.add(floors[0][i], 1, (FLOOR_COUNT-1)-i);
			elevator2.add(floors[1][i], 1, (FLOOR_COUNT-1)-i);
			elevator3.add(floors[2][i], 1, (FLOOR_COUNT-1)-i);
			elevator4.add(floors[3][i], 1, (FLOOR_COUNT-1)-i);
		}
		
		for(int i=0;i<ELEVATOR_COUNT;i++){
			id[i] = new TextField(Integer.toString(i));		
			cFloor[i] = new TextField();
			tFloor[i] = new TextField();
			tEnergy[i] = new TextField();
		}

		VBox vbox1 = new VBox();
		Label idTitle1 = new Label("ID");
		Label totalEnergyTitle1 = new Label("Total Energy Consumed");
		Label currentFloorTitle1 = new Label("Current Floor");
		Label targetFloorTitle1 = new Label("Target Floor");

		VBox vbox2 = new VBox();
		Label idTitle2 = new Label("ID");
		Label totalEnergyTitle2 = new Label("Total Energy Consumed");
		Label currentFloorTitle2 = new Label("Current Floor");
		Label targetFloorTitle2 = new Label("Target Floor");
		
		VBox vbox3 = new VBox();
		Label idTitle3 = new Label("ID");
		Label totalEnergyTitle3 = new Label("Total Energy Consumed");
		Label currentFloorTitle3 = new Label("Current Floor");
		Label targetFloorTitle3 = new Label("Target Floor");
		
		VBox vbox4 = new VBox();
		Label idTitle4 = new Label("ID");
		Label totalEnergyTitle4 = new Label("Total Energy Consumed");
		Label currentFloorTitle4 = new Label("Current Floor");
		Label targetFloorTitle4 = new Label("Target Floor");

		
		vbox1.getChildren().addAll(idTitle1,id[0],targetFloorTitle1, tFloor[0], currentFloorTitle1, cFloor[0], totalEnergyTitle1, tEnergy[0]);
		vbox2.getChildren().addAll(idTitle2,id[1],targetFloorTitle2, tFloor[1], currentFloorTitle2, cFloor[1], totalEnergyTitle2, tEnergy[1]);
		vbox3.getChildren().addAll(idTitle3,id[2],targetFloorTitle3, tFloor[2], currentFloorTitle3, cFloor[2], totalEnergyTitle3, tEnergy[2]);
		vbox4.getChildren().addAll(idTitle4,id[3],targetFloorTitle4, tFloor[3], currentFloorTitle4, cFloor[3], totalEnergyTitle4, tEnergy[3]);

		HBox hbox1 = new HBox();
		hbox1.getChildren().addAll(elevator1, vbox1);
		
		HBox hbox2 = new HBox();
		hbox2.getChildren().addAll(elevator2, vbox2);
		
		HBox hbox3 = new HBox();
		hbox3.getChildren().addAll(elevator3, vbox3);
		
		HBox hbox4 = new HBox();
		hbox4.getChildren().addAll(elevator4, vbox4);

		HBox hbox_root = new HBox();
		hbox_root.getChildren().addAll(hbox1, hbox2, hbox3, hbox4);
		
		Scene scene = new Scene(hbox_root);
		scene.getStylesheets().add(ElavtorApplication.class.getResource("elevator.css").toExternalForm());
		scene.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				ea.stop();
				stage.hide();
			}
		});

		stage.setScene(scene);
		stage.setTitle("Elevator");
		stage.show();
		ea.start();
		simulator.start();
	}

	/**
	 * Extends AnimationTimer, add a new method;
	 * 
	 * @author Chenxiao
	 *
	 */
	public class ElevatorAnime extends AnimationTimer {

		private int step;
		private Queue<List<Number>> queue = new LinkedList<>();

		public void addPair(List<Number> pair) {
			
			if (pair == null) {
				throw new NullPointerException("the pair should not be null");
			}
				
				queue.add(pair);
		}
			

		@Override
		public void handle(long now) {
			if (queue.isEmpty()) {
				return;
			}
			
			List<Number> pair = queue.poll();
			int _id = pair.get(0).intValue();
			
			floors[_id][currentFloor[_id]].setId("empty");
			currentFloor[_id] = pair.get(1).intValue();
			targetFloor[_id] = pair.get(2).intValue();
			powerUsed[_id] = pair.get(3).doubleValue();
			floors[_id][targetFloor[_id]].setId("target");
			floors[_id][currentFloor[_id]].setId("elevator");
			
			cFloor[_id].setText(String.valueOf(currentFloor[_id]));
			tFloor[_id].setText(String.valueOf(targetFloor[_id]));
			tEnergy[_id].setText(String.valueOf(powerUsed[_id]));
			
		}
	};

	@Override
	public void stop() throws Exception {
		super.stop();
		simulator.shutdown();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
