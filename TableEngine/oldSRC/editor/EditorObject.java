package org.engine.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.engine.geometry.Cube;

import com.badlogic.gdx.math.Matrix4;
import org.engine.geometry.Rectangle;
import org.engine.geometry.Vector2;
import org.engine.geometry.Vector3;

public class EditorObject extends Cube {

	public static final String count = "<NUMBER>";
	public static final String[] nonNewTypes = new String[] { "boolean",
			"Boolean", "int", "Integer", "float", "Float", "Double", "double",
			"String", "boolean[]", "int[]", "float[]", "double[]", "String[]" };

	int angle;
	String className;
	/** The dragging. */
	protected boolean dragging = false;
	/** The drag start point. */
	protected Vector2 dragStartPoint = new Vector2();
	/** The drag start z. */
	protected float dragStartZ;
	String hint = "";

	// DRAG DROP STUFF (From DragDropSynchronizable)

	/** The input mouse position. */
	protected Vector2 inputMousePosition = new Vector2();

	String name = "<Unnamed>";

	ProjectionPanel parent;

	ArrayList<Property> props = new ArrayList<Property>();

	/** The was catched by me. */
	protected boolean wasCatchedByMe = false;

	public EditorObject(final String paramString, final ProjectionPanel parent) {

		this.parent = parent;
		loadFromString(paramString);
	}

	public String getContent(final String name) {

		for (final Property p : props) {
			if (p.name.equals(name)) {
				return p.content;
			}
		}
		return null;
	}

	public ArrayList<String[]> getPropertyTable() {

		final ArrayList<String[]> export = new ArrayList<String[]>();
		for (final Property p : props) {
			export.add(new String[] { p.name, p.type, p.content });
		}
		return export;
	}

	@Override
	public Rectangle getRectangle2D() {

		return new Rectangle(getX(), getY(), getSizeIfAngleWas(angle).x,
				getSizeIfAngleWas(angle).y);
	}

	/**
	 * Gets the size if angle was.
	 * 
	 * @param angle
	 *            the angle
	 * @return the size if angle was
	 */
	private Vector3 getSizeIfAngleWas(final int angle) {

		final Matrix4 at = new Matrix4();
		at.rotate(0, 0, 0, angle);
		final Vector3 pTopLeft = new Vector3(0, 0, 0);
		final Vector3 pTopRight = new Vector3(getWidth(), 0, 0);
		final Vector3 pBottomLeft = new Vector3(0, getHeight(), 0);
		final Vector3 pBottomRight = new Vector3(getWidth(), getHeight(), 0);

		final float[] valueX = { pTopLeft.x, pTopRight.x, pBottomLeft.x,
				pBottomRight.x };
		final float[] valueY = { pTopLeft.y, pTopRight.y, pBottomLeft.y,
				pBottomRight.y };
		Arrays.sort(valueX);
		Arrays.sort(valueY);

		final float deltaX = valueX[3] - valueX[0];
		final float deltaY = valueY[3] - valueY[0];
		return new Vector3(deltaX, deltaY, getDepth());
	}

	public String getType(final String name) {

		for (final Property p : props) {
			if (p.name.equals(name)) {
				return p.type;
			}
		}
		return null;
	}

	public boolean hasProperty(final String name) {

		for (final Property p : props) {
			if (p.name.equals(name)) {
				return true;
			}
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.engine.Synchronizable#input(java.awt.event.MouseEvent,
	 * java.awt.event.KeyEvent, java.awt.event.MouseWheelEvent)
	 */
	public boolean input(final MouseEvent e, final boolean wasCatchedAbove) {

		boolean isCatchedByMe = false;

		if (e != null) {

			if (e.getID() == MouseEvent.MOUSE_MOVED) {

				if (getRectangle2D().contains(inputMousePosition.x,
						inputMousePosition.y)
						&& getRectangle2D().contains(e.getPoint().x,
								e.getPoint().y)) {
					// mouse moved in area -> comes from or goes to overlying
					// object?
					if (!wasCatchedAbove) {
						// this is the topmost object
						if (wasCatchedByMe) {
							// was last time in area
							isCatchedByMe = mouseMoved(e);
						} else {
							// was last time not in area
							isCatchedByMe = mouseEntered(e);
						}
					} else {
						// this is not the topmost object
						if (wasCatchedByMe) {
							// was last time in area, is now in overlying object
							isCatchedByMe = mouseExited(e);
						}
					}

				}

				if (!getRectangle2D().contains(inputMousePosition.x,
						inputMousePosition.y)
						&& getRectangle2D().contains(e.getPoint().x,
								e.getPoint().y) && !wasCatchedAbove) {

					isCatchedByMe = mouseEntered(e);

				}

				if (getRectangle2D().contains(inputMousePosition.x,
						inputMousePosition.y)
						&& !getRectangle2D().contains(e.getPoint().x,
								e.getPoint().y) && wasCatchedByMe) {

					isCatchedByMe = mouseExited(e);
				}

			}
			if ((getRectangle2D().contains(e.getPoint().x, e.getPoint().y) || dragging)
					&& !wasCatchedAbove) {

				switch (e.getID()) {
				case (MouseEvent.MOUSE_CLICKED):
					isCatchedByMe = mouseClicked(e);
					break;
				case (MouseEvent.MOUSE_RELEASED):
					isCatchedByMe = mouseReleased(e);
					break;
				case (MouseEvent.MOUSE_PRESSED):
					isCatchedByMe = mousePressed(e);
					break;
				case (MouseEvent.MOUSE_DRAGGED):
					isCatchedByMe = mouseDragged(e);
					break;
				}
			}
			inputMousePosition = new Vector2(e.getPoint().x, e.getPoint().y);

		}
		wasCatchedByMe = isCatchedByMe;
		if (isCatchedByMe || wasCatchedAbove) {
			return true;
		} else {
			return false;
		}
	}

	public void loadFromString(final String paramString) {

		final String[] s = paramString.split("<>");
		className = s[0];
		name = s[1];
		hint = s[2];
		for (int i = 3; i < s.length; i++) {
			if (s[i].contains(";")) {
				final Property p = new Property(s[i].split(";"));
				props.add(p);
				if (p.name.equals("Size")) {
					setBounds(new Cube(p.content.substring(
							p.content.indexOf("\"") + 1,
							p.content.lastIndexOf("\""))));
				}
				if (p.name.equals("UniqueID")) {
					name = p.content.substring(p.content.indexOf("\"") + 1,
							p.content.lastIndexOf("\""));
				}
				if (p.name.equals("Angle")) {
					angle = Integer.parseInt(p.content);
				}
			}

		}

	}

	/**
	 * Mouse clicked.
	 * 
	 * @param e
	 *            the Event
	 * @return true, if blocking input to underlying objects
	 */
	public boolean mouseClicked(final MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.getClickCount() > 1) {
				new EditorObjectChangeSizeAngle(this).setVisible(true);

			} else {

				parent.descriptionList.setSelectedValue(this, true);
			}

		}
		return true;

	}

	/**
	 * Mouse dragged.
	 * 
	 * @param e
	 *            the Event
	 * @return true, if blocking input to underlying objects
	 */
	public boolean mouseDragged(final MouseEvent e) {

		if (dragging) {
			final float dX = inputMousePosition.x - e.getPoint().x;
			final float dY = inputMousePosition.y - e.getPoint().y;
			setLocation(getX() - dX, getY() - dY, getZ());
			parent.repaint();
		}
		return true;

	}

	/**
	 * Mouse entered.
	 * 
	 * @param e
	 *            the Event
	 * @return true, if blocking input to underlying objects
	 */
	public boolean mouseEntered(final MouseEvent e) {

		if (dragging) {
			final float dX = inputMousePosition.x - e.getPoint().x;
			final float dY = inputMousePosition.y - e.getPoint().y;

			setLocation(getX() - dX, getY() - dY, getZ());
			parent.repaint();
			return true;
		}
		return true;

	}

	// User Input -> From DragDropSynchronizable

	/**
	 * Mouse exited.
	 * 
	 * @param e
	 *            the Event
	 * @return true, if blocking input to underlying objects
	 */
	public boolean mouseExited(final MouseEvent e) {

		if (dragging) {
			final float dX = inputMousePosition.x - e.getPoint().x;
			final float dY = inputMousePosition.y - e.getPoint().y;
			setLocation(getX() - dX, getY() - dY, getZ());
			parent.repaint();
			return true;
		}
		return false;

	}

	/**
	 * Mouse moved.
	 * 
	 * @param e
	 *            the Event
	 * @return true, if blocking input to underlying objects
	 */
	public boolean mouseMoved(final MouseEvent e) {

		return true;

	}

	/**
	 * Mouse pressed.
	 * 
	 * @param e
	 *            the Event
	 * @return true, if blocking input to underlying objects
	 */
	public boolean mousePressed(final MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON1) {
			dragging = true;
			dragStartZ = getZ();
			dragStartPoint = new Vector2(getX(), getY());
			for (final EditorObject s : parent.getAllObjects()) {
				if (s.getBounds().getZ() <= getZ()) {
					setZ(s.getBounds().getZ() - 1);
				}
			}
		}
		return true;

	}

	/**
	 * Mouse released.
	 * 
	 * @param e
	 *            the Event
	 * @return true, if blocking input to underlying objects
	 */
	public boolean mouseReleased(final MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON1) {

			if (dragging) {
				int dx = (int) (getX() % parent.gridSquare);
				if (dx > (parent.gridSquare / 2)) {
					dx = -(parent.gridSquare - dx);
				}
				int dy = (int) (getY() % parent.gridSquare);
				if (dy > (parent.gridSquare / 2)) {
					dy = -(parent.gridSquare - dy);
				}
				setLocation(getX() - dx, getY() - dy, dragStartZ);
				dragging = false;
				for (final EditorObject s : parent.getAllObjects()) {
					if (s.getBounds().getRectangle2D()
							.overlaps(getBounds().getRectangle2D())
							&& (s.getBounds().getZ() <= getZ())
							&& !s.equals(this)) {
						setZ(s.getBounds().getZ() - 1);
					}
				}
				parent.updateObjects(this);
				parent.repaint();
			}
		}
		return true;

	}

	public void paint(final Graphics g) {

		final Vector3 c = getSizeIfAngleWas(angle);

		g.setColor(Color.WHITE);
		g.fillRect((int) getX(), (int) getY(), (int) c.x, (int) c.y);

		g.setColor(Color.RED);
		((Graphics2D) g).setStroke(new BasicStroke(3F));
		g.drawRect((int) getX(), (int) getY(), (int) c.x, (int) c.y);

		g.setColor(Color.BLACK);
		g.setFont(new Font("Monospaced", Font.BOLD, 15));
		g.drawString(name + "(" + className + ")",
				(int) ((getX() + (c.x / 2)) - (g.getFontMetrics()
						.getStringBounds(name + "(" + className + ")", null)
						.getWidth() / 2)), (int) (getY() + 25));
	}

	public String saveToString() {

		String s = className + "<>" + name + "<>" + hint;
		for (final Property p : props) {
			s += "<>" + p.toString();
		}
		return s;
	}

	public void setAngle(final int angle) {

		this.angle = angle;
		setProperty("Angle", String.valueOf(angle));
	}

	@Override
	public void setBounds(final Cube c) {

		super.setBounds(c);
		setProperty("Size", "\"" + getBoundsString() + "\"");

	}

	@Override
	public void setBounds(final float x, final float y, final float z,
			final float width, final float height, final float depth) {

		super.setBounds(x, y, z, width, height, depth);
		setProperty("Size", "\"" + getBoundsString() + "\"");

	}

	@Override
	public void setDepth(final float d) {

		super.setDepth(d);
		setProperty("Size", "\"" + getBoundsString() + "\"");

	}

	@Override
	public void setHeight(final float h) {

		super.setHeight(h);
		setProperty("Size", "\"" + getBoundsString());

	}

	@Override
	public void setLocation(final float x, final float y, final float z) {

		super.setLocation(x, y, z);
		setProperty("Size", "\"" + getBoundsString() + "\"");

	}

	@Override
	public void setLocation(final Vector3 p) {

		super.setLocation(p);
		setProperty("Size", "\"" + getBoundsString() + "\"");

	}

	public void setName(final String text) {

		name = text.substring(text.indexOf("\"") + 1, text.lastIndexOf("\""));
		if (hasProperty("UniqueID")) {
			setProperty("UniqueID", text);
		}

	}

	public boolean setProperty(final String name, final String content) {

		for (final Property p : props) {
			if (p.name.equals(name)) {
				p.content = content;
				return true;
			}
		}
		return false;

	}

	@Override
	public void setSize(final float width, final float height, final float depth) {

		super.setSize(width, height, depth);
		setProperty("Size", "\"" + getBoundsString() + "\"");

	}

	@Override
	public void setSize(final Vector3 d) {

		super.setSize(d);
		setProperty("Size", "\"" + getBoundsString() + "\"");

	}

	@Override
	public void setWidth(final float w) {

		super.setWidth(w);
		setProperty("Size", "\"" + getBoundsString() + "\"");

	}

	@Override
	public void setX(final float x) {

		super.setX(x);
		setProperty("Size", "\"" + getBoundsString() + "\"");
	}

	@Override
	public void setY(final float y) {

		super.setY(y);
		setProperty("Size", "\"" + getBoundsString() + "\"");
	}

	@Override
	public void setZ(final float z) {

		super.setZ(z);
		setProperty("Size", "\"" + getBoundsString() + "\"");

	}

	public String toCode(final int iAmNumber) {

		String code = "parentUniverse.getLayer(Universe.LayerBackGround).getModel().addObject( new "
				+ className + "(";
		for (final Property p : props) {
			String cont = p.content;
			if (p.name.equals("UniqueID")) {
				cont = cont.replace(EditorObject.count,
						String.valueOf(iAmNumber));
			}
			boolean isNoNewType = false;
			for (final String noNew : EditorObject.nonNewTypes) {
				if (p.type.equals(noNew) || cont.equals("null")) {
					isNoNewType = true;
				}
			}
			if (isNoNewType) {
				code += " " + cont + ", ";
			} else {
				code += " new " + p.type + "( " + cont + " ),";
			}
		}
		code = code.substring(0, code.lastIndexOf(",")) + "));\n";
		return code;
	}

	@Override
	public String toString() {

		final String s = name + " (" + className + ")";
		return s;
	}
}

/**
 * The Class SortedSyncList.
 */
class SortedEditorObjectList extends LinkedList<EditorObject> {

	/** The Constant COMPARE_X. */
	public static final Comparator<EditorObject> COMPARE_X = new XSyncComparator();

	/** The Constant COMPARE_DEPTH. */
	public static final Comparator<EditorObject> COMPARE_Z = new ZSyncComparator();

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The c. */
	private final Comparator<EditorObject> c;

	/**
	 * Instantiates a new sorted sync classList.
	 * 
	 * @param c
	 *            the c
	 */
	public SortedEditorObjectList(final Comparator<EditorObject> c) {

		super();
		this.c = c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.LinkedList#add(java.lang.Object)
	 */
	@Override
	public boolean add(final EditorObject o) {

		final boolean b = super.add(o);
		Collections.sort(this, c);
		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.LinkedList#add(int, java.lang.Object)
	 */
	@Override
	public void add(final int index, final EditorObject element) {

		super.add(index, element);
		Collections.sort(this, c);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.LinkedList#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(final Collection<? extends EditorObject> c) {

		final boolean b = super.addAll(c);
		Collections.sort(this, this.c);
		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.LinkedList#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(final int index,
			final Collection<? extends EditorObject> c) {

		final boolean b = super.addAll(index, c);
		Collections.sort(this, this.c);
		return b;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.LinkedList#addFirst(java.lang.Object)
	 */
	@Override
	public void addFirst(final EditorObject o) {

		super.add(o);
		Collections.sort(this, c);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.LinkedList#addLast(java.lang.Object)
	 */
	@Override
	public void addLast(final EditorObject o) {

		super.addLast(o);
		Collections.sort(this, c);

	}
}

class XSyncComparator implements Comparator<EditorObject> {

	@Override
	public int compare(final EditorObject o1, final EditorObject o2) {

		final float x1 = o1.getBounds().getX();
		final float x2 = o2.getBounds().getX();
		return (int) (x1 - x2);
	}

}

class ZSyncComparator implements Comparator<EditorObject> {

	@Override
	public int compare(final EditorObject o1, final EditorObject o2) {

		final float depth1 = o1.getBounds().getZ();
		final float depth2 = o2.getBounds().getZ();
		return (int) (depth1 - depth2);
	}

}
