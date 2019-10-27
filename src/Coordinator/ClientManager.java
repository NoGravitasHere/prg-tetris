package Coordinator;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientManager implements Runnable {

    //***********************
    // Variables
    //***********************    
    private String playerData;
    public Thread t;
    private Socket socket;
    private DataInputStream streamIn;
    private DataOutputStream streamOut;

    //***********************
    // Constructor(s)
    //***********************
    public ClientManager(Socket s) throws IOException {
        socket = s;
        t = new Thread(this);
        streamIn = new DataInputStream(socket.getInputStream());
        streamOut = new DataOutputStream(socket.getOutputStream());
        t.start();
    }

    //***********************
    // Methods
    //***********************    
    /**
     * Fetches the linked players data continually as well as writing all the data from
     * the server to the player
     */
    @Override
    public void run() {
        while (true) {
            try {
                streamOut.writeUTF(Server.allPlayersData());
                playerData = streamIn.readUTF();
            } catch (IOException ex) {
                break; //User disconnected
            }
        }

        try {
            System.out.println(socket.getInetAddress().getHostName() + " disconnected.");
            socket.close();
        } catch (IOException e) {
        }
    }

    public String getPlayerData() {
        return playerData;
    }
}
