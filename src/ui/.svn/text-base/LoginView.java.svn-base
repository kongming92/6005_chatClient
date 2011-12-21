package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import client.Model;



/**
 * LoginView is a subclass of JPanel whose root container is the MainWindow.
 * @author Jeffrey
 *
 */
@SuppressWarnings("serial")
public class LoginView extends JPanel{
	
	//this view's model
	private Model model;
	//can be null
	
	//View objects used to display this view
	private final JLabel messageLabel;
	private final JLabel usernameLabel;
	private final JTextField username;
	private final JLabel passwordLabel;
	private final JPasswordField password;
	private final JButton loginButton;
	private final JButton registerButton;
	//Rep Invariant: != null
	
	/**
	 * Constructs the LoginView object, initializes the JComponents, defines the layout, and adds the listeners.
	 * @param m The Model for the GUI
	 */
	public LoginView(Model m){
		//initialize model
		this.model = m;
		
		//create the components
		messageLabel = new JLabel("Enter Login.");
		usernameLabel = new JLabel("Username:");
		username = new JTextField();
		loginButton = new JButton("Login");
		passwordLabel = new JLabel("Password:");
		password = new JPasswordField();
		registerButton = new JButton("Register");
		
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
        				.addComponent(passwordLabel))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(messageLabel)
        				.addComponent(username, 100, 150, Short.MAX_VALUE)
        				.addComponent(password, 100, 150, Short.MAX_VALUE)
        				.addComponent(loginButton)
        				.addComponent(registerButton))
        );
        
        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		.addComponent(messageLabel)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(usernameLabel)
        				.addComponent(username, GroupLayout.PREFERRED_SIZE, 25,
        				          GroupLayout.PREFERRED_SIZE))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(passwordLabel)
        				.addComponent(password, GroupLayout.PREFERRED_SIZE, 25,
        				          GroupLayout.PREFERRED_SIZE))
        		.addComponent(loginButton)
        		.addComponent(registerButton)
        );
        
        // add listeners for user input
        username.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginUser(username.getText(), password.getPassword());
                }
            }
        });
        
        password.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginUser(username.getText(), password.getPassword());
                }
            }
        });
        
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loginUser(username.getText(), password.getPassword());
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event){
                register();
            }
        });
        
	}
	
	//send login user to model
	private void loginUser(String s, char[] p){
		System.out.println("login user");
		model.login(s, new String(p));
	}
	
	//sends the register command
	private void register(){
		try{
			model.registerView();
		}catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
}