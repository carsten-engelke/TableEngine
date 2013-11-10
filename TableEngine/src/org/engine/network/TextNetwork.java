package org.engine.network;

import org.engine.TableEngine;
import org.engine.network.Server.Slot;
import org.engine.property.Information;
import org.engine.utils.Array;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

public class TextNetwork implements Network {

	private TableEngine t;
	private long lastTS = 0;

	public TextNetwork() {

	}
	
	@Override
	public void requestInfo(NetworkListener l) {
		
		Array<Slot> slots = new Array<Slot>();
		for (int i = 1; i <= 12; i ++) {
			slots.add(new Slot(i, false, "Test-Slot " + i , "id" + i));
		}
		l.info(slots);
	}

	@Override
	public void requestKey(NetworkListener l) {

		l.key(true);
	}

	@Override
	public void requestReceive(NetworkListener l) {

		String s = readFromFile();
		Long lastTimeStamp = Long.parseLong(s.substring(0, s.indexOf("<TEST>")));
		String response = s.substring(s.indexOf("<TEST>") + 6);
		l.receive(lastTimeStamp, response);
	}

	@Override
	public void requestSend(NetworkListener l) {

		lastTS = TimeUtils.millis();
		writeToFile(lastTS + "<TEST>" + Information.PropertiesToString(t.getProperties()));
	}

	@Override
	public void requestTimestamp(NetworkListener l) {

		l.timestamp(lastTS);
	}

	@Override
	public void requestDebug(NetworkListener l) {

		l.debug("TEXTNETWORK");
	}

	public static void writeToFile(String s) {
		
		Gdx.files.external("server.txt").writeString(s, false);
	}
	
	public static String readFromFile() {
		
		return Gdx.files.external("server.txt").readString();
	}

	@Override
	public void initialize(TableEngine t) {

		this.t = t;
	}
	
	
}
