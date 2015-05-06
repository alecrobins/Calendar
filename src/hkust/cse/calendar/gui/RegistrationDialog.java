package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageNullImpl;
import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.unit.User;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegistrationDialog extends JFrame implements ActionListener {
	private JTextField userName;
	private JPasswordField password;
	private JButton closeButton;
	private JButton registerButton;
	private JComboBox setAdmin;
	
	private final String[] makeAdmin= { "User", "Admin" };

	public RegistrationDialog() // Create a dialog to register
	{

		setTitle("Register");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		Container contentPane;
		contentPane = getContentPane();

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

		JPanel messPanel = new JPanel();
		messPanel.add(new JLabel(
				"Please choose a user name and password to register."));
		top.add(messPanel);

		JPanel namePanel = new JPanel();
		namePanel.add(new JLabel("User Name:"));
		userName = new JTextField(15);
		namePanel.add(userName);
		top.add(namePanel);

		JPanel pwPanel = new JPanel();
		pwPanel.add(new JLabel("Password:  "));
		password = new JPasswordField(15);
		pwPanel.add(password);
		top.add(pwPanel);
		
		JPanel adminPanel = new JPanel();
		adminPanel.add(new JLabel("Wanna be a User or an Admin?:"));
		setAdmin = new JComboBox();
		setAdmin.addActionListener(this);
		setAdmin.setPreferredSize(new Dimension(100, 20));
		for (int cnt = 0; cnt <2; cnt++)
			setAdmin.addItem(makeAdmin[cnt]);
		setAdmin.setSelectedIndex(0);
		adminPanel.add(setAdmin);
		
		top.add(adminPanel);

		JPanel signupPanel = new JPanel();
		registerButton = new JButton("Register now");
		registerButton.addActionListener(this);
		signupPanel.add(registerButton);

		contentPane.add("North", top);

		JPanel butPanel = new JPanel();
		butPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		closeButton = new JButton("Close program");
		closeButton.addActionListener(this);
		
		butPanel.add(signupPanel);
		butPanel.add(closeButton);
		

		contentPane.add("South", butPanel);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		 if (e.getSource() == registerButton) {
			// Save the user to the DataBase

			 
			 ApptStorageSQLImpl dataBase= new ApptStorageSQLImpl();
			 
			 Boolean isAdmin = false;

				String name = userName.getText().trim(); // getting the username
				String pass = password.getPassword().toString(); // getting the
																	// password
				if(setAdmin.getSelectedItem().equals("Admin")){  //check if he/she is admin
					isAdmin=true;
				}
			 
			if(dataBase.isUserNameAvailable(name)){
			User newUser=new User(name,pass,null,null," ",isAdmin);
			int userID=dataBase.createUser(newUser);
			newUser.setID(userID);
			
			JOptionPane.showMessageDialog(null, "Registration is successfull, Close this window please", "Congratulations!",
                    JOptionPane.INFORMATION_MESSAGE);
		
			LoginDialog loginDialog = new LoginDialog();
			setVisible(false);
			
			
			}
				
			System.out.println("check");


		} else if (e.getSource() == closeButton) {
			int n = JOptionPane.showConfirmDialog(null, "Exit Program ?",
					"Confirm", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION)
				System.exit(0);
		}
	}

	// This method checks whether a string is a valid user name or password, as
	// they can contains only letters and numbers
	public static boolean ValidString(String s) {
		char[] sChar = s.toCharArray();
		for (int i = 0; i < sChar.length; i++) {
			int sInt = (int) sChar[i];
			if (sInt < 48 || sInt > 122)
				return false;
			if (sInt > 57 && sInt < 65)
				return false;
			if (sInt > 90 && sInt < 97)
				return false;
		}
		return true;
	}
}

