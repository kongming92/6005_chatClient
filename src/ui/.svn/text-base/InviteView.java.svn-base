package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;

import client.Model;
import client.Room;

import java.util.Iterator;

@SuppressWarnings("serial")
public class InviteView extends JPanel {
	//this view's model
	private Model model;
	private Room room;
	private DefaultListModel listModel;
	//can be null
	
	//View objects used to display this view
	private final JLabel listLabel;
	private final JList list;
	private final JScrollPane listScroller;
	private final JButton inviteButton;
	//Rep invariant != null
	
	
	/**
	 * Constructs the InviteView object.
	 * @param m The model that corresponds to the user using the GUI
	 * @param r The room that corresponds to the room that the InviteView object spawns from.
	 */
	public InviteView(Model m, Room r){
		//initializes models
		model = m;
		room = r;
		listModel = new DefaultListModel();
		refresh();
		
		//create components
		listLabel = new JLabel("Select a user to invite");
		list = new JList(listModel);
		listScroller = new JScrollPane(list);
		inviteButton = new JButton("Invite to chat");
		
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
        		.addComponent(listLabel)
        		.addComponent(listScroller)
        		.addComponent(inviteButton)
        );
        
        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		.addComponent(listLabel)
        		.addComponent(listScroller)
        		.addComponent(inviteButton)
        );
        
        inviteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                inviteUser();
            }
        });
        
	}
	
	/**
	 * Refreshes the invite list based on changes to the users online/in the room.
	 */
	public void refresh(){
		listModel.clear();
		Set<String> l = model.getBuddyList();
		Iterator <String> i = l.iterator();
		while(i.hasNext()){
			String username = i.next();
			if(!(room.getMembersList().keySet().contains(username) || room.getInvited().contains(username))){ // makes sure not in room or already invited
				listModel.addElement(username);
			}
		}
	}
	
	//Invites the user
	private void inviteUser(){
		if (list.isSelectionEmpty()) return; // no user selected, can't switch to it
        int selectedIndex = list.getMinSelectionIndex();
        model.inviteContact((String) listModel.get(selectedIndex), room.getNumber());
	}
	
}
