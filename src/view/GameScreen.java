package view;
import controller.Game;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameScreen extends Panel {
    private Dimension dimOff;
    private Image imgOff;
    private Graphics grpOff;
    public Grid grid = new Grid();
    private GameFrame gmf;
    private Font font = new Font("Monospaced", Font.PLAIN, 12);
    private Font fontBig = new Font("Monospaced", Font.PLAIN + Font.ITALIC, 36);
    private final Color retroGreen = new Color(0xd3f0cb);
    private final Color borderGreen = new Color(0x2E8B57); // Slightly darker green for border
    private final Color customBackground = new Color(0x55552e);
    private FontMetrics fontMetrics;
    private int nFontWidth;
    private int nFontHeight;
    private String strDisplay = "";
    public Tetronimo tetronimoOnDeck;
    public Tetronimo tetronimoCurrent;
    private Timer timer;

    public GameScreen(Dimension dimension){
        gmf = new GameFrame();
        gmf.getContentPane().add(this);
        gmf.pack();
        initView();

        gmf.setSize(dimension);
        gmf.setTitle("Old School Tetris");
        gmf.setResizable(true);
        gmf.setVisible(true);
        this.setFocusable(true);

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    restartGame();
                }
            }
        });
    }

    private void drawScore(Graphics graphics){
        graphics.setColor(Color.white);
        graphics.setFont(font);
        if(GameLogic.getInstance().getbScore() != 0){
            graphics.drawString(" SCORE : " + GameLogic.getInstance().getbScore() + "     HIGH SCORE :  " + GameLogic.getInstance().getbHighScore(), nFontWidth, nFontHeight);
        } else {
            graphics.drawString("SCORE: 0" + "HIGH SCORE: " + GameLogic.getInstance().getbHighScore(), nFontWidth, nFontHeight);
        }
    }

    @SuppressWarnings("unchecked")
    public void update(Graphics graphics){
        //Logging
        System.out.println("Game State: Playing=" + GameLogic.getInstance().isbPlaying() +
                ", Paused=" + GameLogic.getInstance().isbPaused() +
                ", Restarted=" + GameLogic.getInstance().isbRestarted());



        Dimension dimension = this.getSize();
        if(grpOff == null || dimension.width != dimOff.width || dimension.height != dimOff.height){
            dimOff = dimension;
            imgOff = createImage(dimension.width, dimension.height);
            grpOff = imgOff.getGraphics();
        }

        grpOff.setColor(customBackground);

        grpOff.fillRect(0,0, dimension.width, dimension.height);
        grpOff.setColor(Color.white);
        grpOff.setFont(font);

        // Handle restarted state
        if (GameLogic.getInstance().isbRestarted()) {
            GameLogic.getInstance().setbRestarted(false); // Reset the state
            // Ensure proper rendering of the grid and tetromino after restarting
            drawGameElements(dimension);
            drawTetronimo(grpOff, tetronimoCurrent, dimension);
            return; // Exit early for this frame
        }

        if(GameLogic.getInstance().isbGameOver()){
            displayTextOnScreen();
        } else if (!GameLogic.getInstance().isbPlaying()){
            strDisplay = "OLD SCHOOL TETRIS";
            if(!GameLogic.getInstance().isbLoaded()){
                strDisplay = "Loading beatsies...";
                grpOff.drawString(strDisplay, (dimension.width - fontMetrics.stringWidth(strDisplay))/2, dimension.height / 4 );
            } else {
                displayStartText();
            }
        } else if(GameLogic.getInstance().isbPaused()) {
            // Draw "Game Paused" text
            strDisplay = "Game Paused";
            grpOff.drawString(
                    strDisplay,
                    (Game.DIM.width - fontMetrics.stringWidth(strDisplay)) / 2,
                    Game.DIM.height / 4
            );

            // Add "Press 'R' to Restart" below "Game Paused"
            String restartText = "Press 'R' to Restart";
            grpOff.drawString(
                    restartText,
                    (Game.DIM.width - fontMetrics.stringWidth(restartText)) / 2,
                    (Game.DIM.height / 4) + nFontHeight + 20
            );
        }

        else {
            int nBy = (dimension.height - 150) / Grid.ROWS;
            int nBx = (dimension.width - 150) / Grid.COLS;

            // Render the current tetromino before drawing the grid
            if (tetronimoCurrent != null) {
                drawTetronimo(grpOff, tetronimoCurrent, dimension);
            }

            // Draw the grid
            grpOff.setColor(Color.WHITE); // Grid lines color
            for (int i = 0; i <= Grid.ROWS; i++) {
                int y = i * nBy + 150; // Adjust for top margin
                grpOff.drawLine(0, y, nBx * Grid.COLS, y); // Horizontal lines
            }
            for (int j = 0; j <= Grid.COLS; j++) {
                int x = j * nBx;
                grpOff.drawLine(x, 150, x, nBy * Grid.ROWS + 150); // Vertical lines
            }

            // Render blocks on the grid
            Block[][] block = grid.getbBlock();
            for (int i = 0; i < block.length; i++) {
                for (int j = 0; j < block[0].length; j++) {
                    grpOff.setColor(block[i][j].getColor());
                    grpOff.fill3DRect(j * nBx, i * nBy + 150, nBx, nBy, true);
                }
            }

            // Draw next piece preview box
            grpOff.setColor(Color.white);
            grpOff.drawString("Next Piece:", dimension.width - 140, 30);
            grpOff.draw3DRect(dimension.width - 140, 40, 120, 120, true);

            // Draw the next piece
            if (tetronimoOnDeck != null) {
                boolean[][] nextPieceShape = tetronimoOnDeck.getColoredSquares(tetronimoOnDeck.getbOrientation());
                grpOff.setColor(tetronimoOnDeck.getbColor());
                
                // Calculate center position for the preview piece
                int previewBlockSize = 20;
                int startX = dimension.width - 100;
                int startY = 80;
                
                for (int i = 0; i < nextPieceShape.length; i++) {
                    for (int j = 0; j < nextPieceShape[i].length; j++) {
                        if (nextPieceShape[i][j]) {
                            grpOff.fill3DRect(
                                startX + (j * previewBlockSize),
                                startY + (i * previewBlockSize),
                                previewBlockSize,
                                previewBlockSize,
                                true
                            );
                        }
                    }
                }
            }
        }

        drawScore(grpOff);
        grpOff.drawString("Press 'R' to Restart", nFontWidth, nFontHeight + 20);

        graphics.drawImage(imgOff, 0, 0, this);
    }


    //helper function
    private void drawGameElements(Dimension dimension) {
        int nBy = (dimension.height - 150) / Grid.ROWS;
        int nBx = (dimension.width - 150) / Grid.COLS;
        Block[][] block = grid.getbBlock();

        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[0].length; j++) {
                grpOff.setColor(block[i][j].getColor());
                grpOff.fill3DRect(j * nBx, i * nBy + 150, nBx, nBy, true);
            }
        }

        grpOff.setColor(Color.white);
        grpOff.draw3DRect(dimension.width - 150, 0, 150, dimension.height, true);
        grpOff.draw3DRect(dimension.width - 140, 10, 130, 130, true );
        boolean[][] lts = tetronimoOnDeck.getColoredSquares(tetronimoOnDeck.getbOrientation());
        Color color = tetronimoOnDeck.getbColor();
        for (int i = 0; i < Grid.DIM; i++) {
            for (int j = 0; j < Grid.DIM; j++) {
                if(lts[j][i]){
                    grpOff.setColor(color);
                    grpOff.fill3DRect(i * nBx + 360, j * nBy + 20, nBx, nBy, true);
                }
            }
        }
        drawScore(grpOff);
    }

    private void drawTetronimo(Graphics grpOff, Tetronimo tetronimo, Dimension dimension) {
        boolean[][] bC = tetronimo.getColoredSquares(tetronimo.getbOrientation());
        Color color = tetronimo.getbColor();
        int nBy = (dimension.height - 150) / Grid.ROWS;
        int nBx = (dimension.width - 150) / Grid.COLS;

        System.out.println("Drawing tetromino at Row: " + tetronimo.bRow + ", Col: " + tetronimo.bCol);

        for (int i = tetronimo.bCol; i < tetronimo.bCol + Grid.DIM; i++) {
            for (int j = tetronimo.bRow; j < tetronimo.bRow + Grid.DIM; j++) {
                if (bC[j - tetronimo.bRow][i - tetronimo.bCol]) {
                    grpOff.setColor(color);
                    grpOff.fill3DRect(i * nBx, j * nBy + 150, nBx, nBy, true);
                    System.out.println("Block at Grid[" + j + "][" + i + "] drawn.");
                }
            }
        }
    }



    private void initView(){
        Graphics graphics = getGraphics();
        graphics.setFont(font);
        fontMetrics = graphics.getFontMetrics();
        nFontWidth = fontMetrics.getMaxAdvance();
        nFontHeight = fontMetrics.getHeight();
        graphics.setFont(fontBig);
    }

    private void displayStartText() {
        String[] instructions = {
                "OLD SCHOOL TETRIS",
                "Use Arrow Keys to Move Pieces",
                "Use Space Bar to Rotate Piece",
                "[S] to Start",
                "[P] to Pause",
                "[Q] to Quit",
                "[M] to Mute or Play Music"
        };
        int yOffset = Game.DIM.height / 4;
        for (String line : instructions) {
            grpOff.drawString(line, (Game.DIM.width - fontMetrics.stringWidth(line)) / 2, yOffset);
            yOffset += nFontHeight + 20;
        }
    }

    private void restartGame() {
        // Reset the grid
        grid = new Grid();
        
        // Reset game state but keep playing
        GameLogic.getInstance().setbPlaying(true);
        GameLogic.getInstance().setbPaused(false);
        GameLogic.getInstance().setbGameOver(false);
        GameLogic.getInstance().setbRestarted(true);
        GameLogic.getInstance().initGame();
        
        // Create new tetrominos
        tetronimoCurrent = new LongPiece();  // Start with a new piece
        tetronimoOnDeck = new SquarePiece(); // Set up next piece
        
        // Force a repaint
        repaint();
    }

    private void displayTextOnScreen() {
        String[] gameOverText = {
                "GAME OVER",
                "Use Arrow Keys to Move Pieces",
                "Use Space Bar to Rotate Piece",
                "[S] to Start",
                "[P] to Pause",
                "[Q] to Quit",
                "[R] to Restart",
                "[M] to Mute or Play Music"
        };
        int yOffset = Game.DIM.height / 4;
        for (String line : gameOverText) {
            grpOff.drawString(line, (Game.DIM.width - fontMetrics.stringWidth(line)) / 2, yOffset);
            yOffset += nFontHeight + 20;
        }
    }

    public GameFrame getFrame(){return this.gmf;}
    public void setFrame(GameFrame frame){this.gmf = frame;}
}
