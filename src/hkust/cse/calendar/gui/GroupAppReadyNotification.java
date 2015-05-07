package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.apptstorage.LocationStorage;
import hkust.cse.calendar.controllers.EventController;
import hkust.cse.calendar.controllers.EventController.EventReturnMessage;
import hkust.cse.calendar.controllers.GroupController;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Clock;
import hkust.cse.calendar.unit.LocationList;
import hkust.cse.calendar.unit.Appt.Frequency;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


public class GroupAppReadyNotification extends JDialog implements ActionListener,
ComponentListener {

	// constant
	private int INTERVAL_PER_HOUR = 4;

	// Old labels

	// Additional UI elements
	private JLabel locationDL;
	private JComboBox locationD; // drop down button
	private JLabel frequencyDL;
	private JComboBox frequencyD; 
	private JLabel yearDL;
	private JComboBox yearD; 
	private JLabel monthDL;
	private JComboBox monthD; 
	private JLabel dayDL;
	private JComboBox dayD; 
	private JLabel numDaysL;
	private JComboBox numDays;
	private JLabel numUsersL;
	private JComboBox numUsers;



	private JTextField titleField;

	private JButton saveBut;	
	private JButton CancelBut;
	private JButton inviteBut;
	private JButton rejectBut;

	private Appt NewAppt;
	private CalGrid parent;
	private boolean isNew = true;
	private boolean isChanged = true;
	private boolean isJoint = false;
	
	
	private List<JCheckBox> checkBoxList;


	private final String[] months = { "January", "Feburary", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };

	// responsible for communication between scheduler events and the contoller
	private EventController eventController;



	//	private JTextField attendField;
	//	private JTextField rejectField;
	//	private JTextField waitingField;

	private int selectedApptId = -1;

	private LocationStorage ls; 
	private GroupController gc;
	private List<User> userList;
	private List<Timestamp> dates;
	private boolean[] rowBool;
	private TimeSpan suggested;


	private void commonConstructor(boolean[] rowVal, CalGrid cal, LocationStorage _ls, List<User> userLis, TimeSpan s) {
		rowBool = rowVal;
		suggested = s;
		gc = new GroupController(cal);
		// set up the NoticationController & The LocationStorage
		ls = _ls;

		// set up the event controller
		eventController = new EventController(cal); 

		parent = cal;
		this.setAlwaysOnTop(true);

		setModal(false);

		Container contentPane;
		contentPane = getContentPane();

		JPanel pUsers = new JPanel();

		Border userBorder = new TitledBorder(null, "USERS");
		pUsers.setBorder(userBorder);

		numUsersL = new JLabel("Invite Users: ");

		//load users
		userList = new LinkedList<User>();
		userList = userLis;
		checkBoxList = new LinkedList<JCheckBox>();
		for (User u: userList){
			JCheckBox j = new JCheckBox(u.getUsername());
			checkBoxList.add(j);
			j.setSelected(true);
			j.setEnabled(false);
			pUsers.add(j);
		}
		
		//add num panels to select each user to invite

		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		top.setBorder(new BevelBorder(BevelBorder.RAISED));

		JPanel innerTop = new JPanel();
		innerTop.setLayout(new BorderLayout());
		innerTop.setBorder(new BevelBorder(BevelBorder.RAISED));
		innerTop.add(pUsers, BorderLayout.NORTH);

		top.add("North", innerTop);
		contentPane.add("North", top);
		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout(FlowLayout.RIGHT));

		saveBut = new JButton("Schedule Group Appointment");
		saveBut.addActionListener(this);
		panel2.add(saveBut);
		
		

		contentPane.add("South", panel2);

		pack();

		//		inviteBut = new JButton("Invite");
		//		inviteBut.addActionListener(this);
		//		panel2.add(inviteBut);
	}

	private JComboBox loadDay(String _type) {
		JComboBox temp = new JComboBox();
		// make first null if reminder
		if(_type == "rem")
			temp.addItem(null);

		// Fill with days
		for (int i = 1; i < 32; i++){
			// format the days
			String d = i < 10 ? "0" + Integer.toString(i) : Integer.toString(i);

			temp.addItem(d);
		}

		return temp;
	}

	// LOAD THE DROP DOWN
	// load only on 15 minute intervlas
	private JComboBox loadMinutesInterval() {
		JComboBox temp = new JComboBox();
		int interval = 60 / INTERVAL_PER_HOUR;
		// Fill with locations
		for (int i = 0; i < INTERVAL_PER_HOUR; i++){
			// format the minutes
			int minutes = interval * i;
			String m = minutes < 10 ? "0" + Integer.toString(minutes) : Integer.toString(minutes);

			temp.addItem(m);
		}

		return temp;
	}

	private JComboBox loadMinutes() {
		JComboBox temp = new JComboBox();
		// make first null bc reminder
		temp.addItem(null);

		// Fill with locations
		for (int i = 0; i < 60; i++){
			// format the days
			String m = i < 10 ? "0" + Integer.toString(i) : Integer.toString(i);
			temp.addItem(m);
		}

		return temp;
	}

	private JComboBox loadMonth(String _type) {
		JComboBox temp = new JComboBox();

		// make first null if reminder
		if(_type == "rem")
			temp.addItem(null);

		// Fill with months
		for (int i = 1; i < 13; i++){
			// format the months
			String m = i < 10 ? "0" + Integer.toString(i) : Integer.toString(i);
			temp.addItem(m);
		}

		return temp;
	}

	private JComboBox loadYear(String _type) {
		JComboBox temp = new JComboBox();
		Date now = parent.mClock.getChangedTimeDate();

		// make first null if reminder
		if(_type == "rem")
			temp.addItem(null);

		// provide a 50 year range of picking 
		int currentYear = now.getYear() + 1900;
		// Fill with locations
		for (int i = 0; i < 50; i++)
			temp.addItem(currentYear + i);

		return temp;
	}

	private JComboBox loadHour(String _type) {
		JComboBox temp = new JComboBox();

		// make first null if reminder
		if(_type == "rem")
			temp.addItem(null);

		// Fill with locations
		for (int i = 1; i < 25; i++){
			// format hour
			String h = i < 10 ? "0" + Integer.toString(i) : Integer.toString(i);
			temp.addItem(h);
		}

		return temp;
	}

	// load the locations into the drop down
	private JComboBox loadLocations() {
		JComboBox temp = new JComboBox();
		LocationList locations = new LocationList(ls);
		List<String> locationList = locations.getCityList();

		// make first null
		temp.addItem(null);

		// Fill with locations
		for (int i = 0; i < locationList.size(); i++)
			temp.addItem(locationList.get(i));

		return temp;
	}

	// load the locations into the drop down
	private JComboBox loadFrequency() {
		JComboBox temp = new JComboBox();
		Frequency[] frequencies = Frequency.values();

		// make first null
		temp.addItem(null);
		// Get
		for (int i = 0; i < frequencies.length; i++) {
			temp.addItem(frequencies[i].name());
		}

		return temp;
	}

	GroupAppReadyNotification(boolean[] rowVal, CalGrid cal, LocationStorage _ls, List<User> userLis, int selectedApptId, TimeSpan s) {
		this.selectedApptId = selectedApptId;
		commonConstructor(rowVal, cal, _ls, userLis, s);
	}

	GroupAppReadyNotification(boolean[] rowVal, CalGrid cal, LocationStorage _ls, List<User> userLis, TimeSpan s) {
		commonConstructor(rowVal, cal, _ls, userLis, s);
	}

	public void actionPerformed(ActionEvent e) {
		
		// distinguish which button is clicked and continue with require function
		if (e.getSource() == CancelBut) {

			setVisible(false);
			dispose();
		} else if (e.getSource() == saveBut) {
			saveButtonResponse();

		}
		else if (e.getSource() == rejectBut){
			if (JOptionPane.showConfirmDialog(this, "Reject this joint appointment?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0){
				NewAppt.addReject(getCurrentUser());
				NewAppt.getAttendList().remove(getCurrentUser());
				NewAppt.getWaitingList().remove(getCurrentUser());
				this.setVisible(false);
				dispose();
			}
		}
		parent.getAppList().clear();
		parent.getAppList().setTodayAppt(parent.GetTodayAppt());
		parent.repaint();
	}

	private JPanel createPartOperaPane() {
		JPanel POperaPane = new JPanel();
		JPanel browsePane = new JPanel();
		JPanel controPane = new JPanel();

		POperaPane.setLayout(new BorderLayout());
		TitledBorder titledBorder1 = new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(178, 178, 178)),
				"Add Participant:");
		browsePane.setBorder(titledBorder1);

		POperaPane.add(controPane, BorderLayout.SOUTH);
		POperaPane.add(browsePane, BorderLayout.CENTER);
		POperaPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		return POperaPane;

	}

	
	private int getTime(JTextField h, JTextField min) {

		int hour = Utility.getNumber(h.getText());
		if (hour == -1)
			return -1;
		int minute = Utility.getNumber(min.getText());
		if (minute == -1)
			return -1;

		return (hour * 60 + minute);

	}


	private void saveButtonResponse() {

		//GroupApptScheduler gas = new GroupApptScheduler(rowBool, "New Group Event",  parent,  parent.locationStorage, suggested);
//		gas.updateSetApp(hkust.cse.calendar.gui.Utility.createDefaultAppt(
//				parent.currentY, parent.currentM, parent.currentD,
//				parent.mCurrUser));
//		gas.setLocationRelativeTo(null);
//		gas.show();
//		setVisible(false);
	}

	private void alertMessage(String message){
		JOptionPane.showMessageDialog(null, message);
	}

	private Timestamp CreateTimeStamp(int[] date, int time) {
		Timestamp stamp = new Timestamp(0);
		stamp.setYear(date[0]);
		stamp.setMonth(date[1] - 1);
		stamp.setDate(date[2]);
		stamp.setHours(time / 60);
		stamp.setMinutes(time % 60);
		return stamp;
	}

	public void updateSetApp(Appt appt) {
		int i = appt.TimeSpan().StartTime().getMonth();
		monthD.setSelectedIndex(i);
		i = appt.TimeSpan().StartTime().getDate();
		dayD.setSelectedIndex(i - 1);
	}

	public void componentHidden(ComponentEvent e) {

	}

	public void componentMoved(ComponentEvent e) {

	}

	public void componentResized(ComponentEvent e) {

		Dimension dm = new Dimension(500, 500);
		double width = dm.width * 0.93;
		double height = dm.getHeight() * 0.6;
		
	}

	public void componentShown(ComponentEvent e) {

	}

	public String getCurrentUser()		// get the id of the current user
	{
		return this.parent.mCurrUser.getUsername();
	}

	private void allDisableEdit(){

		titleField.setEditable(false);
	
	}
}
