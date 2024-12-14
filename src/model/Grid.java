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
    synchronized public void checkCompletedRow(){
        LinkedList<Integer> fullRowItems = new LinkedList<>();
        LinkedList<Block> repositioningItems = new LinkedList<>();
        int rowsCleared = 0;

        int nRows = Grid.ROWS - 1;
        while(nRows >= 0){
            fullRowItems.clear();
            // Count blocks in current row
            for (int i = bOccupiedBlocks.size() - 1; i >= 0; i--) {
                Block block = (Block) bOccupiedBlocks.get(i);
                if(block.getbRow() == nRows){
                    fullRowItems.add(i);
                }
            }

            if (fullRowItems.size() == Grid.COLS) {
                rowsCleared++;
                // Remove all blocks in the full row
                for (Integer index : fullRowItems) {
                    Block block = (Block) bOccupiedBlocks.remove(index.intValue());
                    GameLogic.getInstance().addbScore(block.getPOINTVALUE());
                }

                // Update high score if needed
                if(GameLogic.getInstance().getbScore() > GameLogic.getInstance().getbHighScore()){
                    GameLogic.getInstance().setbHighScore(GameLogic.getInstance().getbScore());
                }
                GameLogic.getInstance().checkbThreshold();

                // Move all blocks above this row down by one position
                for (int i = bOccupiedBlocks.size() - 1; i >= 0; i--) {
                    Block block = (Block) bOccupiedBlocks.get(i);
                    if (block.getbRow() < nRows) {
                        bOccupiedBlocks.remove(i);
                        block.setbRow(block.getbRow() + rowsCleared);
                        repositioningItems.add(block);
                    }
                }
            } else {
                nRows--;
            }
        }

        // Add back all repositioned blocks
        while(!repositioningItems.isEmpty()) {
            bOccupiedBlocks.add(repositioningItems.removeLast());
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

//    synchronized public void setbBlock(Tetronimo tetronimo) {
//        boolean[][] bC = tetronimo.getColoredSquares(tetronimo.bOrientation);
//        Color color = tetronimo.bColor;
//
//        // Reset the grid's blocks to default state
//        for (int i = 0; i < ROWS; i++) {
//            for (int j = 0; j < COLS; j++) {
//                if (bBlock[i][j] == null || !bBlock[i][j].isOccupied()) {
//                    // Only reset blocks that are not occupied
//                    bBlock[i][j] = new Block(false, Color.black, i, j); // Default black background
//                }
//            }
//        }
//
//        // Map Tetronimo blocks onto the grid
//        for (int i = tetronimo.bCol; i < tetronimo.bCol + DIM; i++) {
//            for (int j = tetronimo.bRow; j < tetronimo.bRow + DIM; j++) {
//                // Ensure Tetronimo blocks are within bounds
//                if (i >= 0 && i < COLS && j >= 0 && j < ROWS) {
//                    if (bC[j - tetronimo.bRow][i - tetronimo.bCol]) {
//                        bBlock[j][i] = new Block(true, color, j, i); // Occupied block
//                    }
//                }
//            }
//        }
//
//        // Restore occupied blocks that are part of the static grid
//        for (Object bOccupiedBlock : bOccupiedBlocks) {
//            Block b = (Block) bOccupiedBlock;
//            try {
//                // Ensure blocks are within bounds before adding them
//                if (b.getbRow() >= 0 && b.getbRow() < ROWS && b.getbCol() >= 0 && b.getbCol() < COLS) {
//                    bBlock[b.getbRow()][b.getbCol()] = new Block(true, b.getColor(), b.getbRow(), b.getbCol());
//                }
//            } catch (NullPointerException e) {
//                System.err.println("NullPointerException in setbBlock: " + e.getMessage());
//            }
//        }
//    }



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
