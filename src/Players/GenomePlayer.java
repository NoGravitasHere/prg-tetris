/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Players;

import Tetris.Board;
import Tetris.GameView;
import Tetris.MyGameModel;
import ga.Genome;
import ga.Move;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pontus.soderlund
 */
public class GenomePlayer extends Player {

    //***********************
    // Variables
    //***********************    
    private Genome genome;
    private MyGameModel model;
    private int delay;
    private int actionDelay;

    //***********************
    // Constructor
    //***********************    
    public GenomePlayer(MyGameModel model, GameView view, Genome g, String name, int delay, int actionDelay) {
        super(model, view, true, name);
        super.thread = new Thread(this);
        this.genome = g;
        this.model = model;
        this.delay = delay;
        this.actionDelay = actionDelay;
        model.generatePiece(model.getNextIdAndRemove());
    }
    
    //***********************
    // Methods
    //***********************    
    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(delay);
                makeNextMove();
                model.generatePiece(model.getNextIdAndRemove());
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * Makes the best possible move.
     */
    public void makeNextMove() {
        ArrayList<Move> possibleMoves = getPossibleMoves();
        Collections.sort(possibleMoves);

        Move nextMove = new Move();
        if (!possibleMoves.isEmpty()) {
            nextMove = possibleMoves.get(0);
        } else {
            model.lost();
        }

        for (int i = 0; i < nextMove.getNumberOfMoves(); i++) {
            switch (nextMove.getMoves().charAt(i)) {
                case 'w':
                    super.move("rotate", false);
                    break;
                case 'a':
                    super.move("left", false);
                    break;
                case 's':
                    super.move("down", false);
                    break;
                case 'd':
                    super.move("right", false);
                    break;
            }
            try {
                setScore(model.getScore());
                setMoves(model.getNumberOfMoves());
                String s = streamIn.readUTF();
                updateScores(s);
                sendData(super.toString());
            } catch (IOException ex) {
                System.out.println(ex);
            }

            genome.incrementMovesTaken();
            try {
                Thread.sleep(actionDelay);
            } catch (InterruptedException ex) {
            }
        }

        model.getBoard().deselectPiece();
        model.clearRows();
    }

    /**
     * Puts all the possible moves and their value (calculated by using the current
     * genomes values) in an ArrayList containing Moves.
     *
     * @return An ArrayList with all possible moves.
     */
    public ArrayList<Move> getPossibleMoves() {
        MyGameModel saveState = model.clone();

        ArrayList<Move> moves = new ArrayList<>();

        for (int rotations = 0; rotations < 4; rotations++) {
            for (int i = -5; i <= 5; i++) {
                Move m = new Move("", 0);
                String s = "";
                double moveScore = 0.0;

                loadState(saveState);

                for (int j = 0; j < rotations; j++) {
                    super.move("rotate", true);
                    m.move('w');
                }
                if (0 > i) {
                    for (int j = 0; j < Math.abs(i); j++) {
                        if (model.getBoard().move("left")) {
                            m.move('a');
                        }
                    }
                } else if (0 < i) {
                    for (int j = 0; j < Math.abs(i); j++) {
                        if (model.getBoard().move("right")) {
                            m.move('d');
                        }
                    }
                }

                while (model.getBoard().move("down")) {
                    m.move('s');
                }

                model.getBoard().deselectPiece();
                moveScore += genome.getBumpiness() * model.getBoard().getBumpiness();
                moveScore += genome.getCumulativeHeight() * model.getBoard().getTotalHeight();
                moveScore += genome.getHoles() * model.getBoard().getNumberOfHoles();
                moveScore += genome.getRowsCleared() * model.getBoard().getNumberOfLinesCleared();
                moveScore += genome.getWeightedHeight() * model.getBoard().getHeightOfHighestCollumn();

                if (!model.getBoard().canGenerateNewPiece(model.getNextPieceId())) {
                    moveScore -= 100;
                }

                m.setMoveScore(moveScore);
                moves.add(m);
            }
        }

        loadState(saveState);
        return moves;
    }

    /**
     * Reloads the current saveState into a previous.
     * @param saveState the state to be loaded.
     */
    public void loadState(MyGameModel saveState) {
        model.setBoard(saveState.getBoard().clone());
        model.setScore(saveState.getScore());
        model.setNumberOfMoves(saveState.getNumberOfMoves());
    }

}
