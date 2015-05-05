package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.LocationStorage;
import hkust.cse.calendar.controllers.GroupController;
import hkust.cse.calendar.unit.Appt;
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
import java.util.List;

import javax.swing.BorderFactory;
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

public class MultipleUserSchedule {

	//input:  week of:  mm/dd where the dd is the date of a given monday
	private MouseEvent tempe;
	public int previousRow;
	public int previousCol;
	public int currentRow;
	public int currentCol;
	public int currentD;
	public int currentM;
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
	
	private CalGrid parent;
	private LocationStorage parentLS;
	
	private GroupController gc;

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
		boolean[] rowVal = new boolean[(1+dates.size())*40];
		for (int i = 0; i < rowVal.length; i++){   //initialize to true
			rowVal[i] = true;
		}
		for (List<Appt> ap : userMap.values()){    //for each user
			for (int i = 0; i < ap.size(); i++){   //for each appt in user's list
				//System.out.println("appt start time = " +ap.get(i).TimeSpan().StartTime().getTime());
				//System.out.println("appt end time = " +ap.get(i).TimeSpan().EndTime().getTime());
				long rowStart = 0;
				long rowEnd = rowStart + 900000;
				for (int j = 0; j < ((1+dates.size())*40); j++){   //for each row in rowVal
					if (rowVal[j] == true){
					if (!((ap.get(i).TimeSpan().EndTime().getTime() <= rowStart)
							|| (ap.get(i).TimeSpan().StartTime().getTime() >= rowEnd))){  //if intersection
						rowVal[j] = false;   
					}
					}
					//System.out.println("rowEnd "+j+" = "+rowEnd);
					rowStart += 900000;
					rowEnd += 900000;
				}
			}
		}
		rowBool = rowVal;
		return rowVal;
	}
	
	public MultipleUserSchedule(CalGrid cal, HashMap<User, List<Appt>> userMap, List<Timestamp> dates){
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
		mi = (JMenuItem) pop.add(new JMenuItem("New"));

		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				NewAppt();
			}
		});
		
		mi = (JMenuItem) pop.add(new JMenuItem("Pick Slot"));

		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				pickSlot();
			}
		});
	}
	
	public void setParent(CalGrid grid) {
		parent = grid;
		parentLS = parent.locationStorage;
	}
	
	private void NewAppt() {   // only initiator can use this, and it should send invites out
	}
	private void pickSlot() {
		GroupSlotPicker gsp = new GroupSlotPicker("New Slot", parent, parentLS);
		//right now it doesn't work yet since the CalGrid hasn't been instantiated first
		//we need a function in CalGrid that says "hey --- want a group event???"
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
	}

	public void getColumnTitles(int month, int date){
		currentM = month+1;
		currentD = date;
		columnTitles = new String[numDays+1];
		int[] monthNum = new int[7];
		int[] dateNum = new int[7];

		if (currentM == 2){  //feb
			for (int i = 0; i < 7; i++){
				monthNum[i] = currentM;
				dateNum[i] = currentD;
				if (currentD == 28){
					currentM++;
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
