package server_main;

import java.io.IOException;

import server.ChatServer;

public class Server {

    /**
     * Start a chat server.
     */
    public static void main(String[] args)  {
        ChatServer chatServer;
        try {
            if (args.length == 1 && args[0].matches("\\d+")) {
                chatServer = new ChatServer(Integer.parseInt(args[0]));
            }
            else {
                chatServer = new ChatServer();
            }
            chatServer.start();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

