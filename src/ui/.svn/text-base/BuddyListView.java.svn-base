package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;

import client.Model;

import java.awt.Dimension;


/**
 * The GUI for the BuddyList. Its root container is the MainWindow which it is contained in.
 */
@SuppressWarnings("serial")
public class BuddyListView extends JPanel{
	
	//this view's model
	private Model model;
	private DefaultListModel listModel;
	//can be null
	
	//View objects used to display this view
	private final JLabel userListLabel;
	private final JList userList;
	private final JButton createRoomButton;
	private final JButton logoutButton;
	private final JScrollPane listScroller;
	//Rep Invariant: != null
	
	/**
	 * Constructs a BuddyListView object. Initializes the components, creates the layout, and initializes the listeners.
	 * @param m The model object associated with this GUI.
	 */
	public BuddyListView(Model m){
		//creates the model
		this.model = m;
		listModel = new DefaultListModel();
		System.out.println("BEFORE REFRESH "+(m==null));
		refresh();
		
		//create the components
		userListLabel = new JLabel(model.getUsername() + "'s User List");
		
		userList = new JList(listModel);
		userList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		userList.setVisibleRowCount(-1);
		userList.setAlignmentX(CENTER_ALIGNMENT);
		//userList.setSize(new Dimension(75,300));
		
		createRoomButton = new JButton("Create a New Room");
		logoutButton = new JButton("Logout");
		
		listScroller = new JScrollPane(userList);
		listScroller.setPreferredSize(new Dimension(120,300));
		listScroller.setAlignmentX(CENTER_ALIGNMENT);
		listScroller.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		listScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		//define layout
		GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        //setPreferredSize(new Dimension(400,400));
        
        // get some margins around components by default
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        
        // place the components in the layout (which also adds them
        // as children of this view)
        layout.setHorizontalGroup(
        		layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        		.addComponent(userListLabel)
        		.addComponent(listScroller, GroupLayout.PREFERRED_SIZE, 70,
				          GroupLayout.PREFERRED_SIZE)
        		.addComponent(createRoomButton)
        		.addComponent(logoutButton)
        );
        
        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		.addComponent(userListLabel)
        		.addComponent(listScroller, GroupLayout.PREFERRED_SIZE, 300,
				          GroupLayout.PREFERRED_SIZE)
        		.addComponent(createRoomButton)
        		.addComponent(logoutButton)
        );
        
        //add listeners
        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                // respond only on double-click
                if (event.getClickCount() == 2) {
                    createRoomAndInvite();
                }
            }
        });
        
        userList.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    createRoomAndInvite();
                }
            }
        });
        
        createRoomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                createRoom();
            }
        });
        
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                logout();
            }
        });
        //setVisible(true);
	}
	
	/**
	 * Refreshes the view for the user to see based on changes in the model.
	 * Modifies: listModel.
	 */
	public void refresh(){
		System.out.println("LISTMODEL IS NULL?"+(listModel==null));
		listModel.clear();
		Set<String> l = model.getBuddyList();
		
		//debugging purposes
		System.out.println("buddies online");
		for(String s: l){
			System.out.print(s+" ");
		}
		System.out.println();
		
		//repopulates buddy list
		Iterator <String> i = l.iterator();
		while(i.hasNext()){
			listModel.addElement(i.next());
		}
	}
	
	//Simultaneously creates a room and invites the selected user to that room.
	private void createRoomAndInvite(){
		if (userList.isSelectionEmpty()) return; // no buddy selected, can't switch to it
        int selectedIndex = userList.getMinSelectionIndex();
        String username = (String) listModel.get(selectedIndex);
        model.createAndInvite(username);
	}
	
	//Logs the user out.
	private void logout(){
		try{
			model.logout();
		}catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	//Creates a room.
	private void createRoom(){
		model.createRoom();
	}
	
}
