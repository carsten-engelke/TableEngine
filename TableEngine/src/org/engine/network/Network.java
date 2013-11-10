package org.engine.network;

import org.engine.TableEngine;
import org.engine.network.Server.Slot;
import org.engine.utils.Array;

public interface Network {

	public void requestInfo(NetworkListener l);

	public void requestKey(NetworkListener l);

	public void requestReceive(NetworkListener l);

	public void requestSend(NetworkListener l);

	public void requestTimestamp(NetworkListener l);
	
	public void requestDebug(NetworkListener l);
	
	public void initialize(TableEngine t);
			
	public interface NetworkListener {

		public void error(Throwable t, String action);

		public void info(Array<Slot> slotArray);

		public void key(boolean validKey);

		public void receive(long lastTimeStamp, String response);

		public void send(long lastTimeStamp, boolean sendSuccess);

		public void timestamp(long lastTimeStamp);
		
		public void debug(String response);
	}
}
