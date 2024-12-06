package model;

import controller.Game;

import java.awt.*;

public class Tetronimo implements Movable{
    public final static int ORIENTATION = 4;
    public final static int DIM = 4;
    public int bRow;
    public int bCol;
    public int bOrientation;
    public Color bColor;
    public boolean[][][] bColoredSquares;

    public Tetronimo(){
        bCol = Game.R.nextInt(Grid.COLS - DIM);
        bOrientation = Game.R.nextInt(ORIENTATION);
        bColoredSquares = new boolean[ORIENTATION][DIM][DIM];
    }

    public static int getORIENTATION(){return ORIENTATION;}
    public int getbRow() {
        return bRow;
    }

    public void setbRow(int bRow) {
        this.bRow = bRow;
    }

    public int getbCol() {
        return bCol;
    }

    public void setbCol(int bCol) {
        this.bCol = bCol;
    }

    public int getbOrientation() {
        return bOrientation;
    }

    public void setbOrientation(int bOrientation) {
        this.bOrientation = bOrientation;
    }

    public Color getbColor() {
        return bColor;
    }

    public void setbColor(Color bColor) {
        this.bColor = bColor;
    }

    public boolean[][][] getbColoredSquares() {
        return bColoredSquares;
    }

    public boolean[][] getColoredSquares(int bOrientation){
        boolean[][] bC = new boolean[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                bC[i][j] = bColoredSquares[bOrientation][i][j];
            }
        }
        return bC;
    }

    public Tetronimo cloneTetronimo(){
        Tetronimo tetronimo = new Tetronimo();
        tetronimo.bRow = bRow;
        tetronimo.bCol = bCol;
        tetronimo.bOrientation = bOrientation;
        tetronimo.bColor = bColor;
        tetronimo.bColoredSquares = bColoredSquares;
        return  tetronimo;
    }

    public void setbColoredSquares(boolean[][][] bColoredSquares) {
        this.bColoredSquares = bColoredSquares;
    }

    @Override
    public void moveLeft() {
        bCol--;
    }

    @Override
    public void moveRight() {
        bCol++;
    }

    @Override
    public void moveDown() {
        bRow++;
    }

    @Override
    public void rotate() {
        if(bOrientation >= 3){
            bOrientation = 0;
        }else {
            bOrientation++;
        }
    }
}
