package controller;

import model.*;
import view.GameScreen;
import sounds.Sound;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Game implements Runnable, KeyListener {
    public static final Dimension DIM = new Dimension(500, 700);
    public static final int THRESHOLD = 2400;
    public static int nAutoDelay = 300;
    public static final int TETRONIMO_NO = 100;
    private GameScreen gmsScreen;
    public static Random R = new Random();
    public final static int ANIMATION_DELAY = 45;
    private static Game instance;
    private long lastRestartTime = 0;
    private static final long RESTART_COOLDOWN = 500; // 500ms cooldown

    //Gameplay Threads
    private Thread threadAnimation;
    private Thread threadAutoDown;
    private Thread threadLoaded;
    private long playTime;
    private long lTimeStep;
    final static int INPUT_DELAY = 40;
    private boolean bMuted = true;
    private boolean isRestarting = false;

    private final int PAUSE = 80, // p key
            QUIT = 81, // q key
            LEFT = 37, // move piece left; left arrow
            RIGHT = 39, // move piece right; right arrow
            START = 83, // s key
            MUTE = 77, // m-key
            DOWN = 40, // move piece down; down arrow
            UP = 38, // rotate piece; up arrow
            SPACE = 32, // hard drop
            NORMAL = 49, // 1 key for normal mode
            HARD = 50;   // 2 key for hard mode

    private Clip clipBGM;
    private Clip clipBomb;

    public Game(){
        gmsScreen = new GameScreen(DIM);
        gmsScreen.addKeyListener(this);
        clipBGM = Sound.clipForLoopFactory("tetris_tone_loop_1_.wav");
        instance = this;
    }

    public static Game getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try{
                Game game = new Game();
                game.callThreads();
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void callThreads(){
        if(threadAnimation == null){
            threadAnimation = new Thread(this);
            threadAnimation.start();
        }
        if(threadAutoDown == null){
            threadAutoDown = new Thread(this);
            threadAutoDown.start();
        }

        if(!GameLogic.getInstance().isbLoaded() && threadLoaded == null){
            threadLoaded = new Thread(this);
            threadLoaded.start();
        }
    }

    public void restartGame() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRestartTime < RESTART_COOLDOWN || isRestarting) {
            return; // Ignore restart if too soon or already restarting
        }
        
        isRestarting = true;
        lastRestartTime = currentTime;
        
        try {
            // Stop all existing threads first
            stopThreads();
            
            // Reset the game state
            GameLogic.getInstance().clearBoard();
            GameLogic.getInstance().initGame();
            GameLogic.getInstance().setbPlaying(true);
            GameLogic.getInstance().setbPaused(false);
            GameLogic.getInstance().setbGameOver(false);
            GameLogic.getInstance().setbRestarted(true);
            
            // Reset the screen
            gmsScreen.resetGame();
            
            // Create new pieces
            gmsScreen.tetronimoCurrent = createNewTetronimo();
            gmsScreen.tetronimoOnDeck = createNewTetronimo();
            
            // Reset time tracking
            lTimeStep = System.currentTimeMillis();
            playTime = System.currentTimeMillis();
            
            // Start new threads
            callThreads();
            
            // Reset music if not muted
            if(!bMuted){
                if(clipBGM.isRunning()) {
                    clipBGM.stop();
                }
                clipBGM.setFramePosition(0);
                clipBGM.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } finally {
            isRestarting = false;
        }
    }

    private void stopThreads() {
        if(threadAnimation != null) {
            threadAnimation.interrupt();
            try {
                threadAnimation.join(100); // Wait for thread to die
            } catch (InterruptedException e) {
                // Ignore
            }
            threadAnimation = null;
        }
        
        if(threadAutoDown != null) {
            threadAutoDown.interrupt();
            try {
                threadAutoDown.join(100); // Wait for thread to die
            } catch (InterruptedException e) {
                // Ignore
            }
            threadAutoDown = null;
        }
    }

    private void restartThreads() {
        stopThreads();
        callThreads();
    }

    public void startGame(){
        gmsScreen.tetronimoCurrent = createNewTetronimo();
        gmsScreen.tetronimoOnDeck = createNewTetronimo();

        GameLogic.getInstance().clearBoard();
        GameLogic.getInstance().initGame();
        GameLogic.getInstance().setbPlaying(true);
        GameLogic.getInstance().setbPaused(false);
        GameLogic.getInstance().setbGameOver(false);
        
        // Restart threads when starting game
        restartThreads();
        
        if(!bMuted){
            clipBGM.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void run(){
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        long lStartTime = System.currentTimeMillis();
        if(!GameLogic.getInstance().isbLoaded() && Thread.currentThread() == threadLoaded){
            GameLogic.getInstance().setbLoaded(true);
        }

        while(Thread.currentThread() == threadAutoDown){
            if(!GameLogic.getInstance().isbPaused() && GameLogic.getInstance().isbPlaying()){
                tryMovingDown();
            }
            gmsScreen.repaint();
            try{
                lStartTime += nAutoDelay;
                Thread.sleep(Math.max(0, lStartTime - System.currentTimeMillis()));
            } catch (InterruptedException e){
                break;
            }
        }

        while (Thread.currentThread() == threadAnimation){
            if(!GameLogic.getInstance().isbPaused() && GameLogic.getInstance().isbPlaying()){
                updateGrid();
            }

            gmsScreen.repaint();

            try{
                lStartTime += ANIMATION_DELAY;
                Thread.sleep(Math.max(0, lStartTime - System.currentTimeMillis()));
            } catch (InterruptedException e){
                break;
            }
        }
    }

    private void updateGrid(){
        gmsScreen.grid.setbBlock(gmsScreen.tetronimoCurrent);
    }

    private void tryMovingDown(){
        Tetronimo tetronimotest = gmsScreen.tetronimoCurrent.cloneTetronimo();
        tetronimotest.moveDown();
        if(gmsScreen.grid.requestDown(tetronimotest)){
            gmsScreen.tetronimoCurrent.moveDown();
            tetronimotest = null;
        }

        //else if bomb

        else if(GameLogic.getInstance().isbPlaying()){
            gmsScreen.grid.addToOccupied(gmsScreen.tetronimoCurrent);
            gmsScreen.grid.checkTopRow();
            gmsScreen.grid.checkCompletedRow();
            gmsScreen.tetronimoCurrent = gmsScreen.tetronimoOnDeck;
            gmsScreen.tetronimoOnDeck = createNewTetronimo();
            tetronimotest = null;
        } else {
            tetronimotest = null;
        }
    }

    private void hardDrop() {
        while (true) {
            Tetronimo tetronimotest = gmsScreen.tetronimoCurrent.cloneTetronimo();
            tetronimotest.moveDown();
            if (gmsScreen.grid.requestDown(tetronimotest)) {
                gmsScreen.tetronimoCurrent.moveDown();
                tetronimotest = null;
            } else {
                if (GameLogic.getInstance().isbPlaying()) {
                    gmsScreen.grid.addToOccupied(gmsScreen.tetronimoCurrent);
                    gmsScreen.grid.checkTopRow();
                    gmsScreen.grid.checkCompletedRow();
                    gmsScreen.tetronimoCurrent = gmsScreen.tetronimoOnDeck;
                    gmsScreen.tetronimoOnDeck = createNewTetronimo();
                }
                break;
            }
        }
    }

    private Tetronimo createNewTetronimo() {
        int nKey = R.nextInt(TETRONIMO_NO);
        if (nKey >= 0 && nKey <= 14) {
            return new LongPiece();
        } else if (nKey > 14 && nKey <= 28) {
            return new SquarePiece();
        } else if (nKey > 28 && nKey <= 42) {
            return new SPiece();
        } else if (nKey > 42 && nKey <= 56) {
            return new TPiece();
        } else if (nKey > 56 && nKey <= 70) {
            return new ZPiece();
        } else if (nKey > 70 && nKey <= 84) {
            return new LPiece();
        } else if (nKey > 84 && nKey <= 98) {
            return new JYPiece();
        } else {
            return new Bomb(); //REPLACE WITH POWERUPS
        }
    }

    private static void stopLoopingSounds(Clip... clpClips){
        for (Clip clp : clpClips) {
        clp.stop();
    }
    }

    @Override
    public void keyPressed(KeyEvent event){
        playTime = System.currentTimeMillis();
        int nKeyPressed = event.getKeyCode();
        
        // Handle restart key (R)
        if(nKeyPressed == KeyEvent.VK_R) {
            restartGame();
            return;
        }

        // Handle difficulty selection before game starts
        if (!GameLogic.getInstance().isbPlaying() && GameLogic.getInstance().isbLoaded()) {
            if (nKeyPressed == NORMAL) {
                GameLogic.getInstance().setHardMode(false);
                System.out.println("Normal mode selected");
            } else if (nKeyPressed == HARD) {
                GameLogic.getInstance().setHardMode(true);
                System.out.println("Hard mode selected");
            }
        }

        if(nKeyPressed == START && GameLogic.getInstance().isbLoaded() && !GameLogic.getInstance().isbPlaying()){
            startGame();
        }

        if(nKeyPressed == PAUSE & playTime > lTimeStep + INPUT_DELAY){
            boolean wasPaused = GameLogic.getInstance().isbPaused();
            GameLogic.getInstance().setbPaused(!wasPaused);
            
            // If unpausing, restart threads
            if (wasPaused) {
                restartThreads();
            }
            
            lTimeStep = System.currentTimeMillis();
        }

        if(nKeyPressed == QUIT && playTime > lTimeStep + INPUT_DELAY){
            System.exit(0);
        }

        if(nKeyPressed == MUTE && playTime > lTimeStep + INPUT_DELAY) {
            bMuted = !bMuted;
            if(bMuted) {
                if(clipBGM != null) clipBGM.stop();
            } else {
                if(clipBGM != null) {
                    clipBGM.setMicrosecondPosition(0);
                    clipBGM.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
            lTimeStep = System.currentTimeMillis();
        }

        if(nKeyPressed == SPACE && GameLogic.getInstance().isbPlaying() && !GameLogic.getInstance().isbPaused()) {
            hardDrop();
            lTimeStep = System.currentTimeMillis();
        }

        if(nKeyPressed == DOWN && (playTime > lTimeStep + INPUT_DELAY - 35) && GameLogic.getInstance().isbPlaying()){
            tryMovingDown();
            lTimeStep = System.currentTimeMillis();
        }
        if (nKeyPressed == RIGHT && playTime > lTimeStep + INPUT_DELAY) {
            Tetronimo tetronimoTest = gmsScreen.tetronimoCurrent.cloneTetronimo();
            tetronimoTest.moveRight();
            if (gmsScreen.grid.requestLateral(tetronimoTest)) {
                gmsScreen.tetronimoCurrent.moveRight();
                tetronimoTest = null;
                lTimeStep = System.currentTimeMillis();
            } else {
                tetronimoTest = null;
            }
        }
        if (nKeyPressed == LEFT && playTime > lTimeStep + INPUT_DELAY) {
            Tetronimo tetronimoTest = gmsScreen.tetronimoCurrent.cloneTetronimo();
            tetronimoTest.moveLeft();
            if (gmsScreen.grid.requestLateral(tetronimoTest)) {
                gmsScreen.tetronimoCurrent.moveLeft();
                tetronimoTest = null;
                lTimeStep = System.currentTimeMillis();
            } else {
                tetronimoTest = null;
            }
        }
        // up = rotate clockwise
        if (nKeyPressed == UP) {
            Tetronimo tetronimoTest = gmsScreen.tetronimoCurrent.cloneTetronimo();
            tetronimoTest.rotate();
            if (gmsScreen.grid.requestLateral(tetronimoTest)) {
                gmsScreen.tetronimoCurrent.rotate();
                tetronimoTest = null;
                lTimeStep = System.currentTimeMillis();
            } else {
                tetronimoTest = null;
            }
        }
    }
    @Override
    // Needed because of KeyListener implementation
    public void keyReleased(KeyEvent e) {

    }

    @Override
    // Needed because of KeyListener implementation
    public void keyTyped(KeyEvent e) {
    }
}
