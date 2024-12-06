package model;

import java.awt.*;

public class Block {
    private boolean bOccupied;
    private Color bColor;
    private int bRow;
    private int bCol;
    private static int POINTVALUE = 100;

    public boolean isOccupied() {
        return bOccupied;
    }

    public void setOccupied(boolean bOccupied) {
        this.bOccupied = bOccupied;
    }

    public Color getColor() {
        return bColor;
    }

    public void setColor(Color bColor) {
        this.bColor = bColor;
    }

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

    public int getPOINTVALUE() {
        return POINTVALUE;
    }

    public static void setPOINTVALUE(int POINTVALUE) {
        Block.POINTVALUE = POINTVALUE;
    }

    public Block(boolean bOccupied, Color color, int nRow, int nCol) {
        this.bOccupied = bOccupied;
        this.bColor = color;
        this.bRow = nRow;
        this.bCol = nCol;
    }
}
