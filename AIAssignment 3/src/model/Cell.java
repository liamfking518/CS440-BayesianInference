package model;

import java.util.ArrayList;

public class Cell {

	public int x;
	public int y;
	char terrain;
	double currentProb;
	double priorProb;
	public ArrayList<Cell> parents;
	public Cell parent;

	public Cell(int x, int y, char terrain){
		this.x = x;
		this.y = y;
		this.terrain = terrain;
		this.parents = new ArrayList<Cell>();
		this.parent=null;
	}

	public Cell(int x, int y, char terrain, double initialProb){
		this.x = x;
		this.y = y;
		this.terrain = terrain;
		this.currentProb = initialProb;
		this.priorProb = initialProb;
		this.parents = new ArrayList<Cell>();
		this.parent=null;
	}

	public void setInitialProb(double prob){
		this.currentProb = prob;
		this.priorProb = prob;
	}

}
