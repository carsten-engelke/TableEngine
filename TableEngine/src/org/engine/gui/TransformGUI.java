/*
 * 
 */
package org.engine.gui;

import org.engine.Layer;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;

import org.engine.geometry.Rectangle;
import org.engine.geometry.Vector2;
import org.engine.geometry.Vector3;
import org.engine.menu.CompassItem;
import org.engine.menu.Menu;
import org.engine.utils.SortableInteractableArray;
import org.engine.*;

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

	private TransformView v = new TransformView();

	/** The dragging. */
	private boolean dragging;

	/** The drawing area (This area is filled with the background-image/colour). */
	Rectangle drawingArea = new Rectangle();

	Matrix4 invertMatrix;

	/** The drag start point. */
	private Vector2 lastInputPoint = new Vector2();

	Matrix4 lastStandardMatrix = new Matrix4();

	/** The parent layer. */
	private Layer l;

	/** The rotating. */
	private boolean rotating;

	/** The affine transform. */
	Matrix4 transformMatrix;

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
		final Vector3 NonTransformedPoint = new Vector3(
				unconvertedInputEvent.getX_on_Screen(), e.getY_on_Screen(), 0);
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

			if (e.getType() == InputEvent.TYPE_PRESS_POS) {

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

			if (e.getType() == InputEvent.TYPE_RELEASE_POS) {
				dragging = false;
				rotating = false;

				// hang in rotation
				if ((v.angle % v.angleStep) >= (v.angleStep / 2)) {
					v.angle = (v.angle - (v.angle % v.angleStep)) + v.angleStep;
				} else {
					v.angle = v.angle - (v.angle % v.angleStep);
				}

				// update Compass when changing angle on the GUI
				((CompassItem) l.t.menu.getMenuItem(Menu.COMPASS_ITEM)).angle.set(v.angle);
				
				Gdx.graphics.requestRendering();
				return true;
			}

			if (e.getType() == InputEvent.TYPE_DRAG_POS) {

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
					((CompassItem) l.t.menu.getMenuItem(Menu.COMPASS_ITEM)).angle.set(v.angle);

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
					Gdx.graphics.requestRendering();
					return true;

				}
			}
			if (e.getType() == InputEvent.TYPE_ZOOM) {
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
				Gdx.graphics.requestRendering();

				return true;
			}
		}
		return wasCatchedAbove;

	}

	@Override
	public void output(final Graphics g) {
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
		transformMatrix = new Matrix4();
		v.fromString(l.t.getPrefString("playerView"));
	}

	/**
	 * Update affine transform.
	 */
	private void updateAffineTransform() {

		transformMatrix = new Matrix4();
		transformMatrix.translate(v.pos.x, v.pos.y, 0);
		transformMatrix.translate(((Gdx.graphics.getWidth() / 2) - v.pos.x),
				((Gdx.graphics.getHeight() / 2) - v.pos.y), 0);
		transformMatrix.rotate(0, 0, 1, v.angle);
		transformMatrix.translate(((-Gdx.graphics.getWidth() / 2) + v.pos.x),
				((-Gdx.graphics.getHeight() / 2) + v.pos.y), 0);
		transformMatrix.scale(v.scale, v.scale, 1);

		invertMatrix = new Matrix4(transformMatrix);
		invertMatrix.inv();
	}
	
	public void setView(TransformView v) {
		
		this.v = v;
		checkViewLimits();
	}

	public static class TransformView {

		public boolean freeMove = true;
		public Vector2 minPos = new Vector2(-100000, -100000);
		public Vector2 maxPos = new Vector2(100000, 100000);
		public Vector2 pos = new Vector2(0, 0);

		public float minScale = 0.125F;
		public float maxScale = 2;
		public float scale = 1;

		public int minAngle = 0;
		public int maxAngle = 360;
		public int angleStep = 90;
		public int angle = 0;
		
		public int viewNo = 0;
		
		public TransformView() {

		}
		
		public TransformView(String s) {
			
			fromString(s);
		}
		
		public String toString() {
			
			String s = "";
			s+= freeMove + ":";
			s+= minPos.x + ":";
			s+= minPos.y + ":";
			s+= maxPos.x + ":";
			s+= maxPos.y + ":";
			s+= pos.x + ":";
			s+= pos.y + ":";
			s+= minScale + ":";
			s+= maxScale + ":";
			s+= scale + ":";
			s+= minAngle + ":";
			s+= maxAngle + ":";
			s+= angleStep + ":";
			s+= angle + ":";
			s+= viewNo;
			return s;
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
			viewNo = Integer.parseInt(v[14]);
		}
	}
}
