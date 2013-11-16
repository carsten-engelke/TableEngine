package org.engine.object;

import org.engine.Interactable;
import org.engine.Interactable.Animateable;
import org.engine.Layer;
import org.engine.Skinnable;
import org.engine.TableEngine;
import org.engine.geometry.Rectangle;
import org.engine.geometry.Vector2;
import org.engine.geometry.Vector3;
import org.engine.gui.TransformGUI;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.object.area.Area;
import org.engine.object.popup.Popup;
import org.engine.object.popup.PopupSlave;
import org.engine.property.FloatProperty;
import org.engine.property.Information;
import org.engine.property.IntegerProperty;
import org.engine.property.Property.Flag;
import org.engine.resource.BasicResource;
import org.engine.utils.Array;
import org.engine.utils.MathUtils;
import org.engine.utils.SortableInteractableArray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * The Superclass for all objects put into the TableEngine. You can specify
 * which interaction is applicable by overwriting the abilities of this class.
 */
public class BasicInteractable extends Rectangle implements Interactable, Animateable,
		PopupSlave, Skinnable {

	/**
	 * History: 1: old class import 2: set to rectangle and matrix
	 */
	private static final long serialVersionUID = 2L;

	// MODE CONSTANTS
	public static final String MODE_RESIZE = "RESIZE";
	public static final String MODE_ROTATE = "ROTATE";
	public static final String MODE_SCALE = "SCALE";
	public static final String MODE_SELECT = "SELECT";
	public static final String MODE_SHIFT = "SHIFT";
	protected final Array<String> abilities = new Array<String>();
	protected String mode = null;

	// PROPERTIES' TAG
	public static final String OBJECT_TAG = "O";

	// STYLE
	protected InteractableStyle style;

	// PARENT
	protected Layer l;
	protected TableEngine t;

	// SIZE AND POSITION
	public final RectangleProperty metrics = new RectangleProperty("METRICS",
			OBJECT_TAG, Flag.NONE, this);
	public final FloatProperty z = new FloatProperty("Z", OBJECT_TAG, Flag.NONE, 0.0F);
	public final IntegerProperty shiftGrid = new IntegerProperty("SHIFT-GRID",
			OBJECT_TAG, Flag.NONE, 1);
	protected boolean shifting = false;
	private final Vector2 dragStartPoint = new Vector2();
	private float dragStartZ;
	public final IntegerProperty resizeGrid = new IntegerProperty(
			"RESIZE-GRID", OBJECT_TAG, Flag.NONE, 1);
	protected boolean resizing = false;
	private final Vector2 resizeStartSize = new Vector2();
	private final Vector2 resizeOldOppositePosition = new Vector2();
	private final Vector2 resizeNewOppositePosition = new Vector2();
	private ResizingPoint resizeArea;
	private final Rectangle ll = new Rectangle();
	private final Rectangle lr = new Rectangle();
	private final Rectangle ul = new Rectangle();
	private final Rectangle ur = new Rectangle();

	// ROTATION
	public final IntegerProperty angle = new IntegerProperty("ANGLE",
			OBJECT_TAG, Flag.NONE, 0);
	public final IntegerProperty rotateGrid = new IntegerProperty(
			"ROTATE-GRID", OBJECT_TAG, Flag.NONE, 90);
	private int rotateStartAngle;
	private final Vector2 rotateStartMidPoint = new Vector2();
	private final Vector2 rotateStartPoint = new Vector2();
	protected boolean rotating = false;

	// SCALING
	public final FloatProperty scale = new FloatProperty("SCALE", OBJECT_TAG, Flag.NONE,
			1.0F);
	public final FloatProperty scaleGrid = new FloatProperty("SCALE-GRID",
			OBJECT_TAG, Flag.NONE, 0.25F);
	protected boolean scaling = false;
	private float startScale;

	// SELECTION
	protected boolean selected = false;

	// AFFINE TRANSFORM MATRICES AND VECTORS
	protected final Matrix4 transform = new Matrix4();
	protected final Matrix4 invertedTransform = new Matrix4();
	protected final Vector3 transformPoint = new Vector3();

	// INPUT
	protected boolean wasCatchedByMe = false;
	protected final Vector2 lastInputPosition = new Vector2();
	protected final Vector2 relativePosition = new Vector2();
	protected final Vector2 lastRelativeInputPosition = new Vector2();

	// OUTPUT
	private final Matrix4 oldAT = new Matrix4();
	private final Matrix4 newAT = new Matrix4();

	// PROPERTY
	protected Information i;
	protected Flag flagged = Flag.NONE;

	// COLLISION DETECTION
	private final SortableInteractableArray depthList = new SortableInteractableArray();
	private final Vector2 collidingPoint = new Vector2();
	private final Vector2 pLL = new Vector2();
	private final Vector2 pLR = new Vector2();
	private final Vector2 pUL = new Vector2();
	private final Vector2 pUR = new Vector2();
	private final Vector2 pM = new Vector2();

	public BasicInteractable() {

		super();
	}

	public BasicInteractable(final Rectangle r, final int shiftGrid) {

		this();
		set(r);
		this.shiftGrid.set(shiftGrid);
	}

	public BasicInteractable(final Rectangle r, final int shiftGrid, final int angle,
			final int rotateGrid) {

		this();
		set(r);
		this.shiftGrid.set(shiftGrid);
		this.angle.set(angle);
		this.rotateGrid.set(rotateGrid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.network.Animator#animate(java.lang.String)
	 */
	@Override
	public void animate(String animationString) {

		if (animationString.startsWith(i.id)) {
			animationString = animationString.substring(i.id.length() + 1);
			if (animationString.startsWith("shift")) {
				t.setBlockIncomingNetworkTraffic(true);
				final String[] p = animationString.substring(
						animationString.indexOf("(") + 1,
						animationString.indexOf(")")).split(":");
				final Vector2 start = new Vector2(Float.valueOf(p[0]),
						Float.valueOf(p[1]));
				final Vector2 end = new Vector2(Float.valueOf(p[2]),
						Float.valueOf(p[3]));
				new ShiftAnimationThread(this, start, end).start();
			}
			if (animationString.startsWith("rotate")) {
				t.setBlockIncomingNetworkTraffic(true);
				final String[] p = animationString.substring(
						animationString.indexOf("(") + 1,
						animationString.indexOf(")")).split(":");
				final int start = Integer.parseInt(p[0]);
				final int end = Integer.parseInt(p[1]);
				new RotateThread(this, start, end).start();
			}
		}
	}

	@Override
	public Layer getParentLayer() {

		return l;
	}

	public Vector2 transformPointToRelativePoint(final Vector2 point) {

		return transformPointToRelativePoint(point.x, point.y);
	}

	public Vector2 transformPointToRelativePoint(final float x, final float y) {

		transformPoint.set(x, y, 0);
		transformPoint.mul(invertedTransform);
		return transformPoint.toVector2D();
	}

	public Vector2 transformRelativePointToPoint(final Vector2 relative) {

		return transformRelativePointToPoint(relative.x, relative.y);
	}

	public Vector2 transformRelativePointToPoint(final float x, final float y) {
		transformPoint.set(x, y, 0);
		transformPoint.mul(transform);
		return transformPoint.toVector2D();
	}

	@Override
	public void initialize(final Layer l) {

		this.l = l;
		t = l.t;
		style(t.uiSkin, t.uiStyleName);
		adaptToScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		i = new Information(t.getNewObjectID(getClass()), Layer.LAYER_TAG, Flag.NONE, "");
	}

	public boolean containsRelative(Vector2 v) {

		return containsRelative(v.x, v.y);
	}

	public boolean containsRelative(float x, float y) {

		if (x >= 0 && x <= width && y >= 0 && y <= height) {
			return true;
		}
		return false;
	}

	@Override
	public boolean input(final InputEvent e, final boolean wasCatchedAbove) {

		boolean isCatchedByMe = false;
		relativePosition.set(transformPointToRelativePoint(e.getPosition()));
		updateTransform();

		// See if the MOVE-EVENT is for this instance a ENTER OR EXIT EVENT.
		if (e.getType() == InputEvent.TYPE_TOUCH_MOVED) {

			if (containsRelative(lastRelativeInputPosition)
					&& containsRelative(relativePosition)) {
				// mouse moved in area -> comes from or goes to overlying
				// object?
				if (!wasCatchedAbove) {
					// this is the topmost object
					if (wasCatchedByMe) {
						// was last time in area
						isCatchedByMe = touchMove(e);
					} else {
						// was last time not in area
						isCatchedByMe = touchEnter(e);
					}
				} else {
					// this is not the topmost object
					if (wasCatchedByMe) {
						// was last time in area, is now in overlying object
						isCatchedByMe = touchExit(e);
					}
				}
			}

			if (!containsRelative(lastRelativeInputPosition)
					&& containsRelative(relativePosition) && !wasCatchedAbove) {

				isCatchedByMe = touchEnter(e);

			}

			if (containsRelative(lastRelativeInputPosition)
					&& !containsRelative(relativePosition) && wasCatchedByMe) {

				isCatchedByMe = touchExit(e);
			}

		}
		if ((e.getType() >= InputEvent.touchIndexLowerLimit)
				&& (e.getType() <= InputEvent.touchIndexUpperLimit)) {
			if ((containsRelative(relativePosition) || shifting || resizing
					|| rotating || scaling)
					&& !wasCatchedAbove) {

				switch (e.getType()) {
				case (InputEvent.TYPE_TOUCH_CLICKED):
					isCatchedByMe = touchClick(e);
					break;
				case (InputEvent.TYPE_TOUCH_UP):
					isCatchedByMe = touchUp(e);
					break;
				case (InputEvent.TYPE_TOUCH_DOWN):
					isCatchedByMe = touchDown(e);
					break;
				case (InputEvent.TYPE_TOUCH_DRAGGED):
					isCatchedByMe = touchDrag(e);
					break;
				}
			} else {

				if (!containsRelative(relativePosition)) {

					switch (e.getType()) {
					case (InputEvent.TYPE_TOUCH_CLICKED):
						isCatchedByMe = touchClickOutside(e);
						break;
					case (InputEvent.TYPE_TOUCH_UP):
						isCatchedByMe = touchUpOutside(e);
						break;
					case (InputEvent.TYPE_TOUCH_DOWN):
						isCatchedByMe = touchDownOutside(e);
						break;
					case (InputEvent.TYPE_TOUCH_DRAGGED):
						isCatchedByMe = touchDragOutside(e);
						break;
					}
				}
			}

			lastInputPosition.set(e.getPosition());
			lastRelativeInputPosition.set(transformPointToRelativePoint(e
					.getPosition()));
		}

		if (e.getType() == InputEvent.TYPE_INPUT_CHAR) {

			isCatchedByMe = keyTyped(e);
		}

		wasCatchedByMe = isCatchedByMe;
		return isCatchedByMe;
	}

	public boolean keyTyped(final InputEvent e) {

		return false;

	}

	@Override
	public void output(final Graphics g) {

		paintUntransformed(g);
		updateTransform();
		oldAT.set(g.getTransformMatrix());
		newAT.set(g.getTransformMatrix());
		newAT.mul(transform);
		g.setTransformMatrix(newAT);
		paintTransformed(g);
		paintForeground(g);
		g.setTransformMatrix(oldAT);
		paintForegroundUntransformed(g);
		if (t.getPrefBoolean("debug")) {
			paintDebug(g);
		}
	}

	private void paintDebug(Graphics g) {

		g.setFont(t.uiSkin.getFont("default-font"));
		g.drawString(getClass().getCanonicalName() + " r=" + asString()
				+ ", a=" + angle.get() + "°, s=" + scale.get() + "F", x, y);
	}

	/**
	 * Draws the foreground on top of the actual object. This is generally the
	 * currently active ability as an icon
	 * 
	 * @param g
	 *            the g
	 */
	protected void paintForeground(final Graphics g) {

		if (resizing || shifting || rotating || scaling || selected) {

			g.drawUI(style.selected, 0, 0, width, height);

			if (mode.equals(BasicInteractable.MODE_RESIZE) && selected) {
				g.drawUI(style.resize, -style.resize.getMinWidth() / 2,
						-style.resize.getMinHeight() / 2,
						style.resize.getMinWidth(), style.resize.getMinHeight());
				g.drawUI(style.resize,
						(width) - style.resize.getMinWidth() / 2,
						-style.resize.getMinHeight() / 2,
						style.resize.getMinWidth(), style.resize.getMinHeight());
				g.drawUI(style.resize, -style.resize.getMinWidth() / 2,
						(height) - style.resize.getMinHeight() / 2,
						style.resize.getMinWidth(), style.resize.getMinHeight());
				g.drawUI(style.resize,
						(width) - style.resize.getMinWidth() / 2, (height)
								- style.resize.getMinHeight() / 2,
						style.resize.getMinWidth(), style.resize.getMinHeight());
			}
			if (mode.equals(BasicInteractable.MODE_SHIFT) && selected) {
				g.drawUI(style.shift, (width) - style.shift.getMinWidth() / 2,
						(height) - style.shift.getMinHeight() / 2,
						style.shift.getMinWidth(), style.shift.getMinHeight());
			}
			if (mode.equals(BasicInteractable.MODE_ROTATE) && selected) {
				g.drawUI(style.rotate,
						(width) - style.rotate.getMinWidth() / 2, (height)
								- style.rotate.getMinHeight() / 2,
						style.rotate.getMinWidth(), style.rotate.getMinHeight());
			}
			if (mode.equals(BasicInteractable.MODE_SCALE) && selected) {
				g.drawUI(style.scale, (width) - style.scale.getMinWidth() / 2,
						(height) - style.scale.getMinHeight() / 2,
						style.scale.getMinWidth(), style.scale.getMinHeight());
			}
		}
	}

	protected void paintForegroundUntransformed(final Graphics g) {

	}

	protected void paintTransformed(final Graphics g) {

		g.drawUI(style.background, 0, 0, width, height);
	}

	protected void paintUntransformed(final Graphics g) {

	}

	public boolean touchClick(final InputEvent e) {

		stopShifting();
		stopRotating();
		stopResizing();
		if (e.getCharacter() == InputEvent.CHAR_NORMAL_POS) {

			if (selected) {
				switchMode();
			} else {
				if (abilities.getSize() > 0) {
					selected = true;
				}
			}
		} else {

			showPopup(e.getX_on_Screen(), e.getY_on_Screen());
		}
		return true;

	}

	protected boolean touchClickOutside(final InputEvent e) {

		stopShifting();
		stopRotating();
		selected = false;
		return false;
	}

	public boolean touchDrag(final InputEvent e) {

		updateShifting(e);
		updateRotating(e);
		updateResizing(e);
		updateScaling(e);
		return true;

	}

	protected boolean touchDragOutside(final InputEvent e) {
		return false;
	}

	public boolean touchEnter(final InputEvent e) {

		updateShifting(e);
		return true;

	}

	public boolean touchExit(final InputEvent e) {

		if (shifting && (shiftGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_SHIFT, false)) {

			final float dX = lastInputPosition.x - e.getX();
			final float dY = lastInputPosition.y - e.getY();

			setPosition(x - dX, y - dY);
			Gdx.graphics.requestRendering();
			return true;
		}

		return true;

	}

	public boolean touchMove(final InputEvent e) {

		return true;

	}

	public boolean touchDown(final InputEvent e) {

		if (abilities.getSize() > 0) {
			selected = true;
			if (mode == null) {
				switchMode();
			}
		}
		startShifting(e);
		startRotating(e);
		startResizing(e);
		startScaling(e);
		return true;

	}

	protected boolean touchDownOutside(final InputEvent e) {
		if ((mode == BasicInteractable.MODE_RESIZE) && (resizeGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_RESIZE, false)
				&& getTouchResizingArea(relativePosition) != ResizingPoint.NONE) {

			resizeStartSize.set(e.getPosition());
			resizing = true;
			return true;
		}
		return false;
	}

	public boolean touchUp(final InputEvent e) {

		if (shifting && (shiftGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_SHIFT, false)) {
			t.getSound(BasicResource.DESCRIPTION_ID, "drop").play();
			stopShifting();
		}
		if (rotating && (rotateGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_ROTATE, false)) {
			t.getSound(BasicResource.DESCRIPTION_ID, "drop").play();
			stopRotating();
		}
		if (resizing && (resizeGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_RESIZE, false)) {
			t.getSound(BasicResource.DESCRIPTION_ID, "drop").play();
			stopResizing();
		}
		if (scaling && (scaleGrid.get() > 0)
				&& abilities.contains(MODE_SCALE, false)) {
			t.getSound(BasicResource.DESCRIPTION_ID, "drop").play();
			stopScaling();
		}
		return true;

	}

	protected boolean touchUpOutside(final InputEvent e) {
		return false;
	}

	public boolean scroll(final InputEvent e) {

		return true;

	}

	@Override
	public void receiveOrder(final String order) {

		System.out.println("Order: " + order);
		// t.setBlockIncomingNetworkTraffic(true);
	}

	protected void showPopup(final float x, final float y) {

		t.setBlockIncomingNetworkTraffic(true);
		final Popup p = new Popup(new Array<String>(new String[] {
				"Test Option 1", "Test Option 2" }), this);
		p.setPosition(x, y);
		mode = BasicInteractable.MODE_SELECT;
		t.getLayer(TableEngine.POPUP_LAYER).addInteractable(p);
	}


	private enum ResizingPoint {
		LowerLeft, LowerRight, UpperLeft, UpperRight, NONE;
	}

	private ResizingPoint getTouchResizingArea(Vector2 relativeToOrigin) {

		ll.set(-1 * style.resize.getMinWidth() / 2,
				-1 * style.resize.getMinWidth() / 2,
				style.resize.getMinWidth(), style.resize.getMinWidth());
		lr.set(width - style.resize.getMinWidth() / 2,
				-1 * style.resize.getMinWidth() / 2,
				style.resize.getMinWidth(), style.resize.getMinWidth());
		ul.set(-1 * style.resize.getMinWidth() / 2,
				height - style.resize.getMinWidth() / 2,
				style.resize.getMinWidth(), style.resize.getMinWidth());
		ur.set(width - style.resize.getMinWidth() / 2,
				height - style.resize.getMinWidth() / 2,
				style.resize.getMinWidth(), style.resize.getMinWidth());
		if (ur.contains(relativeToOrigin)) {
			return ResizingPoint.UpperRight;
		}
		if (ll.contains(relativeToOrigin)) {
			return ResizingPoint.LowerLeft;
		}
		if (ul.contains(relativeToOrigin)) {
			return ResizingPoint.UpperLeft;
		}
		if (lr.contains(relativeToOrigin)) {
			return ResizingPoint.LowerRight;
		}
		return ResizingPoint.NONE;
	}
	
	private void startResizing(final InputEvent e) {

		if ((mode == BasicInteractable.MODE_RESIZE) && (resizeGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_RESIZE, false)) {
			if (getTouchResizingArea(relativePosition) != ResizingPoint.NONE) {
				resizeStartSize.set(width, height);
				dragStartPoint.set(x, y);
				resizeArea = getTouchResizingArea(relativePosition);
				resizing = true;
			}
		}
	}

	private void startRotating(final InputEvent e) {

		if ((mode == BasicInteractable.MODE_ROTATE) && (rotateGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_ROTATE, false)) {
			t.setBlockIncomingNetworkTraffic(true);
			// start rotating
			rotateStartPoint.set(e.getPosition());
			rotateStartMidPoint.set(getX() + (getWidth() / 2), getY()
					+ (getHeight() / 2));
			rotateStartAngle = angle.get();
			while (rotateStartAngle > (360)) {
				rotateStartAngle -= 360;
			}
			while (rotateStartAngle < 0) {
				rotateStartAngle += 360;
			}
			rotating = true;
			dragStartZ = z.get();
			for (final Interactable i : l.getAllInteractables()) {
				if (i.depth() <= z.get()) {
					z.set(i.depth() - 1);
				}
			}
		}
	}

	private void startShifting(final InputEvent e) {

		if ((mode == BasicInteractable.MODE_SHIFT) && (shiftGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_SHIFT, false)) {
			t.setBlockIncomingNetworkTraffic(true);
			shifting = true;
			dragStartZ = z.get();
			dragStartPoint.set(getX(), getY());
			for (final Interactable i : l.getAllInteractables()) {
				if (i.depth() <= z.get()) {
					z.set(i.depth() - 1);
				}
			}
		}
	}

	private void startScaling(InputEvent e) {

		if ((mode == BasicInteractable.MODE_SCALE) && (scaleGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_SCALE, false)) {
			t.setBlockIncomingNetworkTraffic(true);
			scaling = true;
			startScale = scale.get();
		}
	}

	private void stopResizing() {

		if (resizing && abilities.contains(BasicInteractable.MODE_RESIZE, false)) {
			float dx = (width % resizeGrid.get());
			if (dx > (resizeGrid.get() / 2)) {
				dx = -(resizeGrid.get() - dx);
			}
			float dy = (height % resizeGrid.get());
			if (dy > (resizeGrid.get() / 2)) {
				dy = -(resizeGrid.get() - dy);
			}
			setSize(width - dx, height - dy);
			dx = (x % shiftGrid.get());
			if (dx > (shiftGrid.get() / 2)) {
				dx = -(shiftGrid.get() - dx);
			}
			dy = (y % shiftGrid.get());
			if (dy > (shiftGrid.get() / 2)) {
				dy = -(shiftGrid.get() - dy);
			}
			setPosition(x - dx, y - dy);
			resizing = false;
			t.addAnimation(i.id + ":resize(" + resizeStartSize.x + ":"
					+ resizeStartSize.y + ":" + width + ":" + height + ":"
					+ dragStartPoint.x + ":" + dragStartPoint.y + ":" + x + ":"
					+ y + ")");
			metrics.setFlag(Flag.ADD_CHANGE);
			Gdx.graphics.requestRendering();
		}
	}

	private void stopRotating() {

		if (rotating && (rotateGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_ROTATE, false)) {
			rotating = false;
			// hang in rotation
			if ((angle.get() % rotateGrid.get()) >= (rotateGrid.get() / 2)) {
				angle.set((angle.get() - (angle.get() % rotateGrid.get()))
						+ rotateGrid.get());
			} else {
				angle.set(angle.get() - (angle.get() % rotateGrid.get()));
			}
			while (angle.get() > (360)) {
				angle.set(angle.get() - 360);
			}
			while (angle.get() < 0) {
				angle.set(angle.get() + 360);
			}
			t.addAnimation(i.id + ":rotate(" + rotateStartAngle + ":" + angle
					+ ")");
			angle.setFlag(Flag.ADD_CHANGE);
			Gdx.graphics.requestRendering();
		}
	}

	private void stopShifting() {

		if (shifting && abilities.contains(BasicInteractable.MODE_SHIFT, false)) {
			float dx = (x % shiftGrid.get());
			if (dx > (shiftGrid.get() / 2)) {
				dx = -(shiftGrid.get() - dx);
			}
			float dy = (y % shiftGrid.get());
			if (dy > (shiftGrid.get() / 2)) {
				dy = -(shiftGrid.get() - dy);
			}
			setPosition(x - dx, y - dy);
			z.set(dragStartZ);
			shifting = false;
			for (final Interactable i : l.getAllInteractables()) {
				if (i.depth() <= z.get()
						&& i.isColliding(this, angle.get(), scale.get())) {
					z.set(i.depth() - 1);
				}
			}
			depthList.clear();
			depthList.addAll(l.getAllInteractables());
			depthList.sort(SortableInteractableArray.COMPARE_Z_HIGHEST_BELOW);
			t.setBlockIncomingNetworkTraffic(true);
			for (final Interactable i : depthList) {

				if (Area.class.isAssignableFrom(i.getClass())) {

					final Area a = (Area) i;
					if (a.receiveActionRequest(this, this)) {
						break;
					}
				}
			}
			t.addAnimation(i.id + ":shift(" + dragStartPoint.x + ":"
					+ dragStartPoint.y + ":" + getX() + ":" + getY() + ")");
			metrics.setFlag(Flag.ADD_CHANGE);
			z.setFlag(Flag.ADD_CHANGE);
			Gdx.graphics.requestRendering();
		}
	}

	private void stopScaling() {

		if (scaling && abilities.contains(BasicInteractable.MODE_SCALE, false)) {

			if ((scale.get() % scaleGrid.get()) >= (scaleGrid.get() / 2)) {
				scale.set((scale.get() - (scale.get() % scaleGrid.get()))
						+ scaleGrid.get());
			} else {
				scale.set(scale.get() - (scale.get() % scaleGrid.get()));
			}
			if (scale.get() * width > 10 * Gdx.graphics.getWidth()) {
				scale.set(Gdx.graphics.getWidth() * 10 / width);
			}
			if (scale.get() * width < 10) {
				scale.set(10 / width);
			}
			if (scale.get() * height > 10 * Gdx.graphics.getHeight()) {
				scale.set(Gdx.graphics.getHeight() * 10 / width);
			}
			if (scale.get() * height < 10) {
				scale.set(10 / height);
			}
			scaling = false;
			t.addAnimation(i.id + ":scale(" + startScale + ":" + scale.get()
					+ ")");
			scale.setFlag(Flag.ADD_CHANGE);
			Gdx.graphics.requestRendering();
		}
	}

	protected void switchMode() {

		if (abilities.getSize() > 0) {

			if (mode != null) {

				final int nextIndex = abilities.indexOf(mode, false) + 1;
				if (nextIndex < abilities.getSize()) {

					mode = abilities.get(nextIndex);
				} else {

					mode = abilities.first();
				}
			} else {

				mode = abilities.first();
			}
		}
	}

	protected void updateTransform() {

		updateTransform(this, angle.get(), scale.get());
	}
	
	protected void updateTransformWithoutPosition() {

		updateTransform(new Rectangle(0, 0, width, height), angle.get(), scale.get());
	}

	protected void updateTransform(Rectangle r, int angle, float scale) {

		transform.set(TransformGUI.NORMAL_MATRIX);

		transform.translate(r.x + r.width / 2, r.y + r.height / 2, 0);
		transform.rotate(0, 0, 1, angle);
		transform.scale(scale, scale, 1);
		transform.translate(-r.width / 2, -r.height / 2, 0);

		invertedTransform.set(transform);
		invertedTransform.inv();
	}

	private void updateResizing(final InputEvent e) {

		if (resizing && (resizeGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_RESIZE, false)) {

			float dX = (lastRelativeInputPosition.x - relativePosition.x);
			float dY = (lastRelativeInputPosition.y - relativePosition.y);
			if (resizeArea == ResizingPoint.LowerLeft) {
				resizeOldOppositePosition.set(transformRelativePointToPoint(width, height));
				setSize(width + dX, height + dY);
				updateTransform();
				resizeNewOppositePosition.set(transformRelativePointToPoint(width, height));
				float dsX = resizeNewOppositePosition.x - resizeOldOppositePosition.x;
				float dsY = resizeNewOppositePosition.y - resizeOldOppositePosition.y;
				setPosition(x - dsX, y - dsY);
			}
			if (resizeArea == ResizingPoint.LowerRight) {
				resizeOldOppositePosition.set(transformRelativePointToPoint(0, height));
				setSize(width - dX, height + dY);
				updateTransform();
				resizeNewOppositePosition.set(transformRelativePointToPoint(0, height));
				float dsX = resizeNewOppositePosition.x - resizeOldOppositePosition.x;
				float dsY = resizeNewOppositePosition.y - resizeOldOppositePosition.y;
				setPosition(x - dsX, y - dsY);
			}
			if (resizeArea == ResizingPoint.UpperLeft) {
				resizeOldOppositePosition.set(transformRelativePointToPoint(width, 0));
				setSize(width + dX, height - dY);
				updateTransform();
				resizeNewOppositePosition.set(transformRelativePointToPoint(width, 0));
				float dsX = resizeNewOppositePosition.x - resizeOldOppositePosition.x;
				float dsY = resizeNewOppositePosition.y - resizeOldOppositePosition.y;
				setPosition(x - dsX, y - dsY);
			}
			if (resizeArea == ResizingPoint.UpperRight) {
				resizeOldOppositePosition.set(transformRelativePointToPoint(0, 0));
				setSize(width - dX, height - dY);
				updateTransform();
				resizeNewOppositePosition.set(transformRelativePointToPoint(0, 0));
				float dsX = resizeNewOppositePosition.x - resizeOldOppositePosition.x;
				float dsY = resizeNewOppositePosition.y - resizeOldOppositePosition.y;
				setPosition(x - dsX, y - dsY);
			}
			updateTransform();
			relativePosition.set(transformPointToRelativePoint(e.getPosition()));
			Gdx.graphics.requestRendering();
		}
	}

	private void updateRotating(final InputEvent e) {

		if (rotating && (rotateGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_ROTATE, false)) {

			int actualAngleToMid = 0;
			float diffX = e.getX() - rotateStartMidPoint.x;
			float diffY = e.getY() - rotateStartMidPoint.y;
			if (diffY < 0.0D) {
				actualAngleToMid = (int) Math.toDegrees(Math
						.atan(diffX / diffY));
			} else {
				actualAngleToMid = (int) Math.toDegrees(Math.PI
						+ Math.atan(diffX / diffY));
			}

			int startAngleToMid = 0;
			diffX = rotateStartPoint.x - rotateStartMidPoint.x;
			diffY = rotateStartPoint.y - rotateStartMidPoint.y;
			if (diffY < 0.0D) {
				startAngleToMid = (int) Math
						.toDegrees(Math.atan(diffX / diffY));
			} else {
				startAngleToMid = (int) Math.toDegrees(Math.PI
						+ Math.atan(diffX / diffY));
			}
			actualAngleToMid = (rotateStartAngle + startAngleToMid)
					- actualAngleToMid;
			while (actualAngleToMid > (360)) {
				actualAngleToMid -= 360;
			}
			while (actualAngleToMid < 0) {
				actualAngleToMid += 360;
			}
			angle.set(actualAngleToMid);
			Gdx.graphics.requestRendering();
			wasCatchedByMe = true;
		}
	}

	private void updateShifting(final InputEvent e) {

		if (shifting && (shiftGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_SHIFT, false)) {

			final float dX = lastInputPosition.x - e.getX();
			final float dY = lastInputPosition.y - e.getY();

			setPosition(x - dX, y - dY);
			Gdx.graphics.requestRendering();
		}
	}

	private void updateScaling(final InputEvent e) {

		if (scaling && (scaleGrid.get() > 0)
				&& abilities.contains(BasicInteractable.MODE_SCALE, false)) {

			final float dX = (lastRelativeInputPosition.x - relativePosition.x)
					/ width;
			final float dY = (lastRelativeInputPosition.y - relativePosition.y)
					/ height;

			scale.set(scale.get() - dX - dY);
			Gdx.graphics.requestRendering();
		}
	}

	@Override
	public void style(Skin skin) {

		style = skin.get("default", InteractableStyle.class);
	}

	@Override
	public void style(Skin skin, String styleName) {

		style = skin.get(styleName, InteractableStyle.class);
	}

	@Override
	public void style(Style style) {

		if (InteractableStyle.class.isAssignableFrom(style.getClass())) {
			this.style = (InteractableStyle) style;
		}
	}

	@Override
	public void adaptToScreenSize(int width, int height) {

	}

	public static class ResizeAnimationThread extends Thread {

		private final float animationSeconds;
		Vector2 end;
		private final int fps;

		BasicInteractable parent;
		Vector2 start;
		private final float toStartSeconds;

		public ResizeAnimationThread(final BasicInteractable parent,
				final Vector2 start, final Vector2 end) {

			this.parent = parent;
			this.start = start;
			this.end = end;

			toStartSeconds = 0.5F;
			animationSeconds = 1.0F;
			fps = 60;
		}

		ResizeAnimationThread(final BasicInteractable parent, final Vector2 start,
				final Vector2 end, final int toStartFrames,
				final float animationSeconds, final int fps) {

			this.parent = parent;
			this.start = start;
			this.end = end;

			this.toStartSeconds = toStartFrames;
			this.animationSeconds = animationSeconds;
			this.fps = fps;
		}

		@Override
		public void run() {

			parent.t.setBlockIncomingNetworkTraffic(true);

			long starttime = TimeUtils.millis();
			long time = starttime;
			final Vector2 actualSize = new Vector2(parent.width, parent.height);
			if (!actualSize.equals(end)) {
				while (time - starttime < toStartSeconds * 1000) {
					float d = (time - starttime) / (toStartSeconds * 1000);
					parent.width = actualSize.x
							- ((actualSize.x - start.x) * d);
					parent.height = actualSize.y
							- ((actualSize.y - start.y) * d);
					Gdx.graphics.requestRendering();
					final long sleep = TimeUtils.millis() - time;
					if (sleep < (1000 / fps)) {
						try {
							Thread.sleep((1000 / fps) - sleep);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(1);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}
					time = TimeUtils.millis();
				}
			}

			starttime = TimeUtils.millis();
			time = starttime;
			while (time - starttime < animationSeconds * 1000) {
				// MOVE FROM START TO END
				float d = (time - starttime) / (animationSeconds * 1000);
				parent.width = start.x - ((start.x - end.x) * d);
				parent.height = start.y - ((start.y - end.y) * d);
				Gdx.graphics.requestRendering();
				final long sleep = TimeUtils.millis() - time;
				if (sleep < (1000 / fps)) {
					try {
						Thread.sleep((1000 / fps) - sleep);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep(1);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
				time = TimeUtils.millis();
			}
			parent.setSize(end.x, end.y);
			Gdx.graphics.requestRendering();
			parent.t.setBlockIncomingNetworkTraffic(false);
		}
	}

	public class RotateThread extends Thread {

		private final float animationSeconds;

		private final int end;

		private final int fps;

		private final BasicInteractable parent;
		private final int start;
		private final float toStartSeconds;

		public RotateThread(final BasicInteractable parent, final int start,
				final int end) {

			this.parent = parent;
			this.start = start;
			this.end = end;

			toStartSeconds = 0.5F;
			animationSeconds = 1;
			fps = 60;
		}

		public RotateThread(final BasicInteractable parent, final int start,
				final int end, final float toStartSeconds,
				final float animationSeconds, final int fps) {

			this.parent = parent;
			this.start = start;
			this.end = end;

			this.toStartSeconds = toStartSeconds;
			this.animationSeconds = animationSeconds;
			this.fps = fps;
		}

		@Override
		public void run() {

			parent.t.setBlockIncomingNetworkTraffic(true);
			final int initialAng = parent.angle.get();
			long starttime = TimeUtils.millis();
			long time = starttime;
			if (initialAng != end) {
				// MOVE TO START
				while (time - starttime < toStartSeconds * 1000) {
					float d = (time - starttime) / (toStartSeconds * 1000);
					parent.angle.set(MathUtils.ceil(initialAng
							- (initialAng - start) * d));
					Gdx.graphics.requestRendering();
					final long sleep = TimeUtils.millis() - time;
					if (sleep < (1000 / fps)) {
						try {
							Thread.sleep((1000 / fps) - sleep);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(1);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}
					time = TimeUtils.millis();
				}

				starttime = TimeUtils.millis();
				time = starttime;
				// MOVE FROM START TO END
				while (time - starttime < animationSeconds * 1000) {
					float d = (time - starttime) / (animationSeconds * 1000);
					parent.angle.set(MathUtils.ceil(start - (start - end) * d));
					Gdx.graphics.requestRendering();
					final long sleep = TimeUtils.millis() - time;
					if (sleep < (1000 / fps)) {
						try {
							Thread.sleep((1000 / fps) - sleep);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(1);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}
					time = TimeUtils.millis();
				}
				parent.angle.set(end);
				Gdx.graphics.requestRendering();
				parent.t.setBlockIncomingNetworkTraffic(false);
			}
		}
	}

	public class ShiftAnimationThread extends Thread {

		private final float animationSeconds;
		Vector2 end;
		private final int fps;

		BasicInteractable parent;
		Vector2 start;
		private final float toStartSeconds;

		public ShiftAnimationThread(final BasicInteractable parent,
				final Vector2 start, final Vector2 end) {

			this.parent = parent;
			this.start = start;
			this.end = end;

			toStartSeconds = 0.5F;
			animationSeconds = 1;
			fps = 60;
		}

		ShiftAnimationThread(final BasicInteractable parent, final Vector2 start,
				final Vector2 end, final float toStartSeconds,
				final float animationSeconds, final int fps) {

			this.parent = parent;
			this.start = start;
			this.end = end;

			this.toStartSeconds = toStartSeconds;
			this.animationSeconds = animationSeconds;
			this.fps = fps;
		}

		@Override
		public void run() {

			parent.t.setBlockIncomingNetworkTraffic(true);
			final Vector2 actualPos = new Vector2(parent.x, parent.y);

			long starttime = TimeUtils.millis();
			long time = starttime;
			if (!actualPos.equals(end)) {
				// MOVE TO START
				while (time - starttime < toStartSeconds * 1000) {
					float d = (time - starttime) / (toStartSeconds * 1000);
					parent.setX(actualPos.x - (actualPos.x - start.x) * d);
					parent.setY(actualPos.y - (actualPos.y - start.y) * d);
					Gdx.graphics.requestRendering();
					final long sleep = TimeUtils.millis() - time;
					if (sleep < (1000 / fps)) {
						try {
							Thread.sleep((1000 / fps) - sleep);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(1);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}
					time = TimeUtils.millis();
				}

				starttime = TimeUtils.millis();
				time = starttime;
				// MOVE FROM START TO END
				while (time - starttime < animationSeconds / 2) {
					float d = (time - starttime) / (animationSeconds / 2);
					parent.setX(start.x - (end.x - start.x) * d);
					parent.setY(start.y - (end.y - start.y) * d);
					Gdx.graphics.requestRendering();
					final long sleep = TimeUtils.millis() - time;
					if (sleep < (1000 / fps)) {
						try {
							Thread.sleep((1000 / fps) - sleep);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(1);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}
					time = TimeUtils.millis();
				}
				parent.setPosition(end.x, end.y);
				Gdx.graphics.requestRendering();
				parent.t.setBlockIncomingNetworkTraffic(false);
			}
		}
	}

	
	@Override
	public boolean isColliding(Vector2 point) {

		collidingPoint.set(point);
		collidingPoint.set(transformPointToRelativePoint(collidingPoint));
		if (collidingPoint.x >= 0 && collidingPoint.x <= width
				&& collidingPoint.y >= 0 && collidingPoint.y <= height) {
			return true;
		}
		return false;
	}

	@Override
	public float depth() {

		return z.get();
	}

	@Override
	public boolean isColliding(Rectangle r, int angle, float scale) {

		updateTransform(r, angle, scale);
		pLL.set(0, 0);
		pLR.set(r.width, 0);
		pUL.set(0, r.height);
		pUR.set(r.width, r.height);
		pM.set(r.width / 2, r.height / 2);
		return (isColliding(pLL) || isColliding(pLR) || isColliding(pUL)
				|| isColliding(pUR) || isColliding(pM));
	}
}
