package es.rafaelsf80.apps.irccfree.Data;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;


public class Channel implements Parcelable  {


	private boolean isJoined;
	private String name;
	private String topic;
	private ArrayList<MyMessage> messageArray;

	public Channel () {

		this.isJoined = false;
		this.name = "";
		this.topic = "";
		ArrayList<MyMessage> c14 = new ArrayList<MyMessage>();
		this.messageArray = c14;

	}

	public Channel(boolean isJoined, String name, String topic) {

		this.isJoined = isJoined;
		this.name = name;
		this.topic = topic;
		ArrayList<MyMessage> c14 = new ArrayList<MyMessage>();
		this.messageArray = c14;
	}




	public Channel(Parcel in) {
		readFromParcel(in);
	}



	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		//dest.writeInt( (int) channelStatus);
		dest.writeString(name);
		dest.writeString(topic);
		//dest.writeSparseArray(channelBuffer);


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
		//channelStatus = (boolean) in.readint();
		name = in.readString();
		topic = in.readString();
		//channelBuffer = in.readSparseArray(loader);	
	}


	public boolean isJoined() {
		return isJoined;
	}

	public void setJoined(boolean isJoined) {
		this.isJoined = isJoined;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public ArrayList<MyMessage> getMessageArray() {
		return messageArray;
	}

	public void setMessageArray(ArrayList<MyMessage> messageArray) {
		this.messageArray = messageArray;
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
		public Channel createFromParcel(Parcel in) {
			return new Channel(in);
		}

		public Channel[] newArray(int size) {
			return new Channel[size];
		}
	};
}