package model;

import controller.Game;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private long bHighScore;
    private int bThreshold;
    private long bScore;
    private boolean bPlaying = false;
    private boolean bPaused = false;
    private boolean bGameOver = false;
    private boolean bLoaded = false;
    private boolean bRestarted = false;
    private boolean isHardMode = false;
    private int rowsCleared = 0;
    private static final int ROWS_FOR_SPEED_INCREASE = 4;
    private static final int NORMAL_SPEED_INCREASE = 15;
    private static final int HARD_SPEED_INCREASE = 30;

    private List<Movable> moveTetronimoes = new ArrayList<>(300);
    private GameOperationsList operationsList = new GameOperationsList();

    private static GameLogic instance = null;

    private GameLogic(){}
    public void initGame(){
        setbScore(0);
        setbThreshold(2400);
    }

    public long getbHighScore() {
        return bHighScore;
    }

    public void setbHighScore(long bHighScore) {
        this.bHighScore = bHighScore;
    }

    public int getbThreshold() {
        return bThreshold;
    }

    public void setbThreshold(int bThreshold) {
        this.bThreshold = bThreshold;
    }
    public void checkbThreshold(){
        if(bScore > bThreshold && Game.nAutoDelay > 30){
            bThreshold += Game.THRESHOLD;
            Game.nAutoDelay -= 15;
        }
    }

    public long getbScore() {
        return bScore;
    }


    public void setbScore(long bScore) {
        this.bScore = bScore;
    }

    public void addbScore(long bScore){this.bScore += bScore;}

    public boolean isbPlaying() {
        return bPlaying;
    }

    public void setbPlaying(boolean bPlaying) {
        this.bPlaying = bPlaying;
    }

    public boolean isbPaused() {
        return bPaused;
    }

    public void setbPaused(boolean bPaused) {
        this.bPaused = bPaused;
    }

    public boolean isbRestarted() {
        return bRestarted;
    }

    public void setbRestarted(boolean bRestarted) {
        this.bRestarted = bRestarted;
    }

    public boolean isbLoaded() {
        return bLoaded;
    }

    public void setbLoaded(boolean bLoaded) {
        this.bLoaded = bLoaded;
    }

    public boolean isbGameOver() {
        return bGameOver;
    }

    public void setbGameOver(boolean bGameOver) {
        this.bGameOver = bGameOver;
    }

    public List<Movable> getMoveTetronimoes() {
        return moveTetronimoes;
    }

    public void setMoveTetronimoes(List<Movable> moveTetronimoes) {
        this.moveTetronimoes = moveTetronimoes;
    }

    public GameOperationsList getOperationsList() {
        return operationsList;
    }

    public void clearBoard(){moveTetronimoes.clear();}

    public static GameLogic getInstance() {
        if (instance == null){
            instance = new GameLogic();
        }
        return instance;
    }

    public void setHardMode(boolean hardMode) {
        this.isHardMode = hardMode;
        this.rowsCleared = 0;
        // Reset speed when changing difficulty
        Game.nAutoDelay = 500;
    }

    public boolean isHardMode() {
        return isHardMode;
    }

    public void addRowCleared() {
        rowsCleared++;
        if (rowsCleared >= ROWS_FOR_SPEED_INCREASE) {
            rowsCleared = 0;
            if (isHardMode && Game.nAutoDelay > 50) {
                Game.nAutoDelay -= HARD_SPEED_INCREASE;
            } else if (!isHardMode && Game.nAutoDelay > 100) {
                Game.nAutoDelay -= NORMAL_SPEED_INCREASE;
            }
        }
    }

    // Reset game state
    public void resetGame() {
        bScore = 0;         
        bGameOver = false;
        bPlaying = true;
        bPaused = false;
        bRestarted = true;
        rowsCleared = 0;

        // Clear any remaining moving tetrominos
        moveTetronimoes.clear();

        // Reinitialize difficulty threshold and speed based on mode
        bThreshold = 2400;
        Game.nAutoDelay = 500;
    }

    public static void setInstance(GameLogic instance) {
        GameLogic.instance = instance;
    }
}
