package hkust.cse.calendar.gui;

import java.awt.Color;
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

class CalCellRenderer extends DefaultTableCellRenderer

{

	private int r;

	private int c;

	public CalCellRenderer(Object value, Vector v) {
		setHorizontalAlignment(SwingConstants.RIGHT);
		setVerticalAlignment(SwingConstants.TOP);
		if (value != null) {
			setForeground(Color.red);
		} else
			setForeground(Color.black);
		if (v.isEmpty()) {
			setBackground(Color.white);
		}
		else{
			switch ((int)v.get(0)) {
			case 1:
				setBackground(Color.blue);	//not group project is blue
				return;
			case 2:
				setBackground(Color.green);	//group project is green
				return;
			case 0:
				setBackground(Color.white);
			}
		}

	}

	public int row() {
		return r;
	}

	public int col() {
		return c;
	}

}
