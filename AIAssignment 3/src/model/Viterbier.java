package model;

import java.util.ArrayList;

public class Viterbier {

	Map map;
	Cell[][] cells;
	int numRows;
	int numColumns;
	int step = 0;


	public Viterbier(Map map){
		this.map = map;
		cells = map.cells;
		numRows = map.numRows;
		numColumns = map.numColumns;
		int step = 0;
	}

	public Viterbier(Cell[][] cells){
		this.cells = cells;
		numRows = cells.length;
		numColumns = cells[0].length;
		int step = 0;
	}

	public Viterbier(GroundTruth truth){
		map = truth.map;
		cells = truth.map.cells;
		numRows = truth.map.numRows;
		numColumns = truth.map.numColumns;
		int step = 0;
	}

	public int getStep(){
		return this.step;
	}

	public void setStep(int step){
		this.step=step;
	}

	public Cell computeMaxPath(char action, char evidence){

		double sumProbs = 0;

		//calculate new currentProb using Viterbi equation
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				Cell xi = cells[i][j];
				if (xi.terrain != 'b'){
					Cell maxPath = argmaxPath(action, xi);
					
					//**************
					//not sure if this is where parent should be updated. cells can be returned more than once as maxPath
					//and can therefore add multiple "previous" cells
					//**************
					
					//xi.parent=maxPath;
					//maxPath.parents=xi.parents;
					//maxPath.parents.add(xi);
					xi.currentProb = computeObservation(evidence, xi) * maxPath.priorProb * computeTransition(xi, maxPath, action);
					sumProbs = sumProbs + xi.currentProb;
					
					//-------
					System.out.println("max is: ("+maxPath.x+", "+maxPath.y+"). Parents are: ");
					for(int n = maxPath.parents.size(); n > 0; n--){
						System.out.print("("+maxPath.parents.get(n-1).x+", "+maxPath.parents.get(n-1).y+")");
					}
					System.out.println();
					//-------
					
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

		//finds cell with max currentProb
		Cell max = cells[0][0];
		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				Cell xi = cells[i][j];
				if(xi.currentProb>max.currentProb)
					max=xi;
			}
		}
		
		System.out.println("max: ("+max.x+", "+max.y+")");
		
		System.out.println();
		
		return max;

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

	//determines cell with highest prob. using prior belief and transition model
	private Cell argmaxPath(char action, Cell xi){

		//find neighbors (includes self)
		ArrayList<Cell> neighbors = map.findNeighbors(xi);

		if(neighbors.isEmpty())
			return xi;

		//list of paths, max prob. path will be chosen based on max value within transitionProb
		ArrayList<Cell> possiblePaths = new ArrayList<Cell>();
		double transitionProb[] = new double[5];
		
		Cell parent;
		
		for (int i = 0; i < neighbors.size(); i++){
			parent = neighbors.get(i);

			possiblePaths.add(parent);
			transitionProb[i] = computeTransition(xi, parent, action) * parent.priorProb;
			//System.out.println("transitionProb["+i+"]: "+transitionProb[i]);

		}
		
		//System.out.println("Neighbors of ("+xi.x+", "+xi.y+")");
		/*
		for(int n=0; n<possiblePaths.size(); n++){
			System.out.print("("+possiblePaths.get(n).x+", "+possiblePaths.get(n).y+")");
		}
		System.out.println();
*/
		int index=0;
		double max=transitionProb[0];

		for (int j = 1;j < transitionProb.length; j++){
			//System.out.println("index: "+index+", transitionProb["+j+"]: "+transitionProb[j]+", max: "+max);
			if(transitionProb[j]>max){
				index=j;
				max=transitionProb[j];
			}
			//System.out.println("index: "+index+", transitionProb["+j+"]: "+transitionProb[j]+", max: "+max);
		}
		
		Cell next = possiblePaths.get(index);
		//next.parents.clear();
		//System.out.println("next: ("+next.x+", "+next.y+"), xi: ("+xi.x+", "+xi.y+")");
		/*for(int n = 0; n < xi.parents.size(); n++){
			next.parents.add(xi.parents.get(n));
		}
		next.parents.add(xi);
*/
		next.parents=xi.parents;
		next.parents.add(xi);
		
	/*	
		System.out.println("next is: ("+next.x+", "+next.y+"). Parents are: ");
		for(int n = next.parents.size(); n > 0; n--){
			System.out.print("("+next.parents.get(n-1).x+", "+next.parents.get(n-1).y+")");
		}
		System.out.println();
		*/
		return next;
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
				transitionModel = 0.1;
			}
		}

		//System.out.println("transitionModel: " + transitionModel);

		return transitionModel;
	}
	
	//returns String containing coordinates of highest prob. path
	public String viterbiPath(Cell cell, int step){
			
		ArrayList<Cell> path = new ArrayList<Cell>();
		/*
		for(int i = 0; i < step; i++){
			path.add(cell);
			cell=cell.parent;
		}
		*/
		String pathCoords = "";
		/*
		for(int j = step-1; j >= 0; j--){
			pathCoords = pathCoords + "("+path.get(j).x+", "+path.get(j).y+")";
			if(j!=0)
				pathCoords = pathCoords + ", ";
		}
		System.out.println("pathCoords: '"+pathCoords+"'");
		*/
		
		path = findMaxParents(cell, step);
		
		for(int j = step-1; j>=0; j--){
			pathCoords = pathCoords + "("+path.get(j).x+", "+path.get(j).y+")";
			if(j!=0)
				pathCoords = pathCoords + ", ";
			System.out.println("pathCoords: '"+pathCoords+"'");
		}
		
			
		return pathCoords;
	}
	
	public ArrayList<Cell> findMaxParents(Cell cell, int step){
		
		ArrayList<Cell> path = new ArrayList<Cell>();
		
		path.add(cell);
		Cell maxParent = cell;
		for(int i = 0; i < step-1; i++){
			maxParent = maxParent(cell); 
			path.add(maxParent);
		}
		
		return path;
	}
	
	public Cell maxParent(Cell cell){
		
		Cell max = cell.parents.get(0);
		
		for(int i = 1; i < cell.parents.size(); i++){
			if(cell.parents.get(i).currentProb > max.currentProb){
				max = cell.parents.get(i);
			}
		}
		
		return max;
	}
	//-----------------------------
}
