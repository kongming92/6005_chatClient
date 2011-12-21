package ui;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;

import client.Model;

import java.awt.Dimension;


/**
 * The MainWindow of the GUI that is a subclass of JFrame. It is the top-level container for the 
 *  original login page, the buddy list page, the connect page, and the register page.
 * @author Jeffrey
 *
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame{
	
	//JPanels that the MainWindow displays
	private LoginView loginView;
	private BuddyListView buddyView;
	private RegisterView registerView;
	private ConnectView connectView;
	private Model m;
	
	/**
	 * Constructs the MainWindow object with its initial component being the loginView.
	 * @throws IOException
	 */
	public MainWindow()throws IOException{
		setTitle("IM Client");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(250,200));
		setMinimumSize(new Dimension(250,200));
        connectView = new ConnectView(this);
        add(connectView, BorderLayout.CENTER);
        pack(); 
	}

	/**
	 * used only for debugging
	 * @return the connect view of this model
	 */
	public ConnectView getConnectView(){
		return connectView;
	}
	/**
	 * Switches the JPanel inside the MainWindow to the BuddyListView after a successful login.
	 * @throws IOException
	 */
	public void switchToBuddyView() throws IOException{
		System.out.println("switch to buddy view");
		
		setVisible(false);
		getContentPane().remove(loginView);
		buddyView = new BuddyListView(m);
		getContentPane().add(buddyView, BorderLayout.CENTER);
		setMinimumSize(new Dimension(180,500));
		setMaximumSize(new Dimension(180,500));
		setPreferredSize(new Dimension(180,500));
		
		getContentPane().validate();
		getContentPane().repaint();
		setVisible(true);
	}
	
	/**
	 * Switches the JPanel inside the MainWindow to the RegisterView for a new user.
	 * @throws IOException
	 */
	public void switchToRegisterView() throws IOException{
		System.out.println("switch to register view");
		
		setVisible(false);
		getContentPane().remove(loginView);
		registerView = new RegisterView(m);
		setPreferredSize(new Dimension(325,210));
		setMinimumSize(new Dimension(325,210));
		
		getContentPane().add(registerView, BorderLayout.CENTER);
		getContentPane().validate();
		getContentPane().repaint();
		setVisible(true);
	}
	
	/**
	 * Switches the JPanel inside the MainWindow to the LoginView after a new user has registered.
	 * @throws IOException
	 */
	public void switchToLoginView() throws IOException{
		System.out.println("switch to login view");
		
		setVisible(false);
		removeViews();
		setPreferredSize(new Dimension(240,200));
		setMinimumSize(new Dimension(215,200));
		
		getContentPane().add(loginView, BorderLayout.CENTER);
		getContentPane().validate();
		getContentPane().repaint();
		setVisible(true);
	}
	
	private void removeViews(){
		if(loginView!=null)getContentPane().remove(loginView);
		if(buddyView!=null)getContentPane().remove(buddyView);
		if(registerView!=null)getContentPane().remove(registerView);
		if(connectView!=null)getContentPane().remove(connectView);
	}
	
	/**
	 * Switches the JPanel inside the MainWindow from the ConnectView to the LoginView after the server
	 * has connected to the client and the model has been created.
	 */
	public void switchFromConnectView(){
		System.out.println("switch to login view from connect view");
		
		setVisible(false);
		getContentPane().remove(connectView);
		m = connectView.getModel();
		loginView = new LoginView(m);
		getContentPane().add(loginView, BorderLayout.CENTER);
		setPreferredSize(new Dimension(240,200));
		setMinimumSize(new Dimension(215,200));
		
		getContentPane().validate();
		getContentPane().repaint();
		setVisible(true);
	}
	
	
	/**
	 * Refreshes the buddylistview after changes occurred in the model.
	 */
	public void refreshBuddyView(){//Added by Evan
		buddyView.refresh();
	}
	

}
