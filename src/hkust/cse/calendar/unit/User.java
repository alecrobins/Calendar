package hkust.cse.calendar.unit;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String password;				// User password
	private int id;						// User id
	private boolean isAdmin;
	private String username;

	// Getter of the user id
	public int getID() {		
		return id;
	}

	// Constructor of class 'User' which set up the user id and password
	public User(String _username, String _password) {
		username = _username;
		password = _password;
	}
	
	// Constructor of class 'User' which set up the user id and password
	public User(int _id, String _username, String _password, boolean _isAdmin) {
		username = _username;
		password = _password;
		id = _id; 
		isAdmin = _isAdmin;
	}

	// Another getter of the user id
	public String toString() {
		return username;
	}

	// Getter of the user password
	public String Password() {
		return password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean isAdmin(){
		return isAdmin;
	}

	// Setter of the user password
	public void Password(String pass) {
		password = pass;
	}
}
