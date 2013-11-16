package org.engine;

import org.engine.gui.TransformGUI.TransformView;
import org.engine.property.Information;
import org.engine.property.Property;
import org.engine.utils.Array;

public class Player {

	public static final String PLAYER_TAG = "PL";

	public String id = PLAYER_TAG;
	public String nickname = PLAYER_TAG;
	public String avatar = PLAYER_TAG;
	public TransformView view = new TransformView();

	public String toString() {

		return id + ";" + nickname + ";" + avatar + ";" + view.toString();
	}

	public void fromString(String s) {

		String[] str = s.split(";");
		id = str[0];
		nickname = str[1];
		avatar = str[2];
		view.fromString(str[3]);
	}

	public static class PlayerArrayProperty implements
			Property<Array<Player>> {

		private Array<Player> value;
		private Information i;
		
		public PlayerArrayProperty(String id, String tag, Flag f,
				Array<Player> value) {

			this.value = value;
			i = new Information(id, tag, f, "");
			i.content = getInfoString();
		}

		private String getInfoString() {

			String s = "";
			String tag = "<" + i.tag + ">";
			for (Player p : value) {
				s += p.toString() + tag;
			}
			return s;
		}

		public Information info() {
			i.content = value.toString();
			return i;
		}

		public void applySyncInfo(Information i) {
			if (i.id.equals(this.i.id)) {

				value.clear();
				for (String s : i.content.split("<" + i.tag + ">")) {
					Player p = new Player();
					p.fromString(s);
					value.add(p);
				}
				this.i.tag = i.tag;
				this.i.content = i.content;
			}
		}

		public Player getByID(String id) {

			for (Player p : value) {
				if (p.id.equals(id)) {
					return p;
				}
			}
			return null;
		}

		public void setByID(String id, Player p) {

			for (Player pl : value) {
				if (pl.id.equals(id)) {
					pl.fromString(p.toString());
				}
			}
			i.content = getInfoString();
		}

		public boolean contains(String id) {

			for (Player pl : value) {
				if (pl.id.equals(id)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void set(Array<Player> value) {

			this.value = value;
			i.content = getInfoString();
		}

		@Override
		public Array<Player> get() {

			return value;
		}

		@Override
		public Flag flag() {

			return i.flag;
		}

		@Override
		public void setFlag(Flag f) {
			
			i.flag = f;
		}
	}
}
