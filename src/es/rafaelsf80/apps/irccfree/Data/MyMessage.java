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
	boolean isDcc;
	
	/**
	 * NOT USED: boolean to determine, whether the message is a a file transfer (DCC).
	 */
	boolean isAdMessage;
		
	/**
	 * Constructor to make a status Message object
	 * consider the parameters are swaped from default Message constructor,
	 *  not a good approach but have to go with it.
	 */
	public MyMessage(String message, boolean isMine, boolean isDcc) {
		super();
		this.message = message;
		this.isMine = isMine;
		this.isDcc = isDcc;
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
	public boolean isDcc() {
		return isDcc;
	}
	public void setDcc(boolean isDcc) {
		this.isDcc = isDcc;
	}
	public boolean isAdMessage() {
		return isAdMessage;
	}
	public void setAdMessage(boolean isAdMessage) {
		this.isAdMessage = isAdMessage;
	}
}