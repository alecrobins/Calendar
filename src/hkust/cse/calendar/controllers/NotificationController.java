package hkust.cse.calendar.controllers;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JLabel;

import hkust.cse.calendar.unit.Clock;
import hkust.cse.calendar.unit.Notification;

//
// In charge of controlling when notifications are fired
// and adding notifications to the list of upcoming notification
public class NotificationController {

	// creating a NotificationController means firing of all
	// stored notifications
	public NotificationController(){
		// load notifications
		// set appropiate timers per notification
	}
	
//	public NotificationController(Notification[] n){}
	
	public void setNotification(Notification n){
		Date d = Clock.getInstance().newDate();
		// get the number of milliseconds until the when to notify
		int delay = (int) (n.getDate().getTime() - d.getTime()); 
		
		// create the event listner
		 NotificationComplete  taskPerformer = new NotificationComplete(n);
		 // set the new timer
		 Timer timer = new Timer(delay, taskPerformer);
		 timer.start();
		 
	}
	
	private class NotificationComplete implements ActionListener {
		
		private Notification notification;
		
		public NotificationComplete(Notification n){
			notification = n;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//TODO: update the UI to display the notification
			System.out.println("NOTIFICATION");
			System.out.println(notification.getTitle());
			System.out.println(notification.getInformation());
			
			alertUser(notification);
			
			// stop the timer
			((Timer)e.getSource()).stop();
		}
		
		private void alertUser(final Notification n){
			
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					NotificationBox not = new NotificationBox(n.getTitle(), n.getInformation());
				}
			});
			
		}
		
		private class NotificationBox extends JFrame {

			JLabel lbl;
			
			public NotificationBox(String title, String descrip) {
				setTitle(title);
				JPanel contentPane = new JPanel();
				lbl = new JLabel(descrip);
				contentPane.add(lbl);
				setContentPane(contentPane);
				Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
				int width = screensize.width;
				int height = screensize.height;
				getContentPane().setPreferredSize(new Dimension(width/4, height/6));
			    setLocation(width/2-width/8, height/2-height/12);
			    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				pack();
				setVisible(true);
			}

		}
		
	}
	
	

	public void alertUser(){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
	}
		});
	}
	
}



