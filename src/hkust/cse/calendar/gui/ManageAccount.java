package hkust.cse.calendar.gui;

import javax.swing.JDialog;

import hkust.cse.calendar.apptstorage.LocationStorage;
import hkust.cse.calendar.gui.AppScheduler;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class ManageAccount extends JDialog implements ActionListener,
ComponentListener {

	private CalGrid parent;
	
	private Object [][] data = new Object[20][1];
	
	private JTextField userName;
	private JTextField pass;
	
	private JLabel labelName;
	private JLabel labelPass;
	
	private JButton changeNameBut;
	private JButton changePassBut;
	private JButton finishBut;
	

	
	private void commonConstructer(CalGrid cal){
		
		parent = cal;
		Container con;
		con = getContentPane();
	
		setTitle("Settings");
		
		
		/////////////////WE HAVE PANEL, NAMEPANEL AND PASSPANEL////////////////////////
		JPanel panel = new JPanel();
		//panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		JPanel namePanel=new JPanel();
		namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		labelName = new JLabel("User Name:");
		userName = new JTextField(15);
		namePanel.add(labelName);
		namePanel.add(userName);
		
		changeNameBut = new JButton("Change");
		changeNameBut.addActionListener(this);
		namePanel.add(changeNameBut);
		
		JPanel passPanel=new JPanel();
		labelPass = new JLabel("Password:");
		pass = new JTextField(15);
		passPanel.add(labelPass);
		passPanel.add(pass);
		
		changePassBut = new JButton("Change");
		changePassBut.addActionListener(this);
		passPanel.add(changePassBut);
		
		finishBut = new JButton("Finish");
		finishBut.addActionListener(this);
		
		passPanel.add(finishBut);
		
		
		con.add("North",namePanel);
		con.add("South",passPanel);
		
		pack();

	}
	
	  ManageAccount(CalGrid cal) {
			commonConstructer(cal);
		
	}
	
	public TableModel prepareTableModel(){
		TableModel dataModel = new AbstractTableModel(){	

			public int getColumnCount() {
				return 1;
			}

			public int getRowCount() {
//				return son.locationNum();
				return 1; 
			}
			
			public Object getValueAt(int row, int col) {
				return data[row][col];
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
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		/*if(e.getSource() == addBut){
			if(ls.addLocation(locationT.getText())){
				JOptionPane.showMessageDialog(null, "Location added successfully.");
			} else{
				JOptionPane.showMessageDialog(null, "The location is already existed.");
			}
			locationT.setText(null);
			
		} else if(e.getSource() == deleteBut){
			if(ls.deleteLocation(locationT.getText())){
				JOptionPane.showMessageDialog(null, "Location deleted successfully.");
			} else {
				JOptionPane.showMessageDialog(null, "The location is not in the list.");
			}
			locationT.setText(null);
			
		} else if(e.getSource() == finishBut){
			setVisible(false);
			dispose();
			
		}*/
	}

	
	
}