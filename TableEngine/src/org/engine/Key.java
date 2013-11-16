package org.engine;

/**
 * The Key class is able to provide an String key from an Integer that is
 * usually the gameID. Hence the php-Server contains a deconstructing
 * counterpart, this can be used to verify if the user has a valid key. (E.g.
 * after paying). Very basic stuff though, is easily hackable I guess.
 */
public class Key {

	/**
	 * Creates a String key from the Integer gameID. Use to provide a user with a key.
	 * 
	 * @param code
	 *            the gameID. 
	 * @return the key.
	 */
	public static String create(int code) {

		if ((code < 100) || (code > 999)) {
			code = 456;
		}
		int i = (int) (Math.ceil(Math.random() * 2) + 7);
		int in = (int) (Math.ceil(Math.random() * (Math.pow(10, i))));
		in = in - (in % code);
		if (in < 0) {
			in *= -1;
		}
		final double pow = 1 + ((double) (code) / 1000);
		double out = Math.ceil(Math.pow(in, pow));
		while ((out % 4) != 2) {
			if (in > 0) {
				in -= code;
			} else {
				i = (int) (Math.ceil(Math.random() * 2) + 7);
				in = (int) (Math.ceil(Math.random() * (Math.pow(10, i))));
				in = in - (in % code);
			}
			out = Math.ceil(Math.pow(in, pow));
		}
		String s = String.valueOf(i) + String.valueOf(in) + String.valueOf(out);
		int Eindex = s.length() - s.indexOf("E") - 1;
		if (!s.contains("E")) {
			Eindex = 0;
		}
		int Pindex = String.valueOf(out).indexOf(".");
		if (!String.valueOf(out).contains(".")) {
			Pindex = 0;
		}
		s = String.valueOf(i) + String.valueOf(Eindex) + String.valueOf(Pindex)
				+ String.valueOf(in) + String.valueOf(out);
		s = s.replace(".", "");
		s = s.replace("E", "");
		return s;
	}
}
