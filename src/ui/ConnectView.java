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
import javax.swing.JTextField;

import client.Model;


/**
 * ConnectView is a subclass of JPanel whose root container is the MainWindow.
 * @author Jeffrey
 *
 */
@SuppressWarnings("serial")
public class ConnectView extends JPanel {
	
	//this view's model
	private Model model;
	private final MainWindow frame;
	//can be null
	
	//View objects used to display this view
	private final JLabel messageLabel;
	private final JLabel hostLabel;
	private final JTextField host;
	private final JLabel portLabel;
	private final JTextField port;
	private final JButton connectButton;
	//Rep Invariant: != null
	
	/**
	 * The constructor for the ConnectView object.
	 * @param frame The top-level container that the ConnectView object is contained in.
	 */
	public ConnectView(MainWindow frame){
		this.frame = frame;
		
		//create components
		messageLabel = new JLabel("Enter the server address.");
		hostLabel = new JLabel("Host:");
		host = new JTextField();
		portLabel = new JLabel("Port:");
		port = new JTextField();
		connectButton = new JButton("Connect");
		
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
        				.addComponent(hostLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
        				          GroupLayout.PREFERRED_SIZE)
        				.addComponent(portLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
        				          GroupLayout.PREFERRED_SIZE))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(messageLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
        				          GroupLayout.PREFERRED_SIZE)
        				.addComponent(host, 100, 150, Short.MAX_VALUE)
        				.addComponent(port, 100, 150, Short.MAX_VALUE)
        				.addComponent(connectButton))
        );
        
        layout.setVerticalGroup(
        		layout.createSequentialGroup()
        		.addComponent(messageLabel)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(hostLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
      				          GroupLayout.PREFERRED_SIZE)
        				.addComponent(host, GroupLayout.PREFERRED_SIZE, 25,
      				          GroupLayout.PREFERRED_SIZE))
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
        				.addComponent(portLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
      				          GroupLayout.PREFERRED_SIZE)
        				.addComponent(port, GroupLayout.PREFERRED_SIZE, 25,
        				          GroupLayout.PREFERRED_SIZE))
        		.addComponent(connectButton)
        );
        
        //add listeners
        
        host.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    connect(host.getText(), port.getText());
                }
            }
        });
        
        port.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                	connect(host.getText(), port.getText());
                }
            }
        });
        
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event){
            	connectButton.setEnabled(false);
            	connect(host.getText(), port.getText());
            }
        });
	}
	
	/**
	 * used only for debugging purposes, connects to this username and host
	 * @param host host you are trying to connect to
	 * @param port port you are trying to connect to
	 */
	public void debugConnect(String host,String port){
		//Attempts to connect to the server address
	
		try{
			model = new Model(host, Integer.parseInt(port));
			model.start(this.frame);
		}
		catch(IOException e){
			NotificationDialog.connectionFailedError();
			connectButton.setEnabled(true);
		}
		catch(NumberFormatException e){
			NotificationDialog.hostPortFormatError();
			connectButton.setEnabled(true);
		}
	}
	//Attempts to connect to the server address
	private void connect(String host, String port){
		try{
			//creates the model object that represents the data of the user using the GUI
			model = new Model(host, Integer.parseInt(port));
			model.start(this.frame);
		}
		catch(IOException e){
			NotificationDialog.connectionFailedError();
			connectButton.setEnabled(true);
		}
		catch(NumberFormatException e){
			NotificationDialog.hostPortFormatError();
			connectButton.setEnabled(true);
		}
		
	}
	
	/**
	 * Returns the model created after connecting to the server.
	 * @return model which is the Model object.
	 */
	public Model getModel(){
		return model;
	}
	


}
