package hkust.cse.calendar.gui;

import hkust.cse.calendar.Main.CalendarMain;
import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.LocationStorage;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Clock;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.metal.MetalBorders.Flush3DBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class CalGrid extends JFrame implements ActionListener {

	// private User mNewUser;
	private static final long serialVersionUID = 1L;
	public ApptStorageControllerImpl controller;
	public User mCurrUser;
	private String mCurrTitle = "Desktop Calendar - No User - ";
	public Clock mClock;
	private GregorianCalendar today;
	public int currentD;
	public int currentM;
	public int currentY;
	public int previousRow;
	public int previousCol;
	public int currentRow;
	public int currentCol;
	private BasicArrowButton eButton;
	private BasicArrowButton wButton;
	private JLabel year;
	private JComboBox month;

	private final Object[][] data = new Object[6][7];
	private final Vector[][] apptMarker = new Vector[6][7];
	private final String[] names = { "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };
	private final String[] months = { "January", "Feburary", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };
	private JTable tableView;
	private AppList applist;
	private MultipleUserSchedule mus;
	
	public static final int[] monthDays = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
			31, 30, 31 };
	private JTextPane note;

	private JSplitPane upper;
	private JSplitPane whole;
	private JScrollPane scrollpane;
	private StyledDocument mem_doc = null;
	private SimpleAttributeSet sab = null;
	// private boolean isLogin = false;
	private JMenu Appmenu = new JMenu("Appointment");
	private JMenu Clockmenu = new JMenu("TimeMachine");
	private JMenu Accountmenu=new JMenu("Settings");

	private final String[] holidays = {
			"New Years Day\nSpring Festival\n",
			"President's Day (US)\n",
			"",
			"Ching Ming Festival\nGood Friday\nThe day following Good Friday\nEaster Monday\n",
			"Labour Day\nThe Buddha���s Birthday\nTuen Ng Festival\n",
			"",
			"Hong Kong Special Administrative Region Establishment Day\n",
			"Civic Holiday(CAN)\n",
			"",
			"National Day\nChinese Mid-Autumn Festival\nChung Yeung Festival\nThanksgiving Day\n",
			"Veterans Day(US)\nThanksgiving Day(US)\n", "Christmas\n" };

	private AppScheduler setAppDial;
	public LocationStorage locationStorage;

	public ApptStorageControllerImpl getController(){
		return controller;
	}
	public CalGrid(ApptStorageControllerImpl con) {
		super();
		
		locationStorage = new LocationStorage();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		controller = con;
		mCurrUser = null;

		previousRow = 0;
		previousCol = 0;
		currentRow = 0;
		currentCol = 0;

		applist = new AppList();
		applist.setParent(this);

//		mus = new MultipleUserSchedule();
//		mus.setParent(this);
		
		setJMenuBar(createMenuBar());

		mClock = new Clock();
		today = mClock.getUnchangedTime();
		currentY = today.get(Calendar.YEAR);
		currentD = today.get(today.DAY_OF_MONTH);
		int temp = today.get(today.MONTH) + 1;
		currentM = 12; 	

		getDateArray(data);

		JPanel leftP = new JPanel();
		leftP.setLayout(new BorderLayout());
		leftP.setPreferredSize(new Dimension(500, 300));

		JLabel textL = new JLabel("Important Days");
		textL.setForeground(Color.red);

		note = new JTextPane();
		note.setEditable(false);
		note.setBorder(new Flush3DBorder());
		mem_doc = note.getStyledDocument();
		sab = new SimpleAttributeSet();
		StyleConstants.setBold(sab, true);
		StyleConstants.setFontSize(sab, 30);

		JPanel noteP = new JPanel();
		noteP.setLayout(new BorderLayout());
		noteP.add(textL, BorderLayout.NORTH);
		noteP.add(note, BorderLayout.CENTER);

		leftP.add(noteP, BorderLayout.CENTER);

		eButton = new BasicArrowButton(SwingConstants.EAST);
		eButton.setEnabled(true);
		eButton.addActionListener(this);
		wButton = new BasicArrowButton(SwingConstants.WEST);
		wButton.setEnabled(true);
		wButton.addActionListener(this);

		year = new JLabel(new Integer(currentY).toString());
		month = new JComboBox();
		month.addActionListener(this);
		month.setPreferredSize(new Dimension(200, 30));
		for (int cnt = 0; cnt < 12; cnt++)
			month.addItem(months[cnt]);
		month.setSelectedIndex(temp - 1);

		JPanel yearGroup = new JPanel();
		yearGroup.setLayout(new FlowLayout());
		yearGroup.setBorder(new Flush3DBorder());
		yearGroup.add(wButton);
		yearGroup.add(year);
		yearGroup.add(eButton);
		yearGroup.add(month);

		leftP.add(yearGroup, BorderLayout.NORTH);

		TableModel dataModel = prepareTableModel();
		
		// create the month calendar view
		tableView = new JTable(dataModel) {
			public TableCellRenderer getCellRenderer(int row, int col) {
				String tem = (String) data[row][col];

				if (tem.equals("") == false) {
					try {
						if (today.get(Calendar.YEAR) == currentY
								&& today.get(today.MONTH) + 1 == currentM
								&& today.get(today.DAY_OF_MONTH) == Integer
										.parseInt(tem)) {
							
							return new CalCellRenderer(today, apptMarker[row][col]);
						}
					} catch (Throwable e) {
						System.exit(1);
					}

				}
				return new CalCellRenderer(null, apptMarker[row][col]);
			}
		};

		tableView.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableView.setRowHeight(40);
		tableView.setRowSelectionAllowed(false);
		tableView.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				mousePressResponse();
			}

			public void mouseReleased(MouseEvent e) {
				mouseResponse();
			}
		});

		JTableHeader head = tableView.getTableHeader();
		head.setReorderingAllowed(false);
		head.setResizingAllowed(true);

		scrollpane = new JScrollPane(tableView);
		scrollpane.setBorder(new BevelBorder(BevelBorder.RAISED));
		scrollpane.setPreferredSize(new Dimension(536, 260));

		upper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftP, scrollpane);

		whole = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upper, applist);
		getContentPane().add(whole);

		
		initializeSystem(); // for you to add.
		
		//mCurrUser = getCurrUser(); // totally meaningless code
		Appmenu.setEnabled(true);
		Clockmenu.setEnabled(true);
		Accountmenu.setEnabled(true);
		
		// Refreshes calendar every 0.5 second, in order to trigger notification
		ActionListener actionL = new ActionListener(){
			public void actionPerformed(ActionEvent actionE){
				triggerReminder();
			}
		};
		Timer timer = new Timer(500, actionL );
		timer.start();

		UpdateCal();
		pack();				// sized the window to a preferred size
		setVisible(true);	//set the window to be visible
		
		
		
	}

	// A function to trigger reminder
	private void triggerReminder(){
		refreshTime();
		
		TimeSpan note = controller.getNotification();
		if (note == null) return;
		if (note.StartTime().compareTo(mClock.getChangedTimeDate()) <= 0) {
			Timestamp startTime = note.EndTime();
			Appt noti = controller.getAppt(startTime);
			DetailsDialog info = new DetailsDialog(noti, "Notification");
			info.setVisible(true);
			
			controller.deleteNotification(note);
			
			if (controller.getNotification() != null) {
				if (note.StartTime().compareTo(controller.getNotification().StartTime()) == 0) {
					triggerReminder();
				}
			}
		}
	}
	
	public TableModel prepareTableModel() {

		TableModel dataModel = new AbstractTableModel() {

			public int getColumnCount() {
				return names.length;
			}

			public int getRowCount() {
				return 6;
			}

			public Object getValueAt(int row, int col) {
				return data[row][col];
			}

			public String getColumnName(int column) {
				return names[column];
			}

			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public void setValueAt(Object aValue, int row, int column) {
				System.out.println("Setting value to: " + aValue);
				data[row][column] = aValue;
			}
		};
		return dataModel;
	}

	public void getDateArray(Object[][] data) {
		GregorianCalendar c = new GregorianCalendar(currentY, currentM - 1, 1);
		int day;
		int date;
		Date d = c.getTime();
		c.setTime(d);
		day = d.getDay();
		date = d.getDate();

		if (c.isLeapYear(currentY)) {

			monthDays[1] = 29;
		} else
			monthDays[1] = 28;

		int temp = day - date % 7;
		if (temp > 0)
			day = temp + 1;
		else if (temp < 0)
			day = temp + 1 + 7;
		else
			day = date % 7;
		day %= 7;
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 7; j++) {
				int temp1 = i * 7 + j - day + 1;
				if (temp1 > 0 && temp1 <= monthDays[currentM - 1])
					data[i][j] = new Integer(temp1).toString();
				else
					data[i][j] = new String("");
			}

	}

	JMenuBar createMenuBar() {

		ActionListener listener = new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Manual Scheduling")) {
					AppScheduler a = new AppScheduler("New", CalGrid.this, locationStorage);
					a.updateSetApp(hkust.cse.calendar.gui.Utility
							.createDefaultAppt(currentY, currentM, currentD,
									mCurrUser));
					a.setLocationRelativeTo(null);
					a.show();
					TableModel t = prepareTableModel();
					tableView.setModel(t);
					tableView.repaint();
				}else if (e.getActionCommand().equals("Manage Locations")) {
					ManageLocation b = new ManageLocation(CalGrid.this, locationStorage);
					b.setLocationRelativeTo(null);
					b.show();
					TableModel t = prepareTableModel();
					tableView.setModel(t);
					tableView.repaint();
				}else if (e.getActionCommand().equals("Change Time")) {
					System.out.println("CHANGE TIME");
					ManageTimeMachine t = new ManageTimeMachine(CalGrid.this);
					t.setLocationRelativeTo(null);
					t.show();
					TableModel tm = prepareTableModel();
					tableView.setModel(tm);
					tableView.repaint();
					
					/******************EDITED FOR ACCOUNT*********************************************/
				}else if (e.getActionCommand().equals("Change Settings")) {
					System.out.println("CHANGE SETTINGS");
					ManageAccount m = new ManageAccount(CalGrid.this);
					m.setLocationRelativeTo(null);
					m.show();
					TableModel tm = prepareTableModel();
					tableView.setModel(tm);
					tableView.repaint();
				}

			}
		};
		JMenuBar menuBar = new JMenuBar();
		menuBar.getAccessibleContext().setAccessibleName("Calendar Choices");
		JMenuItem mi;

		JMenu Access = (JMenu) menuBar.add(new JMenu("Access"));
		Access.setMnemonic('A');
		Access.getAccessibleContext().setAccessibleDescription(
				"Account Access Management");

		mi = (JMenuItem) Access.add(new JMenuItem("Logout"));	//adding a Logout menu button for user to logout
		mi.setMnemonic('L');
		mi.getAccessibleContext().setAccessibleDescription("For user logout");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "Logout?",
						"Comfirm", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION){
					//controller.dumpStorageToFile();
					//System.out.println("closed");
					dispose();
					CalendarMain.logOut = true;
					return;	//return to CalendarMain()
				}
			}
		});
		
		mi = (JMenuItem) Access.add(new JMenuItem("Exit"));
		mi.setMnemonic('E');
		mi.getAccessibleContext().setAccessibleDescription("Exit Program");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "Exit Program ?",
						"Comfirm", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION)
					System.exit(0);

			}
		});

		menuBar.add(Appmenu);
		Appmenu.setEnabled(false);
		Appmenu.setMnemonic('p');
		Appmenu.getAccessibleContext().setAccessibleDescription(
				"Appointment Management");
		mi = new JMenuItem("Manual Scheduling");
		mi.addActionListener(listener);
		Appmenu.add(mi);
		
		//////////
		mi = new JMenuItem("Manage Locations");
		mi.addActionListener(listener);
		Appmenu.add(mi);
		//////////
		
		menuBar.add(Clockmenu);
		Clockmenu.setEnabled(false);
		Clockmenu.setMnemonic('p');
		Clockmenu.getAccessibleContext().setAccessibleDescription(
				"Time Management");
		mi = new JMenuItem("Change Time");
		mi.addActionListener(listener);
		Clockmenu.add(mi);

		/***********EDITED FOR SETTINGS*****************/
		
		menuBar.add(Accountmenu);
		Accountmenu.setEnabled(false);
		Accountmenu.setMnemonic('p');
		Accountmenu.getAccessibleContext().setAccessibleDescription(
				"Account Management");
		mi = new JMenuItem("Change Settings");
		mi.addActionListener(listener);
		Accountmenu.add(mi);
		
		
		/***********************************************/
		
		
		return menuBar;
	}

	private void initializeSystem() {

		mCurrUser = this.controller.getDefaultUser();	//get User from controller
		controller.LoadApptFromXml();
		// Fix Me !
		// Load the saved appointments from disk
		checkUpdateJoinAppt();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == eButton) {
			if (year == null)
				return;
			currentY = currentY + 1;
			year.setText(new Integer(currentY).toString());
			CalGrid.this.setTitle("Desktop Calendar - No User - (" + currentY
					+ "-" + currentM + "-" + currentD + ")");
			getDateArray(data);
			if (tableView != null) {
				TableModel t = prepareTableModel();
				tableView.setModel(t);
				tableView.repaint();

			}
			UpdateCal();
		} else if (e.getSource() == wButton) {
			if (year == null)
				return;
			currentY = currentY - 1;
			year.setText(new Integer(currentY).toString());
			CalGrid.this.setTitle("Desktop Calendar - No User - (" + currentY
					+ "-" + currentM + "-" + currentD + ")");
			getDateArray(data);
			if (tableView != null) {
				TableModel t = prepareTableModel();
				tableView.setModel(t);
				tableView.repaint();

			}
			UpdateCal();
		} else if (e.getSource() == month) {
			if (month.getSelectedItem() != null) {
				currentM = month.getSelectedIndex() + 1;
				try {
					mem_doc.remove(0, mem_doc.getLength());
					mem_doc.insertString(0, holidays[currentM - 1], sab);
				} catch (BadLocationException e1) {

					e1.printStackTrace();
				}

				CalGrid.this.setTitle("Desktop Calendar - No User - ("
						+ currentY + "-" + currentM + "-" + currentD + ")");
				getDateArray(data);
				if (tableView != null) {
					TableModel t = prepareTableModel();
					tableView.setModel(t);
					tableView.repaint();
				}
				UpdateCal();
			}
		}
	}
	
	// refresh time
	public void refreshTime(){
		today = mClock.getChangedTime();
	}
	
	// refresh calendar after changing time in time machine
	public void refreshCal(){
		refreshTime();
		currentY = today.get(GregorianCalendar.YEAR);
		currentD = today.get(today.DAY_OF_MONTH);
		currentM = mClock.getChangedTimeDate().getMonth() + 1;
		month.setSelectedIndex(currentM - 1);
		year.setText(new Integer(currentY).toString());
		CalGrid.this.setTitle("Desktop Calendar - No User - (" + currentY
				+ "-" + currentM + "-" + currentD + ")");
		getDateArray(data);
		if (tableView != null) {
			TableModel t = prepareTableModel();
			tableView.setModel(t);
			tableView.repaint();
		}
		UpdateCal();
	}

	// update the appointment list on gui
	public void updateAppList() {
		applist.clear();
		applist.repaint();
		applist.setTodayAppt(GetTodayAppt());
	}

	public void UpdateCal() {
		/*today = Clock.getInstance().newGregorianCalendar();
		currentY = today.get(GregorianCalendar.YEAR);
		currentD = today.get(today.DAY_OF_MONTH);
		currentM= today.get(today.MONTH)+1;
		System.out.println(today.getTime());*/
		
		if (mCurrUser != null) {
			mCurrTitle = "Desktop Calendar - " + mCurrUser.getUsername() + " - ";
			this.setTitle(mCurrTitle + "(" + currentY + "-" + currentM + "-"
					+ currentD + ")");
			Appt[] monthAppts = null;
			GetMonthAppts();

			for (int i = 0; i < 6; i++){
				for (int j = 0; j < 7; j++){
					apptMarker[i][j] = new Vector(100);
					if(((String)data[i][j]).equals("")){
						apptMarker[i][j].clear();
						continue;
					}
					Appt[] temp = GetApptOnN(Integer.parseInt((String)data[i][j]));
					if(temp == null){
						apptMarker[i][j].clear();
						continue;
					}
					for(int k = 0; k < temp.length; k++){
						if(apptMarker[i][j].isEmpty()){
							if(temp[k].getIsPublic() == true)
								apptMarker[i][j].add(2);
							else apptMarker[i][j].add(1);
						}
					}
				}
			}

			TableModel t = prepareTableModel();
			this.tableView.setModel(t);
			this.tableView.repaint();
			updateAppList();
		}
	}
	
	

	public void clear() {
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 7; j++)
				apptMarker[i][j] = new Vector(10, 1);
		TableModel t = prepareTableModel();
		tableView.setModel(t);
		tableView.repaint();
		applist.clear();
	}

	private Appt[] GetMonthAppts() {
		Timestamp start = new Timestamp(0);
		start.setYear(currentY);
		start.setMonth(currentM - 1);
		start.setDate(1);
		start.setHours(0);
		Timestamp end = new Timestamp(0);
		end.setYear(currentY);
		end.setMonth(currentM - 1);
		GregorianCalendar g = new GregorianCalendar(currentY, currentM - 1, 1);
		end.setDate(g.getActualMaximum(mClock.getChangedTime().DAY_OF_MONTH));
		end.setHours(23);
		TimeSpan period = new TimeSpan(start, end);
		return controller.RetrieveAppts(mCurrUser, period);
	}

	private void mousePressResponse() {
		previousRow = tableView.getSelectedRow();
		previousCol = tableView.getSelectedColumn();
	}
	
	private void mouseResponse() {
		int[] selectedRows = tableView.getSelectedRows();
		int[] selectedCols = tableView.getSelectedColumns();
		if(tableView.getSelectedRow() == previousRow && tableView.getSelectedColumn() == previousCol){
			currentRow = selectedRows[selectedRows.length - 1];
			currentCol = selectedCols[selectedCols.length - 1];
		}
		else if(tableView.getSelectedRow() != previousRow && tableView.getSelectedColumn() == previousCol){
			currentRow = tableView.getSelectedRow();
			currentCol = selectedCols[selectedCols.length - 1];
		}
		else if(tableView.getSelectedRow() == previousRow && tableView.getSelectedColumn() != previousCol){
			currentRow = selectedRows[selectedRows.length - 1];
			currentCol = tableView.getSelectedColumn();
		}
		else{
			currentRow = tableView.getSelectedRow();
			currentCol = tableView.getSelectedColumn();
		}
		
		if (currentRow > 5 || currentRow < 0 || currentCol < 0
				|| currentCol > 6)
			return;

		if (tableView.getModel().getValueAt(currentRow, currentCol) != "")
			try {
				currentD = new Integer((String) tableView.getModel()
						.getValueAt(currentRow, currentCol)).intValue();
			} catch (NumberFormatException n) {
				return;
			}
		CalGrid.this.setTitle(mCurrTitle + "(" + currentY + "-" + currentM
				+ "-" + currentD + ")");
		updateAppList();
	}

	public boolean IsTodayAppt(Appt appt) {
		if (appt.TimeSpan().StartTime().getYear() + 1900 != currentY)
			return false;
		if ((appt.TimeSpan().StartTime().getMonth() + 1) != currentM)
			return false;
		if (appt.TimeSpan().StartTime().getDate() != currentD)
			return false;
		return true;
	}

	public boolean IsMonthAppts(Appt appt) {

		if (appt.TimeSpan().StartTime().getYear() + 1900 != currentY)
			return false;

		if ((appt.TimeSpan().StartTime().getMonth() + 1) != currentM)
			return false;
		return true;
	}

	public Appt[] GetApptOnN(int n) {
		Integer temp;
		temp = new Integer(currentD);
		Timestamp start = new Timestamp(0);
		start.setYear(currentY);
		start.setMonth(currentM-1);
		start.setDate(n);
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);
		
		Timestamp end = new Timestamp(0);
		end.setYear(currentY);
		end.setMonth(currentM-1);
		end.setDate(n);
		end.setHours(23);
		end.setMinutes(59);
		end.setSeconds(59);
		
		TimeSpan period = new TimeSpan(start, end);
		return controller.RetrieveAppts(mCurrUser, period);
	}
	
	public Appt[] GetTodayAppt() {
		Integer temp;
		temp = new Integer(currentD);
		Timestamp start = new Timestamp(0);
		start.setYear(currentY);
		start.setMonth(currentM-1);
		start.setDate(currentD);
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);
		
		Timestamp end = new Timestamp(0);
		end.setYear(currentY);
		end.setMonth(currentM-1);
		end.setDate(currentD);
		end.setHours(23);
		end.setMinutes(59);
		end.setSeconds(59);
		
		TimeSpan period = new TimeSpan(start, end);
		return controller.RetrieveAppts(mCurrUser, period);
	}

	public AppList getAppList() {
		return applist;
	}

	public User getCurrUser() {
		return mCurrUser;
	}
	
	public void setCurrUser(User u){
		mCurrUser = u;
	}
	
	// check for any invite or update from join appointment
	public void checkUpdateJoinAppt(){
		// Fix Me!
	}

}
