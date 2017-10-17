package model;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Grid {


	private int LINE_START = 8;
	private int LINE_SPACING = 7;
	private int HOR_LINE_END;
	private int VER_LINE_END;
	private int LOOP_START = 1;
	private int HOR_LOOP_END;
	private int VER_LOOP_END;
	private int CELL_WIDTH_HEIGHT = 7;
	private double LINE_WIDTH = 1.0;

	int numRows;
	int numColumns;
	Map map;
	GroundTruth truth;

	public Grid (GroundTruth truth){
		numRows = truth.numColumns;
		numColumns = truth.numRows;
		map = truth.map;
		this.truth = truth;

		HOR_LINE_END = LINE_START + (LINE_SPACING * numRows) - 1;
		VER_LINE_END = LINE_START + (LINE_SPACING * numColumns) - 1;
		HOR_LOOP_END = numRows + 2;
		VER_LOOP_END = HOR_LOOP_END;

	}

	private void drawGrid(GraphicsContext gc){
		gc.setStroke(Color.GRAY);
        gc.setLineWidth(LINE_WIDTH);

        //Draw vertical lines
        for (int i = LOOP_START; i < VER_LOOP_END; i++){
        	gc.strokeLine(i * LINE_SPACING, LINE_START, i * LINE_SPACING, VER_LINE_END);
        }
        //Draw horizontal lines
        for (int i = LOOP_START; i < HOR_LOOP_END; i++){
        	gc.strokeLine(LINE_START, i * LINE_SPACING, HOR_LINE_END, i * LINE_SPACING);
        }
	}

	//makes heatmap
	private void drawBlockedCells(GraphicsContext gc){

		gc.setFill(Color.BLACK);

		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				if (map.cells[i][j].terrain == 'b'){
					gc.fillRect((i + 1) * LINE_SPACING, (j + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
				}
			}
		}
	}

	private void drawCellProbs(int step, GraphicsContext gc){


		for (int i = 0; i < numRows; i++){
			for (int j = 0; j < numColumns; j++){
				if (map.cells[i][j].terrain != 'b'){
					System.out.println("prob: " + map.cells[i][j].currentProb);
					gc.setFill(Color.rgb(255, 51, 51, map.cells[i][j].currentProb));
					gc.fillRect((i + 1) * LINE_SPACING, (j + 1) * LINE_SPACING, CELL_WIDTH_HEIGHT, CELL_WIDTH_HEIGHT);
				}
			}
		}
	}

	//shows current location of truth agent
	private void drawTruthCell(int step, GraphicsContext gc){

	}


	public void drawFiltering(int step, GraphicsContext gc){
		drawBlockedCells(gc);
		drawGrid(gc);
		drawCellProbs(0, gc);
		//drawTruthCell(gc);
	}

	public void drawViterbi(int step, GraphicsContext gc){

	}
}
