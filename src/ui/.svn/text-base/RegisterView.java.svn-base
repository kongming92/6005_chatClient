package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import client.Model;

/**
 * RegisterView is a subclass of JPanel whose root container is the MainWindow.
 * It contains registration information for a new username.
 * @author Jeffrey
 *
 */
@SuppressWarnings("serial")
public class RegisterView extends JPanel {
	
	//this view's model
	private Model model;
	//can be null
	
	//View objects used to display this view
	private final JLabel usernameLabel;
	private final JTextField usernameField;
	private final JLabel passwordLabel;
	private final JPasswordField passwordField;
	private final JLabel confirmLabel;
	private final JPasswordField confirmField;
	private final JButton register;
	private final JButton back;
	//Rep invariant != null
	
	/**
	 * Constructs the RegisterView object, initializes components, defines the layout, and adds the listeners
	 * @param m The Model object that keeps track of the information that the user using the GUI has. 
	 */
	public RegisterView(Model m){
		//initialize model
		model = m;
		
		//create components
		usernameLabel = new JLabel("Username: ");
		usernameField = new JTextField();
		passwordLabel = new JLabel("Password: ");
		passwordField = new JPasswordField();
		confirmLabel = new JLabel("Re-enter Password: ");
		confirmField = new JPasswordField();
		register = new JButton("Register");
		back = new JButton("Back to login page");
		
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
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        				.addComponent(usernameLabel)
        				.addComponent(passwordLabel)
        				.addComponent(confirmLabel))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(usernameField, 140, 150, Short.MAX_VALUE)
        				.addComponent(passwordField, 140, 150, Short.MAX_VALUE)
        				.addComponent(confirmField, 140, 150, Short.MAX_VALUE)
        				.addComponent(register)
        				.addComponent(back))
        );
        
        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(usernameLabel)
        				.addComponent(usernameField, GroupLayout.PREFERRED_SIZE, 25,
        				          GroupLayout.PREFERRED_SIZE))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(passwordLabel)
        				.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 25,
        				          GroupLayout.PREFERRED_SIZE))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(confirmLabel)
        				.addComponent(confirmField, GroupLayout.PREFERRED_SIZE, 25,
        				          GroupLayout.PREFERRED_SIZE))
        		.addComponent(register)
        		.addComponent(back)
        );
        
        //add listeners
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    registerUser(usernameField.getText(), passwordField.getPassword(), confirmField.getPassword());
                }
            }
        });
        
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    registerUser(usernameField.getText(), passwordField.getPassword(), confirmField.getPassword());
                }
            }
        });
        
        confirmField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    registerUser(usernameField.getText(), passwordField.getPassword(), confirmField.getPassword());
                }
            }
        });
        
        register.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	registerUser(usernameField.getText(), passwordField.getPassword(), confirmField.getPassword());
            }
        });
        
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
            	goBack();
            }
        });
	}
	
	//Registers the user
	private void registerUser(String username, char [] password, char [] confirmPassword){
		
		model.register(username,new String(password), new String(confirmPassword));
		
		
	}
	
	//Goes back to the login page
	private void goBack(){
		try{
			model.loginView();
		}catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

}
