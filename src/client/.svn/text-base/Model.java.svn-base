package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.NoSuchPaddingException;
import javax.swing.Box.Filler;

import secure.SecurePass;
import ui.MainWindow;
import ui.NotificationDialog;
import ui.ResponseWindow;
import encryption.EstablishClientHandshake;
import encryption.StreamPair;

/*TODO: make this threadsafe, you can have the server send a hundred messages to the model at once, each one calling a different method
 * if the methods all take a long time to run they will be running at the same time, so worry about thread saftey
 */
public class Model {
	private final String hostname;
	private final int port;
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	private String username;
	private MainWindow mainwindow;
	
	private Thread mainListener;//thread that listens to the socket for incoming messages
	private final Set<String> buddyList;
	private final Map<Integer,Room> roomList;
	
	private final Queue<String> toInvite;//used for createAndInvite, when a room is created you check to see if anyone is on this list to invite them
	private final Map<Integer,ResponseWindow> invitedList;//list of rooms that you are currently invited to
	
	
	private PrintWriter outputTranscript,inputTranscript,fullTranscript;//used in debugging to record conversations
	/**
	 * Constructs a new Model
	 * @param hostname	-	the host you are trying to connect to, requires that 
	 * @param port		- 	the port that you are connecting to
	 */
	public Model(String hostname, int port){
		this.hostname=hostname;
		this.port = port;
		
		buddyList = Collections.synchronizedSet(new TreeSet<String>());
		roomList = Collections.synchronizedMap(new HashMap<Integer,Room>());
		toInvite = new LinkedList<String>();
		invitedList = Collections.synchronizedMap(new HashMap<Integer,ResponseWindow>());
	}
	
	public void outputTranscript(OutputStream out){
		outputTranscript = new PrintWriter(out,true);
	}
	
	public void inputTranscript(OutputStream out){
		inputTranscript = new PrintWriter(out,true);
	}
	public void fullTranscript(OutputStream out){
		fullTranscript = new PrintWriter(out,true);
	}
	
	public void start (MainWindow mainwindow) throws IOException {
		try{
			socket = new Socket(hostname,port);
			EstablishClientHandshake handshake = new EstablishClientHandshake(
					socket.getInputStream(), socket.getOutputStream());
			handshake.init();
			StreamPair pair = handshake.getStreamPair();
			in = new BufferedReader(new InputStreamReader(pair.inputStream()));
			out=new PrintWriter(pair.outputStream(),true);
		} catch(IOException e){
		    throw e;
		}catch(NoSuchAlgorithmException e){
			
		}catch(InvalidAlgorithmParameterException e){
			
		}catch(ClassNotFoundException e){
			
		}catch(InvalidKeySpecException e){
			
		}catch(NoSuchPaddingException e){
			
		}catch(NoSuchProviderException e){
			
		}catch(InvalidKeyException e){
			
		}
		this.mainwindow = mainwindow;
		mainwindow.setVisible(true);
		mainwindow.switchFromConnectView();
		this.mainListener = new Thread(new ServerListener(in,out));
		mainListener.start();
	}
	
	//requires input be non null, 
	private void handleInput(String input)throws IOException{
		//System.out.println("got command "+input);
		if(fullTranscript!=null){
			synchronized(fullTranscript){
				fullTranscript.print(">>> "+input+"\n");
				fullTranscript.flush();
			}
			
		}
		if(inputTranscript!=null){
			inputTranscript.print(">>> "+input+"\n");
			inputTranscript.flush();
		}
		System.out.println(">>> "+input);
		if(input.equals("connection successful"))return;//useless command as far as the model is concerned
		String[] cmd = input.split(" ");
		if(!Command.contains(cmd[0])){
			//System.out.println(join(cmd,0,cmd.length));
			throw new RuntimeException("SHOULDN'T GET HERE, didn't recognize server command "+cmd[0]);
		}
		
		switch(Command.valueOf(cmd[0])){
			case offline:
				notifyContactOffline(cmd[1]);
				return;
			case userOnline:
				notifyContactOnline(cmd[1]);
				return;
			case error:
				notifyError(cmd);
				return;
			case message:
				notifyNewMsg(Integer.parseInt(cmd[2]), cmd[1], join(cmd,3,cmd.length));
				return;
			case roomcreated:
				roomCreated(Integer.parseInt(cmd[1]));
				return;
			case invite:
				notifyRoomInvite(cmd[2],Integer.parseInt(cmd[1]));
				return;
			case enter:
				notifyContactEnteredRoom(cmd[1],Integer.parseInt(cmd[2]));
				return;
			case leave:
				notifyContactLeftRoom(cmd[1],Integer.parseInt(cmd[2]));
				return;
			case typing:
				notifyUserTyping(cmd[1],Integer.parseInt(cmd[2]));
				return;
			case idle:
				notifyUserIdle(cmd[1],Integer.parseInt(cmd[2]));
				return;
			case enteredText:
				notifyUserEnteredText(cmd[1],Integer.parseInt(cmd[2]));
				return;
			case roomUsers:
				setUsersInRoom(Integer.parseInt(cmd[1]),Arrays.copyOfRange(cmd,2,cmd.length));
				return;
			case registerSuccess:
				registerSuccess();
				return;
			case welcome:
				welcome(cmd[1]);
				return;
			case online:
				onlineResponse(Arrays.copyOfRange(cmd,1,cmd.length));
				return;
			default:
				//System.out.println("SHOULDN'T GET HERE, your switch statement doesn't include all Command types, namley "+cmd[0]);
				throw new RuntimeException("SHOULDN'T GET HERE, your switch statement doesn't include all Command types, namley "+cmd[0]);
		}
	}
	
	
	 // puts an array of strings together seperated by spaces
	private String join(String[] cmd, int start, int end){
		String s="";
		for(int i=start; i<end; i++){
			s+=cmd[i]+" ";
		}
		s=s.trim();
		return s;
	}
	
	private void updateOnlineContacts(){
		sendToServer("online");
	}
	
	private void sendToServer(String s){
		synchronized(out){
			System.out.println("sending "+s);
			out.print(s+"\n");
			out.flush();
		}
		
		if(outputTranscript!=null){
			outputTranscript.print(s+"\n");
			outputTranscript.flush();
		}
		
		if(fullTranscript!=null){
			synchronized(fullTranscript){
				fullTranscript.print(s+"\n");
				fullTranscript.flush();
			}
		}
	}
	
	//*****************************************************************
	//******************Methods that the GUI calls*********************
	//*****************************************************************
	
	
	/**
	 * switches this window to the register view, requires that the model is started
	 * @throws IOException
	 */
	public void registerView()throws IOException{
		mainwindow.switchToRegisterView();
	}
	
	/**
	 * switches this window to the login view, requires that the model is started
	 * @throws IOException
	 */
	public void loginView()throws IOException{
		mainwindow.switchToLoginView();
		
	}
	/**
	 * swtiches this window to the buddy view, requires that tthe model is started
	 * @throws IOException
	 */
	public void buddyView()throws IOException{
		mainwindow.switchToBuddyView();
	}
	
	/**
	 * sends a register message to the server if the username and password are alpha numeric and 
	 * the password equals passwordConf. Otherwise creates a dialog box describing the problem and 
	 * returns immediatley
	 * 
	 * @param username the username that the client wishes to register. Must be non null
	 * @param password the password that the client wishes to register. Must be non null
	 * @param passwordConf  confirmation of the password. Must be non null
	 */
	public void register(String username,String password,String passwordConf){
		if(! username.matches("(\\w|\\d)+")){
			if(outputTranscript!=null)return;//for debugging, so the dialog box doesn't get created
			NotificationDialog.illegalUsernameError();
			return;
		}
		if(!password.matches("(\\w|\\d)+")){
			if(outputTranscript!=null)return;//for debugging, so the dialog box doesn't get created
			NotificationDialog.InvalidPassword();
			return;
		}
		else if(!password.equals(passwordConf)){
			if(outputTranscript!=null)return;//for debugging, so the dialog box doesn't get created
			NotificationDialog.InvalidConfirmedPassword();
			return;
		}
		sendToServer("register "+username+" "+SecurePass.hash(password));
	}
	
	/**
	 * 
	 * @return the username of this model
	 */
	public String getUsername(){ 
		return username;
	}
	
	/**
	 * 
	 * @return a set of strings of the contacts that are currently online
	 */
	public Set<String> getBuddyList(){ //ADDED BY JEFFREY. DO I NEED TO CLONE THIS SOMEHOW?
		System.out.println("NUDDY LIST IS NULL?"+(new TreeSet<String>(buddyList)==null));
		return new TreeSet<String>(buddyList);
	}
	
	/**
	 * creates an invite list for this room
	 * @param roomno  the room number for which you wish to create an invite list
	 */
	public void createInviteList(int roomno){ //ADDED BY JEFFREY
		roomList.get(roomno).createInviteList();
		
	}
	
	/**
	 * sends a login message to the server
	 * @param username the username the client wishes to login as, must be non null
	 * @param password the password the client wishes to login with, must be non null
	 */
	public void login(String username,String password){
		sendToServer("login "+username+" "+SecurePass.hash(password));
	}
	
	/**
	 * sends a logout message to the server
	 * @throws IOException
	 */
	public void logout()throws IOException{//when something is loggin out
		if(username==null)return;//must be logged in to log out
		buddyList.clear();
		
		//we can't just call leaveRoom here because 
		for(Integer i: roomList.keySet()){
			Room r = roomList.get(i);
			if(r!=null)r.leave();
		}
		
		roomList.clear();
		toInvite.clear();
		invitedList.clear();
		sendToServer("logout");
		mainwindow.switchToLoginView();
	}
	
	/**
	 * sends a create message to the server
	 */
	public void createRoom(){
		sendToServer("create");
	}
	
	/**
	 * sends a say message to the server
	 * @param say		the string the client wishes to say
	 * @param roomno	the room number the client wishes to say it in
	 */
	public void say(String say, int roomno){
		sendToServer("say "+roomno+" "+say.trim());
	}
	
	/**
	 * accepts the invite to the room number
	 * @param roomno the room number you are accepting an invite to
	 */
	public void acceptInvite(int roomno){
        roomList.put(roomno, new Room(this,roomno));
		sendToServer("accept "+roomno);
		sendToServer("roomUsers "+roomno);
		invitedList.remove(roomno);
	}
	
	/**
	 * declines the invite to this room number
	 * @param roomno the room number you are declining the invite to
	 */
	public void declineInvite(int roomno){
		sendToServer("decline "+roomno);
		invitedList.remove(roomno);
	}
	
	/**
	 * sends and invite message to the server
	 * 
	 * @param contact the username of the contact you wish to invite. Must be non null
	 * @param roomno the room number you wish to invite the contact to
	 */
	public void inviteContact(String contact,int roomno){
		if(roomList.get(roomno)!=null)roomList.get(roomno).notifyInvited(contact);
		sendToServer("invite "+contact+" "+roomno);
	}
	
	/**
	 * sends a leave message to the server
	 * @param roomno the number of the room you wish to leave
	 */
	public void leaveRoom(int roomno){
		Room r = roomList.get(roomno);
		if(r!=null)roomList.get(roomno).leave();
		roomList.remove(roomno);
		
		sendToServer("leave "+roomno);
	}
	
	
	/**
	 * creates room and schedules this contact to be invited to it as soon as the room is created if the username
	 * is not the username of this model. If username is the username of this model, returns immediatley (does nothing). 
	 * @param username the username of the contact you wish to invite. Must be non null
	 */
	public void createAndInvite(String username){
		if(username.equals(this.username))return;
		toInvite.add(username); 
		createRoom();
		 
	}
	
	
	private synchronized void setUsersInRoom(int roomno, String[] userStatus){
		roomList.get(roomno).instantiateUserList(userStatus);
	}
	
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	//^^^^^^^^^^^^^^^^^^Methods that the GUI calls^^^^^^^^^^^^^^^^^^^^^
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	//******************************************************************
	//******************Methods that call the GUI***********************
	//******************************************************************
	
	
	private void registerSuccess()throws IOException{
		NotificationDialog.registerSuccessful();
		synchronized(mainwindow){
			mainwindow.switchToLoginView();
		}
	}
	//requires that the gui is started (mainwindow is non null)
	private void notifyContactOffline(String contact){
		buddyList.remove(contact);
		synchronized(mainwindow){
			mainwindow.refreshBuddyView();
		}
		
	}
	

	//requires that the gui is started (mainwindow is non null)
	private void notifyContactOnline(String username){
		buddyList.add(username);
		synchronized(mainwindow){
			mainwindow.refreshBuddyView();
		}
		
	}
	
	private void notifyRoomInvite(String contact, int roomno){
		invitedList.put(roomno,new ResponseWindow(this,roomno,contact));
	}

	private void notifyNewMsg(int roomno, String contact, String said){
		Room room = roomList.get(roomno);
		if(room!=null)room.newMsg(contact,said);
	}
	
	private void roomCreated(int roomno){
		sendToServer("roomUsers "+roomno);
		roomList.put(roomno,new Room(this,roomno));
		String s = toInvite.poll();
		if(s==null){//there is nothing waiting for a room 
			
		}else{
			inviteContact(s,roomno);
		}
	}
	
	private void notifyError(String[] arr){
		switch(Integer.parseInt(arr[1])){
			case 0:
				NotificationDialog.malformedCommandError();
				break;
			case 1:
				NotificationDialog.commandNotFoundError();
				break;
			case 2:
				NotificationDialog.userNotOnlineError();
				break;
			case 3:
				NotificationDialog.roomDoesNotExistError();
				break;
			case 4:
				NotificationDialog.userAlreadyLoggedIn();
				break;
			case 5:
				NotificationDialog.illegalUsernameError();
				break;
			case 6:
				NotificationDialog.contactNotOnlineError();
				break;
			case 7:
				NotificationDialog.userAlreadyLoggedIn();
				break;
			case 8:
				NotificationDialog.usernameExists();
				break;
			case 9:
				NotificationDialog.InvalidLogin();
				break;
			default:
				throw new RuntimeException("SHOULDN'T GET HERE!!!, received an unrecognized command");
		}
		
		
	}
	
	private synchronized void welcome(String username)throws IOException{//called when a login succeeds
		this.username=username;
		updateOnlineContacts();
		mainwindow.switchToBuddyView();
		
		
	}
	
	
	
	/*
	 * the response to the server -> client command online, which returns a list of all your contacts who are currently online
	 * (just populates the buddy list)
	 */
	private synchronized void onlineResponse(String[] buds){
		List<String> buddies = Arrays.asList(buds);
		buddies.remove("online");
		buddyList.addAll(buddies);
		mainwindow.refreshBuddyView();
	}
	
		
	private void notifyContactEnteredRoom(String contact,int roomno){
		Room r = roomList.get(roomno);
		if(r!=null)r.notifyEnteredRoom(contact);
	}
	
	private void notifyContactLeftRoom(String contact,int roomno){
		Room r = roomList.get(roomno);
		if(r!=null)r.notifyLeftRoom(contact);
	}
	
	
	private void notifyUserTyping(String contact, int roomno){
		Room r = roomList.get(roomno);
		if(r!=null)r.notifyUserTyping(contact);
	}
	
	private void notifyUserIdle(String contact, int roomno){
		Room r = roomList.get(roomno);
		if(r!=null)r.notifyUserIdle(contact);
	}
	
	private void notifyUserEnteredText(String contact, int roomno){
		Room r = roomList.get(roomno);
		if(r!=null)r.notifyUserEnteredText(contact);
	}
	
	/**
	 * notifies the model typing status of this room has changed, where 0 corresponds to idle, 1 corresponds to typing
	 * and 2 corresponds to entered Text
	 * @param roomno room in which typing status has changed
	 * @param newStatus the status of typing 
	 * @throws InvalidStatusException if newstatus is not in the range [0,2]
	 */
	public void notifyChangedTypingStatus(int roomno, int newStatus)throws InvalidStatusException{
		switch(newStatus){
		
			case 0:
				sendToServer("idle "+roomno);
				break;
			case 1:
				sendToServer("typing "+roomno);
				break;
			case 2:
				sendToServer("enteredText "+roomno);
				break;
			default:
				throw new InvalidStatusException();
		}
	}
	
	
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	//^^^^^^^^^^^^^^^^^^Methods that call the GUI^^^^^^^^^^^^^^^^^^^^^^^
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	
	
	
	private enum Command{
		welcome,offline,invite,message,roomcreated,error,
		enter,leave,roomUsers,online,userOnline,typing,idle,enteredText,registerSuccess;
		
		public static boolean contains(String s){
			for(Command c: values()){
				if(c.name().equals(s)) {
				    return true;
				}
			}
			return false;
		}
	}
	
	private class ServerListener implements Runnable{
		private PrintWriter w;
		private BufferedReader in;
		public ServerListener(BufferedReader in,PrintWriter w){
			this.w=w;
			this.in=in;
		}
		
		public void run(){
			String input;
			try{
				while((input = in.readLine())!=null){
					handleInput(input);
				}
				System.out.println("server died");
			}catch(IOException e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}	
		}
	}
}

class InvalidStatusException extends Exception{
	
}


