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

    //Gameplay Threads
    private Thread threadAnimation;
    private Thread threadAutoDown;
    private Thread threadLoaded;
    private long playTime;
    private long lTimeStep;
    final static int INPUT_DELAY = 40;
    private boolean bMuted = true;

    private final int PAUSE = 80, // p key
            QUIT = 81, // q key
            LEFT = 37, // move piece left; left arrow
            RIGHT = 39, // move piece right; right arrow
            START = 83, // s key
            MUTE = 77, // m-key
            DOWN = 40, // move piece faster down
            SPACE = 32; // rotate piece

    private Clip clipBGM;
    private Clip clipBomb;

    public Game(){
        gmsScreen = new GameScreen(DIM);
        gmsScreen.addKeyListener(this);
        clipBGM = Sound.clipForLoopFactory("tetris_tone_loop_1_.wav");
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

    private void startGame(){
        gmsScreen.tetronimoCurrent = createNewTetronimo();
        gmsScreen.tetronimoOnDeck = createNewTetronimo();

        GameLogic.getInstance().clearBoard();
        GameLogic.getInstance().initGame();
        GameLogic.getInstance().setbPlaying(true);
        GameLogic.getInstance().setbPaused(false);
        GameLogic.getInstance().setbGameOver(false);
        if(!bMuted){
            clipBGM.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private Tetronimo createNewTetronimo() {
        int nKey = R.nextInt(TETRONIMO_NO);
        if (nKey >= 0 && nKey <= 12) {
            return new LongPiece();
        } else if (nKey > 12 && nKey <= 23) {
            return new SquarePiece();
        } else if (nKey > 23 && nKey <= 35) {
            return new SPiece();
        } else if (nKey > 35 && nKey <= 46) {
            return new TPiece();
        } else if (nKey > 46 && nKey <= 58) {
            return new ZPiece();
        } else if (nKey > 58 && nKey <= 71) {
            return new LPiece();
        } else if (nKey > 71 && nKey <= 84) {
            return new JYPiece();
        } else if (nKey > 84 && nKey <= 98) {
            return new PlusPiece();
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
        if(nKeyPressed == START && GameLogic.getInstance().isbLoaded() && !GameLogic.getInstance().isbPlaying()){
            startGame();
        }

        if(nKeyPressed == PAUSE & playTime > lTimeStep + INPUT_DELAY){
            GameLogic.getInstance().setbPaused(!GameLogic.getInstance().isbPaused());
            lTimeStep = System.currentTimeMillis();
        }

        if(nKeyPressed == QUIT && playTime > lTimeStep + INPUT_DELAY){
            System.exit(0);
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
        // space = rotate
        if (nKeyPressed == SPACE) {
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
        if (nKeyPressed == MUTE) {
            if (!bMuted) {
                stopLoopingSounds(clipBGM);

                bMuted = !bMuted;
            } else {
                clipBGM.loop(Clip.LOOP_CONTINUOUSLY);
                bMuted = !bMuted;
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
