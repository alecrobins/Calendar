//main method, code starts here
package hkust.cse.calendar.Main;


import java.util.Date;

import javax.swing.UIManager;

import hkust.cse.calendar.gui.LoginDialog;
import hkust.cse.calendar.unit.ClockConstants;

public class CalendarMain {
	public static boolean logOut = false;
	
	public static void main(String[] args) {
		// set the globabl constants
		ClockConstants.SET_DATE = new Date();
		ClockConstants.TIME_CHANGE = 60*60*1000*24*999912;
		
		while(true){
			logOut = false;
			try{
		//	UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}catch(Exception e){
				
			}
			LoginDialog loginDialog = new LoginDialog();
			while(logOut == false){
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
		
