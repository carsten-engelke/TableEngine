/*
 * 
 */
package org.engine.gui;

import org.engine.Interactable;
import org.engine.Layer;
import org.engine.TableEngine;
import org.engine.geometry.Rectangle;
import org.engine.geometry.Vector2;
import org.engine.geometry.Vector3;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.menu.CompassItem;
import org.engine.menu.Menu;
import org.engine.utils.SortableInteractableArray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Matrix4;

/**
 * The Class TransformGUI. Shows objects transformed by an AffineTransform.
 */
public class TransformGUI implements GUI {

	/** The Constant IS_PAINTING_COLOUR. */
	public static final String IS_PAINTING_COLOUR = "Colour";

	/** The Constant IS_PAINTING_IMAGE. */
	public static final String IS_PAINTING_IMAGE = "Image";

	/** The Constant STANDARD_BACKGROUND. */
	public static final String STANDARD_BACKGROUND = "<standard>";

	public static final Matrix4 NORMAL_MATRIX = new Matrix4();

	private TransformView v = new TransformView();

	private boolean dragging;

	private final Rectangle drawingArea = new Rectangle();

	private final Matrix4 invertMatrix = new Matrix4();

	private Vector2 lastInputPoint = new Vector2();

	Matrix4 lastStandardMatrix = new Matrix4();

	private Layer l;

	private boolean rotating;

	private final Matrix4 transformMatrix = new Matrix4();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.gui.GUI#getParentLayer()
	 */
	@Override
	public Layer getParentLayer() {

		return l;
	}

	private void checkViewLimits() {

		if (v.angle > v.maxAngle) {
			v.angle = v.maxAngle;
		}
		if (v.angle > v.minAngle) {
			v.angle = v.minAngle;
		}

		if (v.pos.x > v.maxPos.x) {
			v.pos.x = v.maxPos.x;
		}
		if (v.pos.y > v.maxPos.y) {
			v.pos.y = v.maxPos.y;
		}
		if (v.pos.x < v.minPos.x) {
			v.pos.x = v.minPos.x;
		}
		if (v.pos.y < v.minPos.y) {
			v.pos.y = v.minPos.y;
		}

		if (v.scale < v.minScale) {
			v.scale = v.minScale;
		}
		if (v.scale > v.maxScale) {
			v.scale = v.maxScale;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.gui.GUI#input(java.awt.event.MouseEvent,
	 * java.awt.event.KeyEvent, java.awt.event.MouseWheelEvent)
	 */
	@Override
	public boolean input(final InputEvent unconvertedInputEvent,
			boolean wasCatchedAbove) {

		updateAffineTransform();
		final InputEvent e = new InputEvent(unconvertedInputEvent);
		final Vector3 NonTransformedPoint = new Vector3(e.getX_on_Screen(),
				e.getY_on_Screen(), 0);
		// compute new input coordinates
		final Vector3 transformedPosition = NonTransformedPoint
				.mul(invertMatrix);
		e.setX(transformedPosition.x);
		e.setY(transformedPosition.y);
		// send input to objects before checking impact on view (so the view
		// only gets changed when no object is hit by input)
		final SortableInteractableArray depthList = new SortableInteractableArray();
		depthList.addAll(l.getAllInteractables());
		depthList.sort(SortableInteractableArray.COMPARE_Z_HIGHEST_ABOVE);
		for (final Interactable i : depthList) {

			if (i.input(e, wasCatchedAbove)) {

				wasCatchedAbove = true;
			}

		}
		// DEBUG TITLE SETTINGS
		// Gdx.graphics.setTitle(shift.x + "|" + shift.y + ", " + angle + ", "
		// + scale + " Mouse: " + e.getX_on_Screen() + "|"
		// + e.getY_on_Screen() + " > " + e.getX() + "|" + e.getY());
		//
		if (wasCatchedAbove) {
			dragging = false;
			rotating = false;
		}
		if (!wasCatchedAbove && v.freeMove) {

			if (e.getType() == InputEvent.TYPE_TOUCH_DOWN) {

				lastInputPoint = e.getPosition();

				if (e.getCharacter() == InputEvent.CHAR_NORMAL_POS) {
					// start dragging
					dragging = true;
					return true;
				}

				if (e.getCharacter() == InputEvent.CHAR_ALT_POS) {
					// start rotating
					rotating = true;
					return true;
				}
				Gdx.graphics.requestRendering();
			}

			if (e.getType() == InputEvent.TYPE_TOUCH_UP) {
				dragging = false;
				rotating = false;

				// hang in rotation
				if ((v.angle % v.angleStep) >= (v.angleStep / 2)) {
					v.angle = (v.angle - (v.angle % v.angleStep)) + v.angleStep;
				} else {
					v.angle = v.angle - (v.angle % v.angleStep);
				}

				// update Compass when changing angle on the GUI
				((CompassItem) l.t.menu.getMenuItem(Menu.COMPASS_ITEM)).angle
						.set(v.angle);
				saveViewToPreferences();
				Gdx.graphics.requestRendering();
				return true;
			}

			if (e.getType() == InputEvent.TYPE_TOUCH_DRAGGED) {

				if (rotating) {

					int angleActual = 0;
					final Vector3 nonTransformedMiddle = new Vector3(
							(Gdx.graphics.getWidth() / 2),
							(Gdx.graphics.getHeight() / 2), 0);
					final Vector3 transformedMiddle = nonTransformedMiddle
							.mul(invertMatrix);
					float diffX = e.getX() - transformedMiddle.x;
					float diffY = e.getY() - transformedMiddle.y;
					if (diffY < 0) {
						angleActual = (int) Math.toDegrees(Math.atan(diffX
								/ diffY));
					} else {
						angleActual = 180 + (int) Math.toDegrees(Math
								.atan(diffX / diffY));
					}

					int angleStart = 0;
					diffX = lastInputPoint.x - transformedMiddle.x;
					diffY = lastInputPoint.y - transformedMiddle.y;
					if (diffY < 0.0D) {
						angleStart = (int) Math.toDegrees(Math.atan(diffX
								/ diffY));
					} else {
						angleStart = 180 + (int) Math.toDegrees(Math.atan(diffX
								/ diffY));
					}

					final int angleDiff = angleStart - angleActual;
					v.angle += angleDiff;
					while (v.angle < 0) {
						v.angle += 360;
					}
					while (v.angle >= 360) {
						v.angle -= 360;
					}
					checkViewLimits();
					// update Compass when changing angle on the GUI
					((CompassItem) l.t.menu.getMenuItem(Menu.COMPASS_ITEM)).angle
							.set(v.angle);
					saveViewToPreferences();
					Gdx.graphics.requestRendering();
					return true;
				}

				if (dragging) {
					final Vector3 oldCursor = new Vector3(lastInputPoint.x,
							lastInputPoint.y, 0);
					final Vector3 newCursor = new Vector3(e.getX_on_Screen(),
							e.getY_on_Screen(), 0);
					newCursor.mul(invertMatrix);
					final float dX = v.pos.x
							- ((oldCursor.x - newCursor.x) * v.scale);
					final float dY = v.pos.y
							- ((oldCursor.y - newCursor.y) * v.scale);
					v.pos = new Vector2(dX, dY);
					checkViewLimits();
					saveViewToPreferences();
					Gdx.graphics.requestRendering();
					return true;

				}
			}
			if (e.getType() == InputEvent.TYPE_SCROLLED) {
				v.scale = e.getIntensity();
				checkViewLimits();
				final Vector3 oldCursor = new Vector3(e.getX_on_Screen(),
						e.getY_on_Screen(), 0);
				oldCursor.mul(invertMatrix);
				updateAffineTransform();
				final Vector3 newCursor = new Vector3(e.getX_on_Screen(),
						e.getY_on_Screen(), 0);
				newCursor.mul(invertMatrix);
				final float dX = v.pos.x
						- ((oldCursor.x - newCursor.x) * v.scale);
				final float dY = v.pos.y
						- ((oldCursor.y - newCursor.y) * v.scale);
				v.pos = new Vector2(dX, dY);
				checkViewLimits();
				saveViewToPreferences();
				Gdx.graphics.requestRendering();
				return true;
			}
		}
		return wasCatchedAbove;

	}

	@Override
	public void output(final Graphics g) {
		v.fromTableEngine(l.t);
		lastStandardMatrix = g.getTransformMatrix();
		updateAffineTransform();
		g.setTransformMatrix(transformMatrix);
		final SortableInteractableArray depthList = new SortableInteractableArray();
		depthList.addAll(l.getAllInteractables());
		depthList.sort(SortableInteractableArray.COMPARE_Z_HIGHEST_BELOW);
		for (final Interactable i : depthList) {

			g.reset();
			i.output(g);
		}
		g.setTransformMatrix(lastStandardMatrix);

	}

	@Override
	public void initialize(final Layer l) {

		this.l = l;
		v.fromTableEngine(l.t);
	}

	/**
	 * Update affine transform.
	 */
	private void updateAffineTransform() {

		transformMatrix.set(NORMAL_MATRIX);
		transformMatrix.translate(v.pos.x, v.pos.y, 0);
		transformMatrix.translate(((Gdx.graphics.getWidth() / 2) - v.pos.x),
				((Gdx.graphics.getHeight() / 2) - v.pos.y), 0);
		transformMatrix.rotate(0, 0, 1, v.angle);
		transformMatrix.translate(((-Gdx.graphics.getWidth() / 2) + v.pos.x),
				((-Gdx.graphics.getHeight() / 2) + v.pos.y), 0);
		transformMatrix.scale(v.scale, v.scale, 1);

		invertMatrix.set(transformMatrix);
		invertMatrix.inv();
	}

	public void setView(TransformView v) {

		this.v = v;
		checkViewLimits();
	}

	private void saveViewToPreferences() {

		l.t.preferences.putString("playerView", v.toString());
	}

	public static class TransformView {

		public boolean freeMove = true;
		public Vector2 minPos = new Vector2(-100000, -100000);
		public Vector2 maxPos = new Vector2(100000, 100000);
		public Vector2 pos = new Vector2(0, 0);

		public float minScale = 0.125F;
		public float maxScale = 2.0F;
		public float scale = 1.0F;

		public int minAngle = 0;
		public int maxAngle = 360;
		public int angleStep = 90;
		public int angle = 0;

		public int viewIndex = 0;

		public TransformView() {

		}

		public TransformView(String s) {

			fromString(s);
		}

		public String toString() {

			return freeMove + ":" + minPos.x + ":" + minPos.y + ":" + maxPos.x
					+ ":" + maxPos.y + ":" + pos.x + ":" + pos.y + ":"
					+ minScale + ":" + maxScale + ":" + scale + ":" + minAngle
					+ ":" + maxAngle + ":" + angleStep + ":" + angle + ":"
					+ viewIndex;
		}

		public void fromString(String s) {

			String[] v = s.split(":");
			freeMove = Boolean.parseBoolean(v[0]);
			minPos.x = Float.parseFloat(v[1]);
			minPos.y = Float.parseFloat(v[2]);
			maxPos.x = Float.parseFloat(v[3]);
			maxPos.y = Float.parseFloat(v[4]);
			pos.x = Float.parseFloat(v[5]);
			pos.y = Float.parseFloat(v[6]);
			minScale = Float.parseFloat(v[7]);
			maxScale = Float.parseFloat(v[8]);
			scale = Float.parseFloat(v[9]);
			minAngle = Integer.parseInt(v[10]);
			maxAngle = Integer.parseInt(v[11]);
			angleStep = Integer.parseInt(v[12]);
			angle = Integer.parseInt(v[13]);
			viewIndex = Integer.parseInt(v[14]);
		}

		public void fromTableEngine(TableEngine t) {

			freeMove = t.getPrefBoolean("viewFreeMove");
			minPos.x = t.getPrefFloat("viewMinX");
			minPos.y = t.getPrefFloat("viewMinY");
			maxPos.x = t.getPrefFloat("viewMaxX");
			maxPos.y = t.getPrefFloat("viewMaxY");
			pos.x = t.getPrefFloat("viewX");
			pos.y = t.getPrefFloat("viewY");
			minScale = t.getPrefFloat("viewMinScale");
			maxScale = t.getPrefFloat("viewMaxScale");
			scale = t.getPrefFloat("viewScale");
			minAngle = t.getPrefInteger("viewMinAngle");
			maxAngle = t.getPrefInteger("viewMaxAngle");
			angleStep = t.getPrefInteger("viewAngleStep");
			angle = t.getPrefInteger("viewAngle");
			viewIndex = t.getPrefInteger("viewIndex");
		}

		public void toTableEngine(TableEngine t) {

			t.putPrefBoolean("viewFreeMove", freeMove);
			t.putPrefFloat("viewMinY", minPos.y);
			t.putPrefFloat("viewMaxX", maxPos.x);
			t.putPrefFloat("viewMaxY", maxPos.y);
			t.putPrefFloat("viewX", pos.x);
			t.putPrefFloat("viewY", pos.y);
			t.putPrefFloat("viewMinScale", minScale);
			t.putPrefFloat("viewMaxScale", maxScale);
			t.putPrefFloat("viewScale", scale);
			t.putPrefInteger("viewMinAngle", minAngle);
			t.putPrefInteger("viewMaxAngle", maxAngle);
			t.putPrefInteger("viewAngleStep", angleStep);
			t.putPrefInteger("viewAngle", angle);
			t.putPrefInteger("viewIndex", viewIndex);
		}

		public void toPreferences(Preferences p) {

			p.putBoolean("viewFreeMove", freeMove);
			p.putFloat("viewMinY", minPos.y);
			p.putFloat("viewMaxX", maxPos.x);
			p.putFloat("viewMaxY", maxPos.y);
			p.putFloat("viewX", pos.x);
			p.putFloat("viewY", pos.y);
			p.putFloat("viewMinScale", minScale);
			p.putFloat("viewMaxScale", maxScale);
			p.putFloat("viewScale", scale);
			p.putInteger("viewMinAngle", minAngle);
			p.putInteger("viewMaxAngle", maxAngle);
			p.putInteger("viewAngleStep", angleStep);
			p.putInteger("viewAngle", angle);
			p.putInteger("viewIndex", viewIndex);
		}

		public static String fromTableEngineToString(TableEngine t) {

			return t.getPrefBoolean("viewFreeMove") + ":"
					+ t.getPrefFloat("viewMinX") + ":"
					+ t.getPrefFloat("viewMinY") + ":"
					+ t.getPrefFloat("viewMaxX") + ":"
					+ t.getPrefFloat("viewMaxY") + ":"
					+ t.getPrefFloat("viewX") + ":" + t.getPrefFloat("viewY")
					+ ":" + t.getPrefFloat("viewMinScale") + ":"
					+ t.getPrefFloat("viewMaxScale") + ":"
					+ t.getPrefFloat("viewScale") + ":"
					+ t.getPrefInteger("viewMinAngle") + ":"
					+ t.getPrefInteger("viewMaxAngle") + ":"
					+ t.getPrefInteger("viewAngleStep") + ":"
					+ t.getPrefInteger("viewAngle") + ":"
					+ t.getPrefInteger("viewIndex");
		}
	}
}
