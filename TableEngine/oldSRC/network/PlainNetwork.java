package org.engine.network;

import java.io.BufferedReader;
import java.io.IOException;

import org.engine.Layer;
import org.engine.Synchronizable;
import org.engine.Universe;
import org.engine.language.BasicDefaultLanguage;
import org.engine.network.Server.HttpListener;
import org.engine.network.Server.Slot;
import org.engine.resource.BasicResource;
import org.engine.utils.Array;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * The Class PlainNetwork. Computes the interaction via internet. The server
 * uses slots for storing information of multiple games at the same time. Every
 * slot contains a timestamp (with it's last update and updater's name) and all
 * information to setup a completely game stored in a String (is obtained by the
 * getAllSynchronizedLayers() function by Universe and is password secured.
 * 
 * So to establish a connection one needs the following: URL of server, slot
 * number, name and password. To obtain this info the following routine takes
 * place: Upon initialisation this class reads the server URL from file and
 * shows a selection screen (offline or online). If the player chooses offline
 * play, no network is set up. In the other case the server is called for
 * information about it's slot occupation. Upon receiving it, the class shows a
 * screen for selecting the slot and entering the name and password.
 * 
 * The connection is then set up. If the slot was empty, it is occupied by this
 * game. If it was occupied before, the current game is downloaded and shown in
 * Universe.
 * 
 * To maintain synchronisation two Threads are started upon connection:
 * 
 * 1. Network Execution Thread. Executes all commands on the networks stack.
 * (When an object wants to receive or send information to the server it's
 * request is put on a stack)
 * 
 * 2. Network Control Thread. Puts a Receive Command on stack every second
 * (Changeable). This thread is inactive when going into manual mode. Then the
 * player has to click a button to receive an update from the server (saves much
 * more internet bandwidth)
 * 
 * The usual line of action is: 1. show screen to select online or offline mode.
 * 2. If ONLINE -> download info, show list of slots, let user select slot. 3.
 * start online mode, start threads, call a receive command: First, call
 * TimeStamp
 */
public class PlainNetwork implements Network, HttpListener {

	public class NetworkControlThread extends Thread {

		private final static int rescueLoopTime = 1;
		private final PlainNetwork parentNetwork;
		public boolean putNewReceiveCommand = false;
		public boolean running = false;

		public NetworkControlThread(final PlainNetwork parentNetwork) {

			this.parentNetwork = parentNetwork;
		}

		public boolean isRunning() {

			return running;
		}

		@Override
		public void run() {

			int receiveTryOuts = 0;
			while (isRunning()) {
				try {
					Thread.sleep(waitReceiveSeconds * 1000);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				// if there is some receive command from another object
				// OR 1000 ms have passed and there is no receive command in
				// line
				// waiting -> put receive command in line
				if (putNewReceiveCommand
						|| ((receiveTryOuts > NetworkControlThread.rescueLoopTime) && !parentNetwork.commandQueue
								.contains(Command.Receive, false))) {
					putNewReceiveCommand = false;
					receiveTryOuts = 0;
					commandQueue.add(Command.Receive);
					Gdx.graphics.requestRendering();
				}
				receiveTryOuts++;
			}
		}

		public void setRunning(final boolean running) {

			this.running = running;
			if (running) {
				if (!isAlive()) {
					start();
				}
			}
		}
	}

	class NetworkExecuteThread extends Thread {

		private final PlainNetwork parentNetwork;
		private boolean running = false;

		public NetworkExecuteThread(final PlainNetwork parentNetwork) {

			this.parentNetwork = parentNetwork;

		}

		public boolean isRunning() {

			return running;
		}

		@Override
		public void run() {

			
		}

		public void setRunning(final boolean running) {

			this.running = running;
			if (running) {
				if (!isAlive()) {
					start();
				}
			}
		}
	}

	private static final int LOG_LIMIT = 20;
	public NetworkControlThread commandControlThread;
	public NetworkExecuteThread commandExecutionThread;
	public Array<Command> commandQueue = new Array<Command>(8);
	public Array<String> communication = new Array<String>(20);
	private int connectionReTry = 0;
	private final InterruptionListener interruptionListener = new InterruptionListener();
	private long lastTimeStamp = 0;

	private Mode mode;

	private Universe parentUniverse;
	Array<String> urlList = new Array<String>();

	private int waitReceiveSeconds = 1;

	private final int waitSendingSeconds = 2;

	@Override
	public void clearUpdateQueue() {

		commandQueue.clear();
	}

	private boolean compareLayerToString(final String contentString,
			final Layer layer) {

		final String[] sObjects = contentString.split(Network.INSTANCE_END);
		final Array<String> layerStrings = new Array<String>();
		for (final String h : layer.getModel().createLayerString()
				.split(Network.INSTANCE_END)) {
			layerStrings.add(h);
		}
		for (final String a : sObjects) {

			String found = null;
			for (final String b : layerStrings) {

				if (a.equals(b)) {
					found = b;
					break;
				}
			}
			if (found == null) {
				return false;
			} else {
				layerStrings.removeValue(found, false);
			}
		}
		if (layerStrings.getSize() > 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String correctUserInputForNetworkIncompatibility(String input) {

		// replace '+' and ',' ->
		input = input.replace("+", "</plus");
		input = input.replace(",", "-");

		final Array<String> forbiddenTerm = new Array<String>();
		
		// Add Server_END Sequence -> this interacts with the PHP code - may 
		forbiddenTerm.add(Server.SERVER_END);
		
		// Add SQL characters (See Server.checkSQLString() and Server php file
		// for more information
		forbiddenTerm.add("'");
		forbiddenTerm.add("&");
		for (final String s : forbiddenTerm) {
			input = input.replace(s, "");
		}
		// input = input.replace("+", "<plus>");
		return input.trim();
	}

	@Override
	public void error(final Throwable t, final String action) {

		if (communication.getSize() < PlainNetwork.LOG_LIMIT) {
			communication.add(" ! ERROR : " + t.getLocalizedMessage());
		} else {
			for (int i = 1; i < PlainNetwork.LOG_LIMIT; i++) {
				communication.set(i - 1, communication.get(i));
			}
			communication.set(PlainNetwork.LOG_LIMIT - 1,
					" ! ERROR : " + t.getLocalizedMessage());
		}
		if (connectionReTry > 5) {

			Server.info(parentUniverse.url, this);
			connectionReTry = 0;
		} else {
			connectionReTry++;
		}
	}

	@Override
	public void fireUpdate() {

		if (mode == Mode.OnlineMode) {

			String modelString = "";
			Server.send(parentUniverse.url, parentUniverse.slot,
					parentUniverse.name, parentUniverse.password,
					parentUniverse.gameID, modelString, this);
		}
		parentUniverse
				.getMenu()
				.getTimemachine()
				.addTimePoint(
						parentUniverse.getLayer(Universe.LayerBackGround)
								.getModel().createLayerString());
	}

	@Override
	public Mode getMode() {

		return mode;
	}

	@Override
	public Universe getParentUniverse() {

		return parentUniverse;
	}

	public int getUpdateIntervallSeconds() {

		return waitReceiveSeconds;
	}

	@Override
	public void info(final Array<Slot> slotArray) {

		if (communication.getSize() < PlainNetwork.LOG_LIMIT) {
			communication.add("INFO: " + slotArray);
		} else {
			for (int i = 1; i < PlainNetwork.LOG_LIMIT; i++) {
				communication.set(i - 1, communication.get(i));
			}
			communication.set(PlainNetwork.LOG_LIMIT - 1, "INFO: " + slotArray);
		}
	}

	@Override
	public void initialize(final Universe parentUniverse) {

		this.parentUniverse = parentUniverse;
	}

	@Override
	public void key(final boolean response) {

		if (communication.getSize() < PlainNetwork.LOG_LIMIT) {
			communication.add("KEY: " + response);
		} else {
			for (int i = 1; i < PlainNetwork.LOG_LIMIT; i++) {
				communication.set(i - 1, communication.get(i));
			}
			communication.set(PlainNetwork.LOG_LIMIT - 1, "KEY : " + response);
		}
		if (!response) {
			Gdx.app.exit();
		}
	}

	@Override
	public void receive(final long serverTimeStamp, String response) {

		if (communication.getSize() < PlainNetwork.LOG_LIMIT) {
			communication.add("RECV: " + response);
		} else {
			for (int i = 1; i < PlainNetwork.LOG_LIMIT; i++) {
				communication.set(i - 1, communication.get(i));
			}
			communication.set(PlainNetwork.LOG_LIMIT - 1, "RECV: " + response);
		}
		// check if the server has been empty
		if (!response.equals(Server.EMPTY_SLOT)
				&& response.contains(Network.LAYER_END)) {
			response = response
					.substring(response.indexOf(Network.LAYER_START));
			boolean gotUpdated = false;
			final String[] layerStrings = response.split(Network.LAYER_END);
			for (final String layerString : layerStrings) {
				final String layerLabel = layerString.substring(
						Network.LAYER_START.length(),
						layerString.indexOf(Network.LAYER_MIDDLE));
				final String contentString = layerString.substring(layerString
						.indexOf(Network.LAYER_MIDDLE)
						+ Network.LAYER_MIDDLE.length());
				if (!compareLayerToString(contentString, getParentUniverse()
						.getLayer(layerLabel))) {

					if (!interruptionListener.gotInterrrupted()) {
						gotUpdated = true;
						getParentUniverse().getLayer(layerLabel).getModel()
								.removeAllObjects();
						getParentUniverse().getLayer(layerLabel).getModel()
								.addObjects(contentString);
						if (layerLabel.equals(Universe.LayerBackGround)) {
							getParentUniverse().getMenu().getTimemachine()
									.addTimePoint(contentString);
						}
					}
				}
			}
			if (gotUpdated) {
				getParentUniverse().getSystem().checkAnimations();
				lastTimeStamp = serverTimeStamp;
			}

		} else {
			// Server is completely empty or wrong set up. Save own objects
			// and set it up.
			fireUpdate();
		}
		connectionReTry = 0;
	}

	@Override
	public void receiveUpdate() {

		if (mode == Mode.OnlineMode) {

			Server.timestamp(parentUniverse.url, parentUniverse.slot,
					parentUniverse.password, parentUniverse.gameID, this);
		}
	}

	@Override
	public void send(final long serverTimeStamp, final boolean response) {

		if (response && (lastTimeStamp < serverTimeStamp)) {
			lastTimeStamp = serverTimeStamp;
		}
	}

	@Override
	public void switchMode(final Mode mode) {

		this.mode = mode;
		if (mode == Mode.OfflineMode) {

			commandExecutionThread.setRunning(false);
			commandControlThread.setRunning(false);
			// add Offline Button
		}
		if (mode == Mode.OnlineMode) {

			commandExecutionThread.setRunning(true);
			commandControlThread.setRunning(true);
		}
		if (mode == Mode.ManualMode) {

			commandExecutionThread.setRunning(true);
			commandControlThread.setRunning(false);
			// add Manual Button
		}
	}

	@Override
	public void timestamp(final long serverTimeStamp) {

		if (communication.getSize() < PlainNetwork.LOG_LIMIT) {
			if (communication.getSize() > 0) {
				if (communication.get(communication.getSize() - 1).startsWith(
						"TIME:" + serverTimeStamp)) {
					final String s = communication
							.get(communication.getSize() - 1);
					int i = Integer.parseInt(s.substring(s.indexOf("->") + 2));
					i++;
					communication.set(communication.getSize() - 1, "TIME:"
							+ serverTimeStamp + " ->" + i);
				} else {
					communication.add("TIME:" + serverTimeStamp + " ->0");
				}
			}
		} else {
			if (!communication.get(PlainNetwork.LOG_LIMIT - 1).equals(
					"TIME:" + serverTimeStamp)) {
				for (int i = 1; i < PlainNetwork.LOG_LIMIT; i++) {
					communication.set(i - 1, communication.get(i));
				}
				communication.set(PlainNetwork.LOG_LIMIT - 1, "TIME:"
						+ serverTimeStamp);
			}
		}

		if (serverTimeStamp == 0) {
			fireUpdate();
		} else {
			if ((lastTimeStamp < serverTimeStamp) && !parentUniverse.isBusy()) {
				// Adds an InterruptionListener to the Universe because this
				// class now initiates a RECEIVE command to the server. The
				// communication is done in a different thread. When the
				// RECEIVE command finally returns to PlainNetwork ( see
				// receive() method ), it checks if the
				// Universe turned busy in the meantime, meaning a user
				// interaction has occured while the interaction with the
				// server took place. In this case PlainNetwork will not

				parentUniverse.addBusyListener(interruptionListener);
				Server.receive(parentUniverse.url, parentUniverse.slot,
						parentUniverse.password, parentUniverse.gameID, this);
			} else {
				commandControlThread.putNewReceiveCommand = true;
			}
		}
	}

	@Override
	public void debug(String httpResponse) {
		// TODO Auto-generated method stub

	}
}
