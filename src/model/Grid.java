package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Grid {
    public static final int ROWS = 20;
    public static final int COLS = 12;
    public static final int DIM = 4;
    private final Color customBackground = new Color(0x55552e);


    Block[][] bBlock;
    ArrayList bOccupiedBlocks;

    public Grid(){
        this.bBlock = new Block[ROWS][COLS];
        //
        this.bOccupiedBlocks = new ArrayList();
    }

    public Block[][] getbBlock() {
        return bBlock;
    }

    synchronized public void initializeBlocks(){
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                bBlock[i][j] = new Block(false, Color.blue, i, j);
            }
        }
    }

    synchronized public boolean requestDown(Tetronimo tetronimo){
        boolean[][] bC;
        bC = tetronimo.getColoredSquares(tetronimo.bOrientation);
        for (int i = tetronimo.bCol; i < tetronimo.bCol + DIM; i++) {
            for (int j = tetronimo.bRow; j < tetronimo.bRow + DIM; j++) {
                if(bC[j - tetronimo.bRow][i - tetronimo.bCol] && j >= Grid.ROWS){
                    return false;
                }
                if(bC[j - tetronimo.bRow][i - tetronimo.bCol] && bBlock[j][i].isOccupied()){
                    return false;
                }
            }
        }
        return true;
    }

    synchronized public void addToOccupied(Tetronimo tetronimo){
        boolean[][] bC;
        bC = tetronimo.getColoredSquares(tetronimo.bOrientation);
        Color color = tetronimo.bColor;
        for (int i = tetronimo.bCol; i < tetronimo.bCol + DIM; i++) {
            for (int j = tetronimo.bRow; j < tetronimo.bRow + DIM; j++) {
                if(bC[j - tetronimo.bRow][i - tetronimo.bCol]){
                    bOccupiedBlocks.add(new Block(true, color, j, i));
                }
            }
        }
    }

    synchronized public void clearGrid() {
        initializeBlocks(); // Reinitialize the grid's blocks
        bOccupiedBlocks.clear(); // Clear all occupied blocks
        System.out.println("Grid cleared and reinitialized.");
    }


    synchronized public void checkTopRow(){
        for(Object bOccupiedBlock : bOccupiedBlocks){
            Block block = (Block) bOccupiedBlock;
            if(block.getbRow() <= 0){
                GameLogic.getInstance().setbPlaying(false);
                GameLogic.getInstance().setbGameOver(true);
                clearGrid();
            }
        }
    }
    synchronized public void checkCompletedRow() {
        int rowsCleared = 0;
        boolean[] rowFull = new boolean[ROWS];
        int[] blocksInRow = new int[ROWS];

        // First, count blocks in each row
        for (Object obj : bOccupiedBlocks) {
            Block block = (Block) obj;
            blocksInRow[block.getbRow()]++;
        }

        // Mark which rows are full
        for (int row = 0; row < ROWS; row++) {
            if (blocksInRow[row] == COLS) {
                rowFull[row] = true;
                rowsCleared++;
            }
        }

        if (rowsCleared > 0) {
            // Award points for cleared rows
            GameLogic.getInstance().addbScore(100 * rowsCleared);
            
            // Notify GameLogic about cleared rows for difficulty adjustment
            for (int i = 0; i < rowsCleared; i++) {
                GameLogic.getInstance().addRowCleared();
            }

            // Update high score if needed
            if (GameLogic.getInstance().getbScore() > GameLogic.getInstance().getbHighScore()) {
                GameLogic.getInstance().setbHighScore(GameLogic.getInstance().getbScore());
            }
            GameLogic.getInstance().checkbThreshold();

            // Create a new list for the remaining blocks
            ArrayList newBlocks = new ArrayList();

            // Process each block
            for (Object obj : bOccupiedBlocks) {
                Block block = (Block) obj;
                int row = block.getbRow();

                // Skip blocks in cleared rows
                if (rowFull[row]) {
                    continue;
                }

                // Calculate how many rows below this block were cleared
                int rowsBelow = 0;
                for (int r = row + 1; r < ROWS; r++) {
                    if (rowFull[r]) {
                        rowsBelow++;
                    }
                }

                // If there were rows cleared below this block, move it down
                if (rowsBelow > 0) {
                    block.setbRow(row + rowsBelow);
                }

                newBlocks.add(block);
            }

            // Replace the old blocks list with the new one
            bOccupiedBlocks = newBlocks;
        }
    }

    synchronized public void setbBlock(Tetronimo tetronimo){
        boolean[][] bC;
        bC = tetronimo.getColoredSquares(tetronimo.bOrientation);
        Color color = tetronimo.bColor;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                bBlock[i][j] = new Block(false, customBackground, i, j);
            }
        }
        for (int i = tetronimo.bCol; i < tetronimo.bCol + DIM; i++) {
            for (int j = tetronimo.bRow; j < tetronimo.bRow + DIM; j++) {
                if(bC[j - tetronimo.bRow][i - tetronimo.bCol]){
                    bBlock[j][i] = new Block(false, color, j - tetronimo.bRow, i - tetronimo.bCol);
                }
            }
        }
        for(Object bOccupiedBlock : bOccupiedBlocks){
            Block b = (Block) bOccupiedBlock;
            try{
                bBlock[b.getbRow()][b.getbCol()] = new Block(true, b.getColor(), b.getbRow(), b.getbCol());
            } catch (NullPointerException e){
                break;
            }
        }
    }

    synchronized public boolean requestLateral(Tetronimo tetronimo){
        boolean[][] bC;
        bC = tetronimo.getColoredSquares(tetronimo.bOrientation);

        for (int i = tetronimo.bCol; i < tetronimo.bCol + DIM ; i++) {
            for (int j = tetronimo.bRow; j < tetronimo.bRow + DIM; j++) {
                if(bC[j - tetronimo.bRow][i - tetronimo.bCol] && (i < 0|| i >= Grid.COLS || j >= Grid.ROWS)){
                    return false;
                }
                if(bC[j - tetronimo.bRow][i - tetronimo.bCol] && bBlock[j][i].isOccupied()){
                    return false;
                }
            }
        }
        return true;
    }
}
