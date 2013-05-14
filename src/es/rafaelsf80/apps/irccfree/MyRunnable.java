package es.rafaelsf80.apps.irccfree;

import es.rafaelsf80.apps.irccfree.Data.Server;

public class MyRunnable implements Runnable {

	public Server server;
	
	public MyRunnable(Server server) {
		this.server = server;
	}

	public void run() {
	}
}