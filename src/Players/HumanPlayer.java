package Players;

import Tetris.GameView;
import Tetris.MyGameModel;
import java.awt.event.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author pontus.soderlund
 */
public class HumanPlayer extends Player {

    //***********************
    // Constructor
    //***********************    
    public HumanPlayer(MyGameModel model, GameView view, String name) {
        super(model, view, false, name);
        super.thread = new Thread(this);
        addKeyBindings(view);
    }

    //***********************
    // Methods
    //***********************    
    @Override
    public void start() {
        thread.start();
    }

    /**
     * Starts the player thread. Continually updates the score and server data.
     */
    @Override
    public void run() {
        model.generatePiece(model.getNextIdAndRemove());
        model.toggleTimer();

        while (!Thread.interrupted()) {
            try {
                sendData(super.toString());

                String s = streamIn.readUTF();
                updateScores(s);

                setScore(model.getScore());
                setMoves(model.getNumberOfMoves());

                Thread.sleep(Coordinator.Coordinator.FALL_FREQUENZY);
            } catch (InterruptedException ex) {
                break;
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Adds key bindings to the frame
     */
    private void addKeyBindings(JComponent comp) {
        //Movement
        addKeyBinding(comp, KeyEvent.VK_W, "rotate", evt -> {
            super.move("rotate", true);
        });
        addKeyBinding(comp, KeyEvent.VK_A, "moveLeft", evt -> {
            super.move("left", true);
        });
        addKeyBinding(comp, KeyEvent.VK_S, "moveDown", evt -> {
            super.move("down", true);
        });
        addKeyBinding(comp, KeyEvent.VK_D, "moveRight", evt -> {
            super.move("right", true);
        });
    }

}
