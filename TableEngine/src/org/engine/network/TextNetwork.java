package org.engine.network;

import org.engine.TableEngine;
import org.engine.network.Server.Slot;
import org.engine.property.InformationArrayStringException;
import org.engine.utils.Array;

import com.badlogic.gdx.Gdx;

public class TextNetwork implements Network {

	private TableEngine t;
	private long lastTS = 0;
	private Array<String> currentTS = new Array<String>(10, 10);

	public TextNetwork() {
		
	}

	@Override
	public void requestInfo(NetworkListener l) {

		Array<Slot> slots = new Array<Slot>();
		for (int i = 1; i <= 12; i++) {
			slots.add(new Slot(i, false, "Test-Slot " + i, "id" + i));
		}
		l.info(slots);
	}

	@Override
	public void requestKey(NetworkListener l) {

		l.key(true);
	}

	@Override
	public void requestReceive(NetworkListener l) {
		
		
		String s = readFromFile("0.txt" );
		Long lastTimeStamp = Long
				.parseLong(s.substring(0, s.indexOf("<TEST>")));
		String response = s.substring(s.indexOf("<TEST>") + 6);
		l.receive(lastTimeStamp, response);
	}

	@Override
	public void requestSend(NetworkListener l) {

		System.out.println("SEND");
	}

	private Array<String> getIDofBottomProperty(Array<String> a, String id,
			String s) {

		System.out.println("getIDofBottom: " + a + " " + id + " " + s);
		String tag = null;
		String start = null;
		String sep = null;
		String end = null;
		String newID = null;
		String content = null;
		try {
			tag = s.substring(s.indexOf("<") + 1, s.indexOf(">"));
			start = "<" + tag + ">";
			sep = "<:" + tag + "<>";
			end = "</" + tag + ">";
			for (String strBlock : s.split(end)) {

				if (strBlock.contains(start)) {

					String cutOUT = strBlock.substring(0,
							strBlock.indexOf(start));
					if (!cutOUT.equals("")) {
						throw new InformationArrayStringException(s
								+ " CUTOUT: " + cutOUT);
					}
					strBlock = strBlock.substring(strBlock.indexOf(start));
					for (String info : strBlock.split(start)) {

						if (info.contains(sep)) {

							newID = info.substring(0, info.indexOf(sep));
							content = info.substring(info.indexOf(">",
									info.indexOf(sep) + sep.length()) + 1);
							if (content.contains("<") && content.contains(">")) {
								a.addAll(getIDofBottomProperty(a, id + "."
										+ newID, content));
								return a;
							} else {
								a.add(id);
								return a;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void requestTimestamp(NetworkListener l) {

		l.timestamp(lastTS);
	}

	@Override
	public void requestDebug(NetworkListener l) {

		l.debug("TEXTNETWORK");
	}

	public static void writeToFile(String fileName, String s) {

		Gdx.files.external(fileName).writeString(s, false);
	}

	public static String readFromFile(String fileName) {

		return Gdx.files.external(fileName).readString();
	}

	@Override
	public void initialize(TableEngine t) {

		this.t = t;
	}

}
