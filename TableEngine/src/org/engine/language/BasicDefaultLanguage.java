package org.engine.language;

import java.util.Locale;

public class BasicDefaultLanguage extends Language {

	public static final String actualise = "actualise";
	public static final String button = "button";

	public static final String card = "card";
	public static final String cardPlayerOnly = "cardPlayerOnly";
	public static final String cardRemove = "cardRemove";
	public static final String cardSide = "cardSide";
	public static final String cardSide0 = "cardSide0";
	public static final String cardSide1 = "cardSide1";
	public static final String cardSideChat = "cardSideChat";
	public static final String cardTap = "cardTap";
	public static final String coinFlip = "coinFlip";
	public static final String coinFlipChat1 = "coinFlipChat1";
	public static final String coinFlipChat2 = "coinFlipChat2";
	public static final String counter = "counter";
	public static final String counterError = "counterError";
	public static final String counterFactory = "counterFactory";
	public static final String counterRemove = "counterRemove";
	public static final String counterSet = "counterSet";
	public static final String counterSetMessage = "counterSetMessage";
	public static final String deck = "deck";
	public static final String deckBottommost = "deckBottommost";
	public static final String deckCard = "deckCard";
	public static final String deckDrawNo = "deckDrawNo";
	public static final String deckGlimpse = "deckGlimpse";
	public static final String deckShuffle = "deckShuffle";
	public static final String deckTopmost = "deckTopmost";
	public static final String descriptionID = "descriptionID";
	public static final String diceArea = "diceArea";
	public static final String diceAreaText = "diceAreaText";
	public static final String dieRoll = "dieRoll";
	public static final String dieRollChat = "dieRollChat";
	public static final String game = "game";
	public static final String gameExit = "gameExit";
	public static final String gameLoad = "gameLoad";
	public static final String gameNew = "gameNew";
	public static final String gameSave = "gameSave";
	public static final String gameSettings = "gameSettings";
	public static final String language = "language";
	public static final String loading = "loading";
	public static final String localeCountry = "localeCountry";
	public static final String localeLanguage = "localeLanguage";
	public static final String menu = "menu";
	public static final String no = "no";
	public static final String offlineClick = "offlineClick";
	public static final String offlineEnableSpectator = "offlineEnableSpectator";
	public static final String offlineEnableSupervisor = "offlineEnableSupervisor";
	public static final String offlineNextPlayer = "offlineNextPlayer";
	public static final String offlineNextPlayerStartCycle = "offlineNextPlayerStartCycle";
	public static final String offlineSetPlayerNumber = "offlineSetPlayerNumber";
	public static final String offlineSpectator = "offlineSpectator";
	public static final String offlineStart = "offlineStart";
	public static final String offlineSupervisor = "offlineSupervisor";
	public static final String offlineURL = "offlineURL";
	public static final String optionChat = "optionChat";
	public static final String optionReturn = "optionReturn";
	public static final String passwordRequired = "passwordRequired";
	public static final String player = "player";
	public static final String playerName = "playerName";
	public static final String playerPosition = "playerPosition";
	/**
	 * 1 = Initiation
	 */
	private static final long serialVersionUID = 1L;
	public static final String server = "server";
	public static final String serverChangeURL = "serverChangeURL";
	public static final String serverConnect = "serverConnect";
	public static final String serverError = "serverError";
	public static final String serverLoading = "serverLoading";
	public static final String serverOffline = "serverOffline";
	public static final String serverOnline = "serverOnline";
	public static final String serverPassword = "serverPassword";
	public static final String serverSelect = "serverSelect";
	public static final String serverTitle = "serverTitle";
	public static final String serverURLmalformed = "serverURLmalformed";
	public static final String settings = "settings";
	public static final String settingsFullScreen = "settingsFullScreen";
	public static final String setViewChangeName = "setViewChangeName";
	public static final String setViewChangeNameChat1 = "setViewChangeNameChat1";
	public static final String setViewChangeNameChat2 = "setViewChangeNameChat2";
	public static final String setViewFree = "setViewFree";
	public static final String setViewFreeChat1 = "setViewFreeChat1";
	public static final String setViewFreeChat2 = "setViewFreeChat2";
	public static final String setViewOccupy = "setViewOccupy";
	public static final String setViewOccupyChat = "setViewOccupyChat";
	public static final String stackEmpty = "stackEmpty";
	public static final String timemachineAsk = "timemachineAsk";
	public static final String timemachineTitle = "timemachineTitle";
	public static final String token = "token";
	public static final String tokenFactory = "tokenFactory";
	public static final String tokenRemove = "tokenRemove";
	public static final String tokenSet = "tokenSet";
	public static final String tokenSetMessage = "tokenSetMessage";
	public static final String yes = "yes";

	public BasicDefaultLanguage() {

		locale = Locale.ENGLISH;
		// Enter default
		put("actualise", "Actualise");
		put(BasicDefaultLanguage.loading, "Loading...");
		put(BasicDefaultLanguage.serverSelect, "Select Mode");
		put(BasicDefaultLanguage.descriptionID, "basic");
		put(BasicDefaultLanguage.localeLanguage, "en");
		put(BasicDefaultLanguage.localeCountry, "EN");
		put(BasicDefaultLanguage.settings, "Settings");
		put(BasicDefaultLanguage.settingsFullScreen, "Switch fullscreen");
		put(BasicDefaultLanguage.menu, "Menu");
		put(BasicDefaultLanguage.game, "Game Name");
		put(BasicDefaultLanguage.gameSettings, "Game");
		put(BasicDefaultLanguage.gameExit, "Exit");
		put(BasicDefaultLanguage.gameNew, "New game");
		put(BasicDefaultLanguage.gameSave, "Save game to file");
		put(BasicDefaultLanguage.gameLoad, "Load game from file");
		put(BasicDefaultLanguage.language, "Language");
		put(BasicDefaultLanguage.server, "Server");
		put(BasicDefaultLanguage.serverLoading, "Loading...");
		put(BasicDefaultLanguage.serverTitle, "Join game or create new Server");
		put(BasicDefaultLanguage.serverPassword, "Password");
		put(BasicDefaultLanguage.serverConnect, "Connect to server");
		put(BasicDefaultLanguage.serverError,
				"Could not connect to server. Please enter new server URL:");
		put(BasicDefaultLanguage.serverURLmalformed,
				"This URL is not well formed (e.g. http://webserver.provider.com/syncserver)");
		put(BasicDefaultLanguage.serverOffline, "Play offline");
		put(BasicDefaultLanguage.serverOnline, "Play online");
		put(BasicDefaultLanguage.serverChangeURL, "Change server/URL");
		put(BasicDefaultLanguage.offlineSetPlayerNumber,
				"Spieleranzahl festlegen (für Rotation)");
		put(BasicDefaultLanguage.offlineStart, "Start game");
		put(BasicDefaultLanguage.offlineEnableSpectator,
				"Show Spectator at end of rotation cycle (Cannot see hand cards)");
		put(BasicDefaultLanguage.offlineEnableSupervisor,
				"Show Supervisor at end of rotation cycle (Can see all cards)");
		put(BasicDefaultLanguage.offlineNextPlayer, "Switch to next player:");
		put(BasicDefaultLanguage.offlineNextPlayerStartCycle,
				"Start player rotation cycle");
		put(BasicDefaultLanguage.offlineClick,
				"Doubleclick to witch to next player");
		put(BasicDefaultLanguage.offlineSpectator, "Spectator");
		put(BasicDefaultLanguage.offlineSupervisor, "Supervisor");
		put(BasicDefaultLanguage.offlineURL, "http://enter.server.url");
		put(BasicDefaultLanguage.player, "Player");
		put(BasicDefaultLanguage.playerName, "Nickname");
		put(BasicDefaultLanguage.playerPosition, "Position");
		put(BasicDefaultLanguage.stackEmpty, "Bin");
		put(BasicDefaultLanguage.deck, "Deck");
		put(BasicDefaultLanguage.deckDrawNo,
				"Draw a card according to position");
		put(BasicDefaultLanguage.deckCard, "Card");
		put(BasicDefaultLanguage.deckTopmost, "(Topmost card)");
		put(BasicDefaultLanguage.deckBottommost, "(Bottommost card)");
		put(BasicDefaultLanguage.deckGlimpse, "Take a glimpse");
		put(BasicDefaultLanguage.deckShuffle, "Shuffle");
		put(BasicDefaultLanguage.card, "Card");
		put(BasicDefaultLanguage.cardTap, "Tap/Untap");
		put(BasicDefaultLanguage.cardSide, "Turn this card");
		put(BasicDefaultLanguage.cardSideChat, "turned a card to");
		put(BasicDefaultLanguage.cardSide0, "the backside");
		put(BasicDefaultLanguage.cardSide1, "the frontside");
		put(BasicDefaultLanguage.cardPlayerOnly,
				"Only participating players may do this");
		put(BasicDefaultLanguage.cardRemove, "Remove card from game");
		put(BasicDefaultLanguage.setViewOccupy, "Occupy this place");
		put(BasicDefaultLanguage.setViewOccupyChat, "occupied place no: ");
		put(BasicDefaultLanguage.setViewFree, "Free this place");
		put(BasicDefaultLanguage.setViewFreeChat1, "set free place no:");
		put(BasicDefaultLanguage.setViewFreeChat2, "kicking away");
		put(BasicDefaultLanguage.setViewChangeName, "Change name");
		put(BasicDefaultLanguage.setViewChangeNameChat1, "changed the name of");
		put(BasicDefaultLanguage.setViewChangeNameChat2, "to");
		put(BasicDefaultLanguage.dieRoll, "Roll this die");
		put(BasicDefaultLanguage.dieRollChat, "rolled a die: ");
		put(BasicDefaultLanguage.diceArea, "Roll all dice in area");
		put(BasicDefaultLanguage.diceAreaText, "rolled some dice:");
		put(BasicDefaultLanguage.coinFlip, "Flip");
		put(BasicDefaultLanguage.coinFlipChat1, "flipped a coin: Heads");
		put(BasicDefaultLanguage.coinFlipChat2, "flipped a coin: Tails");
		put(BasicDefaultLanguage.counter, "Counter");
		put(BasicDefaultLanguage.counterSet, "Set number");
		put(BasicDefaultLanguage.counterSetMessage, "Please enter new amount:");
		put(BasicDefaultLanguage.counterError,
				"Unable to parse number from input:");
		put(BasicDefaultLanguage.counterRemove, "Remove");
		put(BasicDefaultLanguage.button, "Click Me");
		put(BasicDefaultLanguage.token, "Token");
		put(BasicDefaultLanguage.tokenSet, "Set caption");
		put(BasicDefaultLanguage.tokenSetMessage,
				"Please enter caption for token:");
		put(BasicDefaultLanguage.tokenRemove, "Remove");
		put(BasicDefaultLanguage.tokenFactory, "Create token");
		put(BasicDefaultLanguage.counterFactory, "Create counter");
		put(BasicDefaultLanguage.timemachineAsk, "Reset table to state at:");
		put(BasicDefaultLanguage.timemachineTitle, "Reset table?");
		put(BasicDefaultLanguage.passwordRequired, "Password required");
		put(BasicDefaultLanguage.optionChat, "Show/Hide Chat");
		put(BasicDefaultLanguage.optionReturn, "Return");
		put(BasicDefaultLanguage.yes, "Yes");
		put(BasicDefaultLanguage.no, "No");
	}

}
