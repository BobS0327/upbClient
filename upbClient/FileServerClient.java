package upbClient;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public  class FileServerClient {

	 public void receiveFileFromServer() throws Exception{	
	URL url = null;
			URLConnection con = null;
			int i;
			try {
				String urlTemp = "http://" + upbClientWindow.upbServerIPAddress +":"+ upbClientWindow.upbServerDownloadPort + "/" + upbClientWindow.serverdbName;
				 // "Usage: http://127.0.0.1:8080 or http://127.0.0.1:8080/<fileName></b>", false);
		//		url = new URL("http://192.168.1.104:8081//home/pi/upbServer/upbserver.db");
				url = new URL(urlTemp);
				con = url.openConnection();
			//	File file = new File("C:/utils/Java/workspaceMars2/upbClient/upbserver.db");
				
				File file = new File(upbClientWindow.clientdbName);	
				BufferedInputStream bis = new BufferedInputStream(
						con.getInputStream());
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file.getName()));
				while ((i = bis.read()) != -1) {
					bos.write(i);
				}
				bos.flush();
				bis.close();
			} catch (MalformedInputException malformedInputException) {
				malformedInputException.printStackTrace();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	
  
	
	
