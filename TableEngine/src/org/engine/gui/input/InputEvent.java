package org.engine.gui.input;

import com.badlogic.gdx.Gdx;
import org.engine.geometry.Vector2;

public class InputEvent {

	public static final char CHAR_ALT_POS = 'b';

	public static final char CHAR_NORMAL_POS = 'a';

	public static final char CHAR_UNDEFINED = 0xFFFF;
	public static final char KEY_BACKSPACE = 0x0008;
	public static final char KEY_DELETE = 0x007F;
	public static final char KEY_ENTER = 0x000A;
	public static final char KEY_ESCAPE = 0x001B;
	public static final float POSITION_UNDEFINED = -9876.54321F;

	public static final short TYPE_CLICK_POS = 1;

	public static final short TYPE_DRAG_POS = 6;
	public static final short TYPE_DRAG_SCREEN_POS = 11;
	public static final short TYPE_INPUT_CHAR = 8;
	public static final short TYPE_INPUT_KEY = 14;
	public static final short TYPE_MARK_ON_SCREEN = 13;

	public static final short TYPE_MOVE_POS = 5;

	public static final short TYPE_POS_MAX = 6;
	public static final short TYPE_POS_MIN = 1;
	public static final short TYPE_PRESS_POS = 3;
	public static final short TYPE_RELEASE_POS = 4;
	public static final short TYPE_ROTATE_SCREEN_RAD = 12;
	public static final short TYPE_ZOOM = 9;
	// character input events
	private char character;
	private float height;
	private float height_on_screen;
	// complex input events
	private float intensity;

	private int tap_amount;
	private short type;
	public final short TYPE_UNDEFINED = 0;
	private float width;

	private float width_on_screen;
	private float x;
	// positioned input events
	private float x_on_screen;
	private float y;
	private float y_on_screen;

	public InputEvent() {

		setToUndefined();
	}

	InputEvent(final char character) {

		setToUndefined();
		type = InputEvent.TYPE_INPUT_CHAR;
		this.character = character;
	}

	public InputEvent(final InputEvent e) {
		type = e.getType();
		x_on_screen = e.getX_on_Screen();
		y_on_screen = e.getY_on_Screen();
		x = e.getX();
		y = e.getY();
		tap_amount = e.getTap_Amount();
		character = e.getCharacter();
		intensity = e.getIntensity();
		width_on_screen = e.getWidth_on_Screen();
		height_on_screen = e.getHeight_on_Screen();
		width = e.getWidth();
		height = e.getHeight();
	}

	InputEvent(final short type, final char character) {

		setToUndefined();
		this.type = type;
		this.character = character;
	}

	public InputEvent(final short type, final float x_on_screen,
			final float y_on_screen, final boolean alternative) {

		setToUndefined();
		this.type = type;
		this.x_on_screen = x_on_screen;
		this.y_on_screen = y_on_screen;
		x = x_on_screen;
		y = y_on_screen;
		if (alternative) {
			character = InputEvent.CHAR_ALT_POS;
		} else {
			character = InputEvent.CHAR_NORMAL_POS;
		}
	}

	InputEvent(final short type, final float x_on_screen,
			final float y_on_screen, final float intensity) {

		setToUndefined();
		this.type = type;
		this.intensity = intensity;
		this.x_on_screen = x_on_screen;
		this.y_on_screen = y_on_screen;
		x = x_on_screen;
		y = y_on_screen;
	}

	InputEvent(final short type, final float x_on_screen,
			final float y_on_screen, final int tap_amount,
			final boolean alternative) {

		setToUndefined();
		this.type = InputEvent.TYPE_CLICK_POS;
		this.x_on_screen = x_on_screen;
		this.y_on_screen = y_on_screen;
		x = x_on_screen;
		y = y_on_screen;
		this.tap_amount = tap_amount;
		if (alternative) {
			character = InputEvent.CHAR_ALT_POS;
		} else {
			character = InputEvent.CHAR_NORMAL_POS;
		}
	}

	public char getCharacter() {
		return character;
	}

	public float getHeight() {
		return height;
	}

	public float getHeight_on_Screen() {
		return height_on_screen;
	}

	public float getIntensity() {
		return intensity;
	}

	public Vector2 getPosition() {
		if ((x != InputEvent.POSITION_UNDEFINED)
				&& (y != InputEvent.POSITION_UNDEFINED)) {
			return new Vector2(x, y);
		} else {
			return null;
		}
	}

	public Vector2 getPositionOnScreen() {
		if ((x_on_screen != InputEvent.POSITION_UNDEFINED)
				&& (y_on_screen != InputEvent.POSITION_UNDEFINED)) {
			return new Vector2(x_on_screen, y_on_screen);

		} else {
			return null;
		}
	}

	public Vector2 getSize() {
		if ((width != InputEvent.POSITION_UNDEFINED)
				&& (height != InputEvent.POSITION_UNDEFINED)) {
			return new Vector2(width, height);

		} else {
			return null;
		}
	}

	public Vector2 getSizeOnScreen() {
		if ((width_on_screen != InputEvent.POSITION_UNDEFINED)
				&& (height_on_screen != InputEvent.POSITION_UNDEFINED)) {
			return new Vector2(width_on_screen, height_on_screen);

		} else {
			return null;
		}
	}

	public int getTap_Amount() {
		return tap_amount;
	}

	public short getType() {
		return type;
	}

	public float getWidth() {
		return width;
	}

	public float getWidth_on_Screen() {
		return width_on_screen;
	}

	public float getX() {
		return x;
	}

	public float getX_on_Screen() {
		return x_on_screen;
	}

	public float getY() {
		return y;
	}

	public float getY_on_Screen() {
		return y_on_screen;
	}

	public void setCharacter(final char description_char) {
		character = description_char;
	}

	public void setHeight(final float height) {
		this.height = height;
	}

	public void setHeight_on_Screen(final float height_on_screen) {
		this.height_on_screen = height_on_screen;
	}

	public void setIntensity(final float intensity) {
		this.intensity = intensity;
	}

	public void setTap_Amount(final int tap_amount) {
		this.tap_amount = tap_amount;
	}

	private void setToUndefined() {
		type = TYPE_UNDEFINED;
		x_on_screen = InputEvent.POSITION_UNDEFINED;
		y_on_screen = InputEvent.POSITION_UNDEFINED;
		x = InputEvent.POSITION_UNDEFINED;
		y = InputEvent.POSITION_UNDEFINED;
		character = InputEvent.CHAR_UNDEFINED;
		intensity = 0.0F;
		width_on_screen = InputEvent.POSITION_UNDEFINED;
		height_on_screen = InputEvent.POSITION_UNDEFINED;
		width = InputEvent.POSITION_UNDEFINED;
		height = InputEvent.POSITION_UNDEFINED;
	}

	public void setType(final short type) {
		if ((type >= InputEvent.TYPE_POS_MIN)
				&& (type <= InputEvent.TYPE_POS_MAX)) {
			this.type = type;
		}
	}

	public void setWidth(final float width) {
		this.width = width;
	}

	public void setWidth_on_Screen(final float width_on_screen) {
		this.width_on_screen = width_on_screen;
	}

	public void setX(final float x) {
		this.x = x;
	}

	public void setX_on_Screen(final float x) {

		if ((x >= 0) && (x <= Gdx.graphics.getWidth())) {

			x_on_screen = x;

		} else {

			x_on_screen = InputEvent.POSITION_UNDEFINED;
		}
	}

	public void setY(final float y) {
		this.y = y;
	}

	public void setY_on_Screen(final float y) {

		if ((y >= 0) && (y <= Gdx.graphics.getHeight())) {

			y_on_screen = y;

		} else {

			y_on_screen = InputEvent.POSITION_UNDEFINED;
		}
	}

	@Override
	public String toString() {
		return "InputEvent[" + type + ", " + character + ", " + x + ", "
				+ x_on_screen + ", " + y + ", " + y_on_screen + ", " + width
				+ ", " + width_on_screen + ", " + height + ", "
				+ height_on_screen + ", " + intensity + "]";

	}
}
