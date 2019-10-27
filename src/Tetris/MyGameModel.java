package Tetris;

import Coordinator.Coordinator;
import static Coordinator.Coordinator.FALL_FREQUENZY;
import java.awt.event.ActionEvent;
import java.beans.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;

/**
 * @author Pontus Söderlund
 */
public class MyGameModel implements Model, Cloneable, ActionListener {

    //***********************
    // Variables
    //***********************
    private LinkedList<Integer> numberList;     //A list of all the upcomming numbers
    private final PropertyChangeSupport pcs;    //The PropertyChangeSupport
    public Board board;
    private Timer updateTimer;

    private int score;
    private int numberOfMoves;
    private int nextPieceId;
    private boolean lost;
    private String name;
    
    private int numberOfPieces;
    private boolean randomNumbers;
    private static boolean generateRandomNumber;

    //***********************
    // Constructor(s)
    //***********************
    /**
     * Creates a new MyGameModel with Board if true else without Board.
     *
     * @param randomNumbers
     * @param numberOfPieces
     * @param b with or without board
     */
    public MyGameModel(boolean randomNumbers, int numberOfPieces, String name) {
        this.pcs = new PropertyChangeSupport(this);
        this.numberList = new LinkedList<>();
        this.numberOfPieces = numberOfPieces;
        this.randomNumbers = randomNumbers;
        this.board = new Board();
        this.updateTimer = new Timer(FALL_FREQUENZY, this);
        this.lost = false;
        this.name = name;
        generateNumbers();
    }

    /**
     * Creates a board where all the parameters have to be defined.
     *
     * @param pcs
     * @param board
     * @param score
     * @param numberOfMoves
     */
    public MyGameModel(PropertyChangeSupport pcs, Board board, int score, int numberOfMoves) {
        this.pcs = pcs;
        this.board = board;
        this.score = score;
        this.numberOfMoves = numberOfMoves;
        generateNumbers();
    }

    //***********************
    // Board Methods
    //***********************
    /**
     * Moves or rotates the current piece if possible and fires a a PropertyChangeEvent if
     * the "thinking" variable is false.
     *
     * @param direction Which direction to move/rotate
     * @param thinking Whether or not the Genetic Algorithm is making all possible moves
     */
    public void movement(String direction, boolean thinking) {
        int[][] old = board.getBoard().clone();

        if (board.move(direction)) {
            incremenntMoves();
            if (direction.equals("down")) {
                increaseScore(2);
            }
        }
        if (!thinking) {
            pcs.firePropertyChange("movement", old, board.getBoard());
        } else {
            pcs.firePropertyChange("thinkingMovement", old, board.getBoard());
        }
    }

    /**
     * Generates a new piece with the specified id if it does not cause a conflict.
     *
     * @param id the id of the piece to be generated
     */
    public void generatePiece(int id) {
        int[][] b = board.getBoard().clone();
        if (board.canGenerateNewPiece(id)) {
            board.generatePiece(id);
        }
    }

    /**
     * Moves all currently selected tiles down one step and increases the score by one.
     * Fires a PropertyChangeEvent if the drawable is set to true
     *
     * @return false if it cant fall, else true.
     */
    public boolean fall() {
        int[][] old = board.getBoard().clone();
        if (board.canMove("down")) {
            board.move("down");
            increaseScore(1);
        } else {
            return false;
        }
        incremenntMoves();
        pcs.firePropertyChange("fall", old, board.getBoard());
        return true;
    }

    /**
     * Clears all the rows that are clearable and increases the score by the appropiate
     * amount (depending of number of rows cleared). Fires a ProprtyChangeEvent if
     * drawable is true.
     */
    public void clearRows() {
        MyGameModel old = this.clone();
        switch (getBoard().clearRows()) {
            case 1:
                increaseScore(100);
                break;
            case 2:
                increaseScore(300);
            case 3:
                increaseScore(600);
                break;
            case 4:
                increaseScore(1200);
                break;
        }
        pcs.firePropertyChange("clearRows", old, this);
    }

    //***********************
    // Support Methods
    //***********************
    /**
     * Fills the numberList with numbers from the pseudoRandomNumbers.txt file or random
     * numbers.
     */
    private void generateNumbers() {
        numberList = new LinkedList<>();
        if (!randomNumbers) {
            try {
                Scanner numberReader = new Scanner(new File("pseudoRandomNumbers.txt"));
                for (int i = 0; i < numberOfPieces; i++) {
                    numberList.add(numberReader.nextInt());
                }
            } catch (FileNotFoundException e) {
                System.out.println("404: File Not Found");
            }
        } else {
            for (int i = 0; i < numberOfPieces; i++) {
                numberList.add((int) (1 + Math.random() * 7));
            }
        }
    }

    /**
     * Toggles the timer on or off
     */
    public void toggleTimer() {
        if (updateTimer.isRunning()) {
            updateTimer.stop();
        } else {
            updateTimer.start();
        }
    }

    /**
     * The player loses and the relevant data (name, score, speed, date and number of
     * moves) is appended to the Scores.txt file.
     */
    public void lost() {
        pcs.firePropertyChange("lost", lost, true);
        lost = true;
        
        try {
            try (Writer w = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(new File("Score.txt"), true), "UTF-8"))) {
                w.append(System.lineSeparator() + "Score: " + score
                        + "\t Name: " + name + "\t Moves: " + numberOfMoves);
                w.close();
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    /**
     * Resets the current MyGameModel. Fires a PropertyChangeEvent if drawable is true.
     */
    public void reset() {
        MyGameModel old = this.clone();
        board = new Board();
        score = 0;
        numberOfMoves = 0;
        generateNumbers();

        pcs.firePropertyChange("reset", old, this);
    }

    /**
     * Increases the score by the specified amount.
     *
     * @param score the amount to increase with.
     */
    public void increaseScore(int score) {
        this.score += score;
    }

    /**
     * Increases the number of moves by one.
     */
    public void incremenntMoves() {
        this.numberOfMoves++;
    }

    /**
     * Clones the current MyGameModel and returns an immutable version.
     *
     * @return and immutable copy of this MyGameModel.
     */
    @Override
    public MyGameModel clone() {
        Board boa = board.clone();
        Integer sco = ((Integer) score);
        Integer nom = ((Integer) numberOfMoves);

        MyGameModel m;
        m = new MyGameModel(pcs, boa, sco, nom);
        return m;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (!lost) {
            if (!fall()) {
                board.deselectPiece();
                clearRows();
                if (getBoard().canGenerateNewPiece(getNextPieceId())) {
                    generatePiece(getNextIdAndRemove());
                } else {
                    lost();
                }
            }
        }
    }

    //***********************
    // Getter Methods
    //***********************
    public Board getBoard() {
        return board;
    }

    public int getScore() {
        return score;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    /**
     * Returns the element at index 0 of the numberList.
     *
     * @return the index of the piece id.
     */
    public int getNextPieceId() {
        return numberList.peek();
    }

    /**
     * Returns the element at index 0 of the numberList and removes it.
     *
     * @return the index of the piece id.
     */
    public int getNextIdAndRemove() {
        return numberList.pop();
    }

    public boolean isUpdateTimerRunning() {
        return updateTimer.isRunning();
    }

    public boolean isLost() {
        return lost;
    }

    //***********************
    // Setter Methods
    //***********************
    public void setBoard(Board board) {
        this.board = board;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setNumberOfMoves(int numberOfMoves) {
        this.numberOfMoves = numberOfMoves;
    }

    public void setGenerateRandomNumber(boolean generateRandomNumber) {
        this.generateRandomNumber = generateRandomNumber;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    //***********************
    // Property Support Methods
    //***********************
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
        pcs.addPropertyChangeListener(propertyName, l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
        pcs.addPropertyChangeListener(propertyName, l);
    }

}
