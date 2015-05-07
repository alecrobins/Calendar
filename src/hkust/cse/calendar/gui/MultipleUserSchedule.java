package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.apptstorage.LocationStorage;
import hkust.cse.calendar.controllers.GroupController;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class MultipleUserSchedule implements ActionListener{

	//input:  week of:  mm/dd where the dd is the date of a given monday
	private MouseEvent tempe;
	public int previousRow;
	public int previousCol;
	public int currentRow;
	public int currentCol;
	public int currentD;
	public int currentM;
	public int firstM;
	public int secondM;
	public int firstD;
	public int currentY;
	private int pressRow;
	private int pressCol;
	private int releaseRow;
	private int releaseCol;
	boolean[] rowBool;
	public JPopupMenu pop;
	int numDays;
	Object[][] data;
	String[] columnTitles;
	JTable tableView;
	String title;

	private List<TimeSpan> timeOptions;

	private CalGrid parent;
	private LocationStorage parentLS;
	private HashMap<User, List<Appt>> userMap;
	private List<Timestamp> dateList;
	private GroupController gc;

	private JButton reject;

	//public static final int[] monthDays = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	//january, march, may, jul, aug, october, dec --31
	//feb -28
	//april, jun


	//input:  participants (selected by initiator) and dates (1-7)
	//this program should get all the appts for each user and find available timeslots 
	//aka go through each user's log and color in on this view


	//dates = list of Timestamps of 8am's of each date
	//15 minutes = 900,000 milliseconds

	public boolean[] isRowAvailable(HashMap<User, List<Appt>> userMap, List<Timestamp> dates){
		boolean[] rowVal = new boolean[(dates.size())*40];
		System.out.println("row boolean length = "+rowVal.length);
		for (int i = 0; i < rowVal.length; i++){   //initialize to true
			rowVal[i] = true;
		}
		System.out.println("numUsers found = "+userMap.size());
		for (List<Appt> ap : userMap.values()){    //for each user
			for (int i = 0; i < ap.size(); i++){   //for each appt in user's list
				//System.out.println("appt start time = " +ap.get(i).TimeSpan().StartTime().getTime());
				//System.out.println("appt end time = " +ap.get(i).TimeSpan().EndTime().getTime());
				int dateCount=0;
				long rowStart = dates.get(0).getTime();
				System.out.println("INITIAL DATE START = "+rowStart);
				long rowEnd = rowStart + 900000;
				System.out.println("appt start time = "+ap.get(i).TimeSpan().StartTime().getTime());
				System.out.println("appt end time = "+ap.get(i).TimeSpan().EndTime().getTime());
				for (int j = 1; j < ((dates.size())*40)+1; j++){   //for each row in rowVal
					if (!((ap.get(i).TimeSpan().EndTime().getTime() <= rowStart)||(ap.get(i).TimeSpan().StartTime().getTime()>=rowEnd))){
						rowVal[j-1] = false;
					}
					//					if (rowVal[j] == true){
					//						if (isHourOverlap(ap.get(i), new TimeSpan(new Timestamp(rowStart), new Timestamp(rowEnd)))){
					//							rowVal[j] = false;
					//						}
					//					}
					//System.out.println("rowEnd "+j+" = "+rowEnd);
					if (j == 1){
						rowStart += 900000;
						rowEnd += 900000;
					}
					else {
						if (j%40 == 0){
							dateCount++;
							System.out.println("THIS IS THE DATECOUNT = "+ dateCount);
							rowStart = dates.get(0).getTime() + 86400000*dateCount;
							System.out.println("THIS IS THE NEW ROWSTART = "+ rowStart);
							rowEnd = rowStart + 900000;
						}
						else{
							rowStart += 900000;
							rowEnd += 900000;
						}
					}
				}
			}
		}
		rowBool = rowVal;
		return rowVal;
	}
	
	MultipleUserSchedule(String t, CalGrid c, HashMap<User, List<Appt>> h, List<Timestamp> l){
		this.dateList = null;
		commonConstructor(t, c, h, l);
	}

	MultipleUserSchedule(String t, CalGrid c, HashMap<User, List<Appt>> h, List<Timestamp> l, TimeSpan t1){
		this.dateList = null;
		commonConstructor(t, c, h, l);

	}

	private boolean isHourOverlap(Appt appt, TimeSpan purposed){
		TimeSpan eventTime = appt.getEventTime();
		//		
		// check if starttime or end time of propossed is in between start or end time of event
		if( inBetween(purposed.StartTime(), eventTime) || inBetween(purposed.EndTime(), eventTime))
			return true;
		else if( beforeAndAfter(purposed, eventTime))
			return true;

		return false;
	}

	// check if t1 is in between t1 or tw
	private boolean inBetween(Timestamp t1, TimeSpan t2){
		return t1.getTime() >= t2.StartTime().getTime() && t1.getTime() <= t2.EndTime().getTime();
	}

	// check if the start time AND end of t1 start at before AND after t2
	private boolean beforeAndAfter(TimeSpan t1, TimeSpan t2){
		return t1.StartTime().getTime() <= t2.StartTime().getTime() && t1.EndTime().getTime() >= t2.EndTime().getTime();
	}

	public void updateTimeOptions(TimeSpan s){
		timeOptions.add(s);
	}

	public void commonConstructor(String tit, CalGrid cal, HashMap<User, List<Appt>> userMa, List<Timestamp> dates){
		timeOptions = new LinkedList<TimeSpan>();
		userMap = userMa;
		dateList = dates;
		title = tit;
		parent = cal;
		parentLS = cal.locationStorage;
		gc = new GroupController(parent);
		previousRow = 0;
		previousCol = 0;
		currentRow = 0;
		currentCol = 0;
		numDays = dates.size();
		final boolean[] rowVals = isRowAvailable(userMap, dates);
		int startMonth = dates.get(0).getMonth();
		int startDate = dates.get(0).getDate();
		getColumnTitles(startMonth, startDate);
		data = new Object[40][dates.size()+1];
		getDataArray(data);

		TableModel dataModel = prepareTableModel();
		tableView = new JTable(dataModel) {
			public TableCellRenderer getCellRenderer(int row, int col) {
				if (col == 0){
					return new AppCellRenderer(new Object(), true, 1, col, 1, null);
				}
				else if (!rowVals[row + (col-1)*40]){
					return new AppCellRenderer(new Object(), true, 55555, col, 1, null);  //55555 is the codeword!
				}
				else{
					return new AppCellRenderer(new Object(), false, row, col,
							1, null);
				}

			}
		};
		tableView.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableView.setRowSelectionAllowed(false);
		tableView.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				pressResponse(e);
			}

			public void mouseReleased(MouseEvent e) {
				releaseResponse(e);
				if(e.getButton()==1)
					calculateDrag(e);
			}
		});

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(tableView);
		scrollPane.getViewport().setViewPosition(new Point(0,0));
		frame.add(scrollPane, BorderLayout.CENTER);
		if (title == "Invitee"){
			reject = new JButton("Reject Group Appointment");
			reject.addActionListener(this);
			frame.add(reject, BorderLayout.SOUTH);

		}
		frame.setSize(100*(1+numDays), 700);
		frame.pack();
		frame.setVisible(true);

		frame.setLayout(new BorderLayout());
		currentRow = 0;
		currentCol = 0;

		TitledBorder b = BorderFactory
				.createTitledBorder("Group Availability");
		b.setTitleColor(new Color(102, 0, 51));
		Font f = new Font("Helvetica", Font.BOLD + Font.ITALIC, 11);
		b.setTitleFont(f);
		scrollPane.setBorder(b);

		Font f1 = new Font("Helvetica", Font.ITALIC, 11);
		pop = new JPopupMenu();
		pop.setFont(f1);

		JMenuItem mi;

		if (title == "Initiator"){
			mi = (JMenuItem) pop.add(new JMenuItem("wrong title"));

			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {				
					ScheduleGroupEvent();
				}
			});
		}

		if (title == "Invitee"){


			mi = (JMenuItem) pop.add(new JMenuItem("wrong title"));

			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {				
					pickSlot();
				}
			});
		}

		if (title == "Initiator Pre-Response"){

			mi = (JMenuItem) pop.add(new JMenuItem("Pick Possible Group Event Time"));

			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {				
					pickSlot();
				}
			});
			mi = (JMenuItem) pop.add(new JMenuItem("Add Group Event Details"));

			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {				
					ScheduleGroupEvent();
				}
			});
		}
	}

	public void setParent(CalGrid grid) {
		parent = grid;
		parentLS = parent.locationStorage;
	}

	private void ScheduleGroupEvent() {  
		for (int i = 0; i < timeOptions.size(); i++){
			System.out.println(timeOptions.get(i).StartTime().getHours());
			System.out.println(timeOptions.get(i).EndTime().getHours());
		}
		List<User> userList = new LinkedList<User>();
		for (User u: userMap.keySet()){
			userList.add(u);
		}
		GroupApptScheduler gas = new GroupApptScheduler(userList, timeOptions, rowBool, "Add Group Event Details", parent, parentLS);
		gas.setLocationRelativeTo(null);
		gas.show();
	}
	private void pickSlot() {
		GroupSlotPicker gsp = new GroupSlotPicker(this, "Add Possible Group Event Time", rowBool, parent, parentLS);
		gsp.updateSetApp(hkust.cse.calendar.gui.Utility.createDefaultAppt(
				parent.currentY, parent.currentM, parent.currentD,
				parent.mCurrUser));
		gsp.setLocationRelativeTo(null);
		gsp.show();
	}
	private void SendInvites(){

		//		asql.sendInvites(userMap.keySet(), dateList);
	}
	private void delete() {
	}
	private void modify() {
	}
	private void getDetail() {
	}

	public TableModel prepareTableModel() {

		TableModel dataModel = new AbstractTableModel() {

			public int getColumnCount() {
				return columnTitles.length;
			}

			public int getRowCount() {
				return 40;
			}

			public Object getValueAt(int row, int col) {
				return data[row][col];
			}

			public String getColumnName(int column) {
				return columnTitles[column];
			}

			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public void setValueAt(Object aValue, int row, int column) {
				data[row][column] = aValue;
			}
		};
		return dataModel;
	}

	private void pressResponse(MouseEvent e) {
		tempe = e;
		pressRow = tableView.getSelectedRow();
		pressCol = tableView.getSelectedColumn();
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
			pop.show(e.getComponent(), e.getX(), e.getY());
	}
	private void releaseResponse(MouseEvent e) {

		releaseRow = tableView.getSelectedRow();
		releaseCol = tableView.getSelectedColumn();
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
			pop.show(e.getComponent(), e.getX(), e.getY());
	}
	private void calculateDrag(MouseEvent e){
		if(releaseRow==pressRow){		
			currentRow = tableView.getSelectedRow()+tableView.getSelectedRowCount()-1;			
		}else{
			currentRow = releaseRow;

		}

		if(releaseCol==pressCol){			
			currentCol = tableView.getSelectedColumn()+tableView.getSelectedColumnCount()-1;
		}else{
			currentCol = releaseCol;
		}
	}


	public void getDataArray(Object[][] data) {
		int tam = 480;

		String am = new String("AM");

		int i;
		for (i = 0; i < 40; i++) {
			if (tam % 60 == 0){
				data[i][0] = (tam / 60) + ":" + "00" + am;
			}
			else{
				data[i][0] = (tam / 60) + ":" + (tam % 60) + am;
			}
			tam = tam + 15;
			for (int j = 1; j < columnTitles.length; j++){
				data[i][j] = "";
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tableView) {
			pop.show(tableView, currentRow * 20, currentRow * 20);

		}
		if (e.getSource() == reject){
			//			rejectResponse();
		}
	}

	public void getColumnTitles(int month, int date){
		currentM = month+1;
		firstM = month+1;
		secondM = 20;
		currentD = date;
		firstD = date;
		columnTitles = new String[numDays+1];
		int[] monthNum = new int[7];
		int[] dateNum = new int[7];

		if (currentM == 2){  //feb
			for (int i = 0; i < 7; i++){
				monthNum[i] = currentM;
				dateNum[i] = currentD;
				if (currentD == 28){
					currentM++;
					secondM = currentM;
					currentD = 0;
				}
				currentD++;
			}
		}
		else if (currentM == 4 || currentM == 6 || currentM == 9 || currentM == 11){
			for (int j = 0; j < 7; j++){
				monthNum[j] = currentM;
				dateNum[j] = currentD;
				if (currentD == 30){
					currentM++;
					secondM = currentM;
					currentD = 0;
				}
				currentD++;
			}
		}
		else if (currentM == 12){
			for (int k = 0; k < 7; k++){
				monthNum[k] = currentM;
				dateNum[k] = currentD;
				if (currentD == 31){
					currentM = 1;
					currentD = 0;
				}
				currentD++;
			}
		}
		else {
			for (int l = 0; l < 7; l++){
				monthNum[l] = currentM;
				dateNum[l] = currentD;
				if (currentD == 31){
					currentM++;
					secondM = currentM;
					currentD = 0;
				}
				currentD++;
			}
		}
		columnTitles[0] = "Time";
		for (int m = 1; m < columnTitles.length; m++){
			columnTitles[m] = ""+monthNum[m-1]+"/"+dateNum[m-1];
		}


	}

	public void createTable(String[] columnTitle){

	}



	public String[] getColumnTitles() {
		return columnTitles;
	}



}
