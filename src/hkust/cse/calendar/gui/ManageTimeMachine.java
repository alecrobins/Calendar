
package hkust.cse.calendar.gui;

import javax.swing.JDialog;

import hkust.cse.calendar.Main.CalendarMain;
import hkust.cse.calendar.apptstorage.LocationStorage;
import hkust.cse.calendar.gui.AppScheduler;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Clock;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.LocationList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.metal.MetalBorders.Flush3DBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;

// TODO: need to create a gui so that you can change the ClockConstant variables

public class ManageTimeMachine extends JDialog implements ActionListener,
ComponentListener {

	private CalGrid parent;
	
	private Object [][] data = new Object[20][1];
	
	private BasicArrowButton eButton;
	private BasicArrowButton wButton;
	
	private BasicArrowButton ehourButton;
	private BasicArrowButton whourButton;
	
	private BasicArrowButton eminuteButton;
	private BasicArrowButton wminuteButton;
	
	private BasicArrowButton esecondButton;
	private BasicArrowButton wsecondButton;
	
	
	private JLabel labelhour;
	private JLabel labelsecond;
	private JLabel labelminute;
	private JLabel year;
	private JLabel hour;
	private JLabel minute;
	private JLabel second;
	private JComboBox month;
	private JComboBox day;
	private GregorianCalendar today;
	public int currentY;
	public int currentM;
	public int currentD;
	
	public int currentH;
	public int currentMi;
	public int currentS;
	
	public Date newDate;
	
	private JButton changeTimeBut;
	private JButton finishBut;
	
	private final String[] months = { "January", "Feburary", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };
	
	private final String[] names = { "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };
	
	public static final int[] monthDays = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
		31, 30, 31 };
	
	private final String[] days = { "1", "2", "3", "4",
			"5", "6", "7", "8", "9", "10",
			"11", "12", "13","14","15","16","17","18","19","20","21","22","23","24",
			"25","26","27","28","29","30","31" };
	
	private JTable tableView;
	

private void commonConstructer(CalGrid cal){
		
		parent = cal;
		
		Container con;
		con = getContentPane();
	
		setTitle("Manage TimeMachine");
		
		today = parent.mClock.getChangedTime();
		currentY = today.get(GregorianCalendar.YEAR);
		currentD = today.get(today.DAY_OF_MONTH);
		int temp = today.get(today.MONTH) + 1;
		int temp_1=today.get(today.DAY_OF_MONTH);
		currentM = 12;
		currentH=today.get(today.HOUR);
		currentMi=today.get(today.MINUTE);
		currentS=today.get(today.SECOND);
		
		changeTimeBut = new JButton("Change");
		changeTimeBut.addActionListener(this);
		
		finishBut = new JButton("Finish");
		finishBut.addActionListener(this);
		

		eButton = new BasicArrowButton(SwingConstants.EAST);
		eButton.setEnabled(true);
		eButton.addActionListener(this);
		wButton = new BasicArrowButton(SwingConstants.WEST);
		wButton.setEnabled(true);
		wButton.addActionListener(this);

		year = new JLabel(new Integer(currentY).toString());
		
		labelhour = new JLabel("hour");
		labelminute = new JLabel(":");
		labelsecond = new JLabel(":");
		
		hour = new JLabel(new Integer(currentH).toString());
		minute = new JLabel(new Integer(currentMi).toString());
		second = new JLabel(new Integer(currentS).toString());
		
		
		
		month = new JComboBox();
		month.addActionListener(this);
		month.setPreferredSize(new Dimension(200, 30));
		for (int cnt = 0; cnt < 12; cnt++)
			month.addItem(months[cnt]);
		month.setSelectedIndex(temp - 1);
		
		day = new JComboBox();
		day.addActionListener(this);
		day.setPreferredSize(new Dimension(200, 30));
		for (int cnt = 0; cnt < 31; cnt++)
			day.addItem(days[cnt]);
		day.setSelectedIndex(temp_1 - 1);
		
		ehourButton = new BasicArrowButton(SwingConstants.EAST);
		ehourButton.setEnabled(true);
		ehourButton.addActionListener(this);
		whourButton = new BasicArrowButton(SwingConstants.WEST);
		whourButton.setEnabled(true);
		whourButton.addActionListener(this);
		
		eminuteButton = new BasicArrowButton(SwingConstants.EAST);
		eminuteButton.setEnabled(true);
		eminuteButton.addActionListener(this);
		wminuteButton = new BasicArrowButton(SwingConstants.WEST);
		wminuteButton.setEnabled(true);
		wminuteButton.addActionListener(this);
		
		esecondButton = new BasicArrowButton(SwingConstants.EAST);
		esecondButton.setEnabled(true);
		esecondButton.addActionListener(this);
		wsecondButton = new BasicArrowButton(SwingConstants.WEST);
		wsecondButton.setEnabled(true);
		wsecondButton.addActionListener(this);

		JPanel yearGroup = new JPanel();
		yearGroup.setLayout(new FlowLayout());
		yearGroup.setBorder(new Flush3DBorder());
		yearGroup.add(wButton);
		yearGroup.add(year);
		yearGroup.add(eButton);
		yearGroup.add(month);
		yearGroup.add(day);
		
		JPanel hourGroup = new JPanel();
		hourGroup.setLayout(new FlowLayout());
		hourGroup.setBorder(new Flush3DBorder());
		hourGroup.add(whourButton);
		hourGroup.add(hour);
		hourGroup.add(ehourButton);
		
		hourGroup.add(labelminute);
		hourGroup.add(wminuteButton);
		hourGroup.add(minute);
		hourGroup.add(eminuteButton);
		
		hourGroup.add(labelsecond);
		hourGroup.add(wsecondButton);
		hourGroup.add(second);
		hourGroup.add(esecondButton);
		
		hourGroup.add(changeTimeBut);
		hourGroup.add(finishBut);
		
		
		con.add("South",yearGroup);
		con.add("North",hourGroup);
		
		//con.add("East",changeBut);

		pack();

	}
	

   ManageTimeMachine(CalGrid cal) {
	commonConstructer(cal);
	}
	
   
   public TableModel prepareTableModel(){
		TableModel dataModel = new AbstractTableModel(){	

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
			data[row][column] = aValue;
			}
		};
		
		return dataModel;
	}
	
   
   
   
   
   
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == changeTimeBut){
			
			currentD=day.getSelectedIndex()+1;
			currentM=month.getSelectedIndex();
			
			parent.mClock.changeTimeTo(currentY, currentM, currentD, currentH, currentMi, currentS);

		

		//		
//		Clock.getInstance().newCalendar();
//		Clock.getInstance().newGregorianCalendar();
//		
		
		//TODO: update the calendar grid gui
			parent.refreshCal();
			
		}
	 else if(e.getSource() == finishBut){
		 
	 parent.UpdateCal();
		setVisible(false);
		dispose();
	}
		
		
		
		
		
		if (e.getSource() == eButton) {
			if (year == null)
				return;
			currentY = currentY + 1;
			year.setText(new Integer(currentY).toString());
			
		} else if (e.getSource() == wButton) {
			if (year == null)
				return;
			currentY = currentY - 1;
			year.setText(new Integer(currentY).toString());
			
	
		} 
		
		
		/*****************************************/
		
		if (e.getSource() == ehourButton) {
			if (hour == null)
				return;
			currentH = currentH + 1;
			if(currentH>24)
				currentH=1;
			hour.setText(new Integer(currentH).toString());
			
		} else if (e.getSource() == whourButton) {
			if (hour == null)
				return;
			currentH = currentH - 1;
			if(currentH<1)
				currentH=24;
			hour.setText(new Integer(currentH).toString());
			
	
		} 
		
		
		if (e.getSource() == eminuteButton) {
			if (minute == null)
				return;
			currentMi = currentMi + 1;
			if(currentMi>60)
				currentMi=1;
			minute.setText(new Integer(currentMi).toString());
			
		} else if (e.getSource() == wminuteButton) {
			if (minute == null)
				return;
			currentMi = currentMi - 1;
			if(currentMi<1)
				currentMi=60;
			minute.setText(new Integer(currentMi).toString());
			
	
		}
		
		if (e.getSource() == esecondButton) {
			if (second == null)
				return;
			currentS = currentS + 1;
			if(currentS>60)
				currentS=1;
			second.setText(new Integer(currentS).toString());
			
		} else if (e.getSource() == wsecondButton) {
			if (second == null)
				return;
			currentS= currentS - 1;
			if(currentS<1)
				currentS=60;
			second.setText(new Integer(currentS).toString());
			
	
		} 
		
		
		
		
		
		
	}
	

		

	}
	
	
	
	
	
	

