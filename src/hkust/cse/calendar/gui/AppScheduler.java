package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.LocationStorage;
import hkust.cse.calendar.controllers.EventController;
import hkust.cse.calendar.controllers.EventController.EventReturnMessage;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Clock;
import hkust.cse.calendar.unit.LocationList;
import hkust.cse.calendar.unit.Event.Frequency;
import hkust.cse.calendar.unit.TimeSpan;

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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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


public class AppScheduler extends JDialog implements ActionListener,
		ComponentListener {

	// constant
	private int INTERVAL_PER_HOUR = 4;
	
	// Old labels
	private JLabel yearL;
	private JTextField yearF;
	private JLabel monthL;
	private JTextField monthF;
	private JLabel dayL;
	private JTextField dayF;
	private JLabel sTimeHL;
	private JTextField sTimeH;
	private JLabel sTimeML;
	private JTextField sTimeM;
	private JLabel eTimeHL;
	private JTextField eTimeH;
	private JLabel eTimeML;
	private JTextField eTimeM;
	
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
	private JLabel sTimeHDL;
	private JComboBox sTimeHD; 
	private JLabel eTimeHDL;
	private JComboBox eTimeHD;
	private JLabel sTimeMDL;
	private JComboBox sTimeMD; 
	private JLabel eTimeMDL;
	private JComboBox eTimeMD;
	
	// reminder UI
	private JLabel yearReminderDL;
	private JComboBox yearReminderD; 
	private JLabel monthReminderDL;
	private JComboBox monthReminderD; 
	private JLabel dayReminderDL;
	private JComboBox dayReminderD; 
	private JLabel timeReminderHDL;
	private JComboBox timeReminderHD; 
	private JLabel timeReminderMDL;
	private JComboBox timeReminderMD; 
	

	private DefaultListModel model;
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

	private JTextArea detailArea;
	
	private final String[] months = { "January", "Feburary", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };
	
	// responsible for communication between scheduler events and the contoller
	private EventController eventController;

	private JSplitPane pDes;
	JPanel detailPanel;

//	private JTextField attendField;
//	private JTextField rejectField;
//	private JTextField waitingField;
	
	private int selectedApptId = -1;
	
	private LocationStorage ls; 
	

	private void commonConstructor(String title, CalGrid cal, LocationStorage _ls) {
		
		// set up the NoticationController & The LocationStorage
		ls = _ls;
		
		// set up the event controller
		eventController = new EventController(cal); 
		
		parent = cal;
		this.setAlwaysOnTop(true);
		setTitle(title);
		setModal(false);

		Container contentPane;
		contentPane = getContentPane();
		
		if (this.getTitle().equals("New Group Event")){
			JPanel pUsers = new JPanel();
			Border dateBorder = new TitledBorder(null, "USERS");
			pUsers.setBorder(dateBorder);
			JLabel num = new JLabel("Number of Members: ");
			//load users
			//add num panels to select each user to invite
		}
		// Date Panel
		JPanel pDate = new JPanel();
		Border dateBorder = new TitledBorder(null, "DATE");
		pDate.setBorder(dateBorder);
		
		// New Date
		yearDL = new JLabel("YEAR: ");
		pDate.add(yearDL);
		yearD = new JComboBox();
		yearD = loadYear("app");
		pDate.add(yearD);
		monthDL = new JLabel("MONTH: ");
		pDate.add(monthDL);
		monthD = new JComboBox();
		monthD = loadMonth("app");
		pDate.add(monthD);
		dayDL = new JLabel("DAY: ");
		pDate.add(dayDL);
		dayD = new JComboBox();
		dayD = loadDay("app");
		pDate.add(dayD);
		
		
		// New StartTime
		JPanel psTime = new JPanel();
		Border stimeBorder = new TitledBorder(null, "START TIME");
		psTime.setBorder(stimeBorder);
		sTimeHDL = new JLabel("Hour");
		psTime.add(sTimeHDL);
		sTimeHD = new JComboBox();
		sTimeHD = loadHour("app");
		psTime.add(sTimeHD);
		sTimeMDL = new JLabel("Minute");
		psTime.add(sTimeMDL);
		sTimeMD = new JComboBox();
		sTimeMD = loadMinutesInterval();
		psTime.add(sTimeMD);

		// New Time End
		JPanel peTime = new JPanel();
		Border etimeBorder = new TitledBorder(null, "END TIME");
		peTime.setBorder(etimeBorder);
		eTimeHDL = new JLabel("Hour");
		peTime.add(eTimeHDL);
		eTimeHD = new JComboBox();
		eTimeHD = loadHour("app");
		peTime.add(eTimeHD);
		eTimeMDL = new JLabel("Minute");
		peTime.add(eTimeMDL);
		eTimeMD = new JComboBox();
		eTimeMD = loadMinutesInterval();
		peTime.add(eTimeMD);
		
		// Location panel
		JPanel pLocation = new JPanel();
		Border locationBorder = new TitledBorder(null, "Location");
		pLocation.setBorder(locationBorder);
		locationDL = new JLabel("Place");
		pLocation.add(locationDL);
		locationD = new JComboBox();
		locationD = loadLocations();
		pLocation.add(locationD);
		
		// Frequency panel
		JPanel pFrequency = new JPanel();
		Border frequencyBorder = new TitledBorder(null, "Frequency");
		pFrequency.setBorder(frequencyBorder);
		frequencyDL = new JLabel("Event Frequency");
		pFrequency.add(frequencyDL);
		frequencyD = new JComboBox();
		frequencyD = loadFrequency();
		pFrequency.add(frequencyD);
		
		// Reminder Pannel
		JPanel pReminder = new JPanel();
		Border reminderBorder = new TitledBorder(null, "REMINDER");
		pReminder.setBorder(reminderBorder);
			// Reminder Date
		yearReminderDL = new JLabel("YEAR: ");
		pReminder.add(yearReminderDL);
		yearReminderD = new JComboBox();
		yearReminderD = loadYear("rem");
		pReminder.add(yearReminderD);
		monthReminderDL = new JLabel("MONTH: ");
		pReminder.add(monthReminderDL);
		monthReminderD = new JComboBox();
		monthReminderD = loadMonth("rem");
		pReminder.add(monthReminderD);
		dayReminderDL = new JLabel("DAY: ");
		pReminder.add(dayReminderDL);
		dayReminderD = new JComboBox();
		dayReminderD = loadDay("rem");
		pReminder.add(dayReminderD);
			// Reminder Time
		timeReminderHDL = new JLabel("Hour");
		pReminder.add(timeReminderHDL);
		timeReminderHD = new JComboBox();
		timeReminderHD = loadHour("rem");
		pReminder.add(timeReminderHD);
		timeReminderMDL = new JLabel("Minute");
		pReminder.add(timeReminderMDL);
		timeReminderMD = new JComboBox();
		timeReminderMD = loadMinutes();
		pReminder.add(timeReminderMD);

		JPanel pTime = new JPanel();
		pTime.setLayout(new BorderLayout());
		pTime.add("West", psTime);
		pTime.add("East", peTime);
		
		JPanel pExtra = new JPanel();
		pExtra.setLayout(new BorderLayout());
		pExtra.add("West", pLocation);
		pExtra.add("East", pFrequency);

		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		top.setBorder(new BevelBorder(BevelBorder.RAISED));
		
		JPanel innerTop = new JPanel();
		innerTop.setLayout(new BorderLayout());
		innerTop.setBorder(new BevelBorder(BevelBorder.RAISED));
		innerTop.add(pDate, BorderLayout.NORTH);
		innerTop.add(pTime, BorderLayout.CENTER);
		innerTop.add(pExtra, BorderLayout.SOUTH);
		
		top.add("North", innerTop);
		top.add("Center", pReminder);

		contentPane.add("North", top);

		JPanel titleAndTextPanel = new JPanel();
		JLabel titleL = new JLabel("TITLE");
		titleField = new JTextField(15);
		titleAndTextPanel.add(titleL);
		titleAndTextPanel.add(titleField);

		detailPanel = new JPanel();
		detailPanel.setLayout(new BorderLayout());
		Border detailBorder = new TitledBorder(null, "Appointment Description");
		detailPanel.setBorder(detailBorder);
		detailArea = new JTextArea(20, 30);
		
		detailArea.setEditable(true);
		JScrollPane detailScroll = new JScrollPane(detailArea);
		detailPanel.add(detailScroll);

		pDes = new JSplitPane(JSplitPane.VERTICAL_SPLIT, titleAndTextPanel,
				detailPanel);

		top.add(pDes, BorderLayout.SOUTH);

		if (NewAppt != null) {
			detailArea.setText(NewAppt.getInfo());

		}
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout(FlowLayout.RIGHT));

//		inviteBut = new JButton("Invite");
//		inviteBut.addActionListener(this);
//		panel2.add(inviteBut);
		
		saveBut = new JButton("Save");
		saveBut.addActionListener(this);
		panel2.add(saveBut);

		rejectBut = new JButton("Reject");
		rejectBut.addActionListener(this);
		panel2.add(rejectBut);
		rejectBut.show(false);

		CancelBut = new JButton("Cancel");
		CancelBut.addActionListener(this);
		panel2.add(CancelBut);

		contentPane.add("South", panel2);
		NewAppt = new Appt();

		if (this.getTitle().equals("Join Appointment Content Change") || this.getTitle().equals("Join Appointment Invitation")){
			inviteBut.show(false);
			rejectBut.show(true);
			CancelBut.setText("Consider Later");
			saveBut.setText("Accept");
		}
		if (this.getTitle().equals("Someone has responded to your Joint Appointment invitation") ){
			inviteBut.show(false);
			rejectBut.show(false);
			CancelBut.show(false);
			saveBut.setText("confirmed");
		}
		if (this.getTitle().equals("Join Appointment Invitation") || this.getTitle().equals("Someone has responded to your Joint Appointment invitation") || this.getTitle().equals("Join Appointment Content Change")){
			allDisableEdit();
		}
		pack();

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

	AppScheduler(String title, CalGrid cal, int selectedApptId, LocationStorage _ls) {
		this.selectedApptId = selectedApptId;
		commonConstructor(title, cal, _ls);
	}

	AppScheduler(String title, CalGrid cal, LocationStorage _ls) {
		commonConstructor(title, cal, _ls);
	}
	
	public void actionPerformed(ActionEvent e) {

		// distinguish which button is clicked and continue with require function
		if (e.getSource() == CancelBut) {

			setVisible(false);
			dispose();
		} else if (e.getSource() == saveBut) {
			saveButtonResponse();

		} else if (e.getSource() == rejectBut){
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

	private int[] getValidDate() {

		int[] date = new int[3];
		date[0] = Utility.getNumber(yearF.getText());
		date[1] = Utility.getNumber(monthF.getText());
		if (date[0] < 1980 || date[0] > 2100) {
			JOptionPane.showMessageDialog(this, "Please input proper year",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (date[1] <= 0 || date[1] > 12) {
			JOptionPane.showMessageDialog(this, "Please input proper month",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		date[2] = Utility.getNumber(dayF.getText());
		int monthDay = CalGrid.monthDays[date[1] - 1];
		if (date[1] == 2) {
			GregorianCalendar c = new GregorianCalendar();
			if (c.isLeapYear(date[0]))
				monthDay = 29;
		}
		if (date[2] <= 0 || date[2] > monthDay) {
			JOptionPane.showMessageDialog(this,
			"Please input proper month day", "Input Error",
			JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return date;
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

	private int[] getValidTimeInterval() {

		int[] result = new int[2];
		result[0] = getTime(sTimeH, sTimeM);
		result[1] = getTime(eTimeH, eTimeM);
		if ((result[0] % 15) != 0 || (result[1] % 15) != 0) {
			JOptionPane.showMessageDialog(this,
					"Minute Must be 0, 15, 30, or 45 !", "Input Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		if (!sTimeM.getText().equals("0") && !sTimeM.getText().equals("15") && !sTimeM.getText().equals("30") && !sTimeM.getText().equals("45") 
			|| !eTimeM.getText().equals("0") && !eTimeM.getText().equals("15") && !eTimeM.getText().equals("30") && !eTimeM.getText().equals("45")){
			JOptionPane.showMessageDialog(this,
					"Minute Must be 0, 15, 30, or 45 !", "Input Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		if (result[1] == -1 || result[0] == -1) {
			JOptionPane.showMessageDialog(this, "Please check time",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (result[1] <= result[0]) {
			JOptionPane.showMessageDialog(this,
					"End time should be bigger than \nstart time",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if ((result[0] < AppList.OFFSET * 60)
				|| (result[1] > (AppList.OFFSET * 60 + AppList.ROWNUM * 2 * 15))) {
			JOptionPane.showMessageDialog(this, "Out of Appointment Range !",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return result;
	}

	private void saveButtonResponse() {

		System.out.println("YOU HAVE SAVED THE INFORMATION");
		// Get all the fields from the form
		// check if null before assignment
		String _year = yearD.getSelectedItem() == null ? null : yearD.getSelectedItem().toString();
		String _month = monthD.getSelectedItem() == null ? null : monthD.getSelectedItem().toString();
		String _day = dayD.getSelectedItem() == null ? null : dayD.getSelectedItem().toString();
		String _sTimeH = yearD.getSelectedItem() == null ? null : sTimeHD.getSelectedItem().toString();
		String _sTimeM = sTimeMD.getSelectedItem() == null ? null : sTimeMD.getSelectedItem().toString();
		String _eTimeH = eTimeHD.getSelectedItem() == null ? null : eTimeHD.getSelectedItem().toString();
		String _eTimeM = eTimeMD.getSelectedItem() == null ? null : eTimeMD.getSelectedItem().toString();
		String _detailArea = detailArea.getText() == null ? null : detailArea.getText();
		String _titleField = titleField.getText() == null ? null : titleField.getText();
		// Reminder
		String _timeReminderH = timeReminderHD.getSelectedItem() == null ? null : timeReminderHD.getSelectedItem().toString();
		String _timeReminderM = timeReminderMD.getSelectedItem() == null ? null : timeReminderMD.getSelectedItem().toString();
		String _yearReminder = yearReminderD.getSelectedItem() == null ? null : yearReminderD.getSelectedItem().toString();
		String _monthReminder = monthReminderD.getSelectedItem() == null ? null : monthReminderD.getSelectedItem().toString();
		String _dayReminder = dayReminderD.getSelectedItem() == null ? null : dayReminderD.getSelectedItem().toString();
		// Location & Frequency
		String _location = locationD.getSelectedItem() == null ? null : locationD.getSelectedItem().toString();
		String _frequency = frequencyD.getSelectedItem() == null ? null : frequencyD.getSelectedItem().toString();
		
		System.out.println("Creating event . . . ");
		
		// CREATE THE EVENT
		// returns an EventReturnMessage - determines if successful or details an error
		EventReturnMessage returnMessage = eventController.createEvent(
				_year, _month, _day,
				_sTimeH, _sTimeM, _eTimeH, _eTimeM,
				_detailArea, _titleField,
				_timeReminderH, _timeReminderM, _yearReminder, _monthReminder, _dayReminder,
				_frequency, _location, parent);
		//
		//SUCCESS, ERROR_TIME_FORMAT, ERROR_PAST_DATE, ERROR_UNFILLED_REQUIRED_FIELDS,
		//ERROR_REMINDER, ERROR_EVENT_OVERLAP, ERROR_SECOND_DATE_PAST, ERROR
		switch (returnMessage){
			case SUCCESS :
				setVisible(false);
				dispose(); // remove
				parent.repaint();
				System.out.println("success");
				break; 
			case ERROR_TIME_FORMAT :
				alertMessage("There is an error with the format of your time. Make sure it has the proper format");
				break;
			case ERROR_PAST_DATE :
				alertMessage("You cannot create an event from a past date. Please resubmit the event.");
				break;
			case ERROR_UNFILLED_REQUIRED_FIELDS :
				alertMessage("You have some unfilled required fields. Please check and fill out the required fields.");
				break;
			case ERROR_REMINDER :
				alertMessage("There is problem with your reminder. Please ensure your reminder date is before the event and all fields are filled out.");
				break;
			case ERROR_EVENT_OVERLAP :
				alertMessage("You already have an event at that time! Please find a new date for that event. ");
				break;
			case ERROR_SECOND_DATE_PAST :
				alertMessage("Your end date can't be before your start date. Please resubmit. ");
				break;
			default : 
				alertMessage("Something you did was not right. Review your event. ");
				break;
		}
		
//		if(EventReturnMessage.SUCCESS == returnMessage){
//			//close the window
//			setVisible(false);
//			dispose();
//			parent.repaint();
//			System.out.println("success");
//		}else{
//			// report back the erorr message
//			System.out.println("error");
//		}
		
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
		i = appt.TimeSpan().StartTime().getHours();
		sTimeHD.setSelectedIndex(i - 1);
		eTimeHD.setSelectedIndex(i);
		i = appt.TimeSpan().StartTime().getMinutes();
		sTimeMD.setSelectedIndex(i/15);
		eTimeMD.setSelectedIndex(i/15);
	}

	public void componentHidden(ComponentEvent e) {

	}

	public void componentMoved(ComponentEvent e) {

	}

	public void componentResized(ComponentEvent e) {

		Dimension dm = pDes.getSize();
		double width = dm.width * 0.93;
		double height = dm.getHeight() * 0.6;
		detailPanel.setSize((int) width, (int) height);

	}

	public void componentShown(ComponentEvent e) {

	}
	
	public String getCurrentUser()		// get the id of the current user
	{
		return this.parent.mCurrUser.getUsername();
	}
	
	private void allDisableEdit(){
		yearF.setEditable(false);
		monthF.setEditable(false);
		dayF.setEditable(false);
		sTimeH.setEditable(false);
		sTimeM.setEditable(false);
		eTimeH.setEditable(false);
		eTimeM.setEditable(false);
		titleField.setEditable(false);
		detailArea.setEditable(false);
	}
}
