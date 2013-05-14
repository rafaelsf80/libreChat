package es.rafaelsf80.apps.irccfree.Data;

public class MyMessage {
	/**
	 * The content of the message
	 */
	String message;
	
	
	/**
	 * boolean to determine, who is sender of this message
	 */
	boolean isMine;
	/**
	 * boolean to determine, whether the message is a status message or not.
	 * it reflects the changes/updates about the sender is writing, have entered text etc
	 */
	boolean isStatusMessage;
	
	/**
	 * boolean to determine, whether the message is a an ad.
	 */
	boolean isAdMessage;
	
	
	/**
	 * Constructor to make a status Message object
	 * consider the parameters are swaped from default Message constructor,
	 *  not a good approach but have to go with it.
	 */
	public MyMessage(String message, boolean isMine, boolean isStatus, boolean isAd) {
		super();
		this.message = message;
		this.isMine = isMine;
		this.isStatusMessage = isStatus;
		this.isAdMessage = isAd;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isMine() {
		return isMine;
	}
	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}
	public boolean isStatusMessage() {
		return isStatusMessage;
	}
	public void setStatusMessage(boolean isStatusMessage) {
		this.isStatusMessage = isStatusMessage;
	}
	public boolean isAdMessage() {
		return isAdMessage;
	}
	public void setAdMessage(boolean isAdMessage) {
		this.isAdMessage = isAdMessage;
	}
}