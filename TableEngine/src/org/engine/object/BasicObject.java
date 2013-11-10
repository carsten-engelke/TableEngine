package org.engine.object;

import java.util.Arrays;

import org.engine.Interactable;
import org.engine.Interactable.Animateable;
import org.engine.Layer;
import org.engine.Skinnable;
import org.engine.Synchronizable;
import org.engine.TableEngine;
import org.engine.geometry.Cube;
import org.engine.geometry.Vector2;
import org.engine.geometry.Vector3;
import org.engine.geometry.Vector3.Vector3Property;
import org.engine.gui.input.InputEvent;
import org.engine.gui.output.Graphics;
import org.engine.object.area.Area;
import org.engine.object.popup.Popup;
import org.engine.object.popup.PopupSlave;
import org.engine.property.FloatProperty;
import org.engine.property.Information;
import org.engine.property.InformationArrayStringException;
import org.engine.property.IntegerProperty;
import org.engine.property.Property;
import org.engine.resource.BasicResource;
import org.engine.utils.Array;
import org.engine.utils.MathUtils;
import org.engine.utils.SortableInteractableArray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * The Superclass for all objects put into the TableEngine. You can specify
 * which interaction is applicable by overwriting the abilities of this class.
 */
public class BasicObject extends Cube implements Object, Animateable,
		PopupSlave, Skinnable, Synchronizable {

	public static final String MODE_RESIZE = "RESIZE";

	public static final String MODE_ROTATE = "ROTATE";

	public static final String MODE_SCALE = "SCALE";

	public static final String MODE_SELECT = "SELECT";

	public static final String MODE_SHIFT = "SHIFT";

	public static final String OBJECT_TAG = "O";

	protected InteractableStyle style;

	protected Layer l;
	protected TableEngine t;

	protected Array<String> abilities = new Array<String>();

	public IntegerProperty angle = new IntegerProperty("ANGLE", OBJECT_TAG, 0);

	protected Vector2 dragStartPoint = new Vector2();

	protected float dragStartZ;

	protected Matrix4 invertedTransform = new Matrix4();

	/** The input mouse position. */
	protected Vector2 lastInputPosition = new Vector2();
	protected CubeProperty metrics = new CubeProperty("METRICS", OBJECT_TAG,
			this);
	protected String mode = null;

	protected IntegerProperty resizeGrid = new IntegerProperty("RESIZE-GRID",
			OBJECT_TAG, 1);
	protected Vector2 resizeStartSize;

	protected boolean resizing = false;
	protected IntegerProperty rotateGrid = new IntegerProperty("ROTATE-GRID",
			OBJECT_TAG, 90);
	protected int rotateStartAngle;
	protected Vector2 rotateStartMidPoint;

	protected Vector2 rotateStartPoint;
	protected boolean rotating = false;

	protected FloatProperty scale = new FloatProperty("SCALE", OBJECT_TAG, 1.0F);
	protected FloatProperty scaleGrid = new FloatProperty("SCALE-GRID",
			OBJECT_TAG, 0.25F);

	protected boolean scaling = false;
	protected boolean selected = false;
	protected IntegerProperty shiftGrid = new IntegerProperty("SHIFT-GRID",
			OBJECT_TAG, 1);
	protected boolean shifting = false;

	protected Matrix4 transformMatrix = new Matrix4();

	public Vector3Property untransformedSize = new Vector3Property("SIZE",
			OBJECT_TAG, new Vector3());

	protected boolean wasCatchedByMe = false;

	private Information i;

	private boolean flagged;

	public BasicObject() {

		super();
	}

	public BasicObject(final Cube c, final int shiftGrid) {

		this();
		setBounds(c);
		this.shiftGrid.set(shiftGrid);
	}

	public BasicObject(final Cube c, final int shiftGrid, final int angle,
			final int rotateGrid) {

		this();
		setBounds(c);
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
	public Cube getBounds() {

		return this;
	}

	@Override
	public Layer getParentLayer() {

		return l;
	}

	protected Vector2 getPoint2DRelativeToOrigin(final Vector2 pointOnLayer) {

		final Vector3 relativeToOrigin = new Vector3(pointOnLayer.x,
				pointOnLayer.y, z);
		relativeToOrigin.mul(invertedTransform);
		relativeToOrigin.set(relativeToOrigin.x - x, relativeToOrigin.y - y,
				relativeToOrigin.z - z);
		return new Vector2(relativeToOrigin.x, relativeToOrigin.y);
	}

	protected Vector3 getPointRelativeToOrigin(final Vector3 pointInUniverse) {

		final Vector3 relativeToOrigin = pointInUniverse;
		relativeToOrigin.mul(invertedTransform);
		relativeToOrigin.set(relativeToOrigin.x - x, relativeToOrigin.y - y,
				relativeToOrigin.z - z);
		return relativeToOrigin;
	}

	protected Cube getSizeIfTransformWas(final Vector3 untransformedSize,
			final int angle, final float scale) {

		final Matrix4 calculateBoundsMatrix = new Matrix4();
		calculateBoundsMatrix.translate((untransformedSize.x / 2) * scale,
				(untransformedSize.y / 2) * scale, 0);
		calculateBoundsMatrix.rotate(0, 0, 1, angle);
		calculateBoundsMatrix.translate((-untransformedSize.x / 2) * scale,
				(-untransformedSize.y / 2) * scale, 0);
		final Vector3 pTopLeft = new Vector3(0, 0, 0);
		final Vector3 pTopRight = new Vector3(untransformedSize.x * scale, 0, 0);
		final Vector3 pBottomLeft = new Vector3(0, untransformedSize.y * scale,
				0);
		final Vector3 pBottomRight = new Vector3(untransformedSize.x * scale,
				untransformedSize.y * scale, 0);
		pTopLeft.mul(calculateBoundsMatrix);
		pTopRight.mul(calculateBoundsMatrix);
		pBottomLeft.mul(calculateBoundsMatrix);
		pBottomRight.mul(calculateBoundsMatrix);

		final int[] valueX = { (int) pTopLeft.x, (int) pTopRight.x,
				(int) pBottomLeft.x, (int) pBottomRight.x };
		final int[] valueY = { (int) pTopLeft.y, (int) pTopRight.y,
				(int) pBottomLeft.y, (int) pBottomRight.y };
		Arrays.sort(valueX);
		Arrays.sort(valueY);

		final int deltaX = valueX[3] - valueX[0];
		final int deltaY = valueY[3] - valueY[0];
		return new Cube(valueX[0], valueY[0], getZ(), deltaX, deltaY,
				getDepth());
	}

	@Override
	public void initialize(final Layer l) {

		this.l = l;
		t = l.t;
		style(t.uiSkin, t.uiStyleName);
		adaptToScreenSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		i = new Information(t.getNewObjectID(getClass()), OBJECT_TAG,
				Information.PropertiesToString(getProperties()));
	}

	@Override
	public boolean input(final InputEvent e, final boolean wasCatchedAbove) {
		boolean isCatchedByMe = false;

		// See if the MOVE-EVENT is for this instance a ENTER OR EXIT EVENT.
		if (e.getType() == InputEvent.TYPE_MOVE_POS) {

			if (getRectangle2D().contains(lastInputPosition.x,
					lastInputPosition.y)
					&& getRectangle2D().contains(e.getX(), e.getY())) {
				// mouse moved in area -> comes from or goes to overlying
				// object?
				if (!wasCatchedAbove) {
					// this is the topmost object
					if (wasCatchedByMe) {
						// was last time in area
						isCatchedByMe = posMoved(e);
					} else {
						// was last time not in area
						isCatchedByMe = posEntered(e);
					}
				} else {
					// this is not the topmost object
					if (wasCatchedByMe) {
						// was last time in area, is now in overlying object
						isCatchedByMe = posExited(e);
					}
				}

			}

			if (!getRectangle2D().contains(lastInputPosition.x,
					lastInputPosition.y)
					&& getRectangle2D().contains(e.getX(), e.getY())
					&& !wasCatchedAbove) {

				isCatchedByMe = posEntered(e);

			}

			if (getRectangle2D().contains(lastInputPosition.x,
					lastInputPosition.y)
					&& !getRectangle2D().contains(e.getX(), e.getY())
					&& wasCatchedByMe) {

				isCatchedByMe = posExited(e);
			}

		}
		if ((e.getType() >= InputEvent.TYPE_POS_MIN)
				&& (e.getType() <= InputEvent.TYPE_POS_MAX)) {
			if ((getRectangle2D().contains(e.getX(), e.getY()) || shifting
					|| resizing || rotating || scaling)
					&& !wasCatchedAbove) {

				switch (e.getType()) {
				case (InputEvent.TYPE_CLICK_POS):
					isCatchedByMe = posClicked(e);
					break;
				case (InputEvent.TYPE_RELEASE_POS):
					isCatchedByMe = posReleased(e);
					break;
				case (InputEvent.TYPE_PRESS_POS):
					isCatchedByMe = posPressed(e);
					break;
				case (InputEvent.TYPE_DRAG_POS):
					isCatchedByMe = posDragged(e);
					break;
				}
			} else {

				if (!getRectangle2D().contains(e.getX(), e.getY())) {

					switch (e.getType()) {
					case (InputEvent.TYPE_CLICK_POS):
						isCatchedByMe = posClickedOutside(e);
						break;
					case (InputEvent.TYPE_RELEASE_POS):
						isCatchedByMe = posReleasedOutside(e);
						break;
					case (InputEvent.TYPE_PRESS_POS):
						isCatchedByMe = posPressedOutside(e);
						break;
					case (InputEvent.TYPE_DRAG_POS):
						isCatchedByMe = posDraggedOutside(e);
						break;
					}
				}
			}

			lastInputPosition = e.getPosition();
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
		updateAffineTransform();
		final Matrix4 oldAT = new Matrix4(g.getTransformMatrix());
		final Matrix4 newAT = new Matrix4(g.getTransformMatrix());
		newAT.mul(transformMatrix);
		g.setTransformMatrix(newAT);
		paintTransformed(g);
		paintForeground(g);
		g.setTransformMatrix(oldAT);
		paintForegroundUntransformed(g);
		if (t.debugMode) {
			paintDebug(g);
		}
	}

	private void paintDebug(Graphics g) {

		g.setFont(t.uiSkin.getFont("default-font"));
		g.drawString(getClass().getCanonicalName() + "(" + x + ", " + y + ") "
				+ angle + "° " + scale + "F", getX(), getY());
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

			g.drawUI(style.selected, x, y, untransformedSize.get().x,
					untransformedSize.get().y);
		}
	}

	protected void paintForegroundUntransformed(final Graphics g) {

		if (resizing || shifting || rotating || scaling || selected) {

			if (mode.equals(BasicObject.MODE_RESIZE) && selected) {
				g.drawUI(style.resize, (x + width) - 50, (y + height) - 50,
						100, 100);
			}
			if (mode.equals(BasicObject.MODE_SHIFT) && selected) {
				g.drawUI(style.shift, (x + width) - 50, (y + height) - 50, 100,
						100);
			}
			if (mode.equals(BasicObject.MODE_ROTATE) && selected) {
				g.drawUI(style.rotate, (x + width) - 50, (y + height) - 50,
						100, 100);
			}
			if (mode.equals(BasicObject.MODE_SCALE) && selected) {
				g.drawUI(style.scale, (x + width) - 50, (y + height) - 50, 100,
						100);
			}
		}
	}

	protected void paintTransformed(final Graphics g) {

		g.drawUI(style.background, x, y, untransformedSize.get().x,
				untransformedSize.get().y);
	}

	protected void paintUntransformed(final Graphics g) {

	}

	public boolean posClicked(final InputEvent e) {

		stopShifting();
		stopRotating();
		stopResizing();
		if (e.getCharacter() == InputEvent.CHAR_NORMAL_POS) {

			if (abilities.getSize() > 0) {
				selected = true;
			}
			switchMode();
		} else {

			showPopup(e.getX_on_Screen(), e.getY_on_Screen());
		}
		return true;

	}

	protected boolean posClickedOutside(final InputEvent e) {

		stopShifting();
		stopRotating();
		selected = false;
		return false;
	}

	public boolean posDragged(final InputEvent e) {

		updateShifting(e);
		updateRotating(e);
		updateResizing(e);
		return true;

	}

	protected boolean posDraggedOutside(final InputEvent e) {
		return false;
	}

	public boolean posEntered(final InputEvent e) {

		updateShifting(e);
		return true;

	}

	public boolean posExited(final InputEvent e) {

		if (shifting && (shiftGrid.get() > 0)
				&& abilities.contains(BasicObject.MODE_SHIFT, false)) {

			final float dX = lastInputPosition.x - e.getX();
			final float dY = lastInputPosition.y - e.getY();

			setLocation(getX() - dX, getY() - dY, getZ());
			Gdx.graphics.requestRendering();
			return true;
		}

		return true;

	}

	public boolean posMoved(final InputEvent e) {

		return true;

	}

	public boolean posPressed(final InputEvent e) {

		if (abilities.getSize() > 0) {
			selected = true;
			if (mode == null) {
				switchMode();
			}
		}
		startShifting(e);
		startRotating(e);
		startResizing(e);
		return true;

	}

	protected boolean posPressedOutside(final InputEvent e) {
		if ((mode == BasicObject.MODE_RESIZE)
				&& (resizeGrid.get() > 0)
				&& abilities.contains(BasicObject.MODE_RESIZE, false)
				&& new Rectangle((getX() + getWidth()) - 50,
						(getY() + getHeight()) - 50, 100, 100).contains(
						e.getX(), e.getY())) {

			resizeStartSize = e.getPosition();
			resizing = true;
			return true;
		}
		return false;
	}

	public boolean posReleased(final InputEvent e) {

		if (shifting && (shiftGrid.get() > 0)
				&& abilities.contains(BasicObject.MODE_SHIFT, false)) {
			t.getSound(BasicResource.DESCRIPTION_ID, "drop").play();
			stopShifting();
		}
		if (rotating && (rotateGrid.get() > 0)
				&& abilities.contains(BasicObject.MODE_ROTATE, false)) {
			t.getSound(BasicResource.DESCRIPTION_ID, "drop").play();
			stopRotating();
		}
		if (resizing && (resizeGrid.get() > 0)
				&& abilities.contains(BasicObject.MODE_RESIZE, false)) {
			t.getSound(BasicResource.DESCRIPTION_ID, "drop").play();
			stopResizing();
		}
		return true;

	}

	protected boolean posReleasedOutside(final InputEvent e) {
		return false;
	}

	public boolean posZoom(final InputEvent e) {

		return true;

	}

	@Override
	public void setPropertiesFromInformation(final Array<Information> a) {

		for (Information i : a) {
			metrics.applySyncInfo(i);
			untransformedSize.applySyncInfo(i);
			shiftGrid.applySyncInfo(i);
			resizeGrid.applySyncInfo(i);
			rotateGrid.applySyncInfo(i);
			angle.applySyncInfo(i);
			scale.applySyncInfo(i);
		}
	}

	public Array<Property<?>> getProperties() {

		Array<Property<?>> ap = new Array<Property<?>>(new Property<?>[] {
				metrics, untransformedSize, shiftGrid, resizeGrid, rotateGrid,
				angle, scale });
		return ap;

	}

	@Override
	public Array<Property<?>> getPropertiesFlaggedOnly() {

		Property<?>[] props = new Property<?>[] { metrics, untransformedSize,
				shiftGrid, resizeGrid, rotateGrid, angle, scale };
		Array<Property<?>> retProps = new Array<Property<?>>(props.length);
		for (Property<?> p : props) {
			if (p.isFlagged()) {
				retProps.add(p);
			}
		}
		if (retProps.getSize() > 0) {
			return retProps;
		}
		return null;
	}

	@Override
	public boolean isFlagged() {

		return flagged;
	}

	@Override
	public void receiveOrder(final String order) {

		System.out.println("Order: " + order);
		// t.setBlockIncomingNetworkTraffic(true);
	}

	@Override
	public void setBounds(final Cube bounds) {

		setLocation(bounds.getLocation());
		setSize(bounds.getSize());

	}

	protected void showPopup(final float x, final float y) {

		t.setBlockIncomingNetworkTraffic(true);
		final Popup p = new Popup(new Array<String>(new String[] {
				"Test Option 1", "Test Option 2" }), this);
		p.setLocation(x, y, 1000);
		mode = BasicObject.MODE_SELECT;
		t.getLayer(TableEngine.POPUP_LAYER).addInteractable(p);
	}

	private void startResizing(final InputEvent e) {

		if ((mode == BasicObject.MODE_RESIZE) && (resizeGrid.get() > 0)
				&& abilities.contains(BasicObject.MODE_RESIZE, false)) {
			if (new Rectangle((x + width) - 50, (y + height) - 50, 100, 100)
					.contains(e.getX(), e.getY())) {
				resizeStartSize = new Vector2(untransformedSize.get().x,
						untransformedSize.get().y);
				resizing = true;
			}

		}
	}

	private void startRotating(final InputEvent e) {

		if ((mode == BasicObject.MODE_ROTATE) && (rotateGrid.get() > 0)
				&& abilities.contains(BasicObject.MODE_ROTATE, false)) {
			t.setBlockIncomingNetworkTraffic(true);
			// start rotating
			rotateStartPoint = e.getPosition();
			rotateStartMidPoint = new Vector2(getX() + (getWidth() / 2), getY()
					+ (getHeight() / 2));
			rotateStartAngle = angle.get();
			while (rotateStartAngle > (360)) {
				rotateStartAngle -= 360;
			}
			while (rotateStartAngle < 0) {
				rotateStartAngle += 360;
			}
			rotating = true;
			dragStartZ = getZ();
			for (final Interactable i : l.getAllInteractables()) {
				if (i.getBounds().getZ() <= getZ()) {
					setZ(i.getBounds().getZ() - 1);
				}
			}
		}
	}

	private void startShifting(final InputEvent e) {

		if ((mode == BasicObject.MODE_SHIFT) && (shiftGrid.get() > 0)
				&& abilities.contains(BasicObject.MODE_SHIFT, false)) {
			t.setBlockIncomingNetworkTraffic(true);
			shifting = true;
			dragStartZ = getZ();
			dragStartPoint = new Vector2(getX(), getY());
			for (final Interactable i : l.getAllInteractables()) {
				if (i.getBounds().getZ() <= getZ()) {
					setZ(i.getBounds().getZ() - 1);
				}
			}
		}
	}

	private void stopResizing() {

		if (resizing && abilities.contains(BasicObject.MODE_RESIZE, false)) {
			float dx = (untransformedSize.get().x % resizeGrid.get());
			if (dx > (resizeGrid.get() / 2)) {
				dx = -(resizeGrid.get() - dx);
			}
			float dy = (untransformedSize.get().y % resizeGrid.get());
			if (dy > (resizeGrid.get() / 2)) {
				dy = -(resizeGrid.get() - dy);
			}
			untransformedSize.set(new Vector3(untransformedSize.get().x - dx,
					untransformedSize.get().y - dy, untransformedSize.get().z));
			resizing = false;
			setSize(getSizeIfTransformWas(untransformedSize.get(), angle.get(),
					scale.get()).getSize());
			t.addAnimation(i.id + ":resize(" + resizeStartSize.x + ":"
					+ resizeStartSize.y + ":" + untransformedSize.get().x + ":"
					+ untransformedSize.get().y + ")");
			Gdx.graphics.requestRendering();
		}
	}

	private void stopRotating() {

		if (rotating && (rotateGrid.get() > 0)
				&& abilities.contains(BasicObject.MODE_ROTATE, false)) {
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
			setSize(getSizeIfTransformWas(untransformedSize.get(), angle.get(),
					scale.get()).getSize());
			t.addAnimation(i.id + ":rotate(" + rotateStartAngle + ":" + angle
					+ ")");
			Gdx.graphics.requestRendering();
		}
	}

	private void stopShifting() {

		if (shifting && abilities.contains(BasicObject.MODE_SHIFT, false)) {
			float dx = (x % shiftGrid.get());
			if (dx > (shiftGrid.get() / 2)) {
				dx = -(shiftGrid.get() - dx);
			}
			float dy = (y % shiftGrid.get());
			if (dy > (shiftGrid.get() / 2)) {
				dy = -(shiftGrid.get() - dy);
			}
			setLocation(x - dx, y - dy, dragStartZ);
			shifting = false;
			for (final Interactable i : l.getAllInteractables()) {
				if (i.getBounds().getRectangle2D()
						.overlaps(getBounds().getRectangle2D())
						&& (i.getBounds().getZ() <= getZ())) {
					setZ(i.getBounds().getZ() - 1);
				}
			}
			final SortableInteractableArray depthList = new SortableInteractableArray();
			depthList.addAll(l.getAllInteractables());
			depthList.sort(SortableInteractableArray.COMPARE_Z_HIGHEST_BELOW);
			t.setBlockIncomingNetworkTraffic(true);
			for (final Interactable i : depthList) {

				try {
					final Area a = (Area) i;
					if (a.receiveActionRequest(this, getRectangle2D())) {
						break;
					}
				} catch (final ClassCastException c) {
				}
			}
			t.addAnimation(i.id + ":shift(" + dragStartPoint.x + ":"
					+ dragStartPoint.y + ":" + getX() + ":" + getY() + ")");
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

	protected void updateAffineTransform() {

		transformMatrix = new Matrix4();

		// look at the new borders -> transforming will set new borders so
		// estimate how much translation has to be done. Calculate the
		// translation into the rotated world of the image and apply it!
		float dX = getSizeIfTransformWas(untransformedSize.get(), angle.get(),
				scale.get()).getX();
		float dY = getSizeIfTransformWas(untransformedSize.get(), angle.get(),
				scale.get()).getY();

		final Vector3 p = new Vector3(dX, dY, 0);
		// invertedTransform = new Matrix4(transformMatrix);
		// invertedTransform.inv();

		transformMatrix.translate(-p.x, -p.y, 0);
		dX = getX() + (untransformedSize.get().x / 2);
		dY = getY() + (untransformedSize.get().y / 2);
		transformMatrix.translate(dX, dY, 0);
		transformMatrix.rotate(0, 0, 1, angle.get());
		transformMatrix.translate(-dX, -dY, 0);

		// rotate image around the position of object
		invertedTransform = new Matrix4(transformMatrix);
		invertedTransform.inv();
	}

	private void updateResizing(final InputEvent e) {

		if (resizing && (resizeGrid.get() > 0)
				&& abilities.contains(BasicObject.MODE_RESIZE, false)) {

			float dX = (lastInputPosition.x - e.getX()) * scale.get();
			float dY = (lastInputPosition.y - e.getY()) * scale.get();
			float tmp;
			if ((angle.get() == 90) || (angle.get() == 270)) {
				tmp = dX;
				dX = dY;
				dY = tmp;
			}
			untransformedSize.set(new Vector3(untransformedSize.get().x - dX,
					untransformedSize.get().y - dY, untransformedSize.get().z));
			setSize(getSizeIfTransformWas(untransformedSize.get(), angle.get(),
					scale.get()).getSize());
			Gdx.graphics.requestRendering();
		}
	}

	private void updateRotating(final InputEvent e) {

		if (rotating && (rotateGrid.get() > 0)
				&& abilities.contains(BasicObject.MODE_ROTATE, false)) {

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
				&& abilities.contains(BasicObject.MODE_SHIFT, false)) {

			final float dX = lastInputPosition.x - e.getX();
			final float dY = lastInputPosition.y - e.getY();

			setLocation(getX() - dX, getY() - dY, getZ());
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

		BasicObject parent;
		Vector2 start;
		private final float toStartSeconds;

		public ResizeAnimationThread(final BasicObject parent,
				final Vector2 start, final Vector2 end) {

			this.parent = parent;
			this.start = start;
			this.end = end;

			toStartSeconds = 0.5F;
			animationSeconds = 1.0F;
			fps = 60;
		}

		ResizeAnimationThread(final BasicObject parent, final Vector2 start,
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
			final Vector2 actualSize = parent.getSize2D();
			if (!actualSize.equals(end)) {
				while (time - starttime < toStartSeconds * 1000) {
					float d = (time - starttime) / (toStartSeconds * 1000);
					parent.untransformedSize.get().x = actualSize.x
							- ((actualSize.x - start.x) * d);
					parent.untransformedSize.get().y = actualSize.y
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
				parent.untransformedSize.get().x = start.x
						- ((start.x - end.x) * d);
				parent.untransformedSize.get().y = start.y
						- ((start.y - end.y) * d);
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
			parent.untransformedSize.set(new Vector3(end.x, end.y, parent
					.getZ()));
			Gdx.graphics.requestRendering();
			parent.t.setBlockIncomingNetworkTraffic(false);
		}
	}

	public class RotateThread extends Thread {

		private final float animationSeconds;

		private final int end;

		private final int fps;

		private final BasicObject parent;
		private final int start;
		private final float toStartSeconds;

		public RotateThread(final BasicObject parent, final int start,
				final int end) {

			this.parent = parent;
			this.start = start;
			this.end = end;

			toStartSeconds = 0.5F;
			animationSeconds = 1;
			fps = 60;
		}

		public RotateThread(final BasicObject parent, final int start,
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

		BasicObject parent;
		Vector2 start;
		private final float toStartSeconds;

		public ShiftAnimationThread(final BasicObject parent,
				final Vector2 start, final Vector2 end) {

			this.parent = parent;
			this.start = start;
			this.end = end;

			toStartSeconds = 0.5F;
			animationSeconds = 1;
			fps = 60;
		}

		ShiftAnimationThread(final BasicObject parent, final Vector2 start,
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
			final Vector2 actualPos = parent.getLocation2D();

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
				parent.setLocation(end.x, end.y, parent.getZ());
				Gdx.graphics.requestRendering();
				parent.t.setBlockIncomingNetworkTraffic(false);
			}
		}
	}

	@Override
	public Information info() {

		i.content = Information.PropertiesToString(getProperties());
		return i;
	}

	@Override
	public void applySyncInfo(Information i) {

		if (i.id.equals(this.i.id)) {

			try {
				setPropertiesFromInformation(Information
						.StringToInformations(i.content));
				this.i.tag = i.tag;
				this.i.content = i.content;
			} catch (InformationArrayStringException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Synchronizable get() {

		return this;
	}

	@Override
	public void set(Synchronizable value) {

		setPropertiesFromInformation(Information.PropertiesToInformations(value
				.getProperties()));
	}

	@Override
	public void initialize(Information i, Layer l) {

		this.i = i;
		applySyncInfo(i);
		this.l = l;
	}

	@Override
	public void setFlagged(boolean flagged) {

		this.flagged = flagged;
	}
}
