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
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import upbClient.FileServerClient;

import javax.swing.JSlider;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.UIManager;

public class upbClientWindow {

	private static JFrame frame;
	private static JTable table;
	private static JTable linkTable;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private JButton btnReloadDatabaseButton;

	public String configProperties = "C:/utils/Java/workspaceMars2/upbClient/src/config.properties";
	public static String serverdbName; 
	public static String clientdbName; 
	public static String upbServerIPAddress; 
	public static String upbServerCommandPort; 
	public static int upbServerDownloadPort;
	public static String temp = null;
	private JTable table_1;
	public static JLabel StatusLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			properties.load(fileInput);
			fileInput.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		serverdbName = properties.getProperty("serverdbname");
		clientdbName = properties.getProperty("clientdbname");
		upbServerIPAddress = properties.getProperty("upbserveripaddress");
		upbServerCommandPort = properties.getProperty("upbservercommandport");
		temp = properties.getProperty("upbserverdownloadport");
		upbServerDownloadPort  =  Integer.parseInt(temp); 

	}

	public static void loadLinkTable( String dbName, JTable table)
	{
		Connection c = null;
		Statement stmt = null;
		DefaultTableModel dm = (DefaultTableModel) table.getModel();
		deleteAllRows(dm);
		for( int nRow = 0 ; nRow < table.getRowCount() ; nRow++ ){
			for( int nColumn = 0 ; nColumn < table.getColumnCount(); nColumn++ ){
				table.setValueAt("" , nRow , nColumn );
			}
		}
		table.repaint(); // This will reflect the changes (empty table) on the screen

		try {
			Class.forName("org.sqlite.JDBC");
			String conn = "jdbc:sqlite:" + dbName;
			c = DriverManager.getConnection(conn);
			c.setAutoCommit(false);
			stmt = c.createStatement();
			String sel = "SELECT * FROM links;";
			ResultSet rs = stmt.executeQuery(sel);
			while (rs.next()) 
			{
				int mod = 		rs.getInt("linkidnum");
				String id = Integer.toString(mod);
				String name = rs.getString("linkname");	
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				model.addRow(new Object[]{id, name});
			}
			rs.close();
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	public static void loadTable( String dbName, JTable table)
	{
		int index = 0;
		int st = 0;
		int lev = 0;
		Connection c = null;
		Statement stmt = null;
		Statement stmt1 = null;
		Statement stmt2 = null;
		DefaultTableModel dm = (DefaultTableModel) table.getModel();
		deleteAllRows(dm);
		for( int nRow = 0 ; nRow < table.getRowCount() ; nRow++ ){
			for( int nColumn = 0 ; nColumn < table.getColumnCount(); nColumn++ ){
				table.setValueAt("" , nRow , nColumn );
			}
		}
		table.repaint(); // This will reflect the changes (empty table) on the screen

		try {
			Class.forName("org.sqlite.JDBC");
			String conn = "jdbc:sqlite:" + dbName;
			c = DriverManager.getConnection(conn);
			c.setAutoCommit(false);
			stmt = c.createStatement();
			stmt1 = c.createStatement();
			stmt2 = c.createStatement();
			String sel = "SELECT * FROM devices;";
			String sel1 = null;
			String status = null;
			String level = null;
			String info = null;
			String timeStamp = null;
			String temp = null;
			int manf = 0, prod = 0;
			ResultSet rs = stmt.executeQuery(sel);

			String desc = null;
			while (rs.next()) 
			{
				int mod = 		rs.getInt("moduleid");
				String id = Integer.toString(mod);
				String name = rs.getString("devname");	
				String room = rs.getString("roomname");	
				manf = rs.getInt("manufacturer");
				prod = rs.getInt("prodid");
				sel1 = "SELECT * FROM products WHERE manufacturer = " + manf + " AND prodid = " + prod;	
				ResultSet	rs1 = stmt1.executeQuery(sel1);
				while (rs1.next()) 
				{
					desc = rs1.getString("proddesc");	
				}
				rs1.close();
				String 	sel2 = "SELECT * FROM devicestatus WHERE moduleid = " + mod; 	
				ResultSet	rs2 = stmt2.executeQuery(sel2);
				while (rs2.next()) 
				{
					timeStamp = rs2.getString("upddatetime");
					info = rs2.getString("info");
					lev = rs2.getInt("level");
					if(desc.contains("Non-Dimming"))
					{
						level = "N/A";
					}
					else
					{
						level = 	Integer.toString(lev);
					}
					st = rs2.getInt("status");
					if(st == 0)
					{
						status = "Off";
					}
					else
					{
						status = "On";
					}
				}
				rs2.close();
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				if ("".equals(info))
				{ 
					if(st == 0)
					{
						temp = timeStamp +" Device turned off";
					}		 
					else
					{
						temp = timeStamp +" Device turned on";	
					}
					//	temp = "No Activity";	
				}
				else
				{
					temp = timeStamp +" " + info; 

				}
				model.addRow(new Object[]{id, name, room, desc, status, level, temp });
			}

			rs.close();
			stmt.close();
			stmt1.close();
			stmt2.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	/**
	 * Create the application.
	 */
	public upbClientWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		readConfigFile();

		File f = new File(clientdbName);
		if(f.exists() == false) { 

			FileServerClient client = new FileServerClient();

			try {
				client.receiveFileFromServer();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		frame = new JFrame();
		frame.setBounds(100, 100, 1193, 767);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		int numberOfColumns = 7;
		int numberOfRows = 0;
		int linkRows = 0;
		int linkColumns = 2;

		table = new JTable(numberOfRows,numberOfColumns)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;//you can set which column/row can be edited.
			}
		};
		table.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				if (row >= 0 && col >= 0) {

					int retRow =  	table.convertRowIndexToModel( row );
					Object obj0 = GetData(table, retRow, 0);
					Object obj1 = GetData(table, retRow, 1);
					Object obj2 = GetData(table, retRow, 2);
					Object obj3 = GetData(table, retRow, 3);
					Object obj4 = GetData(table, retRow, 4);
					Object obj5 = GetData(table, retRow, 5);

					upbClient.deviceDialog dD = new upbClient.deviceDialog();
					dD.deviceDialog(obj0.toString(),obj1.toString(),obj2.toString(),obj3.toString(),obj4.toString(),obj5.toString(), table );
				}
			}
		});

		table.setBounds(1, 26, 698, 182);
		String header[] = {"id", "name", "room", "type", "status", "level", "info"};

		for(int i=0;i<table.getColumnCount();i++)
		{
			TableColumn column = table.getTableHeader().getColumnModel().getColumn(i);
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

		frame.getContentPane().add(table, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(32, 21, 1126, 278);
		frame.getContentPane().add(scrollPane);

		JLabel lblNewLabel = new JLabel("upbClient Devices");
		lblNewLabel.setBounds(0, 0, 1177, 21);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblNewLabel);

		btnReloadDatabaseButton = new JButton("Refresh Device & Link Tables");
		btnReloadDatabaseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileServerClient client = new FileServerClient();

				try {
					client.receiveFileFromServer();
					loadTable( clientdbName, table);
					table.setAutoCreateRowSorter(true);
					
					loadLinkTable( clientdbName, table_1);
					table_1.setAutoCreateRowSorter(true);
					
					SetStatusText("Device and Link tables successfully updated");
					
				} catch (Exception e) {
					
					SetStatusText("Device and Link update FAILED!!!!");
					e.printStackTrace();
				}
			}
		});
		btnReloadDatabaseButton.setBounds(455, 560, 220, 23);
		frame.getContentPane().add(btnReloadDatabaseButton);
		loadTable( clientdbName, table);
		table.setAutoCreateRowSorter(true);
		String header1[] = {"id", "name"};
		table_1 = new JTable(linkRows,linkColumns)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;//you can set which column/row can be edited.
			}
		};

		for(int i=0;i<table_1.getColumnCount();i++)
		{
			TableColumn linkcolumn = table_1.getTableHeader().getColumnModel().getColumn(i);
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
		scrollPane_1.setViewportView(table_1);
		table_1.setAutoCreateRowSorter(true);
		table_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = table_1.rowAtPoint(e.getPoint());
				int col = table_1.columnAtPoint(e.getPoint());
				if (row >= 0 && col >= 0) {

					int retRow =  	table_1.convertRowIndexToModel( row );
					Object obj0 = GetData(table_1, retRow, 0);
					Object obj1 = GetData(table_1, retRow, 1);
					upbClient.linkDialog dD = new upbClient.linkDialog();
					dD.linkDialog(obj0.toString(),obj1.toString(), table_1 );
				}
			}
		});

		table_1.setBackground(new Color(255, 255, 255));
		loadLinkTable( clientdbName, table_1);
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
	

		DefaultRowSorter sorter = ((DefaultRowSorter)table.getRowSorter());
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
	
	SetStatusText("Program started");
	
	}

	public static void deleteAllRows(final DefaultTableModel model) {
		for( int i = model.getRowCount() - 1; i >= 0; i-- ) {
			model.removeRow(i);
		}
	}

	public static void SetStatusText(String statusText)
	{
		 DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		    Calendar calobj = Calendar.getInstance();
		    	  
		       String temp = calobj.getTime().toString() + "  " + statusText;
	StatusLabel.setText(temp);
	}
	public static final String stackTrace(Throwable e)
	{
		String foo = null;
		try
		{
			// And show the Error Screen.
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
}
