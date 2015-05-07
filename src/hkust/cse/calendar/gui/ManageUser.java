package hkust.cse.calendar.gui;

import javax.swing.JDialog;

import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.apptstorage.LocationStorage;
import hkust.cse.calendar.gui.AppScheduler;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.LocationList;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
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

public class ManageUser extends JDialog implements ActionListener,
ComponentListener {

	private CalGrid parent;
	private ApptStorageSQLImpl asql;

	private Object [][] data = new Object[20][1];

	private JComboBox userList;

	private JButton inspectBut;
	private JButton changeBut;
	private JButton removeBut;
	private JButton finishBut;

	private LocationStorage ls;


	private void commonConstructer(CalGrid cal){

		parent = cal;
		User curr = cal.getCurrUser();
		asql = new ApptStorageSQLImpl(curr);

		List<User> userL = asql.getListOfAllUsers();
		String[] userLis = new String[userL.size()];
		int i = 0;
		for (User u: userL){
			userLis[i] = u.getUsername();
			i++;
		}
		
		Container con;
		con = getContentPane();

		setTitle("Manage Userbase");

		JPanel panel = new JPanel();
		
		userList = new JComboBox(userLis);
		panel.add(userList);
		
		inspectBut = new JButton("Inspect");
		inspectBut.addActionListener(this);
		panel.add(inspectBut);
		
		changeBut = new JButton("Change");
		changeBut.addActionListener(this);
		panel.add(changeBut);
		
		removeBut = new JButton("Remove");
		removeBut.addActionListener(this);
		panel.add(removeBut);
		
		finishBut = new JButton("Finish");
		finishBut.addActionListener(this);
		panel.add(finishBut);
		
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		con.add("North",panel);

		pack();
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
	//locationTable = new JTable(prepareTableModel());
	//scrallpane = new JScrallPane(locationTable);

	ManageUser(CalGrid cal) {
		commonConstructer(cal);
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
		User u = asql.getUser(userList.getSelectedItem().toString());
		String info = "Email: "+u.getEmail()+"    Name: "+u.getFirstname();
		System.out.println("ACTION PERFORMED");
		
		if(e.getSource() == inspectBut){
			if (info != null){
				JOptionPane.showMessageDialog(null, ""+info);
			} else{
				JOptionPane.showMessageDialog(null, "The user has requested that you give him privacy and don't inspect his account.");
			}
		}
			
		if(e.getSource() == changeBut){
				System.out.println("we in hurrr");
				this.setVisible(false);
				ChangeUserAccount a = new ChangeUserAccount(parent, u);
				a.setLocationRelativeTo(null);
				a.show();

		}
		if(e.getSource() == removeBut){
			asql.deleteUser(asql.getUser(userList.getSelectedItem().toString()).getID());
				JOptionPane.showMessageDialog(null, "User removed successfully.");


		}
		if(e.getSource() == finishBut){
			setVisible(false);
			dispose();

		}



	}
}
