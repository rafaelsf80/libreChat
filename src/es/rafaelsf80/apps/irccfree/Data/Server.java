package es.rafaelsf80.apps.irccfree.Data;

import java.io.DataOutputStream;
import java.util.ArrayList;

import es.rafaelsf80.apps.irccfree.Service.ConnectionService;
import es.rafaelsf80.apps.irccfree.Service.ConnectionThread;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

public class Server implements Parcelable {

	/* Status codes */
	public static int DISCONNECTED = 0;
	public static int CONNECTING = 1;
	public static int CONNECTED = 2;
	public static int AUTH_SENT = 3;
	public static int AUTHENTICATED = 4;
	public static int JOIN_SENT = 5;
	public static int JOINED = 6;
	public static int WHO_SENT = 7;
	public static int LIST_SENT = 8;
	public static int LEAVE_SENT = 9;
	public static int QUIT_SENT = 10;

	public static String DEFAULT_ENCODING = "UTF-8";

	private boolean connectOnLaunch;
	private String name;
	private String ip;
	private int port;
	private String group;
	private String nickname;
	private String password;
	private String encoding;
	private SparseArray<Channel> channelArray;
	private boolean isDccOfferReceived;
	private DCC dcc;
	private int status;
	private String StatusMessage; 

	private String totalChannels;
	private Handler handler;
	private ConnectionThread thread;
	private DataOutputStream outStream;
	private int notification;

	private ArrayList<ArrayList<Server>> mArray = new ArrayList<ArrayList<Server>>();

	public Server () {

		this.status = DISCONNECTED;
		this.name="";
		this.group="";
		this.ip="";
		this.port=0;
		this.nickname="";
		this.password="";
		this.encoding="";
		this.connectOnLaunch = false;
		SparseArray<Channel> c = new SparseArray<Channel>();
		this.channelArray = c;
	}

	public Server(String name, String group, String ip, int port, String nickname, String encoding, String password, boolean connectOnLaunch, SparseArray<Channel> c) {

		this.status = DISCONNECTED;
		this.name=name;
		this.group=group;
		this.ip=ip;
		this.port=port;
		this.nickname=nickname;
		this.encoding=encoding;
		this.password=password;
		this.connectOnLaunch = connectOnLaunch;
		this.channelArray = c;
	}


	public Server(Server server) {

		this.connectOnLaunch = server.isConnectOnLaunch();
		this.name = server.getName();
		this.group = server.getGroup();
		this.ip = server.getIp();
		this.port = server.getPort();
		this.nickname = server.getNickname();
		this.password = server.getPassword();
		this.encoding = server.getEncoding();
		this.channelArray = server.getChannelArray();
		this.status = server.getStatus();

		this.handler = server.getHandler();
		this.thread = server.getThread();
		this.outStream = server.getOutStream();

	}


	public Server(Parcel in) {
		// TODO Auto-generated constructor stub
	}

	public boolean isConnectOnLaunch() {
		return connectOnLaunch;
	}

	public void setConnectOnLaunch(boolean connectOnLaunch) {
		this.connectOnLaunch = connectOnLaunch;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public static boolean checkIp (String ip)
	{
		if ((ip == "") || (ip == null))
			return false;
		String [] parts = ip.split ("\\.");
		for (String s : parts)
		{
			int i = Integer.parseInt (s);
			if (i < 0 || i > 255)
			{
				return false;
			}
		}
		return true;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}


	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}


	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusMessage() {
		return StatusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		StatusMessage = statusMessage;
	}




	public boolean readyToSend = false;
	private String readyToSendString;



	public String getReadyToSendString() {
		return readyToSendString;
	}

	public void setReadyToSendString(String readyToSendString) {
		this.readyToSendString = readyToSendString;
	}

	public SparseArray<Channel> getChannelArray() {
		return channelArray;
	}

	public void setChannelArray(SparseArray<Channel> channelArray) {
		this.channelArray = channelArray;
	}

	public boolean isDccOfferReceived() {
		return isDccOfferReceived;
	}

	public void setDccOfferReceived(boolean isDccOfferReceived) {
		this.isDccOfferReceived = isDccOfferReceived;
	}

	public DCC getDcc() {
		return dcc;
	}

	public void setDcc(DCC dcc) {
		this.dcc = dcc;
	}

	public String getTotalChannels() {
		return totalChannels;
	}

	public void setTotalChannels(String totalChannels) {
		this.totalChannels = totalChannels;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public ConnectionThread getThread() {
		return thread;
	}

	public void setThread(ConnectionThread thread) {
		this.thread = thread;
	}

	public int getNotification() {
		return notification;
	}

	public void setNotification(int notification) {
		this.notification = notification;
	}

	public DataOutputStream getOutStream() {
		return outStream;
	}

	public void setOutStream(DataOutputStream outStream) {
		this.outStream = outStream;
	}

	
	public void sync() {

		mArray = MasterArray.getData();
		for (int i = 0; i < mArray.size(); i++) {
			for (int j = 0; j < mArray.get(i).size(); j++) {
				if ((this.getIp() == (mArray.get(i).get(j)).getIp())) {

					this.connectOnLaunch = (mArray.get(i).get(j)).isConnectOnLaunch();
					this.name = (mArray.get(i).get(j)).getName();
					this.ip = (mArray.get(i).get(j)).getIp();
					this.port = (mArray.get(i).get(j)).getPort();
					this.nickname = (mArray.get(i).get(j)).getNickname();
					this.encoding = (mArray.get(i).get(j)).getEncoding();
					this.password = (mArray.get(i).get(j)).getPassword();
					this.channelArray = (mArray.get(i).get(j)).getChannelArray();
					this.status = (mArray.get(i).get(j)).getStatus(); 			
					this.handler = (mArray.get(i).get(j)).getHandler();
					this.thread = (mArray.get(i).get(j)).getThread();
					this.outStream = (mArray.get(i).get(j)).getOutStream();
					this.notification = (mArray.get(i).get(j)).getNotification();
				}
			}
		}
	}

	public boolean isChannel(String channelName) {

		int key = -1;
		String name = "";
		for (int index = 0; index < this.channelArray.size(); index++) {
			key = this.channelArray.keyAt(index);
			name = this.channelArray.get(key).getName();
			if (name.compareTo(channelName) == 0)
				return true;
		}
		return false;
	}

	/**
	 * Return true if I am joined to any channel in this server 
	 * */
	public boolean anyChannelJoined() {

		if ((this.getStatus() != Server.DISCONNECTED) &&
				(this.getStatus() != Server.CONNECTING)) {
			//SparseArray ch = this.getChannelStatus();
			for (int j2 = 0; j2 < this.channelArray.size(); j2++) {
				int key = this.channelArray.keyAt(j2);
				if (this.channelArray.get(key).isJoined() == true) 
					return true;
			}
		}
		return false;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeValue( isConnectOnLaunch() );
		dest.writeString(name);
		dest.writeString(ip);
		dest.writeInt(port);
		dest.writeString(group);
		dest.writeString(nickname);
		dest.writeString(encoding);
		dest.writeString(password);
		dest.writeInt(status);
		
	}
	
	/**
	 *
	 * Called from the constructor to create this
	 * object from a parcel.
	 *
	 * @param in parcel from which to re-create object
	 */
	private void readFromParcel(Parcel in) {

		// We just need to read back each
		// field in the order that it was
		// written to the parcel
		connectOnLaunch = (Boolean) in.readValue(null);
		name = in.readString();
		ip = in.readString();
		port = in.readInt();
		group = in.readString();
		nickname = in.readString();
		encoding = in.readString();
		password = in.readString();
		status = in.readInt();
	}
	
	
	 /**
    *
    * This field is needed for Android to be able to
    * create new objects, individually or as arrays.
    *
    * This also means that you can use use the default
    * constructor to create the object and use another
    * method to hyrdate it as necessary.
    *
    * I just find it easier to use the constructor.
    * It makes sense for the way my brain thinks ;-)
    *
    */
   public static final Parcelable.Creator CREATOR =
   	new Parcelable.Creator() {
           public Server createFromParcel(Parcel in) {
               return new Server(in);
           }

           public Server[] newArray(int size) {
               return new Server[size];
           }
       };
       

}