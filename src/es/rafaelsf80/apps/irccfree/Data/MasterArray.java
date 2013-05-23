package es.rafaelsf80.apps.irccfree.Data;

import java.util.ArrayList;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

public class MasterArray {
	
	private final String TAG = getClass().getSimpleName();
	private static ArrayList<ArrayList<Server>> mArray = new ArrayList<ArrayList<Server>>();
	
	public static ArrayList<ArrayList<Server>> getData() {
		return mArray;
	}
	
	/**
     * Add new server to the Master array. If group already exists, then add it to same group  
     */
	public static void addNewServer(Server server) {

		Server s;
		Server newServer = null;
		boolean server_found = false;
		boolean group_found = false;

		ArrayList<Server> mSecondaryArray3 = null;

		for (int i = 0; i < mArray.size(); i++) 
			for (int j = 0; j < mArray.get(i).size(); j++) {			
				s = mArray.get(i).get(j);
				if ((s.getGroup().compareTo(server.getGroup()) == 0) &&
						(s.getIp().compareTo(server.getIp()) == 0) &&
						(s.getPort() == server.getPort())) {
					newServer = server;
					server_found = true;
					break;
				}
			}
		/* If server exists, the group will also exists, but not the opposite */
		for (int i = 0; i < mArray.size(); i++) 
			for (int j = 0; j < mArray.get(i).size(); j++) {
				s = mArray.get(i).get(j);
				if (s.getGroup().compareTo(server.getGroup()) == 0) {
					mSecondaryArray3 = mArray.get(i);
					group_found = true;
					break;
				}
			}
		/* Create new group */	
		if (!group_found) { 
			mSecondaryArray3 = new ArrayList<Server>();
			mSecondaryArray3.clear();
		}
		/* Create new server, no matter if group exists or not */
		if (!server_found)
			newServer = new Server(server);

		newServer.setConnectOnLaunch(server.isConnectOnLaunch());
		newServer.setName(server.getName());
		newServer.setGroup(server.getGroup());
		newServer.setIp(server.getIp());
		newServer.setPort(server.getPort());
		newServer.setNickname(server.getNickname());
		SparseArray<Channel> ch = new SparseArray<Channel>();
		newServer.setChannelArray(ch);
		newServer.setStatus(Server.DISCONNECTED);

		if (server_found)
			sync(server);
		else {
			if (group_found) 
				mSecondaryArray3.add(newServer);
			else {
				mSecondaryArray3.add(newServer);
				mArray.add(mSecondaryArray3);
			}
		}

		//				/* Update ListView */
		//				if (mIService != null) 	
		//					mIService.updateColors();

	}
	
	
	/**
     * Count total number of channels for connected servers  
     */
	public static int channelSize() {

		Server server;
		int num = 0;
		for (int i = 0; i < mArray.size(); i++) 
			for (int j = 0; j < mArray.get(i).size(); j++) {
				server = mArray.get(i).get(j);
				if ((server.getStatus() != Server.DISCONNECTED) &&
						(server.getStatus() != Server.CONNECTING)) {
					//Log.d("ConnectionService", "channelSize: " + server);
					num += server.getChannelArray().size();
				}
			}
		return num;
	}
	
	
	/**
     * Get JOINED channel list array from all servers who are not in DISCONNECTED/CONNECTING state
     * Those channels are the none shown in fragment chats
	 * Active array list stored on mActiveChannelListArray 
     */
	public static int joinedChannelSize() {

		Server server;
		int num = 0;
		for (int i = 0; i < mArray.size(); i++) 
			for (int j = 0; j < mArray.get(i).size(); j++) {
				server = mArray.get(i).get(j);
				if ((server.getStatus() != Server.DISCONNECTED) &&
						(server.getStatus() != Server.CONNECTING)) {
					SparseArray<Channel> ch = server.getChannelArray();
					for (int j2 = 0; j2 < ch.size(); j2++) {
						int key = server.getChannelArray().keyAt(j2);
						if (server.getChannelArray().get(key).isJoined() == true)
							num++;
					}
				}
			}
		return num;
	}
	
	
	
	public static Server findServerByKey(int key) {

		Server server = null;
		for (int i = 0; i < mArray.size(); i++) 
			for (int j = 0; j < mArray.get(i).size(); j++) {
				server = mArray.get(i).get(j);
				if ((server.getStatus() != Server.DISCONNECTED) &&
					(server.getStatus() != Server.CONNECTING)) {
					if ((server.getChannelArray().get(key)) != null) {
						return server;
					}
				}
			}
		return null;
	}

	
	
	
	/**
     * Find channelname by key in the Master array  
     */
	public static String findChannelNameByKey(int key) {

		Server server = null;
		String channelName = null;
		for (int i = 0; i < mArray.size(); i++) 
			for (int j = 0; j < mArray.get(i).size(); j++) {
				server = mArray.get(i).get(j);
				if ((server.getStatus() != Server.DISCONNECTED) &&
						(server.getStatus() != Server.CONNECTING)) {
					Log.d("ConnectionService", "findChannelNameByKey key=" + String.valueOf(key) + " " + server.getChannelArray().get(key).getName());
					if ((server.getChannelArray().get(key)) != null) {
						channelName = server.getChannelArray().get(key).getName();
					}
				}
			}
		return channelName;
	}


	/**
     * Find key by channelname in the Master array  
     */
	public static int findKeyByChannelName(Server server, String channelName) {
		int key = -1;
		/* Iterate through the sparsearray */
		for (int i=0;i< server.getChannelArray().size(); i++) {
			key = server.getChannelArray().keyAt(i);
			String rafa = server.getChannelArray().get(key).getName();
			if (rafa.compareTo(channelName)==0)
				return key;
		}
		return -1;
	}
	
	public static String findJoinedChannelNameByPosition(int position) {

		Server server = null;
		int joined = 0;
		int all = 0;
		for (int i = 0; i < mArray.size(); i++) 
			for (int j = 0; j < mArray.get(i).size(); j++) {
				server = mArray.get(i).get(j);
				if ((server.getStatus() != Server.DISCONNECTED) &&
						(server.getStatus() != Server.CONNECTING)) {
					joined = 0;
					all = 0;
					while(joined<=position) {
						Log.d("ConnectionService", "findJoinedChannelNameByPosition position=" + String.valueOf(position) + " " + server.getChannelArray().get(all).getName());
						
						if (server.getChannelArray().get(all).isJoined())
							joined++;
						all++;
					}
					return server.getChannelArray().get(all-1).getName();
				}
			}
		return null;
	}
	
	public static int positionToKey(int position) {

		Server server = null;
		int joined = 0;
		int all = 0;
		for (int i = 0; i < mArray.size(); i++) 
			for (int j = 0; j < mArray.get(i).size(); j++) {
				server = mArray.get(i).get(j);
				if ((server.getStatus() != Server.DISCONNECTED) &&
						(server.getStatus() != Server.CONNECTING)) {
					joined = 0;
					all = 0;
					while(joined<=position) {
						Log.d("ConnectionService", "findJoinedChannelNameByPosition position=" + String.valueOf(position) + " " + server.getChannelArray().get(all).getName());
						
						if (server.getChannelArray().get(all).isJoined())
							joined++;
						all++;
					}
					return all-1;
				}
			}
		return -1;
	}
	
	public static int KeyToPosition(int key) {

		Server server = null;
		int joined = 0;		
		for (int i = 0; i < mArray.size(); i++) 
			for (int j = 0; j < mArray.get(i).size(); j++) {
				server = mArray.get(i).get(j);
				if ((server.getStatus() != Server.DISCONNECTED) &&
						(server.getStatus() != Server.CONNECTING)) {
					for (int index = 0; index < server.getChannelArray().size(); index++) {
						 if (server.getChannelArray().keyAt(index) == key)
							 return joined;
						 else
							 joined += 1;
					}
				}
			}
		return -1;
	}
	
	
	
	public static int listToKey(int listPosition) {

		Server server = null;
		int all = 0;
		for (int i = 0; i < mArray.size(); i++) 
			for (int j = 0; j < mArray.get(i).size(); j++) {
				server = mArray.get(i).get(j);
				if ((server.getStatus() == Server.JOINED)) {
					all += server.getChannelArray().size();
					if (all > listPosition) {
						all -= server.getChannelArray().size();
						return server.getChannelArray().keyAt(listPosition - all);
					}
				}
			}
		return -1;
	}
	
	public static int getNewKey() {

		Server server = null;
		int maxKey = 0;
		int index = 0;
		for (int i = 0; i < mArray.size(); i++) 
			for (int j = 0; j < mArray.get(i).size(); j++) {
				server = mArray.get(i).get(j);
				//if ((server.getStatus() != Server.DISCONNECTED) &&
				//		(server.getStatus() != Server.CONNECTING)) {
				Log.d("ConnectionService", "sIZE" + String.valueOf(server.getChannelArray().size()) + " MarKey: " + String.valueOf(maxKey));
					for (index = 0; index < server.getChannelArray().size(); index++)
						 if (server.getChannelArray().keyAt(index) >= maxKey)
							 maxKey = server.getChannelArray().keyAt(index) + 1;
				//}
			}
		return maxKey;
	}
	
	/**
     * Syncs array from the info on {@link es.rafaelsf80.apps.irccfree.server} 
     * Note it does not modify IP, since it is used as unique ID for identification.
     * To modify ip address, use {@link es.rafaelsf80.apps.irccfree.MasterArray.setIp} instead
     */
	public static void sync(Server server) {
		
		for (int i = 0; i < mArray.size(); i++) {
			for (int j = 0; j < mArray.get(i).size(); j++) {
				if ((server.getIp() == (mArray.get(i).get(j)).getIp())) {
					(mArray.get(i).get(j)).setConnectOnLaunch(server.isConnectOnLaunch());
					(mArray.get(i).get(j)).setName (server.getName());
					(mArray.get(i).get(j)).setGroup (server.getGroup());
					(mArray.get(i).get(j)).setIp (server.getIp());
					(mArray.get(i).get(j)).setPort (server.getPort());
					(mArray.get(i).get(j)).setNickname(server.getNickname());
					(mArray.get(i).get(j)).setEncoding(server.getEncoding());
					(mArray.get(i).get(j)).setPassword(server.getPassword());
					(mArray.get(i).get(j)).setChannelArray(server.getChannelArray());					
					(mArray.get(i).get(j)).setStatus (server.getStatus());
					(mArray.get(i).get(j)).setHandler (server.getHandler());
					(mArray.get(i).get(j)).setThread (server.getThread());
					(mArray.get(i).get(j)).setOutStream(server.getOutStream());
				}
			}
		}
	}
	
	public static int getGroupIndex(Server server) {
		for (int i = 0; i < mArray.size(); i++) {
			for (int j = 0; j < mArray.get(i).size(); j++) {
				if ((server.getIp() == (mArray.get(i).get(j)).getIp()) &&
					(server.getPort() == (mArray.get(i).get(j)).getPort())) {
					return i;
					
				}
			}
		}
		return -1;
	}
	
	
	public static int getServerIndex(Server server) {
		for (int i = 0; i < mArray.size(); i++) {
			for (int j = 0; j < mArray.get(i).size(); j++) {
				if ((server.getIp() == (mArray.get(i).get(j)).getIp()) &&
					(server.getPort() == (mArray.get(i).get(j)).getPort())) {
					return j;
					
				}
			}
		}
		return -1;
	}

	/**
     * 
     */
	public static void setIp(int i, int j, String ip) {
		(mArray.get(i).get(j)).setIp ( ip );
	}

	/* Set default data in the Master array */
	public static void setData() {
    	
   	 
	 int newKey = 0;
	  mArray.clear();
   	 
   	 // Group 1
   	 //ArrayList<Server> mSecondaryArray1 = new ArrayList<Server>();
   	 //mSecondaryArray1.clear();
   	 
   	 
   	 Server server1 = new Server();
   	 server1.setConnectOnLaunch(false);
   	 server1.setName("Random EU server");
   	 server1.setGroup("Freenode");
   	 server1.setIp("chat.eu.freenode.net");
   	 server1.setPort(6667);
   	 server1.setNickname("yonaru");
   	 SparseArray<Channel> ch1 = new SparseArray<Channel>();
   	 server1.setChannelArray(ch1);
   	 server1.setStatus(Server.DISCONNECTED);
   	 server1.setStatusMessage("");
   	 
   	 addNewServer(server1);
   	 
   	 
   	 //mSecondaryArray1.add(server1);  
	 //mArray.add(mSecondaryArray1);
   	

   	 
   	 Server server2 = new Server();
   	server2.setConnectOnLaunch(false);
   	
 	 server2.setName("Random server");
   	 server2.setGroup("Freenode");
   	 server2.setIp("chat.freenode.net"); 
   	 
   	 server2.setPort(6667);
   	 server2.setNickname("yonaru");
   	 SparseArray<Channel> ch2 = new SparseArray<Channel>();
  	 server2.setChannelArray(ch2);
   	 server2.setStatus(Server.DISCONNECTED);
   	server2.setStatusMessage("");
   	 
	 addNewServer(server2);
//   	 mSecondaryArray1.add(server2);
//	 mArray.clear();
//   	 mArray.add(mSecondaryArray1);

   	 Server server3 = new Server();
   	server3.setConnectOnLaunch(false);
   	
    server3.setName("Random AU server");
  	 server3.setGroup("Freenode");
  	 server3.setIp("chat.au.freenode.net"); 
  	 

   	 server3.setPort(6667);
   	 server3.setNickname("yonaru");
   	 
   	SparseArray<Channel> ch3 = new SparseArray<Channel>();
  	 server3.setChannelArray(ch3);
   	
  	  server3.setStatus(Server.DISCONNECTED);
  	server3.setStatusMessage("");
//   	 mSecondaryArray1.add(server3);
//   	 mArray.clear();
//  	 mArray.add(mSecondaryArray1);

	 addNewServer(server3);
   	 

   	 //Group 2
//   	 ArrayList<Server> mSecondaryArray2 = new ArrayList<Server>();
//   	 mSecondaryArray2.clear();

   	 Server server4 = new Server();
   	server4.setConnectOnLaunch(false);
   	 server4.setName("Random EU server");
   	 server4.setGroup("Undernet");
   	 server4.setIp("eu.undernet.org");
   	 server4.setPort(6667);
   	 server4.setNickname("yonaru");
   	SparseArray<Channel> ch4 = new SparseArray<Channel>();
  	 server4.setChannelArray(ch4);
   	 server4.setStatus(Server.DISCONNECTED);
   	server4.setStatusMessage("");
   	 //mSecondaryArray2.add(server4);
   	 
	 addNewServer(server4);
//   	 mArray.clear();
//  	 mArray.add(mSecondaryArray1);
//  	 mArray.add(mSecondaryArray2);

   	 Server server5 = new Server();
   	server5.setConnectOnLaunch(false);
   	 server5.setName("US, FL, Tampa");
   	 server5.setGroup("Undernet");
   	 server5.setIp("tampa.fl.us.undernet.org");
   	 server5.setPort(6667);
   	 server5.setNickname("yonaru");
   	SparseArray<Channel> ch5 = new SparseArray<Channel>();
  	 server5.setChannelArray(ch5);
   	 server5.setStatus(Server.DISCONNECTED);
   	server5.setStatusMessage("");
   	//mSecondaryArray2.add(server5);
//   	mArray.clear();
// 	 mArray.add(mSecondaryArray1);
// 	 mArray.add(mSecondaryArray2);
 	 
	 addNewServer(server5);

   	 Server server6 = new Server();
   	 server6.setConnectOnLaunch(false);
   	 server6.setName("EU, HU, Budapest");
   	 server6.setGroup("Undernet");
   	 server6.setIp("Budapest.HU.EU.UnderNet.org");
   	 server6.setPort(6667);
   	 server6.setNickname("yonaru");
   	SparseArray<Channel> ch6 = new SparseArray<Channel>();
  	 server6.setChannelArray(ch6);
   	 server6.setStatus(Server.DISCONNECTED);
   	server6.setStatusMessage("");
//   	 mSecondaryArray2.add(server6);
//   	mArray.clear();
// 	 mArray.add(mSecondaryArray1);
// 	 mArray.add(mSecondaryArray2);
   	 addNewServer(server6);	
   	 

   	 //Group 3
//   	 ArrayList<Server> mSecondaryArray3 = new ArrayList<Server>();
//   	 mSecondaryArray3.clear();

   	 Server server7 = new Server();
   	server7.setConnectOnLaunch(false);
   	 server7.setName("ngIRCd 0.17");
   	 server7.setGroup("ngIRCd");
   	 server7.setIp("192.168.1.10");
   	 server7.setPort(7101);
   	 server7.setNickname("Yolanda");
   	SparseArray<Channel> ch7 = new SparseArray<Channel>();
  	 server7.setChannelArray(ch7);
  	 server7.setStatus(Server.DISCONNECTED);
    	server7.setStatusMessage("");
   	  	 
 	 addNewServer(server7);

   	 
 	 Server server8 = new Server();
    	server8.setConnectOnLaunch(false);
    	 server8.setName("Random");
    	 server8.setGroup("Zerofusion");
    	 server8.setIp("irc.zerofuzion.net");
    	 server8.setPort(6667);
    	 server8.setNickname("Yolanda");
    	SparseArray<Channel> ch8 = new SparseArray<Channel>();
   	 server8.setChannelArray(ch8);
   	 server8.setStatus(Server.DISCONNECTED);
     	server8.setStatusMessage("");
    	  	 
  	 addNewServer(server8);
   	
   	 
  	 Server server9 = new Server();
 	server9.setConnectOnLaunch(false);
 	 server9.setName("Random EU server");
 	 server9.setGroup("Gamesurge");
 	 server9.setIp("irc.eu.gamesurge.net");
 	 server9.setPort(6667);
 	 server9.setNickname("Yolanda");
 	SparseArray<Channel> ch9 = new SparseArray<Channel>();
	 server9.setChannelArray(ch9);
	 server9.setStatus(Server.DISCONNECTED);
  	server9.setStatusMessage("");
 	  	 
	 addNewServer(server9);
   	 
   	 //mSecondaryArray3.add(server7);
   	 
//   	mArray.clear();
// 	 mArray.add(mSecondaryArray1);
// 	 mArray.add(mSecondaryArray2);
//   	 mArray.add(mSecondaryArray3);

   	 // mArray.addAll(mArray);

   }	

}
