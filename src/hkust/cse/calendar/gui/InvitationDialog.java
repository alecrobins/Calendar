package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.apptstorage.LocationStorage;
import hkust.cse.calendar.unit.GroupEvent;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class InvitationDialog extends JDialog implements ActionListener,
ComponentListener {

	private List<JCheckBox> tCheckList;
	private JButton acceptBut;
	private JButton rejectBut;

	private void commonConstructor(GroupEvent event) {

		tCheckList = new LinkedList<JCheckBox>();
		List<TimeSpan> tList = event.getTList();
		
		if (tList.isEmpty()) {
			tCheckList.clear();
		}
		else {
			for (TimeSpan ts : tList) {
				String a = Integer.toString(ts.StartTime().getMonth()) + "/" + Integer.toString(ts.StartTime().getDate()) + "  ";
				a = a + Integer.toString(ts.StartTime().getHours()) + ":" + Integer.toString(ts.StartTime().getMinutes());
				a = a + " - " + Integer.toString(ts.EndTime().getHours()) + ":" + Integer.toString(ts.EndTime().getMinutes());

				JCheckBox c = new JCheckBox(a);
				tCheckList.add(c);
			}
		}
		
		this.setAlwaysOnTop(true);
		setTitle("Invitation");
		setModal(false);

		Container contentPane;
		contentPane = getContentPane();

		JPanel timeP = new JPanel();
		Border dateBorder = new TitledBorder(null, "Select your preference");
		timeP.setBorder(dateBorder);
		timeP.setLayout(new BoxLayout(timeP, BoxLayout.Y_AXIS));
		
		for (JCheckBox c : tCheckList) {
			timeP.add(c);
		}

		contentPane.add("North", timeP);

		JPanel butP = new JPanel();
		butP.setLayout(new FlowLayout(FlowLayout.RIGHT));

		acceptBut = new JButton("Accept");
		acceptBut.addActionListener(this);
		butP.add(acceptBut);

		rejectBut = new JButton("Reject");
		rejectBut.addActionListener(this);
		butP.add(rejectBut);

		contentPane.add("South", butP);

		pack();
		setLocationRelativeTo(null);
		setVisible(true);	
	}

	public void actionPerformed(ActionEvent e){
		if (e.getSource() == "acceptBut") {

		} 

		else if (e.getSource() == "rejectBut") {

		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}


}
