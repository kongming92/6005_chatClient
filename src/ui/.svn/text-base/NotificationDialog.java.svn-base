package ui;

import javax.swing.JOptionPane;

/**
 * Contains static methods for the dialog messages spawned in the GUI.
 * @author Jeffrey
 *
 */
public abstract class NotificationDialog {
	
	/**
	 * Displays a dialog box when the client enters an invalid login.
	 */
	public static void InvalidLogin(){
		JOptionPane.showMessageDialog(null,"Username or Password is incorrect.", "Invalid Login",
			     JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Displays a dialog box when the client registers a non-alphanumeric password.
	 */
	public static void InvalidPassword(){
		JOptionPane.showMessageDialog(null,"Password must be alphanumeric.", "Invalid Password",
			     JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Displays a dialog box when the password and the re-entered password do not match.
	 */
	public static void InvalidConfirmedPassword(){
		JOptionPane.showMessageDialog(null,"Password does not match the confirm password.","Invalid Password",
			     JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Displays a dialog box when the client attempts to register an already existing user.
	 */
	public static void usernameExists(){
		JOptionPane.showMessageDialog(null,"Username is already taken.", "Registration Failed",
			     JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Displays a dialog box when the client registers successfully.
	 */
	public static void registerSuccessful(){
		JOptionPane.showMessageDialog(null,
			    "Register Successful.");
	}
	
	/**
	 * Displays a dialog box when the client attempts to login an already logged in user.
	 */
	public static void userAlreadyLoggedIn(){
		JOptionPane.showMessageDialog(null,"Username already logged in.", "Unsuccessful Login",
			     JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Displays a dialog box when the server returns an malformed command error.
	 */
	public static void malformedCommandError(){
		JOptionPane.showMessageDialog(null,"Malformed Command.",
			    "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Displays a dialog box when the server returns an command not found error.
	 */
	public static void commandNotFoundError(){
		JOptionPane.showMessageDialog(null,"Command Not Found.",
			    "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	
	/**
	 * Displays a dialog box when the user attempts to contact someone while not online.
	 */
	public static void userNotOnlineError(){
		JOptionPane.showMessageDialog(null,"User Not Online.",
			    "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Displays a dialog box when the user attempts to invite a contact who is no longer online.
	 */
	public static void contactNotOnlineError(){
		JOptionPane.showMessageDialog(null,"Contact Not Online.",
			    "Invite Failed", JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Displays a dialog box when the client tries to access a room that does not exist..
	 */
	public static void roomDoesNotExistError(){
		JOptionPane.showMessageDialog(null,"Room does not exist.",
			    "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Displays a dialog box when the client tries to connect to the server but fails.
	 */
	public static void connectionFailedError(){
		JOptionPane.showMessageDialog(null,"Connection Failed.",
			    "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	
	/**
	 * Displays a dialog box when the client triese to register a username that is not composed of alpha numeric characters
	 */
	public static void illegalUsernameError(){
		JOptionPane.showMessageDialog(null,"Username can only contain letters and numbers",
			    "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void hostPortFormatError(){
		JOptionPane.showMessageDialog(null,"hostname or port is formatted incorrectly",
			    "Error", JOptionPane.ERROR_MESSAGE);
	}

}
