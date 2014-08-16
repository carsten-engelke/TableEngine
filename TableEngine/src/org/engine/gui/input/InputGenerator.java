package org.engine.gui.input;

import org.engine.TableEngine;
import org.engine.gui.TransformGUI.TransformView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;

public class InputGenerator extends GestureDetector {

	class StopTypingThread extends Thread {

		char blockMe = InputEvent.CHAR_UNDEFINED;

		@Override
		public void run() {

			changedLetter = false;
			doNotType = blockMe;
			try {
				Thread.sleep(500);
			} catch (final Exception e) {
			}
			if (!changedLetter) {
				doNotType = InputEvent.CHAR_UNDEFINED;
			}
		}
	}
	private boolean changedLetter = true;
	char doNotType = InputEvent.CHAR_UNDEFINED;
	StopTypingThread doNotTypeThread = new StopTypingThread();

	private final TableEngine t;

	public InputGenerator(final TableEngine t) {

		super(new TESender(t));
		this.t = t;
	}

	@Override
	public boolean keyDown(final int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(final char character) {

		if (character != doNotType) {
			changedLetter = true;
			if (Character.isLetterOrDigit(character) || (character == ' ')
					|| (character == '?') || (character == '!')) {
				t.input(new InputEvent(character));
				doNotTypeThread = new StopTypingThread();
				doNotTypeThread.blockMe = character;
				doNotTypeThread.start();
				return true;
			}
		}
		// Everything else is not considered
		return false;
	}

	@Override
	public boolean keyUp(final int keycode) {

		// BACKSPACE
		if (keycode == Input.Keys.BACKSPACE) {
			t.input(new InputEvent(InputEvent.TYPE_INPUT_KEY,
					InputEvent.KEY_BACKSPACE));
			return true;
		}
		// ESCAPE
		if (keycode == Input.Keys.ESCAPE) {
			t.input(new InputEvent(InputEvent.TYPE_INPUT_KEY,
					InputEvent.KEY_ESCAPE));
			return true;
		}
		// ENTER
		if (keycode == Input.Keys.ENTER) {
			t.input(new InputEvent(InputEvent.TYPE_INPUT_KEY,
					InputEvent.KEY_ENTER));
			return true;
		}
		// DELETE
		if (keycode == Input.Keys.DEL) {
			t.input(new InputEvent(InputEvent.TYPE_INPUT_KEY,
					InputEvent.KEY_DELETE));
			return true;
		}
		changedLetter = true;
		doNotType = InputEvent.CHAR_UNDEFINED;
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		super.mouseMoved(screenX, screenY);
		t.input(new InputEvent(InputEvent.TYPE_TOUCH_MOVED, screenX,
				Gdx.graphics.getHeight() - screenY, false));
		return true;
	}

	@Override
	public boolean scrolled(final int amount) {
		super.scrolled(amount);
		float zoom = new TransformView(t.getPrefString("playerView")).scale;
		if (amount > 0) {
			zoom = zoom / 2;
		} else {
			zoom = zoom * 2;
		}
		t.input(new InputEvent(InputEvent.TYPE_SCROLLED, Gdx.input
				.getX(0), Gdx.graphics.getHeight() - Gdx.input.getY(0), zoom));
		return true;
	}

	@Override
	public boolean touchDown(final float x, final float y, final int pointer, final int button) {
		super.touchDown(x, y, pointer, button);
		boolean alt = false;
		if (button != 0) {
			alt = true;
		}
		t.input(new InputEvent(InputEvent.TYPE_TOUCH_DOWN, x,
				Gdx.graphics.getHeight() - y, alt));
		return true;
	}

	@Override
	public boolean touchDragged(final float x, final float y, final int pointer) {
		super.touchDragged(x, y, pointer);
		t.input(new InputEvent(InputEvent.TYPE_TOUCH_DRAGGED, x,
				Gdx.graphics.getHeight() - y, false));
		return true;
	}

	@Override
	public boolean touchUp(final float x, final float y, final int pointer, final int button) {
		super.touchUp(x, y, pointer, button);
		boolean alt = false;
		if (button != 0) {
			alt = true;
		}
		t.input(new InputEvent(InputEvent.TYPE_TOUCH_UP, x,
				Gdx.graphics.getHeight() - y, alt));
		return true;
	}
}

class TESender implements GestureListener {

	private final TableEngine t;

	public TESender(final TableEngine t) {

		this.t = t;
	}

	@Override
	public boolean fling(final float velocityX, final float velocityY, final int button) {
		return false;
	}

	@Override
	public boolean longPress(final float x, final float y) {
		t.input(new InputEvent(InputEvent.TYPE_TOUCH_DOWN, x, y,
				true));
		t.input(new InputEvent(InputEvent.TYPE_TOUCH_UP, x, y,
				false));
		t.input(new InputEvent(InputEvent.TYPE_TOUCH_CLICKED, x, y,
				true));
		return true;
	}

	@Override
	public boolean pan(final float x, final float y, final float deltaX, final float deltaY) {
		return false;
	}

	@Override
	public boolean pinch(final com.badlogic.gdx.math.Vector2 initialPointer1,
			final com.badlogic.gdx.math.Vector2 initialPointer2,
			final com.badlogic.gdx.math.Vector2 pointer1,
			final com.badlogic.gdx.math.Vector2 pointer2) {

		return false;
	}

	@Override
	public boolean tap(final float x, final float y, final int count, final int button) {
		boolean alt = false;
		if (button != 0) {
			alt = true;
		}
		t.input(new InputEvent(InputEvent.TYPE_TOUCH_CLICKED, x,
				Gdx.graphics.getHeight() - y, count, alt));

		return false;
	}

	@Override
	public boolean touchDown(final float x, final float y, final int pointer, final int button) {
		return false;
	}

	@Override
	public boolean zoom(final float initialDistance, final float distance) {
		final float midX = Math.abs(Gdx.input.getX(0) - Gdx.input.getX(1));
		final float midY = Gdx.graphics.getHeight()
				- Math.abs(Gdx.input.getY(0) - Gdx.input.getY(1));
		t.input(new InputEvent(InputEvent.TYPE_SCROLLED, midX, midY,
				initialDistance / distance));
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}
}