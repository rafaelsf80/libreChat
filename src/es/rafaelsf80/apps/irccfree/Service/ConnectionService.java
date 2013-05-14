package es.rafaelsf80.apps.irccfree.Service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import es.rafaelsf80.apps.irccfree.Main;
import es.rafaelsf80.apps.irccfree.MyRunnable;
import es.rafaelsf80.apps.irccfree.Data.Channel;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;
import es.rafaelsf80.apps.irccfree.Data.MyMessage;
import es.rafaelsf80.apps.irccfree.Data.Server;

public class ConnectionService extends Service {
	
	public static int CONNECT_COMMAND = 0;
	
	public static int DICONNECT_COMMAND = 2;
	public static int JOIN_COMMAND = 3;
	public static int AUTH_COMMAND = 4;
	
	public static int WHO_COMMAND = 6;
	public static int LIST_COMMAND = 7;
	public static int LEAVE_COMMAND = 8;
	
	public static int IRC_COMMAND = 0;
	public static int IRC_CHAT = 1;
	public static int IRC_USER_COMMAND = 2;
	


	private final String TAG = getClass().getSimpleName();


	private static IConnectionService mIService;
	
	private static Context mContext;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		mContext = this;
		Log.d(TAG, "onCreate() ConnectionService");
		MasterArray.setData();
		startService();	
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy() ConnectionService");
	}
	
	public void startService() {
	
		
		Server server;
		Log.d(TAG, "Started mArray (groups): " + (MasterArray.getData()).size());
		
		/* Send CONNECT and AUTH to servers with info stored */
		for (int i = 0; i < (MasterArray.getData()).size(); i++) 
			for (int j = 0; j < (MasterArray.getData()).get(i).size(); j++) {
				server = (MasterArray.getData()).get(i).get(j);
				if ((server.getStatus() == Server.DISCONNECTED) &&
						(server.isConnectOnLaunch())) {

					sendIRCCommand(server, "/CONNECT");
					/* Wait 2 seconds and then send /AUTH */
					Handler handler = new Handler(); 
					handler.postDelayed(new MyRunnable(server) { 
						public void run() { 
							if (server.getStatus() == Server.CONNECTED) {
								ConnectionService.sendIRCCommand(server, "/AUTH " + server.getNickname());
							}
						} 
					}, 2000);
					
					/* Wait 3 seconds and then send /LIST */			
					handler = new Handler(); 
					handler.postDelayed(new MyRunnable(server) { 
						public void run() { 
							if (server.getStatus() == Server.AUTHENTICATED) {
								ConnectionService.sendIRCCommand(server, "/LIST");
							}
						} 
					}, 4000);
					
				}	
			}
	}

	
	public interface IConnectionService {
		public void updateUI();
		public void updateColors();
	}
	
	public static void regListener (IConnectionService iConnectionService) {
		mIService = iConnectionService;
	}
	
	public static void unregisterListener () {
		mIService = null;
	}
	
	
	/**
     * Send IRC command 
     * Note this is generated typing by the user. OK to be in the service
     */
	public static void sendIRCCommand(Server server, String input) {
		
		Handler mHandler;
		
		/* Special treatment for CONNECT command */
		if ((input.startsWith("/CONNECT") && 
			(server.getStatus() ==  Server.DISCONNECTED))) {
			
		
			mHandler = new Handler(new Callback() {
				
				@Override
				public boolean handleMessage(Message msg) {
					
					// This message receives info from the threads to be sent to the UI (texts, IRC responses, chats from others, ...)
					
					Server server = (Server) msg.obj;
					//MasterArray.sync(server);
					
										
					if (mIService != null) {
						
						mIService.updateUI();
						mIService.updateColors();
					} else {
						if (server.getNotification() != 0) {
							Log.d("ConnectionService", "Notification");
							showNotification(server, mContext);
						}
							
					}
					server.setNotification(0);
					
					/* After  Authentication, send LIST to update channel list */
					if (server.getStatus() == Server.AUTHENTICATED) {
						ConnectionService.sendIRCCommand(server, "/LIST");
					}
					
										
					return true;
				}
			});
			
			ConnectionThread ct = new ConnectionThread(server, mHandler, Main.context);
			ct.start();
			server.setStatus(Server.CONNECTING);
    		server.setThread( ct );		
    		if (mIService != null) {
				
				mIService.updateUI();
				mIService.updateColors();
			}
			MasterArray.sync(server);
			
			return;
		}
	
		String ircMessage = null;
		String ircCommand = null;
		String textToShow = null;
		
		int index;

		/* get command to uppercase */
		index = input.indexOf(" ");

		if (index > 0) {
			ircCommand = input.substring(0, index);
			input = input.replaceFirst(ircCommand, ircCommand.toUpperCase());
		} else
			input = input.toUpperCase();

		/* process command */
		if (input.startsWith("/AUTH")) {
			input = input.replaceFirst("/AUTH", "NICK");
			ircMessage = input + "\nUSER "+server.getNickname() + " " + server.getIp() + " " + server.getIp() + " " + server.getNickname() + "\n";
			server.setNickname((input.substring(5)).replaceAll("(\\r|\\n)", ""));

			server.setStatus(Server.AUTH_SENT);
		}

		if (input.startsWith("/JOIN")) {
			input = input.replaceFirst("/JOIN", "JOIN");
			ircMessage = input + "\n";
			//server.addActiveChannel((input.substring(5)).replaceAll("(\\r|\\n)", "")); /* esto habría que ponerlo después */
			server.setStatus(Server.JOIN_SENT);
			Log.d("ConnectionService", "hago join_sent");
		}

		if (input.startsWith("/WHO")) {
			input = input.replaceFirst("/WHO", "WHO");
			ircMessage = input + "\n";	
			server.setStatus(Server.WHO_SENT);
		}

		if (input.startsWith("/LIST")) {
			input = input.replaceFirst("/LIST", "LIST");
			ircMessage = input + "\n";
			server.setStatus(Server.LIST_SENT);

		}
		if (input.startsWith("/QUIT")) {
			input = input.replaceFirst("/QUIT", "QUIT");
			ircMessage = input + "\n";
			server.setStatus(Server.QUIT_SENT);
		}

		if (input.startsWith("/LEAVE")) {
			if (server.getStatus() != Server.JOINED) {
				ircMessage = "ERROR: MUST JOIN A CHANNEL FIRST";
				textToShow = ircMessage;
			} else { 
				input = input.replaceFirst("/LEAVE", "PART");
				ircMessage = input + "\n";
				server.setStatus(Server.LEAVE_SENT);
				textToShow = "&lt;" + server.getNickname() + "&gt; " + "CHANNEL LEFT";
			}
		}
		
		
		
		/* Update UI since input is directly coming from the user (except /CONNECT) */
		textToShow = "<font color='green'>" + input + "</font><br>";
		Log.d("ConnectionService", textToShow);
		
	
		
		
		/* Updates for status and buffer fields */
		MasterArray.sync(server);
		if (mIService != null) {
			mIService.updateUI();
			mIService.updateColors();
		}
		

		/* Send through socket */
		if (ircMessage != null) {
				Log.d("ConnectionService", "enviando "+server + " " + ircMessage);
				if (!ircMessage.startsWith("ERROR")) {
					server.readyToSend = true;
					server.setReadyToSendString(ircMessage);
				}
			
		}
	}
	
	
	/**
     * Send IRC chat message 
     * Note this is typing by the user. OK to be in the service
     */
	public static void sendIRCChat(Server server, String channelName, String input) {	
		String ircMessage = null;
		String textToShow = null;

		/* Send IRC chat message to socket 
		  :rafa!~rafa@Yolanda-VAIO PRIVMSG #ro2 :saludos */
		if (server.getStatus() != Server.JOINED) {
			ircMessage = "ERROR: MUST JOIN A CHANNEL FIRST";
			textToShow = ircMessage;
		} else { 
			Log.d("ConnectionService", server.getIp() + " " + server.getNickname());
			ircMessage = ":"+server.getNickname()+"!~"+server.getNickname()+"@192.168.1.17 PRIVMSG "+ "#ro2" +" :" + input + "\n";
			
			textToShow = input;
		}
		
		/* Sending */
		
			if (!ircMessage.startsWith("ERROR")) {
				server.readyToSend = true;
				server.setReadyToSendString(ircMessage);
			}
		
		/* Update UI  */
		Log.d("ConnectionService", textToShow);
		
		Integer key = MasterArray.findKeyByChannelName(server, channelName);
		
		SparseArray<Channel> ch = server.getChannelArray();
		ArrayList<MyMessage> messages = ch.get(key).getMessageArray();
		Log.i("ConnectionService", "key " + key.toString());

		messages.add(new MyMessage(textToShow, true, false, false)); /* true -> message is mine */
//		ch.setValueAt(key, messages); /* append new text */
//		server.setChannelBuffer(sb);
		
		MasterArray.sync(server);
		if (mIService != null)
			mIService.updateUI();
}

	
	
		
	public static void showNotification(Server server, Context context) {
		
//		// TODO: EXTRAER NOTIFICATION
//		
//		String str = "";
//		int key = -1;
//		
//		if (server.isDccOfferReceived())
//			str = "DCC received";
//		else {
//			
//			// Get last message. Channel key is on getNotification()
//			key = server.getNotification();
//			SparseArray<ArrayList<MyMessage>> sb = server.getChannelBuffer();
//            ArrayList<MyMessage> messages = sb.get(key);
//    		
//            int last_message_index = sb.size(); 
//			MyMessage lastMessage = messages.get( sb.keyAt(last_message_index) ); 
//			str = lastMessage.toString(); 
//		}
//		
//		NotificationCompat.Builder mBuilder =
//		        new NotificationCompat.Builder(context)
//		        .setSmallIcon(R.drawable.icon)
//		        .setContentTitle("My notification")
//		        .setContentText(str);
//		// Creates an explicit intent for an Activity in your app
//		
//		Intent resultIntent = new Intent(context, Main.class);
//		resultIntent.putExtra(IRCHelper.NOTIFICATION_IS_DCC, server.isDccOfferReceived() );
//		resultIntent.putExtra(IRCHelper.NOTIFICATION_CHANNEL_KEY, key);
//
//		// The stack builder object will contain an artificial back stack for the
//		// started Activity.
//		// This ensures that navigating backward from the Activity leads out of
//		// your application to the Home screen.
//		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//		// Adds the back stack for the Intent (but not the Intent itself)
//		stackBuilder.addParentStack(Chats.class);
//		// Adds the Intent that starts the Activity to the top of the stack
//		stackBuilder.addNextIntent(resultIntent);
//		PendingIntent resultPendingIntent =
//		        stackBuilder.getPendingIntent(
//		            0,
//		            PendingIntent.FLAG_UPDATE_CURRENT
//		        );
//		mBuilder.setContentIntent(resultPendingIntent);
//		NotificationManager mNotificationManager =
//		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//		// mId allows you to update the notification later on.
//		mNotificationManager.notify( IRCHelper.NOTIFICATION_ID, mBuilder.build() );
	}

	
}
