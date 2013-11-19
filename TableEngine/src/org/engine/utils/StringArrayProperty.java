package org.engine.utils;
import org.engine.property.Information;
import org.engine.property.Property;

public class StringArrayProperty implements Property<Array<String>> {

		private Array<String> value;
		private Information i;

		public StringArrayProperty(String id, String tag, Flag f, Array<String> value) {

			this.value = value;
			i = new Information(id, tag, f, "");
			i.content = getInfoStr();
		}

		public Information info() {

			i.content = getInfoStr();
			return i;
		}

		public void applySyncInfo(Information i) {

			if (i.id.equals(this.i.id)) {

				String tag = "<" + i.tag + ">";
				String[] s = i.content.split(tag);
				value.clear();
				for (String str : s) {

					value.add(str);
				}
				this.i.tag = i.tag;
				this.i.content = i.content;

			}
		}

		public Array<String> get() {

			return value;
		}
		
		public String getString(int index) {
			
			return value.get(index);
		}
		
		public int getIndex(String str) {
			
			for (int i = 0; i < value.getSize(); i++) {
				
				if (str.equals(value.get(i))) {
					return i;
				}
			}
			return -1;
		}

		public void set(Array<String> value) {
			this.value.clear();
			this.value.addAll(value);
			i.content = getInfoStr();
		}
		
		public void setString(int index, String str) {
			
			value.set(index, str);
			i.content = getInfoStr();
		}

		private String getInfoStr() {

			String s = "";
			String tag = "<" + i.tag + ">";
			for (String str : value) {

				s += str + tag;
			}
			if (s.contains(tag)) {
				s = s.substring(0, s.lastIndexOf(tag));
			}
			return s;
		}

		@Override
		public Flag flag() {

			return i.flag;
		}

		@Override
		public void setFlag(Flag f) {

			i.flag = f;
		}

		@Override
		public Information infoFlagOnly() {

			return info();
		}
	}