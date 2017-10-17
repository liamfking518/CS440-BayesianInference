package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

public class GroundTruth {

	//coord of starting cell
	int x0;
	int y0;

	int numRows;
	int numColumns;
	Map map;

	Cell[] states;
	public char [] actions;
	public char [] evidences;

	Cell[][] cells;

	public GroundTruth(Map map){
		this.map = map;
		numRows = map.numRows;
		numColumns = map.numColumns;
		cells = map.cells;

		//determine starting cell
		Random rand = new Random();
		boolean blocking = true;
		while (blocking){
			int x = rand.nextInt(numRows);
			int y = rand.nextInt(numColumns);

			if (cells[x][y].terrain != 'b'){
				x0 = x;
				y0 = y;
				blocking = false;
			}
		}

		generateData();

	}

	public GroundTruth(File file){
		BufferedReader br = null;
		ArrayList<String> description = new ArrayList<String>();
		int count = 0;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null){
				description.add(line);
				//System.out.println("line read " + count);
				count++;
			}
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}

		parseStartCell(description);
		parseActions(description);
		parseEvidences(description);
		parseStatesAndMap(description);

	}

	private void parseStartCell(ArrayList<String> description){
		String[] line = description.get(0).split(" ");

		x0 = Integer.parseInt(line[0]);
		y0 = Integer.parseInt(line[1]);

	}

	private void parseActions(ArrayList<String> description){
		actions = new char[100];

		int index = 0;

		for (int i = 101; i < 201; i++){
			actions[index] = description.get(i).charAt(0);
			index++;
		}
	}

	private void parseEvidences(ArrayList<String> description){
		evidences = new char[100];

		int index = 0;

		for (int i = 201; i < 301; i++){
			evidences[index] = description.get(i).charAt(0);
			index++;
		}
	}

	private void parseStatesAndMap(ArrayList<String> description){
		map = new Map(description);
		cells = map.cells;
		numRows = map.numRows;
		numColumns = map.numColumns;
		states = new Cell[100];
		int index = 0;
		String[] line = null;

		for (int i = 1; i < 101; i++){
			line = description.get(i).split(" ");
			int x = Integer.parseInt(line[0]);
			int y = Integer.parseInt(line[1]);
			states[index] = cells[x][y];
			index++;
		}

	}

	private void generateData(){

		Cell currentCell = cells[x0][y0];

		states = new Cell[100];
		actions = new char[100];
		evidences = new char[100];


		int x = currentCell.x;
		int y = currentCell.y;

		int nextX = 0;
		int nextY = 0;

		Random rand = new Random();

		for (int i = 0; i < 100; i++){
			//determine viable neighbors
			//neighbors = map.findNeighbors(currentCell);

			//determine action
			char action = getAction();
			actions[i] = action;

			//determine the next cell
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
					System.out.println("generateData: cases not right");
			}

			//coords of move are out of bounds or in cell that is blocked
			if (x < 0 || x >= numRows || y < 0 || y >= numColumns || cells[x][y].terrain == 'b'){
				nextX = currentCell.x;
				nextY = currentCell.y;
			}
			else {
				int num = rand.nextInt(10);
				if (num == 0){
					nextX = currentCell.x;
					nextY = currentCell.y;
				}
				else {
					nextX = x;
					nextY = y;
					currentCell = cells[nextX][nextY];
				}
			}
			//is now nextCell
			states[i] = currentCell;

			//determine sensor of the next cell
			evidences[i] = getEvidence(currentCell);

			x = currentCell.x;
			y = currentCell.y;

		}

	}

	private char getAction(){
		Random rand = new Random();
		int num = rand.nextInt(4);
		//System.out.println("action random num: " + num);
		char action = 0;

		switch (num){
			case 0:
				action = 'u';
				break;
			case 1:
				action = 'd';
				break;
			case 2:
				action = 'l';
				break;
			case 3:
				action = 'r';
				break;
			default:
				System.out.println("getAction: error");
		}

		if (action == 0){
			System.out.println("getAction: action not set");
		}

		return action;
	}

	private char getEvidence(Cell currentCell){
		char evidence = 0;
		char terrain = currentCell.terrain;

		//System.out.println("terrain of cell just moved to: " + terrain);
		Random rand = new Random();
		int num = rand.nextInt(20);
		//System.out.println("random num: " + num);

		switch (terrain){
			case 'n':
				switch(num){
					case 0:
						evidence = 't';
						break;
					case 1:
						evidence = 'h';
						break;
					default:
						evidence = 'n';
				}
				break;
			case 't':
				switch(num){
					case 0:
						evidence = 'n';
						break;
					case 1:
						evidence = 'h';
						break;
					default:
						evidence = 't';
				}
				break;
			case 'h':
				switch(num){
					case 0:
						evidence = 'n';
						break;
					case 1:
						evidence = 't';
						break;
					default:
						evidence = 'h';
				}
				break;
			default:
				System.out.println("getEvidence: error");
		}
		//System.out.println("evidence of cell just moved to: " + evidence);
		return evidence;
	}

	public void printGroundTruth(){
		System.out.println("starting cell: " + x0  + " " + y0 + " " + cells[x0][y0].terrain);
		for (int i = 0; i < 100; i++){
			System.out.println("Action: " + actions[i]);
			System.out.println("Cell: " + states[i].x + " " + states[i].y + " " + states[i].terrain + " Evidence:" + evidences[i]);
		}
	}

	public void saveToFile(File file){
		ArrayList<String> contents = new ArrayList<String>();
		String startString = "";

		startString = startString.concat(Integer.toString(x0));
		startString = startString.concat(" ");
		startString = startString.concat(Integer.toString(y0));
		contents.add(startString);

		String statesString = null;

		for (int i = 0; i < 100; i++){
			statesString = "";
			statesString = statesString.concat(Integer.toString(states[i].x));
			statesString = statesString.concat(" ");
			statesString = statesString.concat(Integer.toString(states[i].y));
			//System.out.println("statesString: " + statesString);
			contents.add(statesString);
			//System.out.println("string added");
		}

		String actionsString = null;
		for (int i = 0; i < 100; i++){
			actionsString = "";
			actionsString = actionsString.concat(Character.toString(actions[i]));
			contents.add(actionsString);
		}

		String evidencesString = null;
		for (int i = 0; i < 100; i++){
			evidencesString = "";
			evidencesString = evidencesString.concat(Character.toString(evidences[i]));
			contents.add(evidencesString);
		}

		String mapString = null;
		for (int i = 0; i < numRows; i++){
			mapString = "";
			for (int j = 0; j < numColumns; j++){
				mapString = mapString.concat(Character.toString(cells[i][j].terrain));
				mapString = mapString.concat(" ");
			}
			contents.add(mapString);
		}


		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()), "utf-8"));
			for (int i = 0; i < contents.size(); i++){
				System.out.println("wrote line");
				writer.write(contents.get(i));
				writer.newLine();
			}
			writer.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
