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
	private String firstname;
	private String lastname;
	private String email;
	
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
	
	// Constructor of class 'User' which set up the user id and password
	public User(String _username, String _password, String _firstname, String _lastname, String _email, boolean _isAdmin) {
		username = _username;
		password = _password;
		isAdmin = _isAdmin;
		setFirstname(_firstname);
		setLastname(_lastname);
		setEmail(_email);
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

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setID(int _id){
		this.id = _id;
	}
}
