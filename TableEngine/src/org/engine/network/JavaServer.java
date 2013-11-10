package org.engine.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.engine.network.Network.NetworkListener;
import org.engine.network.Server.Slot;
import org.engine.utils.Array;

// Java corresponder class that can interact with the adjacent 
public class JavaServer {

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

	public static void info(final String url, final NetworkListener listener) {

		// System.out.print("Info...");
		JavaServer.sendHTTP(url, Server.INFO_ACTION, listener, false);

	}

	public static boolean isSQLValid(final String input) {

		// See above, can be used to
		if (input.contains("'") || input.contains("&")) {
			return false;
		}
		return true;
	}

	public static void key(final String url, final String key, final int keyID,
			final NetworkListener listener) {

		// System.out.print("Checking Key...");
		JavaServer.sendHTTP(url, Server.KEY_ACTION + "&key=" + key + "&keyid="
				+ keyID, listener, false);
	}

	public static void receive(final String url, final int slot,
			final String password, final String id, final NetworkListener listener) {

		// System.out.print("Receiving...");
		JavaServer.sendHTTP(url, Server.RECEIVE_ACTION + "&table=" + slot
				+ "&pw=" + password + "&id=" + id, listener, false);
	}

	public static void send(final String url, final int slot,
			final String name, final String password, final String id,
			final String content, final NetworkListener listener) {

		final String submitDataString = Server.checkSQLString(content);
		if (submitDataString.equals(content)) {
			// System.out.print("Sending...");
			JavaServer.sendHTTP(url, Server.SEND_ACTION + "&table=" + slot
					+ "&name=" + name + "&pw=" + password + "&id=" + id
					+ "&value=" + Server.SERVER_START + submitDataString
					+ Server.SERVER_END, listener, false);
		} else {
			//
		}
	}

	private static void sendHTTP(final String url, final String content,
			final NetworkListener listener, final boolean debug) {

		String s = content;
		if (s.contains("&")) {
			s = s.substring(s.indexOf("&"));
		}
		final String action = s;

		new Thread() {

			public void run() {

				try {
					HttpURLConnection request = (HttpURLConnection) new URL(url)
							.openConnection();

					request.setRequestMethod("POST");
					request.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					request.setRequestProperty("charset", "utf-8");
					request.setRequestProperty("Content-Length",
							"" + Integer.toString(content.getBytes().length));
					request.setConnectTimeout(5000);
					request.setDoOutput(true);
					OutputStreamWriter osw = new OutputStreamWriter(
							request.getOutputStream());
					osw.write(content);
					osw.flush();
					osw.close();

					BufferedReader br = new BufferedReader(
							new InputStreamReader(request.getInputStream()));
					String line = br.readLine();
					String response = "";
					while (line != null) {

						response += line + "\n";
						line = br.readLine();
					}
					br.close();

					try {

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
									str = str
											.substring(str.indexOf("slot=") + 5);
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
									lastTimeStamp = Long
											.parseLong(response.substring(0,
													response.indexOf("-")));
								}
								response = response.substring(response
										.indexOf("-") + 1);
								listener.receive(lastTimeStamp, response);
							}

							if (content.startsWith(Server.SEND_ACTION)) {

								final long lastTimeStamp = Long
										.parseLong(response.substring(0,
												response.indexOf("-")));
								response = response.substring(response
										.indexOf("-") + 1);
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

				} catch (Exception e) {
					listener.error(e, action);
				}
			};
		}.start();
	}

	public static void table(final String url, final NetworkListener listener) {

		// System.out.print("Table...");
		JavaServer.sendHTTP(url, Server.TABLE_ACTION, listener, false);

	}

	public static void timestamp(final String url, final int slot,
			final String password, final String id, final NetworkListener listener) {

		// System.out.print("Checking Last Update TimeStamp...");
		JavaServer.sendHTTP(url, Server.TIMESTAMP_ACTION + "&table=" + slot
				+ "&pw=" + password + "&id=" + id, listener, false);

	}

	public static void debug(final String url, final String content,
			final NetworkListener listener) {

		JavaServer.sendHTTP(url, content, listener, true);
	}
}
