package server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class representing a Room object.
 * A Room contains a Map of username to RoomListener for that user that can be used to send server-->client
 * messages to that User
 * A roomid that is final -- it cannot be changed once set
 * A Tablet: keeps track of everything written to the room
 * A reference to the ChatServer that the room sits in.
 * 
 * Threadsafe argument:
 * All operations are protected by the Room object's lock. Thus, only one User thread may modify the 
 * fields of a Room at any given time. No two operations may occur at the same time, since every method
 * is synchronized on the Room's lock.
 */

public class Room {
    
	private final Map<String, RoomListener> listeners; // map of username to RoomListener for that user
	private final Set<String> guestList; // usernames that have been invited
	private final int roomid; // room identifier -- IMMUTABLE
	private final Tablet tablet;
	private final ChatServer server;
	private final Map<String, TypingStatus> statuses; // map of username to typing status
	
	/**
	 * Creates a new Room object
	 * @param server - requires server not null
	 * @param name - requires name not null, must be the username of the User that created this room
	 * @param listener - requires that the listener corresponds to the User given by the username
	 * @param id - id given by the Server, requires id >= 0
	 * @throws IllegalArgumentException if any argument is null or id<0
	 */
	public Room(ChatServer server, String name, RoomListener listener, int id) {
	    if (server == null || name == null || listener == null || id < 0) {
	        throw new IllegalArgumentException("ERROR: Null arguments given to Room constructor");
	    }
	    this.roomid = id;
		this.server = server;
		guestList = Collections.synchronizedSet(new HashSet<String>());
		listeners = Collections.synchronizedMap(new HashMap<String, RoomListener>());
		listeners.put(name, listener);
		tablet = new Tablet();
		statuses = Collections.synchronizedMap(new HashMap<String, TypingStatus>());
	}
	
	/**
	 * @return - the id value of the server
	 */
	public synchronized int getId(){
		return roomid;
	}
	
	/**
	 * Method call for a user to join a room
	 * In order for a user to join successfully, the user must be on the guestList of the room (ie. the list of all users invited)
	 * If so, the user's RoomListener is added to the room, and a notification is sent to all other users
	 * @param username - requires username not null, be the username of a valid user on the server
	 * @param l - requires l not null, be the RoomListener that belongs to the User object whose name is username
	 * 
	 * Modifies:
	 * If username is in guestList: puts username, RoomListener object into listeners map, removes username from guestList
	 * 
	 * @return - true if the user was indeed on the guestList, false otherwise
	 */
	public synchronized boolean join (String username, RoomListener l) {
		if(guestList.contains(username) && username != null && l != null){
			listeners.put(username, l);
			statuses.put(username, TypingStatus.IDLE);
			guestList.remove(username);
			for(String s: listeners.keySet()){
				listeners.get(s).notifyUserEnteredRoom(username, roomid);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Method called when declining an invitation.
	 * @param username Requires username non null, a valid username on the server
	 * Modifies - guestList - removes user from the list if exists
	 */
	public synchronized void decline(String username) {
	    if (guestList.contains(username) && username != null) {
	        guestList.remove(username);
	    }
	}
	
	/**
	 * Method called when a user leaves a room. Notifies all other users in the room of the change
	 * If the room thus becomes empty, the room calls the server function to remove itself from the server
	 * @param username - requires username be a user in the Room
	 * Modifies: removes the username from the listeners map
	 */
	public synchronized void leave(String username){
		if(listeners.containsKey(username) && username != null) {
		    listeners.remove(username);       
	        for(String s: listeners.keySet()){
	            listeners.get(s).notifyUserLeftRoom(username, roomid);
	        }
	        if(listeners.isEmpty()) {
	            server.removeRoom(this);
	        }
		}
	}
	
	/**
	 * Method called when inviting a user to the room. Checks to make sure that the inviting user is
	 * indeed in the room, and if so, adds the invited user to the guestList
	 * @param from - requires from be a non-null String username of a user in the room
	 * @param to - requires to be a non-null String username of a user on the server
	 * Modifies - adds the invited user to the guestList if the inviting user is in the room
	 * @return - returns true if the inviting user is in the room and the guest was successfully added, false otherwise
	 */
	public synchronized boolean inviteUser(String from, String to){
		if (listeners.containsKey(from) && (from != null) && (to != null)){
			guestList.add(to);
			return true;
		}
		return false;
	}
	
	/**
	 * Method called when a user writes to the room. Checks to make sure the specified user is actually in the room
	 * If not, then no effect
	 * @param username - requires username be a non-null String that is the username of a user in the Room
	 * @param said - requires said be a non-null String
	 * Modifies - adds the (username, said) to the tablet of all things said in the room
	 */
	public synchronized void write(String username, String said){
		if(listeners.containsKey(username) && (username != null) && (said != null)) {
		    tablet.addLine(username, said);
	        for(String s: listeners.keySet()){
	            listeners.get(s).notifySay(username,said,roomid);
	        }
		}
	}
	
	/**
	 * Method called when there is a client-->server message that says the user is typing in a given room
	 * Notifies all listeners to send server-->client messages to inform all clients in the room of the typing
	 * @param username - requires username be a valid username of a User in the room. Else no effect
	 */
	public synchronized void setTyping(String username) {
	    if (listeners.containsKey(username) && username != null) {
	        statuses.put(username, TypingStatus.TYPING);
	        for(String s : listeners.keySet()) {
	            listeners.get(s).notifyUserTyping(username, roomid);
	        }
	    }
	}
	
	/**
     * Method called when there is a client-->server message that says the user has entered
     * text but is no longer typing in a given room
     * Notifies all listeners to send server-->client messages to inform all clients in the room
     * @param username - requires username be a valid username of a User in the room. Else no effect
     */
	public synchronized void setEnteredText(String username) {
	    if (listeners.containsKey(username) && username != null) {
	        statuses.put(username, TypingStatus.ENTERED_TEXT);
	        for (String s : listeners.keySet()) {
	            listeners.get(s).notifyUserEnteredText(username, roomid);
	        }
	    }    
	}
	
	/**
     * Method called when there is a client-->server message that says the user is not typing
     * and the text field is blank
     * Notifies all listeners to send server-->client messages to inform all clients in the room
     * @param username - requires username be a valid username of a User in the room. Else no effect
     */
	public synchronized void setIdle(String username) {
	    if (listeners.containsKey(username) && username != null) {
	        statuses.put(username, TypingStatus.IDLE);
	        for (String s : listeners.keySet()) {
	            listeners.get(s).notifyUserIdle(username, roomid);
	        }
	    }
	}
	
	/**
	 * Method that gives all usernames and typing status of all members of the room in a space delimited String
	 * @return - String object, space delimited, that contains all members of a room. 
	 * There is no guarantee on the ordering of usernames in the String.
	 */
	public synchronized String getUsersInRoom() {
	    StringBuilder sb = new StringBuilder();
	    for (String s : listeners.keySet()) {
	        if (statuses.containsKey(s)) {
	            sb.append(s + " " + new Integer(statuses.get(s).ordinal()).toString() + " ");
	        }
	        else {     // really shouldn't be here
	            sb.append(s + " 0 ");
	        }
	    }
	    return sb.substring(0, sb.length() - 1);
	}
	
	/**
	 * Method that tells whether the user given by username is on the guest list
	 * @param username - the username of the user to check. Requires username not null
	 * @return true if username is on the guestList, false otherwise
	 */
	public synchronized boolean isInvited(String username) {
	    return guestList.contains(username) && username != null;
	}
}
