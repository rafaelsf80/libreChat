package es.rafaelsf80.apps.irccfree.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class DCC implements Parcelable {
	
	private String dccIp;
    private int dccPort;
    private int dccFileSize;
    private String dccFileName;
    
    public DCC(Parcel in) {
		readFromParcel(in);
	}

    
    public String getDccIp() {
		return dccIp;
	}

	public void setDccIp(String dccIp) {
		this.dccIp = dccIp;
	}



	public int getDccPort() {
		return dccPort;
	}



	public void setDccPort(int dccPort) {
		this.dccPort = dccPort;
	}



	public int getDccFileSize() {
		return dccFileSize;
	}



	public void setDccFileSize(int dccFileSize) {
		this.dccFileSize = dccFileSize;
	}



	public String getDccFileName() {
		return dccFileName;
	}



	public void setDccFileName(String dccFileName) {
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
