

package com.manicken;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import java.awt.Dimension;

public class SelectionDialog extends JPanel
{
	//private JLabel lblServerport;
	//public JCheckBox chkAutostart;
	public JCheckBox chkDebugMode;	
    //public JTextField txtServerport;

    public SelectionDialog() {
        //construct components
		//lblServerport = new JLabel ("Server Port");
		//chkAutostart = new JCheckBox ("Autostart Server at Arduino IDE start");
		chkDebugMode = new JCheckBox ("Activates some debug output");
        //txtServerport = new JTextField (5);

        //adjust size and set layout
        setPreferredSize (new Dimension (263, 129));
        setLayout (null);

        //add components
		//add (lblServerport);
        //add (chkAutostart);
		//add (txtServerport);
		add (chkDebugMode);

        //set component bounds (only needed by Absolute Positioning)
        //lblServerport.setBounds (5, 5, 100, 25);
        //txtServerport.setBounds (85, 5, 100, 25);
		//chkAutostart.setBounds (4, 30, 232, 30);
		chkDebugMode.setBounds (4, 65, 232, 30);
    }

}
