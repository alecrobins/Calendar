package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageNullImpl;
import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.controllers.GroupController;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.GroupResponse;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.xml.ws.Response;


public class LoginDialog extends JFrame implements ActionListener
{
	private JTextField userName;
	private JPasswordField password;
	private JButton button;
	private JButton closeButton;
	private JButton signupButton;
	
	private ApptStorageSQLImpl db;

	public LoginDialog()		// Create a dialog to log in
	{

		setTitle("Log in");

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
		messPanel.add(new JLabel("Please input your user name and password to log in."));
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

		JPanel signupPanel = new JPanel();
		signupPanel.add(new JLabel("If you don't have an account, please sign up:"));
		signupButton = new JButton("Sign up now");
		signupButton.addActionListener(this);
		signupPanel.add(signupButton);
		top.add(signupPanel);

		contentPane.add("North", top);

		JPanel butPanel = new JPanel();
		butPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		button = new JButton("Log in (You Need to Register First)");
		button.addActionListener(this);
		butPanel.add(button);

		closeButton = new JButton("Close program");
		closeButton.addActionListener(this);
		butPanel.add(closeButton);

		contentPane.add("South", butPanel);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);	

	}


	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == button)
		{
			String name= userName.getText().trim();   //getting the username
//			String pass= password.getPassword().toString();  // getting the password
			String pass = new String(password.getPassword());
						
			ApptStorageSQLImpl dataBase= new ApptStorageSQLImpl();
					
			boolean check=dataBase.logInUser(name, pass);
			
			if(dataBase.logInUser(name, pass)){  //check if the user is in dataBase   ///////////UPDATE HERE
						
				User user = dataBase.getUser(name);
				
				checkIfGroupEvent(user);
				
				System.out.println("oley");
				
				CalGrid grid = new CalGrid(new ApptStorageControllerImpl(new ApptStorageSQLImpl(user)));
				grid.setCurrUser(user);
				setVisible( false );
				
				db = new ApptStorageSQLImpl(user);
				
				 List<GroupResponse> responses = db.getPurposedGroupEventTimeSlots(user);
				
				 if (responses.size() >= 1){
			
					for(int j = 0; j < responses.size(); ++j){
						
					 List<TimeSpan> slots = responses.get(j).getProposedTimeslots();
					 
					 int groupID = responses.get(j).getGroupID();
					 int intiatorID = responses.get(j).getIntiatorID();
					
					 InvitationDialog dialog = new InvitationDialog(slots, groupID, intiatorID, user); 
				 	
					}
				 
				}
				 
			}
			else{
			System.out.println("Please register first");
							
							 JOptionPane.showMessageDialog(null, "Check your username or password again!!!", "Something is Wrong",
			                         JOptionPane.INFORMATION_MESSAGE);
							
			}
			
		}
		
		else if(e.getSource() == signupButton)
		{
			// Create a new account

			RegistrationDialog reg=new RegistrationDialog();
			setVisible(false);


		}
		else if(e.getSource() == closeButton)
		{
			int n = JOptionPane.showConfirmDialog(null, "Exit Program ?",
					"Confirm", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION)
				System.exit(0);			
		}
	}
	
	private void checkIfGroupEvent(User user){
		// Check if user is logged in

		// Request from the db all the appts from the user

//		// load those apps into the hash map . . . this part is not implemented yet
//		ApptStorageSQLImpl asql = new ApptStorageSQLImpl(user);
//		CalGrid grid = new CalGrid(new ApptStorageControllerImpl(asql));
//		setVisible( false );
//
//		//IF USER HAS AN INVITE
//		//GET INVITE INFO== TIMES, USERS
//		if (asql.hasInvite()){
//			for (int i = 0; i < asql.inviteList.size(); i++){
//				List<User> userList = asql.inviteList.get(i).getEventUsers();
//				HashMap<User, List<Appt>> usMap = new HashMap<User, List<Appt>>();
//				for (User u: userList){
//					usMap.put(u, asql.getAllEvents(u.getID()));
//				}
//				List<Timestamp> dates = asql.inviteList.get(i).getProposedTimes();
//
//				MultipleUserSchedule mus = new MultipleUserSchedule("Invitee", grid, usMap, dates);
//			}
//		}
//
//		if (asql.hasResponses()){
//			for (int i = 0; i < asql.responseList.size(); i++){
//				if (asql.responseList.get(i) == reject){
////					alertMessage("Group Event Was Rejected");
//				}
//				else{
//					GroupController gc = new GroupController(grid);
//					List<User> userList = asql.responseList.get(i).getEventUsers();
//					HashMap<User, List<Appt>> usMap = new HashMap<User, List<Appt>>();
//					for (User u: userList){
//						usMap.put(u, asql.getAllEvents(u.getID()));
//					}
//					List<Timestamp> dates = asql.responseList.get(i).getProposedTimes();
//					List<TimeSpan> responses = asql.responseList.get(i).getUserTimes();
//					
//					TimeSpan suggested = gc.suggestedGroupEventTime(responses);
//					
//					MultipleUserSchedule mus = new MultipleUserSchedule("Initiator", grid, usMap, dates, suggested);
//
//				}
//
//			}
//		}

	}

	// This method checks whether a string is a valid user name or password, as they can contains only letters and numbers
	public static boolean ValidString(String s)
	{
		char[] sChar = s.toCharArray();
		for(int i = 0; i < sChar.length; i++)
		{
			int sInt = (int)sChar[i];
			if(sInt < 48 || sInt > 122)
				return false;
			if(sInt > 57 && sInt < 65)
				return false;
			if(sInt > 90 && sInt < 97)
				return false;
		}
		return true;
	}
}
