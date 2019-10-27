package Coordinator;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

public class Server implements Runnable {

    //***********************
    // Variables
    //***********************    
    public Thread t = new Thread(this);
    public static int port;
    public static ArrayList<ClientManager> clientManagers;
    private static String allPlayersData;
    private ServerSocket listeningSocket;

    //***********************
    // Constructor
    //***********************    
    public Server() throws UnknownHostException, IOException {
        clientManagers = new ArrayList<>();
        allPlayersData = "";

        port = 9001;
        listeningSocket = new ServerSocket(port);

        t.start();
    }
    
    //***********************
    // Methods
    //***********************    
    /**
     * Compiles player data from all clients into one string.
     * @return 
     */
    public static String allPlayersData() {
        allPlayersData = "";
        for (ClientManager clientManager : clientManagers) {
            allPlayersData += clientManager.getPlayerData();
            allPlayersData += " <br>";
        }
        return allPlayersData;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = listeningSocket.accept();
                System.out.println(clientSocket.getInetAddress().getHostName() + " connected.");
                clientManagers.add(new ClientManager(clientSocket));
            } catch (IOException ex) {
            }
        }
    }
}
