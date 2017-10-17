package application;

import java.io.File;
import java.util.ArrayList;

import model.Cell;
import model.Filterer;
import model.GroundTruth;
import model.Map;
import model.Viterbier;

public class TestBench {

	int dimension = 20;

	ArrayList<File> files;
	ArrayList<GroundTruth> truths;

	public TestBench(){

	}

	public void runTests(){

	}

	public void generateMaps(){
		int mapNumber = 0;
		int versionNumber = 0;

		files = new ArrayList<File>();
		GroundTruth t = null;
		truths = new ArrayList<GroundTruth>();

		Map[] maps = new Map[10];
		for (int i = 0; i < 10; i++){
			String fileName = "map" + mapNumber + "_v" + versionNumber;
			versionNumber++;
			maps[i] = new Map(dimension, dimension);
			t = new GroundTruth(maps[i]);
			File file = new File("test", fileName);
			if (file.exists()){
				file.delete();
				try {
					file.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			t.saveToFile(file);
			files.add(file);
			for (int j = 1; j < 10; j++){
				t = new GroundTruth(maps[i]);
				fileName = "map" + mapNumber + "_v" + versionNumber;
				versionNumber++;
				file = new File("test", fileName);
				if (file.exists()){
					file.delete();
					try {
						file.createNewFile();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				t.saveToFile(file);
				files.add(file);
			}
			mapNumber++;
			versionNumber = 0;
		}


	}


	public static void main(String[] args) {

        
			Cell [][] cells = new Cell[3][3];

			//initial state
			cells[0][0] = new Cell(0, 0, 'h',0.125);
			cells[0][1] = new Cell(0, 1, 'h',0.125);
			cells[0][2] = new Cell(0, 2, 't',0.125);
			cells[1][0] = new Cell(1, 0, 'n',0.125);
			cells[1][1] = new Cell(1, 1, 'n',0.125);
			cells[1][2] = new Cell(1, 2, 'n',0.125);
			cells[2][0] = new Cell(2, 0, 'n',0.125);
			cells[2][1] = new Cell(2, 1, 'b',0);
			cells[2][2] = new Cell(2, 2, 'h',0.125);
			
			Map testMap = new Map(cells);
			
			//actions
			char[] actions = {'r', 'r', 'd', 'd'};

			//observations
			char[] evidences = {'n', 'n', 'h', 'h'};

			Filterer filterer = new Filterer(testMap);
			Viterbier viterbier = new Viterbier(testMap);

			System.out.println("initial state: filter\n");
			filterer.printCells();

			for (int i = 0; i < 4; ++i){
				filterer.filter(actions[i], evidences[i]);
				System.out.println("t = " + (i + 1) + " action = " + actions[i] + " sensor = " + evidences[i]);
				System.out.println("-------------------------");
				filterer.printCells();
			}
			
			testMap.resetMap(cells);

			System.out.println("initial state: viterbi\n");
			filterer.printCells();
			
			for (int i = 0; i < 4; ++i){
				Cell path = viterbier.computeMaxPath(actions[i], evidences[i]);
				System.out.println("t = " + (i + 1) + " action = " + actions[i] + " sensor = " + evidences[i]);
				//String maxPath = viterbier.viterbiPath(path, viterbier.getStep()); will be used in actual alg
				String maxPath = viterbier.viterbiPath(path, i+1);	//test where steps=i
				System.out.println("max state: ("+path.x+", "+path.y+")");
				System.out.println("maxPath: "+maxPath);
				System.out.println("-------------------------");
				filterer.printCells();
			}
		
			

			/*
			Map map = new Map(20, 20);
			map.printMap();

			GroundTruth groundTruth = new GroundTruth(map);
			groundTruth.printGroundTruth();

			File file = new File("src\\truth");
			groundTruth.saveToFile(file);

			System.out.println("printing truth and map from file");
			GroundTruth gd = new GroundTruth(file);
			groundTruth.map.printMap();
			groundTruth.printGroundTruth();
			

			TestBench test = new TestBench();
			test.generateMaps();
			 */
		}


}
