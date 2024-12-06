package model;

import java.awt.*;

public class PlusPiece extends Tetronimo{
    public PlusPiece() {
        super();
        initialize();
        bColor = Color.pink;
    }

    public void initialize() {
        for (int i = 0; i < DIM; i++) {
            if (i == 0) {
                bColoredSquares[i][0][0] = false;
                bColoredSquares[i][0][1] = true;
                bColoredSquares[i][0][2] = false;
                bColoredSquares[i][0][3] = false;

                bColoredSquares[i][1][0] = true;
                bColoredSquares[i][1][1] = true;
                bColoredSquares[i][1][2] = true;
                bColoredSquares[i][1][3] = false;

                bColoredSquares[i][2][0] = false;
                bColoredSquares[i][2][1] = true;
                bColoredSquares[i][2][2] = false;
                bColoredSquares[i][2][3] = false;

                bColoredSquares[i][3][0] = false;
                bColoredSquares[i][3][1] = false;
                bColoredSquares[i][3][2] = false;
                bColoredSquares[i][3][3] = false;
            } else if (i == 1) {
                bColoredSquares[i][0][0] = false;
                bColoredSquares[i][0][1] = true;
                bColoredSquares[i][0][2] = false;
                bColoredSquares[i][0][3] = false;

                bColoredSquares[i][1][0] = true;
                bColoredSquares[i][1][1] = true;
                bColoredSquares[i][1][2] = true;
                bColoredSquares[i][1][3] = false;

                bColoredSquares[i][2][0] = false;
                bColoredSquares[i][2][1] = true;
                bColoredSquares[i][2][2] = false;
                bColoredSquares[i][2][3] = false;

                bColoredSquares[i][3][0] = false;
                bColoredSquares[i][3][1] = false;
                bColoredSquares[i][3][2] = false;
                bColoredSquares[i][3][3] = false;

            } else if (i == 2) {
                bColoredSquares[i][0][0] = false;
                bColoredSquares[i][0][1] = true;
                bColoredSquares[i][0][2] = false;
                bColoredSquares[i][0][3] = false;

                bColoredSquares[i][1][0] = true;
                bColoredSquares[i][1][1] = true;
                bColoredSquares[i][1][2] = true;
                bColoredSquares[i][1][3] = false;

                bColoredSquares[i][2][0] = false;
                bColoredSquares[i][2][1] = true;
                bColoredSquares[i][2][2] = false;
                bColoredSquares[i][2][3] = false;

                bColoredSquares[i][3][0] = false;
                bColoredSquares[i][3][1] = false;
                bColoredSquares[i][3][2] = false;
                bColoredSquares[i][3][3] = false;

            } else {
                bColoredSquares[i][0][0] = false;
                bColoredSquares[i][0][1] = true;
                bColoredSquares[i][0][2] = false;
                bColoredSquares[i][0][3] = false;

                bColoredSquares[i][1][0] = true;
                bColoredSquares[i][1][1] = true;
                bColoredSquares[i][1][2] = true;
                bColoredSquares[i][1][3] = false;


                bColoredSquares[i][2][0] = false;
                bColoredSquares[i][2][1] = true;
                bColoredSquares[i][2][2] = false;
                bColoredSquares[i][2][3] = false;

                bColoredSquares[i][3][0] = false;
                bColoredSquares[i][3][1] = false;
                bColoredSquares[i][3][2] = false;
                bColoredSquares[i][3][3] = false;

            }

        }
    }
}
