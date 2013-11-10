package org.engine.network;

import org.engine.network.Network.NetworkListener;
import org.engine.utils.Array;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;

public class Server {

	public static class Slot {

		public String id;
		public String name;
		public boolean passwordProtected;
		public int slot;

		public Slot(final int slot, final boolean passwordProtected,
				final String name, final String id) {

			this.slot = slot;
			this.passwordProtected = passwordProtected;
			this.name = name;
			this.id = id;
		}

		@Override
		public String toString() {

			String s = "[ " + slot + " ] " + name;
			if (passwordProtected) {
				s += " Password Required";
			}
			return s;
		}
	}

	public static final String EMPTY_SLOT = "empty";

	public static final String INFO_ACTION = "action=info";

	public static final String KEY_ACTION = "action=key";

	public static final String RECEIVE_ACTION = "action=get";

	public static final String SEND_ACTION = "action=set";

	public static final String SERVER_END = "</syncserver>";

	public static final String SERVER_START = "<syncserver>";

	public static final String TABLE_ACTION = "action=table";

	public static final String TIMESTAMP_ACTION = "action=timestamp";

	public static final String DEBUG = "<DEBUG>";

	public static String checkServerOutputString(String input) throws Exception {

		if (input.contains(Server.SERVER_START)
				&& input.contains(Server.SERVER_END)) {
			input = input.substring(input.indexOf(Server.SERVER_START)
					+ Server.SERVER_START.length(),
					input.indexOf(Server.SERVER_END));
			if (input.startsWith("#")) {
				throw new Exception(input);
			}

			return input;
		} else {

			throw new Exception(
					"Could not find markers for encapsulating string: "
							+ Server.SERVER_START + ", " + Server.SERVER_END
							+ " -> " + input);
		}
	}

	/**
	 * Check sql string.
	 * 
	 * @param input
	 *            the input
	 * @return the string
	 */
	public static String checkSQLString(String input) {

		// See code of php page -> all requests are put in single quotation
		// marks, therefore these are illegal in the content of a request.
		// Remove them. Somehow the '&' sign also produced errors... Dunno why

		input = input.replace("'", "\"");
		return input.replace("&", "");
	}

	public static void requestInfo(final String url, final NetworkListener listener) {

		// System.out.print("Info...");
		Server.sendHTTP(url, Server.INFO_ACTION, listener, false);

	}

	public static boolean isSQLValid(final String input) {

		// See above, can be used to
		if (input.contains("'") || input.contains("&")) {
			return false;
		}
		return true;
	}

	public static void requestKey(final String url, final String key, final int keyID,
			final NetworkListener listener) {

		// System.out.print("Checking Key...");
		Server.sendHTTP(url, Server.KEY_ACTION + "&key=" + key + "&keyid="
				+ keyID, listener, false);
	}

	public static void requestReceive(final String url, final int slot,
			final String password, final String id, final NetworkListener listener) {

		// System.out.print("Receiving...");
		Server.sendHTTP(url, Server.RECEIVE_ACTION + "&table=" + slot + "&pw="
				+ password + "&id=" + id, listener, false);
	}

	public static void requestSend(final String url, final int slot,
			final String name, final String password, final String id,
			final String content, final NetworkListener listener) {

		final String submitDataString = Server.checkSQLString(content);
		if (submitDataString.equals(content)) {
			// System.out.print("Sending...");
			Server.sendHTTP(url, Server.SEND_ACTION + "&table=" + slot
					+ "&name=" + name + "&pw=" + password + "&id=" + id
					+ "&value=" + Server.SERVER_START + submitDataString
					+ Server.SERVER_END, listener, false);
		} else {
			//
		}
	}

	private static void sendHTTP(final String url, final String content,
			final NetworkListener listener, final boolean debug) {

		final HttpRequest request = new HttpRequest(HttpMethods.POST);
		request.setUrl(url);
		request.setContent(content);
		request.setTimeOut(5000);
		String s = content;
		if (s.contains("&")) {
			s = s.substring(s.indexOf("&"));
		}
		final String action = s;
		Gdx.net.sendHttpRequest(request, new HttpResponseListener() {

			@Override
			public void failed(final Throwable t) {
				listener.error(t, action);
			}

			@Override
			public void handleHttpResponse(final HttpResponse httpResponse) {

				try {
					String response = httpResponse.getResultAsString();
					if (debug) {
						listener.debug(response);
					} else {
						response = Server.checkServerOutputString(response);

						if (content.startsWith(Server.TIMESTAMP_ACTION)) {
							if (!response.equals(Server.EMPTY_SLOT)) {
								final long lastTimeStamp = Long
										.parseLong(response);
								listener.timestamp(lastTimeStamp);
							} else {
								listener.timestamp(0);
							}
						}

						if (content.startsWith(Server.INFO_ACTION)) {

							final Array<Slot> slotArray = new Array<Server.Slot>();

							response = response.substring(response
									.indexOf("<slot"));
							response = response.substring(0,
									response.indexOf("</info"));

							final String[] s = response.split("</slot>");
							for (String str : s) {
								str = str.substring(str.indexOf("slot=") + 5);
								final int slot = Integer.parseInt(str
										.substring(0, str.indexOf(" ")));
								str = str.substring(str.indexOf("pw=") + 3);
								final boolean pw = Boolean.parseBoolean(str
										.substring(0, str.indexOf(" ")));
								str = str.substring(str.indexOf("id=") + 3);
								final String id = str.substring(0,
										str.indexOf(">"));
								str = str.substring(str.indexOf(">") + 1);
								slotArray.add(new Slot(slot, pw, id, str));
							}
							slotArray.shrink();
							listener.info(slotArray);
						}

						if (content.startsWith(Server.TABLE_ACTION)) {

						}
						if (content.startsWith(Server.KEY_ACTION)) {
							boolean validKey = false;
							if (response.startsWith("1")) {
								validKey = true;
							}
							listener.key(validKey);
						}

						if (content.startsWith(Server.RECEIVE_ACTION)) {
							long lastTimeStamp = 0;
							if (!response.startsWith(Server.EMPTY_SLOT)) {
								lastTimeStamp = Long.parseLong(response
										.substring(0, response.indexOf("-")));
							}
							response = response.substring(response.indexOf("-") + 1);
							listener.receive(lastTimeStamp, response);
						}

						if (content.startsWith(Server.SEND_ACTION)) {

							final long lastTimeStamp = Long.parseLong(response
									.substring(0, response.indexOf("-")));
							response = response.substring(response.indexOf("-") + 1);
							boolean sendSuccess = false;
							if (response.startsWith("1")) {
								sendSuccess = true;
							}
							listener.send(lastTimeStamp, sendSuccess);
						}
					}
				} catch (final Exception e) {
					listener.error(e, action);
				}
			}
		});
	}

	public static void requestTable(final String url, final NetworkListener listener) {

		// System.out.print("Table...");
		Server.sendHTTP(url, Server.TABLE_ACTION, listener, false);

	}

	public static void requestTimeStamp(final String url, final int slot,
			final String password, final String id, final NetworkListener listener) {

		// System.out.print("Checking Last Update TimeStamp...");
		Server.sendHTTP(url, Server.TIMESTAMP_ACTION + "&table=" + slot
				+ "&pw=" + password + "&id=" + id, listener, false);

	}

	public static void requestDebug(final String url, final String content,
			final NetworkListener listener) {

		Server.sendHTTP(url, content, listener, true);
	}
}