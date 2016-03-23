/*
Copyright (C) 2016  R.W. Sutnavage

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/.
*/
package upbClient;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class deviceDialog
{
	public static void deviceDialog(String  moduleid, String name, String room, String type, String status, String level, JTable t) {
		// Create and set up a frame window
		MouseListener[] listeners = t.getMouseListeners();
		for (MouseListener l : listeners)
		{
			t.removeMouseListener(l);
		}

		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("upbClient Device Update");

		frame.setSize(800, 500);
		frame.setLayout(new GridLayout(12, 1, 10,10));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		removeMinMaxClose(frame);
		frame.setPreferredSize(new Dimension(800, 500));
		frame.pack();
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		JPanel panel4 = new JPanel();
		JPanel panel5 = new JPanel();
		JPanel panel6 = new JPanel();
		JPanel panel7 = new JPanel();

		String deviceInfo = "Device Id: " + moduleid + "    Name: " + name + "    Type: " + type;

		JLabel deviceLabel = new JLabel(deviceInfo, JLabel.CENTER);	
		panel2.add(deviceLabel);

		// Add status label to show the status of the slider
		JLabel sliderStatus = new JLabel("Slide the slider for device level!", JLabel.CENTER);
		// Set the slider
		JSlider slider = new JSlider();	
		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);

		// Set the labels to be painted on the slider
		slider.setPaintLabels(true);

		// Add positions label in the slider
		Hashtable<Integer, JLabel> position = new Hashtable<Integer, JLabel>();
		position.put(0, new JLabel("0"));
		position.put(50, new JLabel("50"));
		position.put(100, new JLabel("100"));

		// Set the label to be drawn
		slider.setLabelTable(position);

		// Add change listener to the slider
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sliderStatus.setText("Level value is : " + ((JSlider)e.getSource()).getValue());
			}
		});
		panel3.add(slider);
		// Add status label to show the status of the slider
		JLabel faderatesliderStatus = new JLabel("Slide the slider for faderate level!", JLabel.CENTER);
		// Set the faderateslider
		JSlider faderateslider = new JSlider(0,255);	
		faderateslider.setMinorTickSpacing(10);
		faderateslider.setPaintTicks(true);

		// Set the labels to be painted on the slider
		faderateslider.setPaintLabels(true);

		// Add positions label in the slider
		Hashtable<Integer, JLabel> faderateposition = new Hashtable<Integer, JLabel>();
		faderateposition.put(0, new JLabel("0"));
		faderateposition.put(125, new JLabel("125"));
		faderateposition.put(255, new JLabel("255"));

		// Set the label to be drawn
		faderateslider.setLabelTable(faderateposition);

		// Add change listener to the slider
		faderateslider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				faderatesliderStatus.setText("FadeRate value is : " + ((JSlider)e.getSource()).getValue());
			}
		});

		// Add the slider to the panel
		panel5.add(faderateslider);

		ButtonGroup group = new ButtonGroup();
		JRadioButton onRadioButton = new JRadioButton("On");
		JRadioButton offRadioButton = new JRadioButton("Off");

		ActionListener sliceActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton aButton = (AbstractButton) actionEvent.getSource();
				System.out.println("Selected: " + aButton.getText());
			}
		};

		panel6.add(onRadioButton);
		group.add(onRadioButton);
		panel6.add(offRadioButton);
		group.add(offRadioButton);

		onRadioButton.addActionListener(sliceActionListener);
		offRadioButton.addActionListener(sliceActionListener);

		JButton okButton = new JButton("Execute Command");
		panel7.add(okButton);
		JButton cancelButton = new JButton("Cancel");
		panel7.add(cancelButton);   

		okButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				//do stuff here Non-Dimming

				final int  GOTO  = 34; // Decimal value of 0x22
				int iLevel = slider.getValue();
				int iFadeRate = faderateslider.getValue();

				int dev = Integer.parseInt(moduleid);
				boolean doesContain = type.toLowerCase().contains("non-dimming");
				if(doesContain == false)
				{
					// Dimming
					System.out.println("dimming");
					sendDeviceCmd(GOTO, dev, iLevel, iFadeRate);
				}
				else
				{
					System.out.println("non-dimming");
					if(onRadioButton.isSelected() == true)
					{
						sendDeviceCmd(GOTO,dev, 100, 255);
					}
					else
					{
						sendDeviceCmd(GOTO, dev, 0, 255);  
					}
				}
				for (MouseListener l : listeners)
				{
					t.addMouseListener(l);
				}
				frame.dispose();
			}
		}); 

		cancelButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				//do stuff here
				System.out.println("Got Cancel");
				//	sendCmd(0x30, 141);
				for (MouseListener l : listeners)
				{
					t.addMouseListener(l);
				}
				frame.dispose();
			}
		}); 

		frame.add(panel1);
		frame.add(panel2);
		frame.add(sliderStatus);
		frame.add(panel3);
		frame.add(panel4);
		frame.add(faderatesliderStatus);
		frame.add(panel5);
		frame.add(panel6);
		frame.add(panel7);
		frame.pack();
		frame.setLocationRelativeTo(null);
		if(type.toUpperCase().contains("NON-DIMMING") == true)
		{
			slider.setVisible(false);
			faderateslider.setVisible(false);
			sliderStatus.setVisible(false);
			faderatesliderStatus.setText("Select On or Off for NON Dimming device");
			if(status == "1")
			{
				group.setSelected(onRadioButton.getModel(), true);
			}
			else
			{
				group.setSelected(onRadioButton.getModel(), false);
			}
		}
		else
		{
			offRadioButton.setVisible(false);
			onRadioButton.setVisible(false);
		}
		frame.setVisible(true);
	}	 

	public static void sendDeviceCmd(int action, int deviceid, int level, int faderate)
	{
		moduleVariables mvInput = new moduleVariables();

		mvInput.clear();
		mvInput.level = level;
		mvInput.fadeRate = faderate;
		mvInput.moduleid = deviceid;
		
		mvInput.sourceid = Integer.parseInt(upbClientWindow.sourceID);
		mvInput.networkid = Integer.parseInt(upbClientWindow.networkID);;
		mvInput.isDevice = true;
		mvInput.action = action;  // report State Command
		buildCmd bc = new buildCmd();

		bc.buildCmd(mvInput);
		String	myCmd = mvInput.message.toString();
		upbClientWindow.buildJSONCommand(0,myCmd, deviceid);
	}

	public static void removeMinMaxClose(Component comp)
	{
		if(comp instanceof AbstractButton)
		{
			comp.getParent().remove(comp);
		}
		if (comp instanceof Container)
		{
			Component[] comps = ((Container)comp).getComponents();
			for(int x = 0, y = comps.length; x < y; x++)
			{
				removeMinMaxClose(comps[x]);
			}
		}
	}
}