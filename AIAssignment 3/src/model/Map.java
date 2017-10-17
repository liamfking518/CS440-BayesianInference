package model;

import java.util.ArrayList;
import java.util.Random;

public class Map {

	Cell[][] cells;
	int numRows;
	int numColumns;

	//generates a grid of cells with differing terrain types and sets the initial probabilities of starting at each cell
	public Map (int rows, int columns){

		cells = new Cell[rows][columns];
		numRows = rows;
		numColumns = columns;

		Random rand = new Random();
		int num = 0;
		char terrain = 0;

		//generate terrain types for cells
		int numBlocked = 0;
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){

				num = rand.nextInt(10);

				if (num == 0){
					terrain = 'b';
					numBlocked++;
				}
				else if (num == 1 || num == 2){
					terrain = 'h';
				}
				else if (num == 3 || num == 4){
					terrain = 't';
				}
				else {
					terrain = 'n';
				}

				cells[i][j] = new Cell(i, j, terrain);
			}
		}

		int availableCells = (numRows * numColumns) - numBlocked;
		double prob = 1.0 / availableCells;

		//set the initial probability of each cell
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				if(cells[i][j].terrain!='b')
					cells[i][j].setInitialProb(prob);
				else
					cells[i][j].setInitialProb(0);
			}
		}
	}

	public Map (Cell[][] cells){

		this.cells = cells;
		numRows = cells.length;
		numColumns = cells[0].length;

		//count blocked cells
		int numBlocked = 0;
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				if(cells[i][j].terrain=='b')
					numBlocked++;
			}
		}
		int availableCells = (numRows * numColumns) - numBlocked;
		double prob = 1.0 / availableCells;

		//set the initial probability of each cell
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				if(cells[i][j].terrain!='b')
					cells[i][j].setInitialProb(prob);
				else
					cells[i][j].setInitialProb(0);
			}
		}
	}

	public Map (ArrayList<String> description){


		String[] line = null;

		int dimension = description.size() - 301;

		cells = new Cell[dimension][dimension];

		numRows = dimension;
		numColumns = dimension;

		System.out.println("dimension: " + dimension);

		int x = 0;
		int y = 0;

		int numBlocked = 0;
		for (int i = 301; i < description.size(); i++){
			line = description.get(i).split(" ");
			for (int j = 0; j < line.length; j++){
				y = j;
				char terrain = line[j].charAt(0);
				if (terrain == 'b'){
					numBlocked++;
				}
				cells[x][y] = new Cell(x, y, terrain);
			}
			x++;
		}

		int availableCells = (numRows * numColumns) - numBlocked;
		double prob = availableCells / (numRows * numColumns);
		//set the initial probability of each cell
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				cells[i][j].setInitialProb(prob);
			}
		}

	}
	
	public void resetMap(Cell[][] cells){
		//count blocked cells
		int numBlocked = 0;
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				if(cells[i][j].terrain=='b')
					numBlocked++;
			}
		}
		int availableCells = (numRows * numColumns) - numBlocked;
		double prob = 1.0 / availableCells;

		//set the initial probability of each cell
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				if(cells[i][j].terrain!='b')
					cells[i][j].setInitialProb(prob);
				else
					cells[i][j].setInitialProb(0);
			}
		}
	}

	public ArrayList<Cell> findNeighbors(Cell xi){

		ArrayList<Cell> neighbors = new ArrayList<Cell>();
		//add current cell to list
		neighbors.add(xi);

		int x = xi.x;
		int y = xi.y;

		//to check bounds of coordinates of possible neighbor
		int nx = x - 1;
		int ny = y;

		if (nx > -1 && cells[nx][ny].terrain != 'b'){
			neighbors.add(cells[nx][ny]);
		}
		nx = x + 1;
		ny = y;
		if (nx < numRows && cells[nx][ny].terrain != 'b'){
			neighbors.add(cells[nx][ny]);
		}

		nx = x;
		ny = y - 1;
		if (ny > -1 && cells[nx][ny].terrain != 'b'){
			neighbors.add(cells[nx][ny]);
		}

		nx = x;
		ny = y + 1;
		if ( ny < numColumns && cells[nx][ny].terrain != 'b'){
			neighbors.add(cells[nx][ny]);
		}

		/*
		System.out.println("cell " + xi.x + " " + xi.y + " has neighbors: ");
		for (int i = 0; i < neighbors.size(); i++){
			System.out.println(neighbors.get(i).x + " " + neighbors.get(i).y);
		}
		*/


		return neighbors;
	}

	public void printMap(){
		System.out.println(numRows + " * " + numColumns);
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				System.out.print(cells[i][j].terrain + "  ");
			}
			System.out.println();
		}
	}
}
