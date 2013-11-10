package org.engine.property;

import org.engine.utils.Array;
import org.engine.utils.ArrayMap;

public class Information {

	public String id;
	public String tag;
	public String content;

	public Information(String id, String tag, String content) {

		this.id = id;
		this.tag = tag;
		this.content = content;
	}
	
	public String toString() {
		
		return Information.InformationToString(this);
	}
	
	public static String PropertyToString(Property<?> p) {
		
		return InformationToString(p.info());
	}
	
	public static String PropertiesToString(Array<Property<?>> ap) {
		
		return InformationsToString(PropertiesToInformations(ap));
	}
	
	public static Array<Information> PropertiesToInformations(Array<Property<?>> ap) {
		
		Array<Information> ai = new Array<Information>(ap.getSize());
		for (Property<?> p : ap) {
			ai.add(p.info());
		}
		return ai;
	}

	public static String InformationToString(Information i) {

		return "<" + i.tag + ">" + i.id + "<:" + i.tag + ">" + i.content
				+ "</" + i.tag + ">";
	}

	public static String ReadableInfoString(Information i, int level) {

		String s = "\n";
		for (int j = 0; j < level; j++) {
			s += "    ";
		}
		s += "<" + i.id + ": ";
		try {
			for (Information subInfo : StringToInformations(i.content)) {
				s+= ReadableInfoString(subInfo, level +1);
			}
		} catch (Exception e) {
			s += i.content + ">";
		}
		return s;
	}

	public static String InformationsToString(Array<Information> array) {

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
			s += "<" + i.tag + ">" + i.id + "<:" + i.tag + ">" + i.content
					+ "</" + i.tag + ">";
		}
		return s;
	}

	public static Information StringToInformation(String input)
			throws InformationStringException {

		String tag = null;
		String start = null;
		String sep = null;
		String end = null;
		String id = null;
		String content = null;
		try {

			tag = input.substring(input.indexOf("<") + 1,
					input.indexOf(">"));
			start = "<" + tag + ">";
			sep = "<:" + tag + ">";
			end = "</" + tag + ">";
			id = input.substring(input.indexOf(start) + start.length(),
					input.indexOf(sep));
			content = input.substring(input.indexOf(sep) + sep.length(),
					input.indexOf(end));
			return new Information(id, tag, content);

		} catch (Exception e) {
			throw new InformationStringException(input);
		}

	}

	public static Array<Information> StringToInformations(String input)
			throws InformationArrayStringException{

		String tag = null;
		String start = null;
		String sep = null;
		String end = null;
		String id = null;
		String content = null;
		Array<Information> a = new Array<Information>();
		try {

			tag = input.substring(input.indexOf("<") + 1,
					input.indexOf(">"));
			start = "<" + tag + ">";
			sep = "<:" + tag + ">";
			end = "</" + tag + ">";

			for (String strBlock : input.split(end)) {

				if (strBlock.contains(start)) {

					// System.out.println("BLOCK:" + strBlock + " START:"
					// + start + " STARTPOS:"
					// + strBlock.indexOf(start));
					String cutOUT = strBlock.substring(0,
							strBlock.indexOf(start));
					// cutOUT = strBlock.substring(0, 0);
					if (!cutOUT.equals("")) {
						throw new InformationArrayStringException(input
								+ " CUTOUT: " + cutOUT);
					}
					strBlock = strBlock.substring(strBlock.indexOf(start));
					for (String info : strBlock.split(start)) {

						if (info.contains(sep)) {

							// System.out.println("PROP: " + prop);
							id = info.substring(0, info.indexOf(sep));
							content = info.substring(info.indexOf(sep)
									+ sep.length());
							a.add(new Information(id, tag, content));

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
}
