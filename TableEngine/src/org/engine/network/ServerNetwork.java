package org.engine.network;

import org.engine.property.Information;
import org.engine.TableEngine;

public class ServerNetwork implements Network {

	public TableEngine t;
	
	public String url;
	
	public String key;
	
	public int keyID;
	
	public int slot;
	
	public String playerID;
	
	public String playerPassword;
	
	public String playerNickName;

	public void initialize(TableEngine t) {

		this.t = t;
	}

	@Override
	public void requestInfo(NetworkListener l) {

		Server.requestInfo(t.getPrefString("url"), l);
	}

	@Override
	public void requestKey(NetworkListener l) {

		Server.requestKey(t.getPrefString("url"), t.getPrefString("key"),
				t.getPrefInteger("gameID"), l);
	}

	@Override
	public void requestReceive(NetworkListener l) {

		Server.requestReceive(t.getPrefString("url"), t.getPrefInteger("slot"),
				t.getPrefString("password"), t.getPrefString("playerID"), l);
	}

	@Override
	public void requestSend(NetworkListener l) {

		Server.requestSend(t.getPrefString("url"), t.getPrefInteger("slot"),
				t.getPrefString("playerName"), t.getPrefString("password"),
				t.getPrefString("playerID"),
				Information.PropertiesToString(t.getProperties()), l);
	}

	@Override
	public void requestTimestamp(NetworkListener l) {

		Server.requestTimeStamp(t.getPrefString("url"), t.getPrefInteger("slot"), t.getPrefString("password"), t.getPrefString("playerID"), l);
	}
	
	@Override
	public void requestDebug(NetworkListener l) {

		Server.requestDebug(t.getPrefString("url"), t.getPrefString("serverDebug"), l);
	}
}
