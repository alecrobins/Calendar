package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.LocationStorage;
import hkust.cse.calendar.controllers.EventController;
import hkust.cse.calendar.controllers.EventController.EventReturnMessage;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Clock;
import hkust.cse.calendar.unit.LocationList;
import hkust.cse.calendar.unit.Appt.Frequency;
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


public class GroupSlotPicker extends JDialog implements ActionListener,
		ComponentListener {

	// constant
	private int INTERVAL_PER_HOUR = 4;
	
	// Old labels
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
	
	private JButton saveBut;
	private JButton CancelBut;
	private JButton inviteBut;
	private JButton rejectBut;
	
	private Appt NewAppt;
	private CalGrid parent;
	private boolean isNew = true;
	private boolean isChanged = true;
	private boolean isJoint = false;

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
	boolean[] rowBool;
	private MultipleUserSchedule mus;
	

	private void commonConstructor(MultipleUserSchedule m, boolean[] rowVals, String title, CalGrid cal, LocationStorage _ls) {
		
		// set up the NoticationController & The LocationStorage
		ls = _ls;
		rowBool = rowVals;
		
		mus = m;
		
		// set up the event controller
		eventController = new EventController(cal); 
		
		parent = cal;
		this.setAlwaysOnTop(true);
		setTitle(title);
		setModal(false);

		Container contentPane;
		contentPane = getContentPane();
		
		// Date Panel
		JPanel pDate = new JPanel();
		Border dateBorder = new TitledBorder(null, "DATE");
		pDate.setBorder(dateBorder);
		
		// New Date
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
		

	
		JPanel pTime = new JPanel();
		pTime.setLayout(new BorderLayout());
		pTime.add("West", psTime);
		pTime.add("East", peTime);

		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		top.setBorder(new BevelBorder(BevelBorder.RAISED));
		
		JPanel innerTop = new JPanel();
		innerTop.setLayout(new BorderLayout());
		innerTop.setBorder(new BevelBorder(BevelBorder.RAISED));
		innerTop.add(pDate, BorderLayout.NORTH);
		innerTop.add(pTime, BorderLayout.CENTER);
		
		top.add("North", innerTop);

		contentPane.add("North", top);
 
		
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

	GroupSlotPicker(MultipleUserSchedule m, String title, boolean[] row, CalGrid cal, int selectedApptId, LocationStorage _ls) {
		this.selectedApptId = selectedApptId;
		commonConstructor(m, row, title, cal, _ls);
	}

	GroupSlotPicker(MultipleUserSchedule m, String title, boolean[] row, CalGrid cal, LocationStorage _ls) {
		commonConstructor(m, row, title, cal, _ls);
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
		date[0] = 0;
		date[1] = Utility.getNumber(monthF.getText());
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
	
	public boolean isTimeSpanAvailable(TimeSpan s){    //needs to be tested
		//convert to rows and then decide
		int day = s.StartTime().getDate();
		int startH = s.StartTime().getHours();
		int startM = s.StartTime().getMinutes();
		int endH = s.EndTime().getHours();
		int endM = s.EndTime().getMinutes();
		
		int startRow = (startH-8)*4+(startM/15);
		int endRow = (endH-8)*4+(endM/15);
		
		for (int i = startRow; i <= endRow; i++){
			if (rowBool[i] == false){
				return false;
			}
		}
		return true;
	}

	private void saveButtonResponse() {

		System.out.println("YOU HAVE SAVED THE INFORMATION");
		// Get all the fields from the form
		// check if null before assignment

		String _month = monthD.getSelectedItem() == null ? null : monthD.getSelectedItem().toString();
		String _day = dayD.getSelectedItem() == null ? null : dayD.getSelectedItem().toString();

		String _sTimeH = sTimeHD.getSelectedItem() == null ? null : sTimeHD.getSelectedItem().toString();
		String _sTimeM = sTimeMD.getSelectedItem() == null ? null : sTimeMD.getSelectedItem().toString();
		String _eTimeH = eTimeHD.getSelectedItem() == null ? null : eTimeHD.getSelectedItem().toString();
		String _eTimeM = eTimeMD.getSelectedItem() == null ? null : eTimeMD.getSelectedItem().toString();

		int month =  Integer.parseInt(_month);
		month = month - 1; // month are 0 - 11 not 1 - 12
		int day =  Integer.parseInt(_day);
		
		int startH = Integer.parseInt(_sTimeH);
		int startM = Integer.parseInt(_sTimeM);
		int endH = Integer.parseInt(_eTimeH);
		int endM = Integer.parseInt(_eTimeM);
		
		Timestamp start = new Timestamp(parent.currentY, month, day, startH, startM, 0, 0);
		Timestamp end = new Timestamp(parent.currentY, month, day, endH, endM, 0, 0);
		TimeSpan slot = new TimeSpan(start, end);
			
		if (isTimeSpanAvailable(slot)){
			mus.updateTimeOptions(slot);
		}
		else {
			alertMessage("Unavailable Slot:  Pick A Time That Works for Everyone :)");
		}
		
		setVisible(false);
		
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
		return ""+this.parent.mCurrUser.getID();
	}
	
	private void allDisableEdit(){

		monthF.setEditable(false);
		dayF.setEditable(false);
		sTimeH.setEditable(false);
		sTimeM.setEditable(false);
		eTimeH.setEditable(false);
		eTimeM.setEditable(false);

	}
}
