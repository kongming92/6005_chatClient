package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import user.User;

/**
 * Server object for the IM program.
 * 
 * Threadsafe argument:
 * The server is threadsafe because the only fields that are modified by multiple threads are the 
 * online, roomList, and userThreads. Each of these is instantiated as a synchronizedMap as is thus
 * a threadsafe datatype. Furthermore, functions that make function calls on these maps are
 * synchronized on the Server's lock.
 */

public class ChatServer{
    
    private final Map<String,User> online;  // username to User object
    private final Map<Integer,Room> roomList;   // room number to Room object
    private final Map<User,Thread> userThreads;     // User object to the Thread that processes that user
    private final ServerSocket serverSocket;
    private static final int DEFAULT_PORT = 4444;   
    private int roomNumber;
    private final Map<String, String> logins;
    private final PrintWriter loginWriter;

    /**
     * Create a new ChatServer instance
     * @param port - requires that the port be a valid positive integer that represents a valid port on the computer
     * Creates threadsafe Maps for online, userThreads, and roomList fields
     * @throws IOException
     * @throws IllegalArgumentException if port is negative
     */
    public ChatServer(int port) throws IOException {
        if (port < 0) {
            throw new IllegalArgumentException("ERROR: Port cannot be negative.");
        }
        serverSocket = new ServerSocket(port);
        online = Collections.synchronizedMap(new HashMap<String, User>());
        userThreads = Collections.synchronizedMap(new HashMap<User,Thread>());
        roomList = Collections.synchronizedMap(new HashMap<Integer, Room>());
        roomNumber = 0;
        logins = Collections.synchronizedMap(new HashMap<String, String>());
        loginWriter = new PrintWriter(new FileWriter(new File("src/server/userfile"), true));
        BufferedReader fin = new BufferedReader(new FileReader("src/server/userfile"));
        
        String next;
        while ((next=fin.readLine()) != null) {
            String[] line = next.split(" ");
            System.out.println("next "+next);
            logins.put(line[0], line[1]);
        }
    }
    
    /**
     * Create a new ChatServer instance on the port given in the constant DEFAULT_PORT
     * @throws IOException
     */
    public ChatServer() throws IOException{
        this(DEFAULT_PORT); 
    }
    
    /**
     * Starts the ChatServer
     * The server blocks until a new client connection is made.
     * It then spawns a new thread to deal with the connection as represented by a User object
     * Modifies - adds the new user's thread to the userThreads map
     */
    public void start() {
        while(true){
            try{
                Socket socket = serverSocket.accept(); //blocks until someone connects to it;
                User u = new User(this, socket.getInputStream(), socket.getOutputStream());
                Thread t = new Thread(u);
                userThreads.put(u,t);
                t.start();
            } 
            catch(IOException e){
                e.printStackTrace();
                break;
            }
        }
    }
    
    public void kill() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method called when a User logs out. Notifies all other online users of the logout.
     * @param u - the User object representing the user that logged out
     * Requires u is a valid User object in the online map. Otherwise this function has no effect
     * Modifies - removes the user from the online users map
     */
    public synchronized void notifyUserLoggedOut(User u) {
        if (online.remove(u.getUsername()) != null) {
            for (User other : online.values()) {
                other.notifyContactOffline(u.getUsername());
            }
        }
    }
    
     /**
     * Method called when a User logs in. Notifies all other online users of the login.
     * @param u - the User object representing the user that logged in.
     * Requires u is a non-null valid user object that has logged in with a valid username.
     * If username == null, this method has no effect
     * Modifies - adds the user to the online users map
     */
    public synchronized void notifyUserLoggedIn(User u) {
        if (u == null) {
            return;
        }
        String username = u.getUsername();
        if (username != null) {
            online.put(u.getUsername(), u);
            for (User other : online.values()) {
                if (!other.getUsername().equals(u.getUsername())) {
                    other.notifyContactOnline(u.getUsername());
                }
            }
        }
    }
    
    //when a user logs in, he gets all the notifications since his last logout using this method
    // TODO: keep this? it does nothing right now.
    public synchronized String[] getNotifications(String username){
        return new String[]{};
    }
    
    /**
     * Creates a new Room object on the server
     * @param creator - the username of the User creating the room. Requires creator not null
     * @param l - the User's RoomListener. Requires l not null
     * Modifies - increments the roomNumber variable
     * Inserts the room into the roomList map
     * @return - a reference to the newly created Room object
     * @throws IllegalArgumentException if any argument is null
     */
    public synchronized Room createRoom(String creator, RoomListener l) {
        if (creator == null || l == null) {
            throw new IllegalArgumentException("ERROR: Cannot create room with null arguments");
        }
        Room room = new Room(this, creator, l, roomNumber);
        roomNumber++;
        roomList.put(room.getId(), room);
        return room;
    }

    /**
     * Removes a Room object from the roomList
     * @param room - requires the room be an existing room, non-null
     * Modifies- removes the Room from the roomList if the Room exists
     */
    public synchronized void removeRoom(Room room){
    	roomList.remove(room.getId());
    }
    
    /**
     * Method to get the User object given a username. 
     * @param username - requires the username be a non-null String
     * If username is not a key in the map (ie. the user does not exist), returns null
     * @return - the User object if it exists (ie. mapped to by username), otherwise returns null
     */
    public synchronized User getUser(String username) {
        if (username == null) {
            return null;
        }
        return online.get(username);
    }
    
    /**
     * Method to determine whether a User is online (ie. is contained within the online map)
     * @param username - requires that username be a non-null String
     * @return - true if username is a key in online, otherwise false
     */
    public synchronized boolean isOnline(String username){
        return online.containsKey(username);
    }
    
    /**
     * Method to get a Room object given a room number
     * @param roomno - requires roomno be a positive integer corresponding to an active room
     * @return - the Room object that has room number given by roomno. If no such Room exists, or if 
     * roomno is invalid (ie. less than zero), returns null
     */
    public synchronized Room getRoom(int roomno) {
        if (roomno < 0) {
            return null;
        }
        return roomList.get(roomno);
    }
    
    /**
     * Method to determine whether a username is valid
     * @param username - requires the username be valid by the grammar, String not null
     * @param pass - requires the password not null
     * @return - true if the username and password are valid
     * @throws IllegalUsernameException - if username or pass are null, or if the username does not conform to the grammar
     */
    public boolean isValidUserPass(String username, String pass) throws IllegalUsernameException {
        if (!isValidUsername(username) || pass == null) {
            throw new IllegalUsernameException();
        }
        if (logins.containsKey(username)) {
            if (logins.get(username).equals(pass)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Method to remove a user and the corresponding thread from the server
     * @param u - a valid non-null User object
     * Requires that the user has logged off already (ie. online.containsKey(u) == false), otherwise
     * this method has no effect
     * Modifies - removes the userThread from the userThreads map
     */
    public synchronized void removeUser(User u) {
        if (!online.containsKey(u) && u != null) {
            userThreads.remove(u);
        }
    }   
    
    /**
     * Method to return the Set of online usernames
     * @return - Set of online usernames
     */
    public synchronized Set<String> getOnlineUsernames() {
        return online.keySet();
    }
    
    public synchronized void register(String user, String pass) throws UserAlreadyExistsException, IllegalUsernameException {
        if (logins.containsKey(user)) {
            throw new UserAlreadyExistsException();
        }
        if (!isValidUsername(user)) {
            throw new IllegalUsernameException();
        }
        logins.put(user, pass);
        loginWriter.write(user + " " + pass + "\n");
        loginWriter.flush();
        
    }
    
    private boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return username.matches("[A-Za-z0-9_-]*");
    }
                                             
    @SuppressWarnings("serial")
    public static class IllegalUsernameException extends Exception {
        public IllegalUsernameException() {
            super();
        }
    }
    
    @SuppressWarnings("serial")
    public static class UserAlreadyExistsException extends Exception {
        public UserAlreadyExistsException() {
            super();
        }
    }
}