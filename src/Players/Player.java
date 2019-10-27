/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Players;

import Tetris.GameView;
import Tetris.MyGameModel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public abstract class Player extends JFrame implements Runnable, Comparable<Player> {

    //***********************
    // Variables
    //***********************    
    MyGameModel model;
    GameView view;
    private JLabel lblPlayers;
    private ArrayList<Player> players;

    protected Thread thread;
    private boolean machine;
    private String name;
    private int score;
    private int moves;

    String serverAddress;
    int serverPort;
    Socket so;
    DataInputStream streamIn;
    DataOutputStream streamOut;

    //***********************
    // Constructor(s)
    //***********************
    public Player(MyGameModel model, GameView view, boolean machine, String name) {
        this.model = model;
        this.view = view;
        this.machine = machine;
        this.score = 0;
        this.moves = 0;
        this.name = name;
        this.lblPlayers = new JLabel();
        this.players = new ArrayList<>();

        addKeyBindings();

        Dimension dim = Coordinator.Coordinator.STANDARD_DIMENSIONS;
        double d = 0.05;
        EmptyBorder emptyBorder = new EmptyBorder((int) (d * dim.getWidth()),
                (int) (d * dim.getHeight()), (int) (d * dim.getWidth()),
                (int) (d * dim.getHeight()));

        lblPlayers.setBorder(emptyBorder);
        lblPlayers.setPreferredSize(dim);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(view);
        p.add(lblPlayers);
        add(p);

        pack();
        setVisible(true);
    }

    //***********************
    // Main
    //***********************
    @Override
    public void run() {
    }

    public abstract void start();

    //***********************
    // Server
    //***********************
    /**
     * Makes the client ready to connect to the specified server.
     *
     * @param serverAddress
     * @param serverPort
     */
    public void setupConnection(String serverAddress, int serverPort) {
        so = new Socket();
        streamIn = null;
        streamOut = null;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (so.isConnected()) {
                    try {
                        so.close();

                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });
    }

    /**
     * Connects the player to the server.
     */
    public void connectToServer() {
        try {
            so = new Socket(serverAddress, serverPort);
            streamIn = new DataInputStream(so.getInputStream());
            streamOut = new DataOutputStream(so.getOutputStream());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (NumberFormatException ex) {
            System.out.println("Wrong format. Try again.");
        }
    }

    /**
     * Sends a string to the server.
     * @param s the string.
     */
    public void sendData(String s) {
        if (streamIn != null && streamOut != null) {
            try {
                streamOut.writeUTF(s);
            } catch (IOException ex) {
            }
        } else {
            System.out.println("Connection not established.");
        }
    }

    //***********************
    // Misc
    //***********************
    public void move(String direction, boolean thinking) {
        model.movement(direction, thinking);
    }

    public void quit() {
        System.exit(0);
    }

    public void pause() {
        model.toggleTimer();
    }

    public void updateScores(String s) {
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 20);
        lblPlayers.setFont(font);
        lblPlayers.setText("<html> <h1><b>Scoreboard</b></h1> <h3>" + s + "</h3>");

    }

    /**
     * Adds key bindings to the frame
     */
    private void addKeyBindings() {
        addKeyBinding(view, KeyEvent.VK_ESCAPE, "quit", evt -> {
            System.exit(0);
        });
        addKeyBinding(view, KeyEvent.VK_P, "pause", evt -> {
            model.toggleTimer();
        });
    }

    /**
     * Adds a key binding to the specifed component.
     *
     * @param comp the component to add to.
     * @param keyCode the key to take action to.
     * @param id the name of the binding.
     * @param lambda the action to trigger.
     */
    public void addKeyBinding(JComponent comp, int keyCode, String id, ActionListener lambda) {
        InputMap im = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap ap = comp.getActionMap();

        im.put(KeyStroke.getKeyStroke(keyCode, 0, false), id);
        ap.put(id, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lambda.actionPerformed(e);
            }
        });
    }

    @Override
    public String toString() {
        return "Name: " + name + " Score: " + score + "\t Moves: " + moves;
    }

    @Override
    public int compareTo(Player t) {
        return this.score - t.score;
    }

    //***********************
    // Getters
    //***********************
    public int getScore() {
        return score;
    }

    public int getMoves() {
        return moves;
    }

    @Override
    public String getName() {
        return name;
    }

    public MyGameModel getModel() {
        return model;
    }

    //***********************
    // Setters
    //***********************
    public void setScore(int score) {
        this.score = score;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
