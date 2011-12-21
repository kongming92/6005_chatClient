package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import client.Model;
import client.Room;

import java.awt.Dimension;

/**
 * The RoomWindow of the GUI that is a subclass of JFrame. It is the top-level container for each room.
 * @author Jeffrey
 *
 */
@SuppressWarnings("serial")
public class RoomWindow extends JFrame{
	
	private final RoomView roomView;
	
	/**
	 * Constructs the RoomWindow object by adding the roomView object as its child.
	 * @param m The model representing the client information.
	 * @param r The model representing the room information
	 */
	public RoomWindow(Model m, Room r){
		setTitle("Room #" + r.getNumber());
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(450,275));
		setLocation(300, 300);
        roomView = new RoomView(this,m,r);
        add(roomView, BorderLayout.CENTER);
        pack();
        setVisible(true);
	}
	
	/**
	 * Refreshes the room and updates the member list of who is in the room. Calls the view's refresh method.
	 */
	public synchronized void refresh(){
		roomView.refresh();
	}
	
	/**
	 * Refreshes the room and updates the member list and the chat log.
	 * @param username The username of the user who sent the message.
	 * @param tokens The String array of text tokens that the user sent.
	 * @param styles The String array of style tokens that the user sent.
	 */
	public synchronized void newMsg(String username,String[] tokens, String[] styles){
		roomView.refresh(username, tokens, styles);
	}
	

}
