package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import client.Model;
import client.Room;


/**
 * The InviteWindow of the GUI that is a subclass of JFrame. It is the top-level container for the 
 *  InviteView.
 * @author Jeffrey
 *
 */
@SuppressWarnings("serial")
public class InviteWindow extends JFrame {
	
	private final InviteView inviteView;
	
	/**
	 * The constructor for the InviteWindow object.
	 * @param m The model for the user that the InviteWindow displays.
	 * @param r The model for the room that the InviteWindow displays.
	 */
	public InviteWindow(Model m,Room r){
		setTitle("Invite List");
		setLayout(new BorderLayout());
        inviteView = new InviteView(m,r);
        add(inviteView, BorderLayout.CENTER);
        pack(); 
        setVisible(true);
	}
	
	/**
	 * Refreshes the members of the invite list based on changes.
	 */
	public void refreshMembers(){
		inviteView.refresh();
	}

}
