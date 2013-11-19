package org.engine.property;

import org.engine.property.Property.Flag;
import org.engine.utils.Array;
import org.engine.utils.ArrayMap;

public class Information {

	public String id;
	public String tag;
	public String content;
	public Flag flag;

	public Information(String id, String tag, Flag flag, String content) {

		this.id = id;
		this.tag = tag;
		this.content = content;
		this.flag = flag;
	}

	public String toString() {

		return Information.InformationToString(this);
	}

	public static char FlagToString(Flag f) {

		if (f == Flag.NONE) {
			return '0';
		}
		if (f == Flag.UPDATE) {
			return '+';
		}
		if (f == Flag.REMOVE) {
			return '-';
		}
		return ' ';
	}

	public static Flag StringToFlag(String s) {
		if (s.startsWith("0")) {
			return Flag.NONE;
		}
		if (s.startsWith("+")) {
			return Flag.UPDATE;
		}
		if (s.startsWith("-")) {
			return Flag.REMOVE;
		}
		return null;
	}

	public static String PropertyToString(Property<?> p) {

		return InformationToString(p.info());
	}

	public static String PropertiesToString(Array<Property<?>> ap) {

		return InformationsToString(PropertiesToInformations(ap));
	}
	
	public static String PropertiesToStringFlagOnly(Array<Property<?>> ap) {

		return InformationsToString(PropertiesToInformationsFlagOnly(ap));
	}

	public static Array<Information> PropertiesToInformations(
			Array<Property<?>> ap) {

		if (ap != null) {

			Array<Information> ai = new Array<Information>(ap.getSize());
			for (Property<?> p : ap) {

				ai.add(p.info());
			}
			return ai;
		}
		return null;
	}
	
	public static Array<Information> PropertiesToInformationsFlagOnly(
			Array<Property<?>> ap) {

		if (ap != null) {

			Array<Information> ai = new Array<Information>(ap.getSize());
			for (Property<?> p : ap) {

				ai.add(p.infoFlagOnly());
			}
			return ai;
		}
		return null;
	}

	public static String InformationToString(Information i) {

		if (i != null) {

			// <TAG>ID<:TAG<>FLAG>CONTENT</TAG>
			return "<" + i.tag + ">" + i.id + "<:" + i.tag + "<>"
					+ Information.FlagToString(i.flag) + ">" + i.content + "</"
					+ i.tag + ">";
		}
		return null;
	}

	public static String ReadableInfoString(Array<Information> a) {
		
		String s = "";
		for (Information i : a) {
			s+= ReadableInfoString(i, 0);
		}
		return s;
	}
	
	public static String ReadableInfoString(Information i, int level) {

		String s = "\n";
		for (int j = 0; j < level; j++) {
			s += "    ";
		}
		s += "<"+ FlagToString(i.flag) + i.id + ": ";
		try {
			s += "{";
			for (Information subInfo : StringToInformations(i.content)) {
				s += ReadableInfoString(subInfo, level + 1);
			}
			s += "\n";
			for (int j = 0; j < level; j++) {
				s += "    ";
			}
			s += "}>";
		} catch (Exception e) {
			s = s.substring(0, s.length() - 1);
			s += i.content + ">";
		}
		return s;
	}

	public static String InformationsToString(Array<Information> array) {

		if (array != null) {

			// sort Array according to tag
			ArrayMap<Information, String> sortedInfo = new ArrayMap<Information, String>(
					array.getSize());
			for (Information i : array) {

				sortedInfo.put(i, i.tag);
			}
			sortedInfo.sortToValues();
			// shorten String (not XML)
			String s = "";
			String lastTag = null;
			for (Information i : sortedInfo.keys()) {

				if (lastTag != null && i.tag.equals(lastTag)) {

					s = s.substring(0, s.lastIndexOf("</" + i.tag + ">"));
				}
				s += "<" + i.tag + ">" + i.id + "<:" + i.tag + "<>"
						+ Information.FlagToString(i.flag) + ">" + i.content
						+ "</" + i.tag + ">";
				lastTag = i.tag;
			}
			return s;
		}
		return null;
	}

	public static Array<Information> StringToInformations(String input)
			throws InformationArrayStringException {

		if (input != null && input.contains("<") && input.contains(">")) {
			String tag = null;
			String start = null;
			String sep = null;
			String end = null;
			String id = null;
			Flag flag = null;
			String content = null;
			Array<Information> a = new Array<Information>();
			try {

				tag = input.substring(input.indexOf("<") + 1,
						input.indexOf(">"));
				start = "<" + tag + ">";
				sep = "<:" + tag + "<>";
				end = "</" + tag + ">";

				for (String strBlock : input.split(end)) {

					if (strBlock.contains(start)) {

						String cutOUT = strBlock.substring(0,
								strBlock.indexOf(start));
						if (!cutOUT.equals("")) {
							throw new InformationArrayStringException(input
									+ " CUTOUT: " + cutOUT);
						}
						strBlock = strBlock.substring(strBlock.indexOf(start));
						for (String info : strBlock.split(start)) {

							if (info.contains(sep)) {

								id = info.substring(0, info.indexOf(sep));
								flag = Information.StringToFlag(info.substring(
										info.indexOf(sep) + sep.length(),
										info.indexOf(sep) + sep.length() + 1));
								content = info.substring(info.indexOf(">",
										info.indexOf(sep) + sep.length()) + 1);
								a.add(new Information(id, tag, flag, content));

							} else {

								if (!info.equals("")) {

									throw new InformationArrayStringException(
											info
													+ " Information String not containing separation tag ("
													+ sep + ")");
								}
							}
						}

					} else {

						if (!strBlock.equals("")) {

							throw new InformationArrayStringException(
									strBlock
											+ "Information String not containing start tag ("
											+ start + ")");
						}
					}
				}
				return a;

			} catch (Exception e) {

				throw new InformationArrayStringException(input);
			}
		}
		return null;
	}
}
