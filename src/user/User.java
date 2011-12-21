package user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

import server.ChatServer;
import server.ChatServer.IllegalUsernameException;
import server.ChatServer.UserAlreadyExistsException;
import server.Room;
import server.RoomListener;
import encryption.EstablishServerHandshake;
import encryption.StreamPair;

/*
    Server --> client
    
    Protocol ::= Message*
    
    Message ::= NotifyContactOffline | NotifyContactOnline | NotifyRoomInvite | NotifyNewMsg |              NotifyRoomCreated | NotifyError | NotifyStatus | NotifyRoomUsers | NotifyEnter |
            NotifyLeave | NotifyWelcome | NotifyTyping | NotifyEnteredText | NotifyIdle | 
            NotifyRegisterSuccess | NotifyOnline
    
    NotifyContactOffline ::= ContactOffline Username Newline
    NotifyContactOnline ::= ContactOnline Username Newline
    NotifyRoomInvite ::= RoomInvite RoomNumber Username Newline
    NotifyNewMsg ::= NewMsg Username RoomNumber Line Newline
    NotifyRoomCreated ::= RoomCreated RoomNumber Newline
    NotifyError ::= Error ErrorMsg Newline
    NotifyStatus ::= Status OnlineStatus (Number)* Newline
    NotifyRoomUsers ::= RoomUsers RoomNumber (Username TypeStatus)+ Newline
    NotifyEnter ::= Enter Username RoomNumber Newline
    NotifyLeave ::= Leave Username RoomNumber Newline
    NotifyWelcome ::= Welcome Username Newline
    NotifyTyping ::= Typing Username RoomNumber Newline
    NotifyEnteredText ::= EnteredText Username RoomNumber Newline
    NotifyIdle ::= Idle Username RoomNumber Newline
    NotifyRegisterSuccess ::= RegisterSuccess Username Newline
    NotifyOnline ::= Online (Username)+
    
    RoomNumber ::= Number
    ErrorMsg ::= error 0 malformed command | 
            error 1 command not found | 
            error 2 user not online |
            error 3 room does not  exist or you are not in it |
            error 4 user with same name is already logged in |
            error 5 bad username |
            error 6 contact is not online | 
            error 7 user already online on same client |
            error 8 user with same username already exists |
            error 9 invalid login or password
            
            
    
    ContactOffline ::= offline 
    ContactOnline ::= userOnline
    RoomInvite ::= invite
    NewMsg ::= message
    RoomCreated ::= roomcreated
    Error ::= error
    Username ::= [A-Za-z0-9_-]+
    OnlineStatus ::= (online | offline)
    RoomUsers ::= roomUsers
    Enter ::= enter 
    Leave ::= leave 
    Welcome ::= welcome
    Typing ::= typing
    EnteredText ::= enteredText
    Idle ::= idle
    RegisterSuccess ::= registerSuccess
    Online ::= online
    Number ::= (1-9)[0-9]*
    Line ::= .*
    Newline ::= \n
    
    
    Client --> server
    
    Protocol ::= Message*
    Message ::= NotifyLogin | NotifyCreateRoom | NotifyInvite | NotifyAccept | NotifyDecline | NotifySay |  NotifyLogout | NotifyCreateAcct | NotifyStatus | NotifyOnline | NotifyLeave | NotifyEnteredText |
        NotifyIdle | NotifyTyping | NotifyRoomUsers
    
    NotifyLogin ::= Login Username Newline
    NotifyCreateRoom ::= CreateRoom Newline
    NotifyInvite ::= Invite Username RoomNumber Newline
    NotifyAccept ::= Accept RoomNumber Newline
    NotifyDecline ::= Decline RoomNumber Newline
    NotifySay ::= Say RoomNumber Line Newline
    NotifyLogout ::= Logout  Newline
    NotifyCreateAcct ::= Register Username Line Newline
    NotifyStatus ::= Status Newline
    NotifyOnline ::= Online Newline
    NotifyLeave ::= Leave RoomNumber Newline
    NotifyEnteredText ::= EnteredText RoomNumber Newline
    NotifyIdle ::= Idle RoomNumber Newline
    NotifyTyping ::= Typing RoomNumber Newline
    NotifyRoomUsers ::= RoomUsers RoomNumber Newline
    
    Login ::= login
    CreateRoom ::= create
    Invite ::= invite
    Accept ::= accept
    Decline ::= decline
    Say ::= say
    Logout ::= logout
    Register ::= register
    Status ::= status
    Online ::= online
    Leave ::= leave
    EnteredText ::= enteredText
    Idle ::= idle
    Typing ::= typing
    RoomUsers ::= roomUsers
    Username ::= [A-Za-z0-9_-]+
    RoomNumber ::= (1-9)[0-9]+
    Line ::= .*
    Newline ::= \n
*/

/**
 * Class that represents a User. Each user runs in its own thread, which takes care of processing client-server
 * messages and sends back server-client messages.
 * 
 * This class is threadsafe because all methods that observe or modify fields are synchronized
 * on the User's lock. We've established the thread-safety of the ChatServer and Room which may be
 * called by other users.
 */
public class User implements Runnable{
	
	private final ChatServer server;
	private PrintWriter out;
	private BufferedReader in;
	private final Map<Integer, Room> roomlist;
	private String username;
	private final boolean debug;
	
	private final RoomListener defaultRoomListener = new RoomListener(){
	    @Override
		public void notifySay(String user, String said, int roomno) {
	        if (user != null && said != null && roomno >=0) {
	            sendToUser("message "+ user + " " + roomno+ " " + said);
	        }
		}
	    @Override
		public void notifyUserEnteredRoom(String user, int roomno) {
	        if (user != null && roomno >=0) {
                sendToUser("enter " + user + " " + roomno);
            }
		}
	    @Override
		public void notifyUserLeftRoom(String user, int roomno) {
	        if (user != null && roomno >=0) {
                sendToUser("leave " + user + " " + roomno);
            }
	    }
        @Override
        public void notifyUserTyping(String user, int roomno) {
            if (user != null && roomno >=0) {
                sendToUser("typing " + user + " " + roomno);
            }
        }
        @Override
        public void notifyUserEnteredText(String user, int roomno) {
            if (user != null && roomno >=0) {
                sendToUser("enteredText " + user + " " + roomno);
            }
        }
        @Override
        public void notifyUserIdle(String user, int roomno) {
            if (user != null && roomno >=0) {
                sendToUser("idle " + user + " " + roomno);
            }
        }
	};
	
	/**
	 * A list of possible commands to the server. These are the first words specified
	 * in the client-->server protocol
	 */
	private enum Command{
        login, logout, create, leave, invite, accept, decline, say, register, status, online,
        typing, enteredText, idle, roomUsers;
        
        public static boolean contains(String s){
            for(Command c: values()){
                if(c.name().equals(s)) {
                    return true;
                }
            }
            return false;
        }
    }
    
	/**
	 * Construct a new User object. Takes the server and the socket's input and output streams.
	 * NOTE: the 
	 * @param server - the ChatServer instance that the User is on. Requires not null
	 * @param inStream - the InputStream to the socket, requires not null
	 * @param outStream - the OutputStream from the socket, requires not null;
	 * @throws IOException
	 */
	public User(ChatServer server, InputStream inStream, OutputStream outStream) throws IOException {
		this(server, inStream, outStream, false, true);
	}
	
	public User(ChatServer server, InputStream inStream, OutputStream outStream, boolean debug, boolean secure) throws IOException {
        if (server == null || inStream == null || outStream == null) {
            throw new IllegalArgumentException("ERROR: Arguments to User constructor cannot be null");
        }
	    this.server = server;
	    try{
	        if (secure) {
	            EstablishServerHandshake handshake = new EstablishServerHandshake(inStream, outStream);
                handshake.init();
                StreamPair pair = handshake.getStreamPair();
                this.in = new BufferedReader(new InputStreamReader(pair.inputStream()));
                this.out = new PrintWriter(pair.outputStream(), true);
	        }
	        else {
	            this.in = new BufferedReader(new InputStreamReader(inStream));
                this.out = new PrintWriter(outStream);
	        }			
		}catch(IOException e){
			e.printStackTrace();
		}catch(NoSuchAlgorithmException e){
	         e.printStackTrace();
		}catch(ClassNotFoundException e){
	         e.printStackTrace();
		}catch(InvalidAlgorithmParameterException e){
	          e.printStackTrace();
		}catch(InvalidKeyException e){
	         e.printStackTrace();
		}catch(InvalidKeySpecException e){
	         e.printStackTrace();
		}catch(NoSuchPaddingException e){
	         e.printStackTrace();
		}catch(NoSuchProviderException e){
	         e.printStackTrace();
		}
        
        this.username = null;
        this.roomlist=Collections.synchronizedMap(new HashMap<Integer,Room>());
        this.debug = debug;
    }
	
	public void run(){
		
		try{
			String input;
			out.print("connection successful\n");
			out.flush();
			while(true) {        // handle commands until the stream stops.
			    input = in.readLine();
			    if (input == null && !debug) {
			        break;
			    }
			    if (input != null) {
			        handleCommand(input);
			    }
			}
		} catch(IOException e){
		} finally {
		    if (username!= null) {
		        logOut();
		    }
		    out.close();
		    server.removeUser(this);
		}
	}
	
	/**
	 * Method to handle all client to server messages. Messages are sent back to the client
	 * directly from this method or methods that it calls.
	 * @param input - requires the input be non-null. Otherwise this method has no effect
	 * Messages that do not conform to the grammar will result in error messages
	 */
	private synchronized void handleCommand(String input){

	    if (input == null) {
	        return;
	    }
		String[] cmd=input.split(" ");
		
		//first see if this starts off as a valid command
		if(!Command.contains(cmd[0])){
		    sendCommandNotFoundError();
			return;
		}

		//handle all commands for which the user doesn't need to be logged in
		switch(Command.valueOf(cmd[0])){
			case login:
				if(cmd.length!=3){
				    sendMalformedCommandError();
				}
				else if(username != null) {
				    sendAlreadyOnlineError();
				}
				else {
					logIn(cmd[1], cmd[2]); // in case the hash function created a space
				}
                return;
				
			case register:
			    if (cmd.length != 3) {
			        sendMalformedCommandError();
			    }
			    else if (username != null) {
			        sendAlreadyOnlineError();
			    }
			    else {
			        register(cmd[1], cmd[2]);
			    }
				return;
				
			case status:
			    if (username == null) {
			        sendToUser("status offline");
			    }
			    else {
			        String s = "status online";
			        for (Integer i : roomlist.keySet()) {
			            s += " " + i.toString();
			        }
			        sendToUser(s);
			    }
			    return;
		}
		
		//make sure you've already logged in by checking if username is null
		if (username==null) {
			sendNotOnlineError();
			return;
		}
		
		try {
		  //handle all commands for which the user needs to be logged in
	        switch(Command.valueOf(cmd[0])){
	            case online:
	                if (cmd.length != 1) {
	                    sendMalformedCommandError();
	                }
	                else {
	                    String s = "online";
	                    for (String user : server.getOnlineUsernames()) {
	                        s += " " + user;
	                    }
	                    sendToUser(s);
	                }
	                return;
	                
	            case logout:
	                if(cmd.length!=1){
	                    sendMalformedCommandError();
	                    return;
	                }
	                logOut();
	                return;
	                
	            case accept:
	                if(cmd.length!=2){
	                    sendMalformedCommandError();
	                    return;
	                }
	                joinRoom(Integer.parseInt(cmd[1]));
	                return;
	                
	            case create:
	                if(cmd.length!=1){
	                    sendMalformedCommandError();
	                    return;
	                }
	                createRoom();
	                return;
	                
	            case leave:
	                if(cmd.length!=2){
	                    sendMalformedCommandError();
	                    return;
	                }
	                leaveRoom(Integer.parseInt(cmd[1]));
	                return;
	                
	            case decline:
	                if(cmd.length!=2){
	                    sendMalformedCommandError();
	                    return;
	                }
	                decline(Integer.parseInt(cmd[1]));
	                return;
	                
	            case invite:
	                if(cmd.length!=3){
	                    sendMalformedCommandError();
	                    return;
	                }
	                inviteContactToRoom(cmd[1], Integer.parseInt(cmd[2]));
	                return;
	                
	            case say:
	                if (cmd.length < 2) {
	                    sendMalformedCommandError();
	                    return;
	                }
	                String line = input.split(" ", 3)[2].trim();
	                if (line.length() > 0) {
	                    say(line, Integer.parseInt(cmd[1]));
	                }
	                return;
	                
	            case typing:
	                if (cmd.length != 2) {
	                    sendMalformedCommandError();
	                    return;
	                }
	                setUserIsTyping(Integer.parseInt(cmd[1]));
	                return;
	                
	            case enteredText:
	                if (cmd.length != 2) {
	                    sendMalformedCommandError();
	                    return;
	                }
	                setUserHasEnteredText(Integer.parseInt(cmd[1]));
	                return;
	                
	            case idle:
	                if (cmd.length != 2) {
	                    sendMalformedCommandError();
	                    return;
	                }
	                setUserIdle(Integer.parseInt(cmd[1]));
	                return;
	                
	            case roomUsers:
	                if (cmd.length != 2) {
	                    sendMalformedCommandError();
	                    return;
	                }
	                getUsersInRoom(Integer.parseInt(cmd[1]));
	                return;
	                
	            default:
	                sendMalformedCommandError();
	                return;
	        } 
		}
		catch (NumberFormatException e) {
		    sendMalformedCommandError();
		}
		
	}
	
	/**
	 * Method called when a user logs in. Checks for a valid username conforming to spec
	 * as well as a valid matching password on the server. This method will give an error message
	 * if: 1) the username is not valid according to the spec (numbers, letters, dashes and underscores),
	 * 2) the password does not match the username, or 3) the user with this username is already 
	 * logged onto the server.
	 * @param username - requires username be a valid username that exists on the server, not null
	 * If the user is already logged in, an error message will be sent.
	 * @param password - requires password not null. NOTE: the password on the server side is 
	 * actually a SHA-256 hash of the user's password
	 * Modifies - notifies the server of the login if successful. Sets the username, writes welcome message to user
	 * @return - true if the login was successful, false otherwise
	 */
	private synchronized boolean logIn(String username, String password) {
	    try {
	        if (!server.isValidUserPass(username, password)) { // bad username-password combo
	            sendInvalidLoginParametersError();
                return false;
	        }
	        if (server.isOnline(username)) {   // already online
	            sendAlreadyLoggedInError();
	            return false;
	        }
	        this.username = username;
	        server.notifyUserLoggedIn(this);
	        sendToUser("welcome " + username);
	        return true;
	    }
	    catch (IllegalUsernameException e) {   // catch bad usernames
	        sendIllegalUsernameError();
	    }
	    return false;
	}
	
	/**
	 * Method called when the user logs out
	 * Modifies - Notifies the server of the logout. Removes user from all rooms.
	 * Sets username field to null
	 */
	private synchronized void logOut(){
		server.notifyUserLoggedOut(this);
		for(int roomno: roomlist.keySet()){
			roomlist.get(roomno).leave(username);
		}
		roomlist.clear();
		username=null; 
	}
	
	/**
	 * Method called when the user tries to register with a user/pass combination
	 * @param user - Username that is going to be registered. Requires that it conforms to the spec,
	 * not null.
	 * @param pass - Password associated with the username. Requires not null. 
	 * On the server side, it is represented as the hex value of the SHA-256 hash.
	 * On successful registration, a registerSuccess message is sent back to the user.
	 * If the username is already taken, or if the username is illegal, an error message is sent back
	 */
	private synchronized void register(String user, String pass) {
	    try {
            server.register(user, pass);
            sendToUser("registerSuccess " + user);
        } catch (UserAlreadyExistsException e) {
            sendUserAlreadyExistsError();
        } catch (IllegalUsernameException e) {
            sendIllegalUsernameError();
        }
	}

	/**
	 * Method called when the user wants to create a room.
	 * Gets a new room object from the server and registers the defaultRoomListener
	 * Modifies - roomlist. Adds the room number and Room object to the roomlist.
	 */
	private synchronized void createRoom(){
		Room room = server.createRoom(username, defaultRoomListener);
		sendToUser("roomcreated " + room.getId());
		roomlist.put(room.getId(),room);
	}
	
	/**
	 * Method called when the user wants to join a room.
	 * @param roomno -requires the room number be a nonnegative integer of a currently existing Room's room number
	 * Modifies - roomlist - adds the room to the User's roomlist if successful.
	 */
	private synchronized void joinRoom(int roomno) {
	    if (roomno < 0) {
	        sendMalformedCommandError();
	        return;
	    }
		Room room = server.getRoom(roomno);
		if (room==null) {      //might happen if the room died since you were invited
		    sendRoomNotExistError();
			return;
		}	
		if (room.join(username, defaultRoomListener)){
			roomlist.put(roomno, room);
		}
		else{
		    sendRoomNotExistError();
		}
	}
	
	/**
	 * Method called when the user declines a room invitation
	 * @param roomno - the room number corresponding to the invitation to be declined
	 * Requires roomno be a valid nonnegative room number
	 */
	private synchronized void decline(int roomno) {
	    if (roomno < 0) {
	        sendMalformedCommandError();
	        return;
	    }
	    Room room = server.getRoom(roomno);
	    if (room == null) {
	        return;
	    }
	    room.decline(username);
	}
	
	/**
	 * Method called when leaving a room.
	 * @param roomno - requires roomno be nonnegative, valid roomnumber of an existing Room
	 * Modifies - roomlist - removes the room number-Room object mapping if it exists
	 */
	private synchronized void leaveRoom(int roomno){
	    if (roomno < 0) {
	        sendMalformedCommandError();
	        return;
	    }
		Room room = roomlist.get(roomno);
		if(room != null) {    // if you are actually in the room
		    room.leave(username);
		}
		roomlist.remove(roomno);
	}
	
	/**
	 * Method called when inviting another contact to the room
	 * @param contactUsername - the username of the other contact. Requires that it is the username
	 * of a valid user on the server
	 * @param roomno - roomnumber to invite to. Requires it to be a room that you are currently in, nonnegative
	 * If the contact is not online, or if the room number does not specify a room that you are in,
	 * writes an appropriate error message.
	 * Modifies - the Room object updates appropriately.
	 */
	private synchronized void inviteContactToRoom(String contactUsername, int roomno) {
	    if (roomno < 0) {
	        sendMalformedCommandError();
	    }
		User contact = server.getUser(contactUsername);
		if(contact==null){
		    sendContactNotOnlineError();
			return;
		}
		Room room = roomlist.get(roomno);
		if(room==null) {
		    sendRoomNotExistError();
			return;
		}
		if (room.isInvited(contactUsername)) {
		    return;   // already invited, ignore this
		}
		if(!room.inviteUser(username, contactUsername)){
		    sendRoomNotExistError();
			return;
		}
		contact.inviteToRoom(username, roomno);
	}
	
	/**
	 * Method is called when a user wants to tell a room they are typing
	 * Calls the setTyping method on the room, which in turn sends messages to all users in the room
	 * @param roomno - requires roomno be the number of a valid Room, nonnegative
	 */
	private synchronized void setUserIsTyping(int roomno) {
	    if (roomno < 0) {
	        sendMalformedCommandError();
	        return;
	    }
	    Room room = roomlist.get(roomno);
	    if (room != null) {
	        room.setTyping(username);
	    }
	}
	
	/**
     * Method is called when a user wants to tell a room they have entered text
     * Calls the setEnteredText method on the room, which in turn sends messages to all users in the room
     * @param roomno - requires roomno be the number of a valid Room, nonnegative
     */
	private synchronized void setUserHasEnteredText(int roomno) {
	    if (roomno < 0) {
	        sendMalformedCommandError();
	    }
	    Room room = roomlist.get(roomno);
	    if (room != null) {
	        room.setEnteredText(username);
	    }
	}
	
	/**
     * Method is called when a user wants to tell a room they are idle (ie. not typing)
     * Calls the setIdle method on the room, which in turn sends messages to all users in the room
     * @param roomno - requires roomno be the number of a valid Room, nonnegative
     */
	private synchronized void setUserIdle (int roomno) {
	    if (roomno < 0) {
            sendMalformedCommandError();
        }
	    Room room = roomlist.get(roomno);
        if (room != null) {
            room.setIdle(username);
        }
	}
	
	/**
	 * Method is called when a user wants to get a list of all users in the room and their statuses
	 * Sends to the user a server to client message that conforms to the protocol
     * @param roomno - requires roomno be the number of a valid Room, nonnegative
	 */
	private synchronized void getUsersInRoom(int roomno) {
	    if (roomno < 0) {
            sendMalformedCommandError();
        }
	    Room room = roomlist.get(roomno);
	    if (room != null) {
	        sendToUser("roomUsers " + roomno + " " + room.getUsersInRoom());
	    }
        else {
            sendRoomNotExistError();
        }
	}
	
	/**
	 * Method called to send a message via the OutputStream to the user
	 * @param s - requires s not null. The string should conform to the protocol (as it should
	 * be formed by other methods that construct the String correctly), but this method does not check that
	 */
	private synchronized void sendToUser(String s) {
	    if (s == null) {
	        return;
	    }
	    out.print(s + "\n");
        out.flush();
	}
	
	/**
	 * Method called when a user wants to say something to a room
	 * @param s - the message that is to be said. Requires s not null
	 * @param roomno - the room to send it to. Room number must be nonnegative
	 */
	private synchronized void say(String s, int roomno) {
	    if (s == null) {
	        return;
	    }
	    if (roomno < 0) {
	        sendMalformedCommandError();
	    }
		Room room=roomlist.get(roomno);
		if(room==null){
		    sendRoomNotExistError();
			return;
		}
		else{
			room.write(username, s);
		}
	}
	
	
	/*
	 * Methods to send the appropriate messages to the user
	 * Assumes that the callers of the methods have checked against the specs
	 * (Strings not null, integer room numbers nonnegative)
	 */
	
	private void inviteToRoom(String from, int roomno){
		sendToUser("invite "+roomno+" "+from);
	}
	
	public void notifyContactOnline(String user){
		sendToUser("userOnline "+user);
	}
	
	public void notifyContactOffline(String user){
		sendToUser("offline "+user);
	}
	
	public void requestContact(String from){
		sendToUser("request "+from);
	}
	
	/////////////////
	
	/**
	 * @return - the username as a String
	 * Requires the user be logged in (ie. username != null), otherwise will return null
	 */
	public String getUsername(){
		return username;
	}
	
	/**
	 * @return - the defaultRoomListener as instantiated when the User was constructed
	 */
	public RoomListener getDefaultRoomListener() {
	    return defaultRoomListener;
	}
	
	/*
	 *  Methods to send error messages to the User
	 *  See the error message protocol given at the top of this file.
	 */

	private void sendMalformedCommandError() {
	    sendToUser("error 0 malformed command");
	}
	
	private void sendCommandNotFoundError() {
	    sendToUser("error 1 command not found");
	}
	
	private void sendNotOnlineError() {
	    sendToUser("error 2 user not online");
	}
	
	private void sendRoomNotExistError() {
	    sendToUser("error 3 room does not exist or you are not in it");
	}
	
	private void sendAlreadyLoggedInError() {
	    sendToUser("error 4 user with same name is already logged in");
	}
	
	private void sendIllegalUsernameError() {
	    sendToUser("error 5 bad username");
	}
	
	private void sendContactNotOnlineError() {
	    sendToUser("error 6 contact is not online");
	}
	
	private void sendAlreadyOnlineError() {
	    sendToUser("error 7 user already online on same client");
	}
	
	private void sendUserAlreadyExistsError() {
	    sendToUser("error 8 user with same username already exists");
	}
	
	private void sendInvalidLoginParametersError() {
	    sendToUser("error 9 invalid login or password");
	}
}