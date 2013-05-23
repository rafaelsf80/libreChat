package es.rafaelsf80.apps.irccfree.Service;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import es.rafaelsf80.apps.irccfree.IRCHelper;
import es.rafaelsf80.apps.irccfree.IRCParser;
import es.rafaelsf80.apps.irccfree.R;
import es.rafaelsf80.apps.irccfree.Data.Channel;
import es.rafaelsf80.apps.irccfree.Data.DCC;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;
import es.rafaelsf80.apps.irccfree.Data.MyMessage;
import es.rafaelsf80.apps.irccfree.Data.Server;


public class ConnectionThread extends Thread {
	
	private final int MAX_BUFFER = 8192;
	
	private final String TAG = getClass().getSimpleName();
	
	boolean loop = true;
	
	Message msg;
	public Server server;
	private Context mContext;
	private Handler mHandler;
	
	String ircMessageBuffer = "";
	String [] tokens;
	
	DataOutputStream outStream = null;
	BufferedReader lectura = null;
	Socket connection = null;
	public String env;
	
	String rec = null;
	String log = null;
	
	public ConnectionThread(Server server, Handler handler, Context ctx) {
		this.server = server;
		this.mContext = ctx;
		this.mHandler = handler;
    }
	
	
	public void stopProcessingCommands  ( )
    {
        loop = false;
        Log.d(TAG, "Thread "+server.getIp() + ":" + Integer.valueOf(server.getPort()) + " stopped");
    }

	
	public void run() {  

		while (loop){

			// Login
			if ((server.getStatus() == Server.CONNECTING) && 
					(server.getIp() != null)) {

				Log.i(TAG, "Connecting to " + server.getIp() + ":" + server.getPort());

				try {
					connection = new Socket(server.getIp(), server.getPort());
				} catch (UnknownHostException e) {
					server.setStatus(Server.DISCONNECTED);
					Log.e(TAG, "Unknown server:" + e);
					loop = false;
					
				} catch (IOException e) {
					server.setStatus(Server.DISCONNECTED);

					Log.e(TAG, "Can not open connection to server: " + server.getIp() + " " + e);
					loop = false;
				}
				if (connection != null) {
					try {
						outStream = new DataOutputStream(connection.getOutputStream());
						lectura = new BufferedReader( new InputStreamReader(connection.getInputStream(), "US-ASCII"));
						server.setStatus(Server.CONNECTED);
						server.setOutStream(outStream);
						Log.i(TAG, "Connected to server: " + server.getIp() + ":" + server.getPort());
						
					} catch (IOException e) {
						log = "inStream outStream";
						Log.e(TAG, log+ " ");
						server.setStatus(Server.DISCONNECTED);
						loop = false;
					}
					
				}
				
				/* Send message to UI */
				Message msg = new Message();
				MasterArray.sync(server);
				msg.obj = server;
				mHandler.sendMessage(msg);
			}


			char [] cbuffer = new char[MAX_BUFFER];
			int read = 0;
		
			/* Received blocking section */
			if ((server.getStatus() != Server.DISCONNECTED) &&
			    (server.getStatus() != Server.CONNECTING)) 
				try {

					if (lectura.ready()) {
						read = lectura.read(cbuffer, 0, MAX_BUFFER);
						if (read > 0)  {
							// convertimos a String para calcular el número de bytes
							// 	solo soporta la codificación por defecto, por lo que es muy limitado
							rec = new String(cbuffer, 0, read);
							Log.d(TAG, "[RECV "+server.getIp()+":"+String.valueOf(server.getPort())+"] " + read + " bytes");
							Log.d(TAG, "[RECV "+server.getIp()+":"+String.valueOf(server.getPort())+ "] " + rec);

							/* Process received message, then add to buffer and callback */
							if (rec.contains("PING :") != true) {
								
								processReceivedMessage(rec);
								
								/* Callback only if IRC message is not partially received */								
//									if ((server.getStatus() == Server.AUTHENTICATED) ||
//											(server.getStatus() == Server.JOINED) ||
//											(server.getStatus() == Server.CONNECTED)) {
										Log.d("ConnectionService", "callback: "+String.valueOf(server.getStatus()) + server);
																			
										Message msg = new Message();
										msg.obj = server;
										mHandler.sendMessage(msg);
									//}
								
							} else {
								/* Special processing for PING-PONG */
								/* PING received, must answer with PONG before xxxx seconds */
								env = rec.replaceFirst("PING", "PONG");
								try {
									outStream.writeBytes(env);
									Log.d(TAG, "[ENV "+server.getIp()+":"+String.valueOf(server.getPort())+ "] " + env);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									Log.e(TAG, "[Exception send()] " + e);
								}
							}
						}
					}
					if (server.readyToSend) {
									
						outStream.writeBytes(server.getReadyToSendString());
						Log.d("ConnectionThread", "[ENV "+server.getIp()+":"+String.valueOf(server.getPort())+"] " + server.getReadyToSendString());
						server.setReadyToSendString("");
						server.readyToSend = false;
					}
					
				} catch (IOException e) {
					Log.e(TAG, "[MAIN_LOOP] " + e);
					server.setStatus(Server.DISCONNECTED);
					loop = false;
				}
		}
	}
	
	
	
	public void processReceivedMessage(String ircMessage)  {
		
		String textToShow = "";
		String name = null, topic = null, users = null;
		
		int index;
		boolean isVisible = false;
		
		IRCParser ircParser = new IRCParser(ircMessage);
		
				
		/* Sync needed in case request is coming from outside this thread */
		server.sync();
		
		/* Chat 
		/* :rafa!~rafa@Yolanda-VAIO PRIVMSG #ro2 :Hola que tal */

		if (server.getStatus() == Server.JOINED)
			if (ircMessage.contains(" PRIVMSG " ) == true) {

				index = ircMessage.indexOf("PRIVMSG ");	 
				String channel [] = (ircMessage.substring(index+8)).split(" ");
				name = channel[0].replaceAll("(\r|\n)", "");
				Log.i(TAG, "Chat received from channel " + name);

				index = ircMessage.indexOf("!");
				textToShow = "<font color='green'>&lt;" + ircMessage.substring(1, index) + "&gt;</font><br> ";
				index = ircMessage.lastIndexOf(name);
				textToShow += ircMessage.substring(index + name.length() + 2);
				textToShow = textToShow.replaceAll("(\r|\n)", "");
				Log.i(TAG, "chat --> " + ircMessage.substring(index + name.length() + 2));
				server.setNotification( MasterArray.findKeyByChannelName(server, name) );
				isVisible = true;
			}
		
		/* Auth chat.eu.freenode.net:6667
			 :morgan.freenode.net 001 yonaru :Welcome to the freenode Internet Relay Chat Network yonaru
		     :morgan.freenode.net 002 yonaru :Your host is morgan.freenode.net[64.32.24.176/6667], running version ircd-seven-1.1.3
		     :morgan.freenode.net 003 yonaru :This server was created Tue Mar 12 2013 at 18:27:25 EDT
			 :morgan.freenode.net 004 yonaru morgan.freenode.net ircd-seven-1.1.3 DOQRSZaghilopswz CFILMPQbcefgijklmnopqrstvz bkloveqjfI
			 :morgan.freenode.net 005 yonaru CHANTYPES=# EXCEPTS INVEX CHANMODES=eIbq,k,flj,CFLMPQcgimnprstz CHANLIMIT=#:120 PREFIX=(ov)@+ MAXLIST=bqeI:100 MODES=4 NETWORK=freenode KNOCK STATUSMSG=@+ CALLERID=g :are supported by this server
			 :morgan.freenode.net 005 yonaru CASEMAPPING=rfc1459 CHARSET=ascii NICKLEN=16 CHANNELLEN=50 TOPICLEN=390 ETRACE CPRIVMSG CNOTICE DEAF=D MONITOR=100 FNC TARGMAX=NAMES:1,LIST:1,KICK:1,WHOIS:1,PRIVMSG:4,NOTICE:4,ACCEPT:,MONITOR: :are supported by this server
			 :morgan.freenode.net 005 yonaru EXTBAN=$,arx WHOX CLIENTVER=3.0 SAFELIST ELIST=CTU :are supported by this server
			 :morgan.freenode.net 251 yonaru :There are 205 users and 85571 invisible on 31 servers
			 :morgan.freenode.net 252 yonaru 34 :IRC Operators online
			 :morgan.freenode.net 253 yonaru 7 :unknown connection(s)
	         :morgan.freenode.net 254 yonaru 47371 :channels formed
			 :morgan.freenode.net 255 yonaru :I have 3584 clients and 1 servers
			 :morgan.freenode.net 265 yonaru 3584 8343 :Current local users 3584, max 8343
			 :morgan.freenode.net 266 yonaru 85776 88680 :Current global users 85776, max 88680
			 :morgan.freenode.net 250 yonaru :Highest connection count: 8344 (8343 clients) (451219 connections received)
			 :morgan.freenode.net 372 yonaru :- Welcome to morgan.freenode.net in Chicago, IL, USA! Thank
			 :morgan.freenode.net 372 yonaru :- you to psyprus for sponsoring this server!
			 :morgan.freenode.net 372 yonaru :-  
			 :morgan.freenode.net 372 yonaru :- MORGAN, RICHARD K. (1965-) is an English science fiction */

		/* Auth ngircd
		 *  :group101.ro2.uc3m.irc.net 001 Yolanda :Welcome to the Internet Relay Network Yolanda!~rafa@Yolanda-VAIO
		    :group101.ro2.uc3m.irc.net 002 Yolanda :Your host is group101.ro2.uc3m.irc.net, running version ngircd-20.1~53-gac32d07 (i686/pc/cygwin)
		    :group101.ro2.uc3m.irc.net 003 Yolanda :This server has been started Tue Mar 26 2013 at 21:00:14 (CET)
			:group101.ro2.uc3m.irc.net 004 Yolanda group101.ro2.uc3m.irc.net ngircd-20.1~53-gac32d07 abBcCioqrRswx abehiIklmMnoOPqQrRstvVz
			:group101.ro2.uc3m.irc.net 005 Yolanda RFC2812 IRCD=ngIRCd CHARSET=UTF-8 CASEMAPPING=ascii PREFIX=(qaohv)~&@%+ CHANTYPES=#&+ CHANMODES=beI,k,l,imMnOPQRstVz CHANLIMIT=#&+:10 :are supported on this server
		    :group101.ro2.uc3m.irc.net 005 Yolanda CHANNELLEN=50 NICKLEN=9 TOPICLEN=490 AWAYLEN=127 KICKLEN=400 MODES=5 MAXLIST=beI:50 EXCEPTS=e INVEX=I PENALTY :are supported on this server
 			:group101.ro2.uc3m.irc.net 251 Yolanda :There are 1 users and 0 services on 1 servers
			:group101.ro2.uc3m.irc.net 254 Yolanda 4 :channels formed
			:group101.ro2.uc3m.irc.net 255 Yolanda :I have 1 users, 0 services and 0 servers
			:group101.ro2.uc3m.irc.net 265 Yolanda 1 1 :Current local users: 1, Max: 1
			:group101.ro2.uc3m.irc.net 266 Yolanda 1 1 :Current global users: 1, Max: 1
			:group101.ro2.uc3m.irc.net 250 Yolanda :Highest connection count: 1 (1 connections received)
			:group101.ro2.uc3m.irc.net 375 Yolanda :- group101.ro2.uc3m.irc.net message of the day
			:group101.ro2.uc3m.irc.net 372 Yolanda :- "Today's MOTD is irrelevant"
			:group101.ro2.uc3m.irc.net 376 Yolanda :End of MOTD command */
		if (server.getStatus() == Server.AUTH_SENT) {
			
			ircParser.setNickName( server.getNickname() );
			server.setTotalChannels( ircParser.getRpl_LUSERCHANNELS() );
			String welcome2 = ircParser.getRpl_LUSERME();
			
			textToShow = "Welcome to " + server.getIp();
			server.setStatus( Server.AUTHENTICATED );
			Log.d(TAG, "Authenticated with: " + server.getIp() + ":" + server.getPort());
		}
		
	
		/* DCC 	
			:Rafa!~rafaelsf@192.168.1.10 NOTICE eralsaz :DCC Send fileName.exe (192.168.1.10)
			:Rafa!~rafaelsf@192.168.1.10 PRIVMSG eralsaz :<CTRL+A>DCC SEND fileName.exe 3232235786 1024 1234808<CTRL+A>
		*/
		if (ircMessage.contains("DCC ") == true) {
			Log.i(TAG, "DCC received");
			DCC dcc = new DCC();
			
			index = ircMessage.indexOf("DCC SEND ");
			Log.i(TAG, "index: "+String.valueOf(index));
			String channel [] = (ircMessage.substring(index+9)).split(" ");
			
			dcc.setFileName(  channel[0].replaceAll("(\r|\n)", "") );
			
			long ip = Long.parseLong(channel[1]);
			try {
				dcc.setIp( InetAddress.getByAddress(IRCParser.unpackIpAdress(ip)).getHostAddress() );
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			
			String portString = channel[2].replaceAll("(\r|\n)", "");
			dcc.setPort( Integer.parseInt(portString.replaceAll("((|))", "")) );
			
			String fileSizeString = channel[3].replaceAll("(\r|\n)", "");
			dcc.setFileSize( Integer.parseInt(fileSizeString.replaceAll("[\u0000-\u001f]", "")) ); /* remove non-numeric characters */
					
			/* DCC received: amStudio.cfg 192.168.1.17 1024 4520 */
			Log.i(TAG, "DCC received: " + dcc.getFileName() + " " + dcc.getIp() + " "+String.valueOf(server.getPort()) + " "+ String.valueOf(dcc.getFileSize()));

			server.setNotification( 0 );
			server.setDcc(dcc);
			server.setDccOfferReceived( true );
				
		}
	
	
		/* QUIT
		 * :group101.ro2.uc3m.irc.net NOTICE Yolanda :Connection statistics: client 0.1 kb, server 1.8 kb.
		   : ERROR :Closing connection */
		if (server.getStatus() == Server.QUIT_SENT) {
			textToShow = "* " + ircMessage + "<br>";
			server.setStatus( Server.DISCONNECTED );
			int i;
			for (i=0; i<server.getChannelArray().size();i++) {
				int key = server.getChannelArray().keyAt(i);
				server.getChannelArray().get(key).setJoined(false);
			}
				
			Log.d(TAG, "Client disconnected");
			stopProcessingCommands();
		}
		
		
		
	/* Response to JOIN #channel:
	 * :Yolanda!~rafa@Yolanda-VAIO JOIN :#ro2
	   :group101.ro2.uc3m.irc.net 332 Yolanda #ro2 :RO2 Channel
	   :group101.ro2.uc3m.irc.net 333 Yolanda #ro2 -Server- 1363179486
	   :group101.ro2.uc3m.irc.net 353 Yolanda = #ro2 :Yolanda
	   :group101.ro2.uc3m.irc.net 366 Yolanda #ro2 :End of NAMES list*/
		if (ircParser.isJoinReply()) {
			
			ircParser.setNickName( server.getNickname() );
			name = ircParser.getChannelName();
			topic = ircParser.getRpl_TOPIC(); 
			users = ircParser.getRpl_NAME(); // Find RPL_NAME 353
			
			Log.i(TAG, "name --> " + name);
			Log.i(TAG, "topic --> " + topic);
			Log.i(TAG, "users --> " + users);
			
			textToShow = ((Activity) mContext).getResources().getString(R.string.status_joined_to_channel)
					+ ": " + topic + "\n" + 
					((Activity) mContext).getResources().getString(R.string.status_users_on_channel)
					+ ": " + users; 
			
			
			if (server.getStatus() == Server.JOIN_SENT) {
				server.setStatus( Server.JOINED );
			}
		}
		
		
		/* Response to WHO:
           :group101.ro2.uc3m.irc.net 352 Yolanda * ~rafa Yolanda-VAIO group101.ro2.uc3m.irc.net Yolanda H :0 Rafa
		   :group101.ro2.uc3m.irc.net 315 Yolanda * :End of WHO list*/
		/* RPL_WHOREPLAY 352  "<channel> <user> <host> <server> <nick>  <H|G>[*][@|+] :<hopcount> <real name>" */
		if (ircParser.isWhoReply()) {  	
		
			textToShow = ((Activity) mContext).getResources().getString(R.string.status_users_on_channel) + ": " + ircParser.getWho(server.getNickname());
			if (server.getStatus() == Server.WHO_SENT)
				server.setStatus( Server.JOINED );
		}
			
		/* Response to LIST:
			:group101.ro2.uc3m.irc.net 322 Yolanda &SERVER 0 :Server Messages
 			:group101.ro2.uc3m.irc.net 322 Yolanda #test1 0 :Test channel 1
			:group101.ro2.uc3m.irc.net 322 Yolanda #test0 0 :Test channel 0
			:group101.ro2.uc3m.irc.net 322 Yolanda #ro2 1 :RO2 Channel
			:group101.ro2.uc3m.irc.net 323 Yolanda :End of LIST*/

		if (ircParser.isListReply()) {
	
			int key, key_new;
			int index_new;
			boolean channel_found;
	
			if (!ircParser.isEndOfListReply()) {
				ircMessageBuffer = ircMessageBuffer + ircMessage;
				tokens = ircMessageBuffer.split("[\\n]");	
				server.setStatusMessage( String.format( ((Activity) mContext).getResources().getString(R.string.status_channel_downloading), 
						String.valueOf(tokens.length)));  					
			} else {
				server.setStatusMessage( String.format( ((Activity) mContext).getResources().getString(R.string.status_channel_downloaded), 
						server.getTotalChannels())); 

				ircMessageBuffer = ircMessageBuffer + ircMessage; /* add last lines */
				ircParser.setMessage( ircMessageBuffer );
				SparseArray<Channel> ch_new = ircParser.getList(server.getNickname());
				SparseArray<Channel> ch = server.getChannelArray();
				
				/* If channel exists, does not add it again */
				for (index_new = 0; index_new < ch_new.size(); index_new++) {
					channel_found = false;
					key_new = ch_new.keyAt(index_new);
					for (index = 0; index < ch.size(); index++) {
						key = ch.keyAt(index);
						if (ch_new.get(key_new).getName().compareTo( ch.get(key).getName() ) == 0) 
								channel_found = true;
					}
					if (!channel_found)
						ch.append(key_new, ch_new.get(key_new));
				}
				
				server.setStatusMessage( String.format( ((Activity) mContext).getResources().getString(R.string.status_channel_downloaded), 
						String.valueOf(ch_new.size()))); 
			}

			/* Split multiline response: \\n is end of message */
			textToShow = "";
					  
			if ((server.getStatus() == Server.LIST_SENT) &&
					(ircParser.isEndOfListReply())) {
				server.setStatus( Server.JOINED );
				ircMessageBuffer = "";
				Log.d(TAG, "End of LIST received");
			}
		}

		/* Response to LEAVE: 
		 :Yolanda!~rafa@192.168.1.17 PART #ro2 :Yolanda
		 */
		if (ircParser.isLeaveReply()) {
			
			textToShow = "* " + ircMessage + "(Left this channel!)";
			if (server.getStatus() == Server.LEAVE_SENT) {
				if (server.anyChannelJoined()) 
					server.setStatus( Server.JOINED );
				else
					server.setStatus( Server.AUTHENTICATED );
			}
			
		}
		
		/* Updates getChannelBuffer sparse array with new text */ 
		
		/* sparsearray.indexOfValue(String) is immature, only works with constant, e.g. sparseArray.indexOfValue("ro2") 
		    workaround here: */
		final String name1 = name;
		final boolean isChat1 = isVisible;
		final String textToShow1 = textToShow;
		
//		((Activity)mContext).runOnUiThread(new Runnable() {
//		      public void run() {
		    	  SparseArray<Channel> ch = server.getChannelArray();
		    	  Integer key = -1;
		    	  if (!textToShow1.isEmpty()) {
		    		  if (isChat1)  {
		    			  
		    			  
		    			  /* Updates only one channel. "name" must contain a valid channel name*/
		    			  key = MasterArray.findKeyByChannelName(server, name1);
		    			  
		    			  Log.i(TAG, "name1: "+ name1 + " key =" + String.valueOf(key));
		    			  if (key != -1) {
		    			 ArrayList<MyMessage> messages = ch.get(key).getMessageArray();
		    			
		    				messages.add(new MyMessage(textToShow1, false, false)); /* true -> message is mine */
		    			  }
		    			  /* Show publicity each 4 messages */
		    			  
//		    			  if ((messages.size() %  4) == 0) {
//		    				  messages.add(new MyMessage("PUB", false, false, true)); /* true -> PUB */
//		    				  Log.d(TAG, "PUB message added: Size: " + String.valueOf(messages.size()));
//		    			  }
		    			  
		    			  //ch.get(key).setMessageArray(messages);
		    			  
		    			  //sb.setValueAt(key, messages); /* append new text */
		    			  //server.setChannelArray(ch);
		    			  
		    			  
		    			  
		    		  } else {
		    			  /* Updates all channels in the server*/
		    			  for (int i=0;i< server.getChannelArray().size(); i++) {
		    				  //key = server.getChannelArray().keyAt(i);
		    				  //Log.i(TAG, "key " + key.toString());
		    				  server.setStatusMessage( textToShow1 );  
		    				  //ArrayList<MyMessage> messages = ch.get(key).getMessageArray();	
		    				  //MyMessage myMessage= new MyMessage(textToShow1, false, false, false);
		    				  //myMessage.setStatusMessage(true); /* true -> message is mine */
		    				  //messages.add(myMessage); 
		    				  
		    				  //ch.get(key).setMessageArray(messages);
		    				  
		    				  //sb.setValueAt(key, messages); /* append new text */
		    			  }
		    			  server.setChannelArray(ch);
		    		  }
		    	  }
		    	  //MasterArray.sync(server);

//		      }
//		   });
	}	
}

