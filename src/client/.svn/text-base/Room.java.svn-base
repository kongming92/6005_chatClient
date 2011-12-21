package client;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ui.InviteWindow;
import ui.RoomWindow;

public class Room {
	private final Model model;
	private final int roomNumber;
	private final RoomWindow roomwindow;
	private int typingStatus;
	
	private Map<String,Integer> userStatus; //Should this be Set? idk.
	private Set<String> invited;
	private InviteWindow invitewindow;
	
	
	public Room(Model model,int roomno){
		this.roomNumber=roomno;	
		userStatus = Collections.synchronizedMap(new HashMap<String,Integer>());
		userStatus.put(model.getUsername(),0);
		roomwindow = new RoomWindow(model,this);
		invited = Collections.synchronizedSet(new HashSet<String>());
		this.model = model;
	}


	/**
	 * takes an instantiate array and instantiates the users and their typing statuses
	 * @param instatiate an array of type {username1,typingStatus,username2,typingStatus,username3,typingStatus...}. Must be non null
	 */
	public void instantiateUserList(String[] instantiate){
		for(int i=0; i<instantiate.length; i+=2){
			userStatus.put(instantiate[i],Integer.parseInt(instantiate[i+1]));
		}
		synchronized(roomwindow){
			roomwindow.refresh();
		}
		
	}
	
	/**
	 * notifies this room that this contact has just entered this room
	 * @param contact the username of the contact, must be non null
	 */
	public void notifyEnteredRoom(String contact){
		invited.remove(contact);
		userStatus.put(contact,0);
		synchronized(roomwindow){
			roomwindow.refresh();
		}
		if(invitewindow!=null){
			synchronized(invitewindow){
				invitewindow.refreshMembers();
			}
			
		}
	}
	
	/**
	 * notifies this room that this contact has just left this room
	 * @param contact the username of the contact, must be non null
	 */
	public void notifyLeftRoom(String contact){
		userStatus.remove(contact);
		synchronized(roomwindow){
			roomwindow.refresh();
		}
		
		if(invitewindow!=null){
			synchronized(invitewindow){
				invitewindow.refreshMembers();
			}
			
		}
	}
	
	/**
	 * notifies this room that this contact has just become idle
	 * @param contact the username of this contact, must be non null
	 */
	public void notifyUserIdle(String contact){
		userStatus.put(contact,0);
		synchronized(roomwindow){
			roomwindow.refresh();
		}
		
	}
	
	/**
	 * notifies this room that this contact is typing
	 * @param contact username of this contact, must be non null
	 */
	public void notifyUserTyping(String contact){
		userStatus.put(contact,1);
		synchronized(roomwindow){
			roomwindow.refresh();
		}
		
	}
	
	/**
	 * notifies this room that this user has entered text
	 * @param contact username of the contact, must be non null
	 */
	public void notifyUserEnteredText(String contact){
		userStatus.put(contact,2);
		synchronized(roomwindow){
			roomwindow.refresh();
		}
	}
	
	
	/**
	 * notifies this room that a new message has just been made in this room
	 * @param contact the username of the contact who just sent a message
	 * @param said the String the contact sent
	 */
	public void newMsg(String contact, String said){
		System.out.println("newMsg "+contact+" "+said);
		Token[] tokens = new MessageTokenizer(said).getTokens();
		synchronized(roomwindow){
			roomwindow.newMsg(contact, MessageParser.getTextArray(tokens), MessageParser.getStyleArray(tokens));
		}
	}
	
	/**
	 * creates the invite view so the client can invite users to this room
	 */
	public void createInviteList(){
		
			invitewindow = new InviteWindow(model,this);
			invitewindow.setVisible(true);
		
		
	}
	
	
	
	public void changeStatus(int newStatus){
		if(newStatus==typingStatus)return;
		typingStatus=newStatus;
		try{
			model.notifyChangedTypingStatus(getNumber(),newStatus);
		}catch(InvalidStatusException e){
			e.printStackTrace();
		}
		
	}
	
	public void notifyInvited(String contact){
		invited.add(contact);
		if(invitewindow!=null){
			synchronized(invitewindow){
				invitewindow.refreshMembers();
			}
		}
		
	}
	
	
	
	public void leave(){
		synchronized(roomwindow){
			roomwindow.setVisible(false);
		}
		
		if(invitewindow!=null){
			synchronized(invitewindow){
				invitewindow.setVisible(false);
			}
		}
	}
	
	public int getNumber(){
		return roomNumber;
	}
	
	public Map<String,Integer> getMembersList(){
		return new HashMap<String,Integer>(userStatus);//we put it in a tree set so it will be sorted
	}
	
	public Set<String> getInvited(){
		return new HashSet<String>(invited);
	}
}
