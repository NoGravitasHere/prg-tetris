package Coordinator;

import Players.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import Tetris.*;
import ga.Genome;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * @author pontus.soderlund
 */
public final class Coordinator extends JFrame {

    /**
     * Det är inte perfekt men det är good enough så att säga.
     */
    //***********************
    // Variables
    //***********************
    public static final int FALL_FREQUENZY = 150;
    public static final Dimension STANDARD_DIMENSIONS = new Dimension(300, 600);
    public static final Color BACKROUND_COLOR = new Color(230, 230, 230);
    public static final Color GRID_COLOR = new Color(100, 100, 100);

    private Writer generationWriter;
    private Writer eliteWriter;
    private Timer updateTimer;

    private ArrayList<Player> players;

    //***********************
    // Constructor(s)
    //***********************
    public Coordinator() {
        //Create server
        try {
            Server server = new Server();
        } catch (UnknownHostException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        //Create players
        players = new ArrayList<>();
        String values1 = "0.10652812530473066, 0.5407461311664348, 0.5464303011913769, 0.9794551203322194, 0.1532341656912941";
        String values2 = "0.1740785301672163, 0.9111742619045178, 0.7033566525084033, 0.17584857016174538, 0.006136556371977742";
        Genome g1 = new Genome(values1);
        Genome g2 = new Genome(values2);

        String name = requestUsername();

        addNewPlayer(name);
        addNewPlayer("HAL", g1);
//        addNewPlayer("SAL", g2);

        for (int i = 0; i < players.size(); i++) {
            players.get(i).setupConnection("localhost", 9001);
            players.get(i).connectToServer();
            players.get(i).start();
        }
    }

    /**
     * Requests that the player submits a valid username. If it's not valid the name will
     * be "Dave".
     *
     * @return The username.
     */
    private String requestUsername() {
        JPanel frame = new JPanel();
        JLabel lblName = new JLabel("Name: ");
        frame.add(lblName);

        String name = JOptionPane.showInputDialog(frame);

        if (invalidName(name)) {
            try {
                throw new InvalidNameException(name);
            } catch (InvalidNameException ex) {
                JLabel a = new JLabel(ex.toString());
                JOptionPane.showMessageDialog(null, ex);
                name = "Dave";
            }
        }
        return name;
    }

    /**
     * Checks if the name is valid
     *
     * @param name the name
     * @return true if its valid else fale.
     */
    private boolean invalidName(String name) {
        if (name == null) {
            return true;
        }
        if (name.equals("HAL") || name.equals("SAL")) {
            return true;
        } else if (name.equals("") || name.equals("null") || name.equals(null)) {
            return true;
        } else {
            return false;
        }
    }

    //***********************
    // Main Methods
    //***********************
    /**
     * @param a the comand line arguments
     */
    public static void main(String[] a) {
        Coordinator c = new Coordinator();
    }

    /**
     * Adds a new human player
     */
    private void addNewPlayer(String name) {
        MyGameModel model = new MyGameModel(false, 10000, name);
        GameView view = new GameView(model, true, STANDARD_DIMENSIONS, STANDARD_DIMENSIONS);

        HumanPlayer humanPlayer = new HumanPlayer(model, view, name);

        view.setDrawable(true);
        players.add(humanPlayer);
    }

    /**
     * Adds a new AI player
     *
     * @param genome the genome the ai should use.
     */
    private void addNewPlayer(String name, Genome genome) {
        MyGameModel model = new MyGameModel(false, 10000, name);
        GameView view = new GameView(model, true, STANDARD_DIMENSIONS, STANDARD_DIMENSIONS);

        GenomePlayer genomePlayer = new GenomePlayer(model, view, genome, name, 0, 25);

        view.setDrawable(true);
        players.add(genomePlayer);
    }
}
