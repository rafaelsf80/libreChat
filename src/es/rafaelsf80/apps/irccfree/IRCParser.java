package es.rafaelsf80.apps.irccfree;

import java.util.ArrayList;

import android.util.Log;
import android.util.SparseArray;
import es.rafaelsf80.apps.irccfree.Data.Channel;
import es.rafaelsf80.apps.irccfree.Data.MasterArray;
import es.rafaelsf80.apps.irccfree.Data.MyMessage;

public class IRCParser {

	
	private String mIrcMessage;
	private String mNickName;

	
	public IRCParser(String ircMessage) {
		mIrcMessage = ircMessage;
	}
	
	public void setMessage(String ircMessage) {
		mIrcMessage = ircMessage;
	}
	public String getNickName() {
		return mNickName;
	}

	public void setNickName(String mNickName) {
		this.mNickName = mNickName;
	}
	 

	public static int packIpAdress(byte[] bytes) {
		  int val = 0;
		  for (int i = 0; i < bytes.length; i++) {
		    val <<= 8;
		    val |= bytes[i];
		  }
		  return val;
		}
	
	public static byte[] unpackIpAdress(long bytes) {
		  return new byte[] {
		    (byte)((bytes >>> 24) & 0xff),
		    (byte)((bytes >>> 16) & 0xff),
		    (byte)((bytes >>>  8) & 0xff),
		    (byte)((bytes       ) & 0xff)
		  };
		}
	
	public boolean isJoinReply() {
		if (mIrcMessage.contains("JOIN :") == true)
			return true;
		else
			return false;
	}
	
	public boolean isWhoReply() {
		if (mIrcMessage.contains(" 352 ") == true)
			return true;
		else
			return false;
	}
	
		
	public boolean isListReply() {
		if (mIrcMessage.contains(" 322 ") == true)
			return true;
		else
			return false;
	}
	
	public boolean isLeaveReply() {
		if (mIrcMessage.contains(" PART ") == true)
			return true;
		else
			return false;
	}
	
	/**  254     RPL_LUSERCHANNELS
        "<integer> :channels formed" */
	public String getRpl_LUSERCHANNELS() {
		int index;

		String [] tokens = mIrcMessage.split("[\\n]");
		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = tokens[i].replaceAll("(\r|\n)", "");
			if (((index = tokens[i].indexOf(" 254 "))) > 0)  { // Find 254
				String [] words = ( tokens[i].substring(index+6 + mNickName.length()) ).split(" ");
				return words[0];
			}			
		}
		return null;
	}
	
	/** 255     RPL_LUSERME
    ":I have <integer> clients and <integer> \
      servers */
	public String getRpl_LUSERME() {
		String [] tokens = mIrcMessage.split("[\\n]");
		for (int i = 0; i < tokens.length; i++) {
			if ((tokens[i].indexOf("255")) > 0)  // Find 255
				return tokens[i];

		}
		return null;
	}

	public String getChannelName() {

		int index;
		String [] tokens = mIrcMessage.split("[\\n]");		
		
		for (int i = 0; i < tokens.length; i++) {
			/* remove \r|\n */
			tokens[i] = tokens[i].replaceAll("(\r|\n)", "");
			if ((index = tokens[i].indexOf("JOIN")) > 0)  // Find NAME
				return tokens[i].substring(index+6);	
		}
		return null;
	}

	public String getRpl_TOPIC() {

		int index;
		String [] tokens = mIrcMessage.split("[\\n]");		
		
		for (int i = 0; i < tokens.length; i++) {
			/* remove \r|\n */
			tokens[i] = tokens[i].replaceAll("(\r|\n)", "");	
			if ((index = tokens[i].indexOf(" 332 ")) > 0)  // Find RPL_TOPIC 332
				return tokens[i].substring(index+7 + mNickName.length() + getChannelName().length());
		}
		return null;
	}

	public String getRpl_NAME() {

		int index;
		String [] tokens = mIrcMessage.split("[\\n]");
		
		for (int i = 0; i < tokens.length; i++) {
			/* remove \r|\n */
			tokens[i] = tokens[i].replaceAll("(\r|\n)", "");
			if ((index = tokens[i].indexOf(" 353 ")) > 0)  // Find RPL_NAME 353
				return tokens[i].substring(index+9 + mNickName.length() + getChannelName().length());
		}
		return null;
	}

	/* Response to LIST:
	:group101.ro2.uc3m.irc.net 322 Yolanda &SERVER 0 :Server Messages
	:group101.ro2.uc3m.irc.net 322 Yolanda #test1 0 :Test channel 1
	:group101.ro2.uc3m.irc.net 322 Yolanda #test0 0 :Test channel 0
	:group101.ro2.uc3m.irc.net 322 Yolanda #ro2 1 :RO2 Channel
	:group101.ro2.uc3m.irc.net 323 Yolanda :End of LIST*/
	
	public SparseArray<Channel> getList(String nickName) {
		
		/* Split multiline response: \\n is end of message */
		
		String [] tokens = mIrcMessage.split("[\\n]");
		String channelName = null, channelTopic = null;
		
		boolean endOfListReceived = false;
		
		SparseArray<Channel> ch = new SparseArray<Channel>();
		
		int index;
		int key = MasterArray.getNewKey();
		/* i=1 since I need to start in second line */
		for (int i = 1; i < tokens.length; i++) {
			if ((tokens[i].indexOf(" 323 ")) > 0)
				endOfListReceived = true;
			
			
			if (((index = tokens[i].indexOf("322 ")) > 0) &&
				 (tokens[i].indexOf("SERVER") == -1))  { // 322  RPL_LIST "<channel> <# visible> :<topic>"
				
				if (tokens[i].length() > index + 5 + nickName.length()) {
					channelName = tokens[i].substring(index + 5 + nickName.length());
					String [] pepe = channelName.split(" ");
					channelName = pepe[0];
					int lastIndex = tokens[i].lastIndexOf(":");
					channelTopic = tokens[i].substring(lastIndex+1);
				}
				else 
					channelName=null;
				
							
				if (channelName != null) {
					
					Channel channel = new Channel(false, channelName, channelTopic);
					ch.append(key, channel);
								
					Log.d("ConnectionThread", "getList: key("+String.valueOf(key) + ") " + channelName + " :" + channelTopic);
					//Log.d(TAG, server + "channel array size: " + MasterArray.channelSize());
					
					key += 1;
				}
			}
		}
		return ch;
	}
	
	public boolean isEndOfListReply() {
	
		String [] tokens = mIrcMessage.split("[\\n]");
		
		/* i=1 since I need to start in second line */
		for (int i = 1; i < tokens.length; i++) {
			if (((tokens[i].indexOf(" 323 ")) > 0) &&
				 tokens[i].contains(":End of "))
				return true;
		}
		return false;
	}
}
