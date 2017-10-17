package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Filterer {

	Map map;
	Cell[][] cells;
	int numRows;
	int numColumns;


	public Filterer(GroundTruth groundTruth){
		map = groundTruth.map;
		cells = map.cells;
		numRows = map.numRows;
		numColumns = map.numColumns;
	}

	public Filterer(Cell[][] cells){
		this.cells = cells;
		numRows = cells.length;
		numColumns = cells[0].length;
	}

	public Filterer(Map map){
		this.map = map;
		this.cells = map.cells;
		this.numRows = map.numRows;
		this.numColumns = map.numColumns;
	}

	//
	public void filter(char action, char evidence){

		double sumProbs = 0;;

		//calculate new currentProb using filtering equation
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				Cell xi = cells[i][j];
				if (xi.terrain != 'b'){
					xi.currentProb = computeObservation(evidence, xi) * computeSummation(action, xi);
					sumProbs = sumProbs + xi.currentProb;
				}

			}
		}

		//normalize new currentProbs
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				Cell xi = cells[i][j];
				xi.currentProb = xi.currentProb / sumProbs;
			}
		}

		//update priorProb for all cells
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				Cell xi = cells[i][j];
				xi.priorProb = xi.currentProb;
			}
		}

	}

	//calculates P(observation | currentCell)
	private double computeObservation(char observation, Cell xi){
		double observationModel = 0;

		if (observation == xi.terrain){
			observationModel = 0.9;
		}
		else {
			observationModel = 0.05;
		}
		return observationModel;
	}

	//calculates summation of the product of transition model and prior belief
	private double computeSummation(char action, Cell xi){

		double summation = 0;

		//find neighbors (includes self)
		ArrayList<Cell> neighbors = map.findNeighbors(xi);

		for (int i = 0; i < neighbors.size(); i++){
			Cell parent = neighbors.get(i);
			summation = summation + (computeTransition(xi, parent, action) * parent.priorProb);
		}

		return summation;
	}

	//x and any correspond to current cell
	private double computeTransition(Cell xi, Cell parent, char action){

		//System.out.println("xi: " + xi.x + " " + xi.y);
		//System.out.println("parent: " + parent.x + " " + parent.y);

		double transitionModel = 0;


		int x = parent.x;
		int y = parent.y;

		//assign x and y the values of the cell that this action 'would' move to if successful
		switch (action){
			case 'u':
				x = x - 1;
				break;
			case 'd':
				x = x + 1;
				break;
			case 'l':
				y = y - 1;
				break;
			case 'r':
				y = y + 1;
				break;
			default:
				System.out.println("computeTransition: cases not right");
		}

		//System.out.println("action: " + action);
		//System.out.println("computed x: " + x);
		//System.out.println("computed y: " + y);

		//cases where xi is same as parent
		if (xi.x == parent.x && xi.y == parent.y){
			if (action == 'u' && x < 0 ||
				action == 'd' && x >= numRows ||
				action == 'l' && y < 0 ||
				action == 'r' && y >= numColumns){

				transitionModel = 1.0;
			}
			else if (cells[x][y].terrain == 'b'){	//cell
				transitionModel = 1.0;
			}
			else {
				transitionModel = 0.1;
			}
		}
		else {	//cases where xi is different than parent
			if (xi.x == x && xi.y == y){	//this means that xi is the cell that would result from a successful action
				transitionModel = 0.9;
			}
			else {
				transitionModel = 0;
			}
		}

		//System.out.println("transitionModel: " + transitionModel);

		return transitionModel;
	}

	public void printCells(){

		int rows = cells.length;
		int columns = cells[0].length;

		for (int i = 0; i < rows; i++){
			for (int j = 0; j < columns; j++){
				System.out.printf("%.8f \t", cells[i][j].currentProb);
			}
			System.out.println("\n\n");
		}
	}

}
