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
package pubNub;


import javax.swing.JTable;

import org.json.JSONException;
import org.json.JSONObject;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import upbClient.upbClientWindow;

public class pubNubMethods {
//	String myChannel = "MountainTopIOT";
//	Pubnub pubnub = new Pubnub("pub-c-01d2221b-3450-4c2a-b5de-aec0af4ce03e", "sub-c-0e48fa3e-e59e-11e5-9dc0-0619f8945a4f");
		String myChannel = upbClientWindow.pubNubChannel;
		Pubnub pubnub = new Pubnub(upbClientWindow.pubNubPublishKey, upbClientWindow.pubNubSubscribeKey);
		
	public void Subscribe() throws PubnubException
	{
		pubnub.subscribe(myChannel, new Callback() {

			@Override
			public void connectCallback(String channel, Object message) {
				System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
						+ " : " + message.getClass() + " : "
						+ message.toString());
			}

			@Override
			public void disconnectCallback(String channel, Object message) {
				System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
						+ " : " + message.getClass() + " : "
						+ message.toString());
			}

			public void reconnectCallback(String channel, Object message) {
				System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
						+ " : " + message.getClass() + " : "
						+ message.toString());
			}
			// This is where we parse the string
			@Override
			public void successCallback(String channel, Object message) {
				System.out.println("SUBSCRIBE : " + channel + " : "
						+ message.getClass() + " : " + message.toString());

				String temp =  message.toString();
				String source = null;

				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(temp);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				String dest = null;
				String jsontimestamp = null, deviceid = null, linkid = null;
				String devicename = null, linkname = null, kind = null,  timestamp = null;
				String room = null, level = null, info = null;
				int action = 0, recnum = 0, reccount = 0;
				int status =0;
				String desc = null;

				try {
					source = (String) jsonObject.get("source");
					dest = (String) jsonObject.get("destination");
					action =  (int) jsonObject.get("action");
					recnum =  (int) jsonObject.get("recnum");
					reccount =  (int) jsonObject.get("reccount");
					room = (String)jsonObject.get("room");
					level = (String)jsonObject.get("level");
					jsontimestamp = (String)jsonObject.get("jsontimestamp");
					deviceid  = (String) jsonObject.get("deviceid");
					linkid = (String) jsonObject.get("linkid");
					devicename = (String) jsonObject.get("devicename");
					linkname = (String) jsonObject.get("linkname");
					kind = (String) jsonObject.get("kind");
					info = (String) jsonObject.get("info");
					desc = (String) jsonObject.get("desc");
					status = (int) jsonObject.get("status");
					timestamp = (String) jsonObject.get("timestamp");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				

				if (!source.equalsIgnoreCase(upbClientWindow.clientIPAddress))
				{
					
					switch(action)
					{
					case 1:
					
					upbClientWindow.loadDeviceTablefromPubNum( upbClientWindow.devicetable,temp);
					break;
					case 3:
					
					upbClientWindow.loadLinkTablefromPubNub(upbClientWindow.linktable, temp);	
					break;
					case 86:
						
						System.out.println(jsonObject.toString() );
						String[] data = new String[7];
						data[0] = deviceid;
						data[1] = devicename;  // Name
						data[2] = room ;  // Room
						data[3] = kind;  // Type
						data[4] = Integer.toString(status);
						data[5] = level;
						data[6] = info;
								upbClientWindow.updateRow(deviceid, data);
						break;
						
					
					default:
						break;
					
					}
					
				}	          
			}

			@Override
			public void errorCallback(String channel, PubnubError error) {
				System.out.println("SUBSCRIBE : ERROR on channel " + channel
						+ " : " + error.toString());
			}
		}
				);

	}

	public void Publish(String input) throws PubnubException
	{

		Callback callback = new Callback() {
			public void successCallback(String channel, Object response) {
				 System.out.println(response.toString());
			}
			public void errorCallback(String channel, PubnubError error) {
				System.out.println(error.toString());
			}
		};
		pubnub.publish(myChannel, input , callback);

	}

	


}


