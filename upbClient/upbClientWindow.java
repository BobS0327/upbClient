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

import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Font;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Color;
import javax.swing.UIManager;

import com.pubnub.api.PubnubException;
import pubNub.pubNubMethods;

public class upbClientWindow {
	private static JFrame frame;
	public static JTable devicetable;
	public static JTable linktable;
	public static String configProperties = "config.properties";
//	linux
//	public static String configProperties = "/home/bob/Temp/upbClient/bin/config.properties";
	public static String serverdbName; 
	public static String clientdbName; 
	public static String clientIPAddress;
	public static String upbServerIPAddress; 
	public static String upbServerCommandPort; 
	public static int upbServerDownloadPort;
	public static String networkInterface;
	public static String pubNubChannel;
	public static String pubNubSubscribeKey;
	public static String pubNubPublishKey;
	public static String networkID;
	public static String sourceID;
	public static String temp = null;

	public static JLabel StatusLabel;
	static pubNubMethods  pnMethods = null; 
	static boolean bFirstTime = true;
	static boolean bLinkFirstTime = true;
	static int totalRecords = 0;
	static int totalLinkRecords = 0;
	static int tempreccount = 0;
	public static int templinkreccount = 0;
	static int recnum = 0;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					if(args.length > 0)
					{
						configProperties = args[0];
						File varFile = new File(configProperties);
						if(varFile.exists() != true)
						{
							System.out.println("Cannot locate" + args[0] + " defaulting to config.properties");
							configProperties = "config.properties";	 
						}
						else
						{
							System.out.println("Using :" + args[0]);	
						}
					}
					else
					{
						System.out.println("Using : config.properties");	
					}

					
					
					
					
					
					
					upbClientWindow window = new upbClientWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Object GetData(JTable table, int row_index, int col_index){
		return table.getModel().getValueAt(row_index, col_index);
	}  

	public void readConfigFile()
	{
		String configfile = new File(configProperties).getAbsolutePath();
		FileInputStream fileInput = null;
		Properties properties = new Properties();
		try {
			fileInput = new FileInputStream(configfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			properties.load(fileInput);
			fileInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		networkInterface = properties.getProperty("networkinterface");
		serverdbName = properties.getProperty("serverdbname");
		clientdbName = properties.getProperty("clientdbname");
		upbServerIPAddress = properties.getProperty("upbserveripaddress");
		upbServerCommandPort = properties.getProperty("upbservercommandport");
		temp = properties.getProperty("upbserverdownloadport");
		upbServerDownloadPort  =  Integer.parseInt(temp);
		pubNubChannel = properties.getProperty("pubnubchannelname");
		pubNubSubscribeKey = properties.getProperty("pubnubsubscribekey");
		pubNubPublishKey = properties.getProperty("pubnubpublishkey");
		networkID = properties.getProperty("networkid");
		sourceID = properties.getProperty("sourceid");
	}

	public static void loadDeviceTablefromPubNum( JTable table, String jsonString)
	{ 
		String source = null, dest = null;
		String jsontimestamp = null, deviceid = null, linkid = null;
		String devicename = null, linkname = null, kind = null, timestamp = null;
		String room = null, level = null;
		String desc = null;
		String info = null;
		int status = 0;
		int action = 0, recnum = 0, reccount = 0;

		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			source = (String) jsonObject.get("source");
			dest = (String) jsonObject.get("destination");
			action =  (int) jsonObject.get("action");
			recnum =  (int) jsonObject.get("recnum");

			room = (String)jsonObject.get("room");
			level = (String)jsonObject.get("level");
			jsontimestamp = (String)jsonObject.get("jsontimestamp");
			deviceid  = (String) jsonObject.get("deviceid");
			linkid = (String) jsonObject.get("linkid");
			devicename = (String) jsonObject.get("devicename");
			linkname = (String) jsonObject.get("linkname");
			kind = (String) jsonObject.get("kind");
			desc = (String) jsonObject.get("desc");
			status = (int) jsonObject.get("status");
			timestamp = (String) jsonObject.get("timestamp");
			info = (String) jsonObject.getString("info");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(bFirstTime == true)
		{
			try {
				totalRecords =  (int) jsonObject.get("reccount");
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			DefaultTableModel dm = (DefaultTableModel) table.getModel();
			deleteAllRows(dm);
			for( int nRow = 0 ; nRow < table.getRowCount() ; nRow++ ){
				for( int nColumn = 0 ; nColumn < table.getColumnCount(); nColumn++ ){
					table.setValueAt("" , nRow , nColumn );
				}
			}
			//		table.repaint(); // This will reflect the changes (empty table) on the screen
			bFirstTime = false;
		}
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		if ("".equals(info))
		{ 
			if(status == 0)
			{
				temp = timestamp +" Device turned off";
			}		 
			else
			{
				temp = timestamp +" Device turned on";	
			}
		}
		else
		{
			temp =  info; 
		}
		model.addRow(new Object[]{deviceid, devicename , room, desc, status, level, temp });
		++tempreccount;
		if(tempreccount == totalRecords)
		{
			bFirstTime = true;
			tempreccount = 0;
		}
	}

	public static void loadLinkTablefromPubNub( JTable table2, String jsonString)
	{
		String source = null, dest = null;
		String jsontimestamp = null, deviceid = null, linkid = null;
		String devicename = null, linkname = null, kind = null, timestamp = null;
		String room = null, level = null;
		String desc = null;
		String info = null;
		int status = 0;
		int action = 0, recnum = 0, reccount = 0;

		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		try {
			source = (String) jsonObject.get("source");
			dest = (String) jsonObject.get("destination");
			action =  (int) jsonObject.get("action");
			recnum =  (int) jsonObject.get("recnum");

			room = (String)jsonObject.get("room");
			level = (String)jsonObject.get("level");
			jsontimestamp = (String)jsonObject.get("jsontimestamp");
			deviceid  = (String) jsonObject.get("deviceid");
			linkid = (String) jsonObject.get("linkid");
			devicename = (String) jsonObject.get("devicename");
			linkname = (String) jsonObject.get("linkname");
			kind = (String) jsonObject.get("kind");
			desc = (String) jsonObject.get("desc");
			status = (int) jsonObject.get("status");
			timestamp = (String) jsonObject.get("timestamp");
			info = (String) jsonObject.getString("info");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(bLinkFirstTime == true)
		{
			try {
				totalLinkRecords =  (int) jsonObject.get("reccount");
			} catch (JSONException e) {
				e.printStackTrace();
			} 	
			DefaultTableModel dm = (DefaultTableModel) table2.getModel();
			deleteAllRows(dm);
			bLinkFirstTime = false;
		}
		table2.repaint(); // This will reflect the changes (empty table) on the screen
		DefaultTableModel model = (DefaultTableModel) table2.getModel();
		model.addRow(new Object[]{linkid, linkname});
		++templinkreccount;
		if(templinkreccount == totalLinkRecords)
		{
			bLinkFirstTime = true;
			templinkreccount = 0;
		}
	}


	public upbClientWindow() {
		initialize();
	}

	private void initialize() {
		readConfigFile();
		pnMethods = new pubNubMethods();
		try {
			pnMethods.Subscribe();
		} catch (PubnubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Dummy method, just executed to get IPAddress of client
		String ipAndHostname = getIPAddressAndHostname.getIPAddresAndHostname();	
		frame = new JFrame();
		frame.setBounds(100, 100, 1193, 767);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		int numberOfColumns = 7;
		int numberOfRows = 0;
		int linkRows = 0;
		int linkColumns = 2;

		devicetable = new JTable(numberOfRows,numberOfColumns)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;//you can set which column/row can be edited.
			}
		};
		devicetable.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = devicetable.rowAtPoint(e.getPoint());
				int col = devicetable.columnAtPoint(e.getPoint());
				if (row >= 0 && col >= 0) {

					int retRow =  	devicetable.convertRowIndexToModel( row );
					Object obj0 = GetData(devicetable, retRow, 0);
					Object obj1 = GetData(devicetable, retRow, 1);
					Object obj2 = GetData(devicetable, retRow, 2);
					Object obj3 = GetData(devicetable, retRow, 3);
					Object obj4 = GetData(devicetable, retRow, 4);
					Object obj5 = GetData(devicetable, retRow, 5);

					upbClient.deviceDialog dD = new upbClient.deviceDialog();
					dD.deviceDialog(obj0.toString(),obj1.toString(),obj2.toString(),obj3.toString(),obj4.toString(),obj5.toString(), devicetable );
				}
			}
		});

		devicetable.setBounds(1, 26, 698, 182);
		String header[] = {"id", "name", "room", "type", "status", "level", "info"};

		for(int i=0;i<devicetable.getColumnCount();i++)
		{
			TableColumn column = devicetable.getTableHeader().getColumnModel().getColumn(i);
			switch(i) 
			{
			case 0:
				column.setPreferredWidth(2);
				break;
			case 3:
				column.setPreferredWidth(200); 
				break;
			case 4:
				column.setPreferredWidth(5);  
				break;
			case 5:
				column.setPreferredWidth(5);  
				break;
			case 6:
				column.setPreferredWidth(200);  
				break;
			default:
				break;
			}
			column.setHeaderValue(header[i]);
		} 
		frame.getContentPane().add(devicetable, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(devicetable);
		scrollPane.setBounds(32, 21, 1126, 278);
		frame.getContentPane().add(scrollPane);
		JLabel lblNewLabel = new JLabel("upbClient Devices");
		lblNewLabel.setBounds(0, 0, 1177, 21);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		devicetable.setAutoCreateRowSorter(true);
		String header1[] = {"id", "name"};
		linktable = new JTable(linkRows,linkColumns)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;//you can set which column/row can be edited.
			}
		};

		for(int i=0;i<linktable.getColumnCount();i++)
		{
			TableColumn linkcolumn = linktable.getTableHeader().getColumnModel().getColumn(i);
			switch(i) 
			{
			case 0:
				linkcolumn.setPreferredWidth(2);
				break;
			case 1:
				linkcolumn.setPreferredWidth(200); 
				break;
			default:
				break;
			}
			linkcolumn.setHeaderValue(header1[i]);
		} 
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(32, 383, 1126, 166);
		frame.getContentPane().add(scrollPane_1);
		scrollPane_1.setViewportView(linktable);
		linktable.setAutoCreateRowSorter(true);
		linktable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = linktable.rowAtPoint(e.getPoint());
				int col = linktable.columnAtPoint(e.getPoint());
				if (row >= 0 && col >= 0) {

					int retRow =  	linktable.convertRowIndexToModel( row );
					Object obj0 = GetData(linktable, retRow, 0);
					Object obj1 = GetData(linktable, retRow, 1);
					upbClient.linkDialog dD = new upbClient.linkDialog();
					dD.linkDialog(obj0.toString(),obj1.toString(), linktable );
				}
			}
		});

		linktable.setBackground(new Color(255, 255, 255));
		//	loadLinkTable( clientdbName, linktable);
		JLabel lblUpbclientSceneslinks = new JLabel("upbClient Scenes (Links)");
		lblUpbclientSceneslinks.setHorizontalAlignment(SwingConstants.CENTER);
		lblUpbclientSceneslinks.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblUpbclientSceneslinks.setBounds(10, 355, 1157, 29);
		frame.getContentPane().add(lblUpbclientSceneslinks);

		StatusLabel = new JLabel("");
		StatusLabel.setBorder(UIManager.getBorder("TextField.border"));
		StatusLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
		StatusLabel.setBounds(32, 688, 1126, 29);
		frame.getContentPane().add(StatusLabel);

		DefaultRowSorter sorter = ((DefaultRowSorter)devicetable.getRowSorter());
		ArrayList list = new ArrayList();
		list.add( new RowSorter.SortKey(0, SortOrder.ASCENDING) );
		sorter.setSortKeys(list);
		sorter.sort();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblStatus.setBounds(32, 656, 88, 21);
		frame.getContentPane().add(lblStatus);

		JButton btnPubnubLoad = new JButton("PubNub Load");
		btnPubnubLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				buildJSONCommand(1, "", -1);
				buildJSONCommand(3, "", -1);
			}
		});
		btnPubnubLoad.setBounds(797, 560, 148, 23);
		frame.getContentPane().add(btnPubnubLoad);
		SetStatusText("Program started");
	}

	public static void deleteAllRows(final DefaultTableModel model) {
		for( int i = model.getRowCount() - 1; i >= 0; i-- ) {
			model.removeRow(i);
		}
	}

	public static void SetStatusText(String statusText)
	{
		Calendar calobj = Calendar.getInstance();
		String temp = calobj.getTime().toString() + "  " + statusText;
		StatusLabel.setText(temp);
	}
	public static final String stackTrace(Throwable e)
	{
		String foo = null;
		try
		{
			ByteArrayOutputStream ostr = new ByteArrayOutputStream();
			e.printStackTrace( new PrintWriter(ostr,true) );
			foo = ostr.toString();
		}
		catch (Exception f)
		{
			// Do nothing.
		}
		return foo;
	}
	public static String getDateandTime()
	{
		long currentDateTime = System.currentTimeMillis();
		Date now = new Date(currentDateTime);
		DateFormat formatter = DateFormat.getDateTimeInstance(); // Date and time
		return ( formatter.format(now));
	}
	public static void buildJSONCommand(int cmd, String executableCmd, int deviceid)
	{
		JSONObject obj = new JSONObject( );

		try {
			// Used for testing only FIX it
			//	obj.put("source", "192.168.1.255");
			obj.put("source", upbClientWindow.clientIPAddress);
			obj.put("destination", upbClientWindow.upbServerIPAddress);
			// retrieve all devices = 1
			obj.put("room", "");
			obj.put("level",  "");
			// action:
			// 0 command
			// 1 retrieve all devices & device status
			// 2  retrieve one device
			// 3 retrieve all links
			obj.put("action", new Integer(cmd));
			obj.put("recnum", new Integer(1));
			obj.put("reccount", new Integer(1));
			obj.put("jsontimestamp", getDateandTime());
			obj.put("deviceid", Integer.toString(deviceid));
			obj.put("linkid", "");
			obj.put("devicename", "");
			obj.put("linkname", "");
			obj.put("kind", "");
			obj.put("info", "");
			obj.put("status", new Integer(0));
			obj.put("timestamp", "");
			obj.put("room", "");
			obj.put("level", "");
			obj.put("recnum", new Integer(1));
			obj.put("reccount", new Integer(1));
			obj.put("desc", "");
			obj.put("info", "");
			obj.put("cmd",  executableCmd);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			pnMethods.Publish(obj.toString());	
		} catch (PubnubException e) {
			e.printStackTrace();
		}
	}
	public static void updateRow(String deviceID, String[] data) {
		boolean bUpdatedRow = false;
		DefaultTableModel dtm = (DefaultTableModel) devicetable.getModel();
	    if (data.length > 7)
	        throw new IllegalArgumentException("data[] is to long");
	    for (int i = 0; i < dtm.getRowCount(); i++)
	        if (dtm.getValueAt(i, 0).equals(deviceID))
	        {
	        	  dtm.setValueAt(data[4], i, 4);
	        	  dtm.setValueAt(data[5], i, 5);
	        	  dtm.setValueAt(data[6], i, 6);
	        	bUpdatedRow = true;
	        }
	    if(bUpdatedRow == false)
	    {
	    dtm.addRow(new Object[]{data[0], data[1], data[2], data[3], data[4], data[5], data[6]});
	    }
	}


}
