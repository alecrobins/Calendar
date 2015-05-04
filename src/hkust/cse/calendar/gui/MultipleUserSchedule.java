package hkust.cse.calendar.gui;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class MultipleUserSchedule {

	//input:  week of:  mm/dd where the dd is the date of a given monday
	int currMonth;
	int currDate;
	int numDays;
	Object[][] data;
	String[] columnTitles;
	JTable tableView;

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
		return rowVal;
	}
	
	public MultipleUserSchedule(HashMap<User, List<Appt>> userMap, List<Timestamp> dates){
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
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JScrollPane scrollPane = new JScrollPane(tableView);
		scrollPane.getViewport().setViewPosition(new Point(0,0));
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.setSize(100*(1+numDays), 680);
		frame.setVisible(true);

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

	public void getColumnTitles(int month, int date){
		currMonth = month+1;
		currDate = date;
		columnTitles = new String[numDays+1];
		int[] monthNum = new int[7];
		int[] dateNum = new int[7];

		if (currMonth == 2){  //feb
			for (int i = 0; i < 7; i++){
				monthNum[i] = currMonth;
				dateNum[i] = currDate;
				if (currDate == 28){
					currMonth++;
					currDate = 0;
				}
				currDate++;
			}
		}
		else if (currMonth == 4 || currMonth == 6 || currMonth == 9 || currMonth == 11){
			for (int j = 0; j < 7; j++){
				monthNum[j] = currMonth;
				dateNum[j] = currDate;
				if (currDate == 30){
					currMonth++;
					currDate = 0;
				}
				currDate++;
			}
		}
		else if (currMonth == 12){
			for (int k = 0; k < 7; k++){
				monthNum[k] = currMonth;
				dateNum[k] = currDate;
				if (currDate == 31){
					currMonth = 1;
					currDate = 0;
				}
				currDate++;
			}
		}
		else {
			for (int l = 0; l < 7; l++){
				monthNum[l] = currMonth;
				dateNum[l] = currDate;
				if (currDate == 31){
					currMonth++;
					currDate = 0;
				}
				currDate++;
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
