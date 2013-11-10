package org.engine.utils;

public class MathUtils extends com.badlogic.gdx.math.MathUtils{
	
	public static float max(float a, float b) {
		if (a > b) {
			return a;
		} else {
			return b;
		}
	}
	
	public static float max(float a, float b, float c) {
		if (a > b) {
			return max(a, c);
		} else {
			return max(b, c);
		}
	}
	
	public static float min(float a, float b) {
		if (a < b) {
			return a;
		} else {
			return b;
		}
	}
	
	public static float min(float a, float b, float c) {
		if (a < b) {
			return max(a, c);
		} else {
			return max(b, c);
		}
	}
}
