package es.rafaelsf80.apps.irccfree.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class DCC implements Parcelable {
	
	private String dccIp;
    private int dccPort;
    private int dccFileSize;
    private String dccFileName;
    
	public DCC () {
		this.dccIp = "";
		this.dccFileSize = 0;
		this.dccPort = 0;
		this.dccFileName = "";
	}
    
    public DCC(Parcel in) {
		readFromParcel(in);
	}

    public String getIp() {
		return dccIp;
	}

	public void setIp(String dccIp) {
		this.dccIp = dccIp;
	}

	public int getPort() {
		return dccPort;
	}

	public void setPort(int dccPort) {
		this.dccPort = dccPort;
	}

	public int getFileSize() {
		return dccFileSize;
	}

	public void setFileSize(int dccFileSize) {
		this.dccFileSize = dccFileSize;
	}

	public String getFileName() {
		return dccFileName;
	}

	public void setFileName(String dccFileName) {
		this.dccFileName = dccFileName;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
   

    @Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(dccIp);
		dest.writeInt(dccPort);
		dest.writeInt(dccFileSize);
		dest.writeString(dccFileName);
    }
	

	private void readFromParcel(Parcel in) {
		
		dccIp = in.readString();
		dccPort = in.readInt();
		dccFileSize = in.readInt();
		dccFileName = in.readString();
			
	}
	
	
   public static final Parcelable.Creator CREATOR =
   	new Parcelable.Creator() {
           public DCC createFromParcel(Parcel in) {
               return new DCC(in);
           }

           public DCC[] newArray(int size) {
               return new DCC[size];
           }
       };     
}
