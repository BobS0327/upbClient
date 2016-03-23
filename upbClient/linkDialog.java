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

public class linkDialog
{

	public static void linkDialog(String  linkid, String name, JTable t) {
		// Create and set up a frame window
		MouseListener[] listeners = t.getMouseListeners();
		for (MouseListener l : listeners)
		{
			t.removeMouseListener(l);
		}

		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("upbClient Link Update");

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
	
		String deviceInfo = "Link Id: " + linkid + "    Name: " + name;

		JLabel deviceLabel = new JLabel(deviceInfo, JLabel.CENTER);	
		panel2.add(deviceLabel);
	
		JButton okButton = new JButton("Activate Link");
		panel4.add(okButton);
		JButton cancelButton = new JButton("Cancel");
		panel4.add(cancelButton);   

		okButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent e){
				//do stuff here Non-Dimming
				final int ACTIVATELINK = 32;
				
				sendLinkCommand(ACTIVATELINK, Integer.parseInt(linkid));
			//		System.out.println("Got OK");

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

				for (MouseListener l : listeners)
				{
					t.addMouseListener(l);
				}
				frame.dispose();
			}
		}); 

		frame.add(panel1);
		frame.add(panel2);
		frame.add(panel3);

		frame.add(panel4);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}	 


	public static void sendLinkCommand(int action, int link )
	{
		moduleVariables mvInput = new moduleVariables();

		mvInput.clear();
		mvInput.moduleid = link;
		mvInput.sourceid = Integer.parseInt(upbClientWindow.sourceID);
		mvInput.networkid = Integer.parseInt(upbClientWindow.networkID);;
		mvInput.isDevice = false;
		mvInput.action = action;  
		buildCmd bc = new buildCmd();

		bc.buildCmd(mvInput);
		String	myCmd = mvInput.message.toString();
		upbClientWindow.buildJSONCommand(0,myCmd, 0);
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