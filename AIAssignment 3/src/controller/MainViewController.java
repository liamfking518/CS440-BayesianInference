package controller;




import java.io.File;

import application.Main;
import application.TestBench;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Cell;
import model.Filterer;
import model.Grid;
import model.GroundTruth;
import model.Viterbier;

public class MainViewController {

	private Stage primaryStage;
	private Main main;

	@FXML
	private MenuItem openMenuItem;
	@FXML
	private MenuItem generateMapsMenuItem;
	@FXML
	private MenuItem runTestsMenuItem;
	@FXML
	private MenuItem closeMenuItem;
	@FXML
	private SplitPane splitPane;
	@FXML
	private AnchorPane leftPane;
	@FXML
	private AnchorPane rightPane;
	@FXML
	private Pane pane;
	@FXML
	private Label timeLabel;
	@FXML
	private Label actionLabel;
	@FXML
	private Label sensorLabel;
	@FXML
	private ComboBox<String> algorithmComboBox;
	@FXML
	private Button forwardButton;
	@FXML
	private Button backButton;
	@FXML
	private Canvas mapCanvas;

	GroundTruth truth;
	Grid grid;
	TestBench testBench;
	Filterer filterer;
	Viterbier viterbier;
	int step;
	GraphicsContext gc;

	@FXML
	private void initialize(){
		step = 0;
		gc = mapCanvas.getGraphicsContext2D();
		ObservableList<String> options =
			    FXCollections.observableArrayList(
			        "Filtering",
			        "Viterbi"
			    );
		algorithmComboBox.setItems(options);
		algorithmComboBox.getSelectionModel().selectFirst();



		assignEventHandlers();

	}

	private void assignEventHandlers(){



		openMenuItem.setOnAction((event) -> {
			File file = getFile();
			if (file != null){
				truth = new GroundTruth(file);
				String algo = algorithmComboBox.getValue();
				grid = new Grid(truth);
				if (algo.equals("Filtering")){
					grid.drawFiltering(0, gc);
				}
				else if (algo.equals("Viterbi")){
					grid.drawViterbi(0, gc);
				}
				else {
					System.out.println("combobox error!");
				}
			}

		});

		generateMapsMenuItem.setOnAction((event) -> {
			testBench = new TestBench();
			testBench.generateMaps();
		});

		runTestsMenuItem.setOnAction((event) -> {
			if (testBench != null){
				testBench.runTests();
			}
		});

		closeMenuItem.setOnAction((event) -> {
			main.handleClose();
		});

		forwardButton.setOnAction((event) -> {
			if (gc != null){
				step++;
				if (truth != null && grid != null && step < 101){

					String algo = algorithmComboBox.getValue();
					if (algo.equals("Filtering")){
						filterer = new Filterer(truth);
						for (int i = 0; i < step; i++){
							filterer.filter(truth.actions[i], truth.evidences[i]);
						}
						grid.drawFiltering(step, gc);
					}
					else if (algo.equals("Viterbi")){

						viterbier = new Viterbier(truth);
						
						viterbier.setStep(step);
						
						Cell path;
						
						for (int i = 0; i < step; i++){
							path = viterbier.computeMaxPath(truth.actions[i], truth.evidences[i]);
						}
						grid.drawViterbi(step, gc);
					}
					else {
						System.out.println("combobox error!");
					}

				}
				if (step == 101){
					step--;
				}
			}

		});

		backButton.setOnAction((event) -> {
			if (gc != null){
				String algo = algorithmComboBox.getValue();
				step--;
				if (truth != null && grid != null && step > 0){
					if (algo.equals("Filtering")){
						filterer = new Filterer(truth);
						for (int i = 0; i < step; i++){
							filterer.filter(truth.actions[i], truth.evidences[i]);
						}
						grid.drawFiltering(step, gc);
					}
					else if (algo.equals("Viterbi")){

						viterbier = new Viterbier(truth);

						for (int i = 0; i < step; i++){
							viterbier.computeMaxPath(truth.actions[i], truth.evidences[i]);
						}
						grid.drawViterbi(step, gc);
					}
					else {
						System.out.println("combobox error!");
					}
				}
				if (step <= 0){
					step = 0;
					if (algo.equals("Filtering")){
						grid.drawFiltering(0, gc);
					}
					else if (algo.equals("Viterbi")){
						grid.drawViterbi(0, gc);
					}
					else {
						System.out.println("combobox error!");
					}
				}
			}

		});
	}

	private File getFile(){

		Stage stage = new Stage();
		FileChooser fc = new FileChooser();
		fc.setTitle("Select map file");
		File file = fc.showOpenDialog(stage);

		return file;
	}


	public void start(Stage stage){
		this.primaryStage = stage;
	}

	public void setMain(Main main){
		this.main = main;
	}



}
