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
import javax.swing.JPasswordField;
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
	private JPasswordField pass;
	private JTextField email;
	
	private JLabel labelName;
	private JLabel labelPass;
	private JLabel labelEmail;
	
	private JButton changeNameBut;
	private JButton changePassBut;
	private JButton changeEmailBut;
	private JButton finishBut;
	private JButton editUserList;
	

	
	private void commonConstructer(CalGrid cal){
		
		parent = cal;
		Container con;
		con = getContentPane();
		
		JPanel top = new JPanel();
	
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
		pass = new JPasswordField(15);
		passPanel.add(labelPass);
		passPanel.add(pass);
		
		changePassBut = new JButton("Change");
		changePassBut.addActionListener(this);
		passPanel.add(changePassBut);
		
		JPanel ePanel=new JPanel();
		labelEmail = new JLabel("E-mail:");
		email = new JPasswordField(15);
		ePanel.add(labelEmail);
		ePanel.add(email);
		
		changeEmailBut = new JButton("Change");
		changeEmailBut.addActionListener(this);
		ePanel.add(changeEmailBut);
		
		
		finishBut = new JButton("Finish");
		finishBut.addActionListener(this);
		if (!cal.getCurrUser().isAdmin()) {
			ePanel.add(finishBut);
		}
		
		top.add(namePanel);
		top.add(passPanel);
		top.add(ePanel);
		
		con.add("North",top);
		
		if (cal.getCurrUser().isAdmin()){
			JPanel userPanel = new JPanel();
			editUserList = new JButton("Edit User List");
			editUserList.addActionListener(this);
			userPanel.add(editUserList);
			JPanel locationPanel = new JPanel();
			con.add("South", userPanel);
		}
			
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

	@SuppressWarnings("unused")
	@Override
	public void actionPerformed(ActionEvent e) {
		
		User user=parent.getCurrUser();  //who is this user
		int userID=user.getID();         //what is his/her ID
		ApptStorageSQLImpl dataBase= new ApptStorageSQLImpl(); //connecting to database
		
		if(e.getSource() == changeNameBut){
			if(userName.getText().trim()!=null && dataBase.isUserNameAvailable(userName.getText().trim())){
				
				User newUser=new User(userID,userName.getText().trim(),user.Password(),user.isAdmin());
				newUser.setEmail(" ");
				dataBase.modifyUser(newUser);
				
				JOptionPane.showMessageDialog(null, "UserName changed successfully.");
			} else{
				JOptionPane.showMessageDialog(null, "UserName is already exist!");
			}
		
		}
			else if(e.getSource() == changeEmailBut){
				if(email.getText().trim()!=null){ //&& dataBase.isEmailAvailable(email.getText().trim())){
					
					User newUser=new User(userID,userName.getText().trim(),user.Password(),user.isAdmin());
					newUser.setEmail(email.getText().trim());
					dataBase.modifyUser(newUser);
					
					JOptionPane.showMessageDialog(null, "E-mail changed successfully.");
				} 
				//else{
					//JOptionPane.showMessageDialog(null, "UserName is already exist!");
				//}
			
		} else if(e.getSource() == changePassBut){
				
				String newPass = new String(pass.getPassword());
				
				if(newPass!=null){
					User newUser=new User(userID,user.getUsername(),newPass,user.isAdmin());
					newUser.setEmail(" ");

					dataBase.modifyUser(newUser);
				
					JOptionPane.showMessageDialog(null, "Password changed successfully.");
				}
				else{
					
					JOptionPane.showMessageDialog(null, "Password can not be empty!");
				}
				
				
		
		}else if(e.getSource() == editUserList){
			setVisible(false);
			ManageUser mu = new ManageUser(parent);
			mu.setLocationRelativeTo(null);
			mu.show();
			
		} 
		
		else if(e.getSource() == finishBut){
			setVisible(false);
			dispose();
			
		}
		
		
	}

	
	
}