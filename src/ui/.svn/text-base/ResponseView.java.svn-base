package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;

import client.Model;

/**
 * A JPanel object that has a top-level container of the ResponseWindow object. It
 * prompts the user to accept or decline a request to join a room.
 * @author Jeffrey
 *
 */
@SuppressWarnings("serial")
public class ResponseView extends JPanel {
	
	//this view's model
	private Model model;
	private final int roomno;
	//can be null
	
	//View objects used to display this view
	private final JLabel requestLabel;
	private final JButton acceptButton;
	private final JButton declineButton;
	private final JFrame window;
	//Rep invariant != null

	/**
	 * Constructs the ResponseView object which creates the components, defines the layout, and adds the listeners
	 * @param win The JFrame object that the ResponseView object is contained in.
	 * @param m The Model object that represents the information that the user of the GUI contains.
	 * @param roomno The room number of the room that the ResponseView object is asking the user to respond to.
	 * @param inviter The username of the user who invited the user to the room.
	 */
	public ResponseView(JFrame win, Model m, int roomno, String inviter){
		//initialize model
		window = win;
		model = m;
		this.roomno = roomno;
		
		//create the components
		requestLabel = new JLabel(inviter + " has invited to join Room #" + roomno);
		acceptButton = new JButton("Accept");
		declineButton = new JButton("Decline");
		
		//define layout
		GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
		        
        // get some margins around components by default
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
		        
		// place the components in the layout (which also adds them
		// as children of this view)
        layout.setHorizontalGroup(
        		layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        		.addComponent(requestLabel)
        		.addGroup(layout.createSequentialGroup()
        				.addComponent(acceptButton)
        				.addComponent(declineButton)
        				)
        );
        
        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		.addComponent(requestLabel)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(acceptButton)
        				.addComponent(declineButton)
        				)
        );
        
        //add listeners
        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                acceptInvite();
            }
        });
        
        declineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                declineInvite();
            }
        });
        
	}
	
	//Accepts the invite
	private void acceptInvite(){
		model.acceptInvite(roomno);
		window.setVisible(false);
	}
	
	//Declines the invite
	private void declineInvite(){
		model.declineInvite(roomno);
		window.setVisible(false);
	}
}
