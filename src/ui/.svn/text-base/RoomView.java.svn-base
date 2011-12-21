package ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import client.Model;
import client.Room;


/**
 * The RoomView is the JPanel whose top-level container is a RoomWindow object.
 * @author Jeffrey
 *
 */
@SuppressWarnings("serial")
public class RoomView extends JPanel{
	
	//the view's model
	private Model model;
	private Room room;
	private DefaultListModel listModel;
	private StyledDocument tabletModel;
	private final JFrame frame;
	//can be null
	
	//View objects used to display this view
	private final JLabel listLabel;
	private final JButton inviteButton;
	private final JList list;
	private final JScrollPane listScroller;
	private final JScrollPane tabletScroller;
	private final JScrollPane areaScroller;
	private final JTextArea textArea;
	private final JTextPane chatHistory;
	//Rep invariant != null
	
	private Timer timer;
	private boolean inFocus;
	
	/**
	 * Constructs the RoomView object. Initializes the model, creates the components, defines the layout, and adds listeners.
	 * @param m - the Model for the RoomView
	 * @param r - the client-side Room object represented by the RoomView 
	 */
	public RoomView(JFrame frame, Model m, Room r){
		//initialize models
		this.frame = frame;
		model = m;
		room = r;
		listModel = new DefaultListModel();
		timer = new Timer();
		refresh();
		inFocus = true;
		
		//create the components
		listLabel = new JLabel("In this room...");
		inviteButton = new JButton("Invite a new user");
		
		textArea = new JTextArea(5,30); //Where you type text
		textArea.setText("Enter your text here.");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		areaScroller = new JScrollPane(textArea); //Scroll for where you type text
		areaScroller.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScroller.setPreferredSize(new Dimension(250, 250)); 
		
		chatHistory = new JTextPane(); //Where the typed text from others/you appears.
		tabletModel = chatHistory.getStyledDocument();
		chatHistory.setEditable(false);
		addStylesToDocument();
		tabletScroller = new JScrollPane(chatHistory); //scroll for chat history.
        tabletScroller.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tabletScroller.setPreferredSize(new Dimension(250, 145)); 
        tabletScroller.setMinimumSize(new Dimension(10, 10)); 
        
        list = new JList(listModel); //list of people in the room
		listScroller = new JScrollPane(list); //scroll for people in list
		listScroller.setVerticalScrollBarPolicy(
                		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScroller.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		listScroller.setPreferredSize(new Dimension(250, 250)); 
		
		//define layout
		GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
		        
        // get some margins around components by default
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
		        
		// place the components in the layout (which also adds them
		// as children of this view)
		layout.setHorizontalGroup(
        		layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(tabletScroller,200,300,Short.MAX_VALUE)
        				.addComponent(areaScroller,200,300,Short.MAX_VALUE))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(listLabel)
        				.addComponent(list,100,100,Short.MAX_VALUE)
        				.addComponent(inviteButton))
        );
		
		layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(tabletScroller)
        				.addGroup(layout.createSequentialGroup()
        						.addComponent(listLabel)
        						.addComponent(list,100,200,Short.MAX_VALUE)
        						.addComponent(inviteButton)))
        		.addComponent(areaScroller,45,45,Short.MAX_VALUE)
        );
        
        //add listeners
        inviteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                inviteUser();
            }
        });
        
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {  // Press enter key
                    sendMessage();
                    hasNoText();
                    event.consume();
                }
            }
            @Override
            public void keyReleased(KeyEvent event) { //Releasing key
            	timer.cancel();
            	timer = new Timer();
            	if (event.getKeyCode() != KeyEvent.VK_ENTER) { //key is enter
            	    if (textArea.getText().trim().equals("")) {
                        hasNoText();
                    }
                    else {									//key is not enter
                        timer.schedule(new TimerTask() {
                            public void run(){
                                hasEnteredText();
                            }
                        }, 1000);
                        isTyping();
                    }
            	}
                
            }
        });
        
        frame.addWindowListener(new WindowAdapter(){ //Leave room when window closed
        	public void windowClosing(WindowEvent event){
        		model.leaveRoom(room.getNumber());
        	}
        });
        
        frame.addWindowFocusListener(new WindowAdapter(){
        	@Override
        	public void windowGainedFocus(WindowEvent event){ //Room back in focus
        		roomFocused();
        	}
        	@Override
        	public void windowLostFocus(WindowEvent event){ //Room leaving focus.
        		roomUnfocused();
        	}
        });
        
        tabletScroller.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() { //Put scrollbar at bottom of chat history  
            public void adjustmentValueChanged(AdjustmentEvent e) {  
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
            }});
	}
	
	/**
	 * Refreshes the members list.
	 * Modifies: listModel
	 */
	public void refresh(){
		listModel.clear();
		Set<String> l = room.getMembersList().keySet();
		Iterator <String> i = l.iterator();
		while(i.hasNext()){
			String username = i.next();
			//typing statuses
			if(room.getMembersList().get(username) == 0){
				listModel.addElement(username);
			}
			else if(room.getMembersList().get(username) == 1){
				listModel.addElement(username + " is typing...");
			}
			else
			{
				listModel.addElement(username + " has entered text.");
			}
		}
	} 
	
	/**
	 * Refreshes the tablet and the members list.
	 * Modifies: tabletModel and listModel
	 * @param username The username of the person who sent text that needs to be updated
	 * @param tokens The String array that contains the text tokens of the message.
	 * @param styles The String array that contains the style tokens of the message.
	 * Requires: Should be the same length as tokens.
	 */
	public void refresh(String username, String[] tokens, String[] styles){
		refresh();
		
		if(!inFocus){ // If window out of focus change the title to show when a new message is sent.
			frame.setTitle("New message...");
		}
		try{
			tabletModel.insertString(tabletModel.getLength(), username + ": ", tabletModel.getStyle("bold"));
			
			for(int i = 0; i < tokens.length-1; i++){
				tabletModel.insertString(tabletModel.getLength(), tokens[i], tabletModel.getStyle(styles[i]));
			}
			tabletModel.insertString(tabletModel.getLength(), tokens[tokens.length-1] + "\n", tabletModel.getStyle(styles[tokens.length - 1]));
		}
		catch(BadLocationException e){
			e.printStackTrace();
		}
	}
	
	//adds styles to the document for the tablet to call
	private void addStylesToDocument(){
		Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

		//regular text
		Style regular = tabletModel.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");

		//italic text
		Style s = tabletModel.addStyle("italic", regular);
		StyleConstants.setItalic(s, true);

		//bold text
		s = tabletModel.addStyle("bold", regular);
		StyleConstants.setBold(s, true);
		
		//strikethrough text
		s = tabletModel.addStyle("strikethrough", regular);
		StyleConstants.setStrikeThrough(s, true);
		
		//bold and italic text
		s = tabletModel.addStyle("bolditalic", regular);
		StyleConstants.setBold(s, true);
		StyleConstants.setItalic(s, true);
		
		//bold and strikethrough text
		s = tabletModel.addStyle("boldstrikethrough", regular);
		StyleConstants.setBold(s, true);
		StyleConstants.setStrikeThrough(s, true);
		
		//italic and strikethrough text
		s = tabletModel.addStyle("italicstrikethrough", regular);
		StyleConstants.setItalic(s, true);
		StyleConstants.setStrikeThrough(s, true);
		
		//bold, italic, and striekthrough text
		s = tabletModel.addStyle("bolditalicstrikethrough", regular);
		StyleConstants.setBold(s,true);
		StyleConstants.setItalic(s, true);
		StyleConstants.setStrikeThrough(s, true);
		
		//adds emoticons
		s = tabletModel.addStyle(":D", regular); // :D
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon colonD = createImageIcon("emoticon/ColonD.gif");
        if (colonD != null) {
            StyleConstants.setIcon(s, colonD);
        }
        
        s = tabletModel.addStyle(":)", regular); // :)
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon colonOpen = createImageIcon("emoticon/ColonClose.gif");
        if (colonOpen != null) {
            StyleConstants.setIcon(s, colonOpen);
        }
        
        s = tabletModel.addStyle(":(", regular); // :(
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon colonClose = createImageIcon("emoticon/ColonOpen.gif");
        if (colonClose != null) {
            StyleConstants.setIcon(s, colonClose);
        }
        
        s = tabletModel.addStyle(":O", regular); // :O
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon colonO = createImageIcon("emoticon/ColonO.gif");
        if (colonO != null) {
            StyleConstants.setIcon(s, colonO);
        }
        
        s = tabletModel.addStyle(":P", regular); //:P
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon colonP = createImageIcon("emoticon/ColonP.gif");
        if (colonP != null) {
            StyleConstants.setIcon(s, colonP);
        }
        
        s = tabletModel.addStyle("8)", regular); //8)
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon eightClose = createImageIcon("emoticon/8Close.gif");
        if (eightClose != null) {
            StyleConstants.setIcon(s, eightClose);
        }
        
        s = tabletModel.addStyle(";)", regular); //;)
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon semiClose = createImageIcon("emoticon/SemicolonClose.gif");
        if (semiClose != null) {
            StyleConstants.setIcon(s, semiClose);
        }
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = RoomView.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
	
	//invites the user
	private void inviteUser(){
		//Spawns user list
		model.createInviteList(room.getNumber());
	}
	
	//sends message
	private void sendMessage(){
		model.say(textArea.getText(), room.getNumber());
		textArea.setText("");
	}
	
	/*
	 * Functions to change the typing status within a room. 
	 */
	private void isTyping(){
		room.changeStatus(1);
	}
	
	private void hasEnteredText(){
		room.changeStatus(2);
	}
	
	private void hasNoText(){
		room.changeStatus(0);
	}
	
	private void roomFocused(){
		inFocus = true;
		frame.setTitle("Room #" + room.getNumber());
	}
	
	private void roomUnfocused(){
		inFocus = false;
	}

}
