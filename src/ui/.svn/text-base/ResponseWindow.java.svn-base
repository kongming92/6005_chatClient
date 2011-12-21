package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import client.Model;

/**
 * The ResponseWindow is the top-level container of the window that prompts the user to accept or decline an invitation to the room
 * @author Jeffrey
 *
 */
@SuppressWarnings("serial")
public class ResponseWindow extends JFrame {
	
	private final ResponseView responseView;
	
	/**
	 * The Constructor of the ResponseWindow object.
	 * @param m The Model object that contains the information of the user that is called on to respond to the invite request.
	 * @param roomno The room number of the room that the invite comes from.
	 * @param inviter The username of the user who sent the invite.
	 */
	public ResponseWindow(Model m, int roomno, String inviter){
		setTitle("Chat Request");
		setLayout(new BorderLayout());
		responseView = new ResponseView(this, m , roomno, inviter);
		add(responseView, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

}
