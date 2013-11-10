package org.engine.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JList;
import javax.swing.JPanel;

class ProjectionListener implements MouseListener, MouseMotionListener {

	private final ProjectionPanel parent;

	public ProjectionListener(final ProjectionPanel parent) {

		this.parent = parent;

	}

	@Override
	public void mouseClicked(final MouseEvent e) {

		parent.input(e);

	}

	@Override
	public void mouseDragged(final MouseEvent e) {

		parent.input(e);
	}

	@Override
	public void mouseEntered(final MouseEvent e) {

		parent.input(e);

	}

	@Override
	public void mouseExited(final MouseEvent e) {

		parent.input(e);

	}

	@Override
	public void mouseMoved(final MouseEvent e) {

		parent.input(e);

	}

	@Override
	public void mousePressed(final MouseEvent e) {

		parent.input(e);

	}

	@Override
	public void mouseReleased(final MouseEvent e) {

		parent.input(e);

	}

}

public class ProjectionPanel extends JPanel {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	public JList descriptionList;
	public int gridSquare = 50;
	ArrayList<EditorObject> objectList = new ArrayList<EditorObject>();
	public int view_height = 600;
	public int view_width = 800;
	public double zoom = 1;

	public ProjectionPanel() {

		final ProjectionListener l = new ProjectionListener(this);
		addMouseListener(l);
		addMouseMotionListener(l);
	}

	public boolean add(final EditorObject editorObject) {

		final boolean success = objectList.add(editorObject);
		if (success) {
			updateObjects(this);
		}
		return success;
	}

	public boolean addAll(final Collection<EditorObject> collection) {

		final boolean success = objectList.addAll(collection);
		if (success) {
			updateObjects(this);
		}
		return success;
	}

	public Collection<EditorObject> getAllObjects() {

		return new ArrayList<EditorObject>(objectList);
	}

	void input(final MouseEvent e) {

		final SortedEditorObjectList l = new SortedEditorObjectList(
				SortedEditorObjectList.COMPARE_Z);
		l.addAll(objectList);
		final MouseEvent zoomEvent = new MouseEvent(e.getComponent(),
				e.getID(), e.getWhen(), e.getModifiers(),
				(int) (e.getX() / zoom), (int) (e.getY() / zoom),
				e.getClickCount(), false, e.getButton());
		boolean wasCatchedAbove = false;
		for (final EditorObject o : l) {
			wasCatchedAbove = o.input(zoomEvent, wasCatchedAbove);
		}
	}

	@Override
	public void paint(final Graphics g) {

		final int actualWidth = (int) (view_width * zoom);
		final int actualHeight = (int) (view_height * zoom);

		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		final BufferedImage backbuffer = new BufferedImage(actualWidth,
				actualHeight, BufferedImage.TYPE_3BYTE_BGR);
		final SortedEditorObjectList l = new SortedEditorObjectList(
				SortedEditorObjectList.COMPARE_Z);
		l.addAll(objectList);
		Collections.reverse(l);
		final Graphics2D g2d = (Graphics2D) backbuffer.getGraphics();
		final AffineTransform at = new AffineTransform();
		at.scale(zoom, zoom);
		g2d.setTransform(at);
		for (final EditorObject o : l) {
			o.paint(g2d);
		}
		g.drawImage(backbuffer, 0, 0, actualWidth, actualHeight, null);
		g.setColor(Color.white);
		g.drawRect(0, 0, actualWidth, actualHeight);

	}

	public boolean remove(final EditorObject editorObject) {

		final boolean success = objectList.remove(editorObject);
		if (success) {
			updateObjects(this);
		}
		return success;
	}

	public boolean removeAll(final Collection<EditorObject> collection) {

		final boolean success = objectList.removeAll(collection);
		if (success) {
			updateObjects(this);
		}
		return success;
	}

	public void updateObjects(final Object source) {

		for (final PropertyChangeListener l : getPropertyChangeListeners()) {
			l.propertyChange(new PropertyChangeEvent(source, "ObjectList",
					null, objectList));
		}
		repaint();

	}
}
