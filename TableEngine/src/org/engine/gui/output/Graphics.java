package org.engine.gui.output;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

class DrawMode {

	public static final short IMAGE_MODE = 1;
	public static final short NO_DRAWING = 0;

	public static final short SHAPE_MODE = 2;
	private final short mode;
	private final ShapeType shape;

	public DrawMode(final short type, final ShapeType shape) {

		mode = type;
		this.shape = shape;
	}

	public short getMode() {
		return mode;
	}

	public ShapeType getShape() {
		return shape;
	}

	@Override
	public String toString() {
		return mode + " - " + shape;
	}
}

public class Graphics {

	private Color color = Color.WHITE;
	private BitmapFont font = new BitmapFont();

	private float lineWidth = 1;
	private DrawMode mode = new DrawMode(DrawMode.NO_DRAWING, null);
	private final ShapeRenderer shape;
	private final SpriteBatch sprite;

	private Matrix4 transform = new Matrix4();
	public float deltaTime = 0;

	public Graphics() {
		sprite = new SpriteBatch();
		shape = new ShapeRenderer();
	}

	public Graphics(final SpriteBatch sprite, final ShapeRenderer shape) {

		this.sprite = sprite;
		this.shape = shape;
	}

	public void dispose() {
		sprite.dispose();
		shape.dispose();
	}

	// DRAWING METHODS

	// IMAGE DRAWING

	public void drawCircle(final float x, final float y, final float radius) {

		switchMode(new DrawMode(DrawMode.SHAPE_MODE, ShapeType.Line));

		shape.circle(x, y, radius);

	}

	public void drawCurve(final float x1, final float y1, final float cx1,
			final float cy1, final float cx2, final float cy2, final float x2,
			final float y2, final int segments) {

		switchMode(new DrawMode(DrawMode.SHAPE_MODE, ShapeType.Line));

		shape.curve(x1, y1, cx1, cy1, cx2, cy2, x2, y2, segments);

	}

	public void drawImage(final TextureRegion region, final float x,
			final float y) {
		hardDrawImage(region, x, y, 0, 0, region.getRegionWidth(),
				region.getRegionHeight(), 1, 1, 0);
	}

	public void drawImage(final TextureRegion region, final float x,
			final float y, final float rotation) {
		hardDrawImage(region, x, y, 0, 0, region.getRegionWidth(),
				region.getRegionHeight(), 1, 1, rotation);
	}

	public void drawImage(final TextureRegion region, final float x,
			final float y, final float width, final float height) {
		hardDrawImage(region, x, y, 0, 0, width, height, 1, 1, 0);
	}

	public void drawImage(final TextureRegion region, final float x,
			final float y, final float width, final float height,
			final float rotation) {
		hardDrawImage(region, x, y, 0, 0, width, height, 1, 1, rotation);
	}

	// SHAPE DRAWING

	public void drawImage(final TextureRegion region, final float x,
			final float y, final float width, final float height,
			final float scaleX, final float scaleY, final float rotation) {
		hardDrawImage(region, x, y, 0, 0, width, height, scaleX, scaleY,
				rotation);
	}

	public void drawRect(final float x, final float y, final float width,
			final float height) {

		switchMode(new DrawMode(DrawMode.SHAPE_MODE, ShapeType.Line));

		shape.rect(x, y, width, height);

		if (lineWidth > 1) {

			switchMode(new DrawMode(DrawMode.SHAPE_MODE, ShapeType.Filled));

			shape.rect(x - (lineWidth / 2), y - (lineWidth / 2), lineWidth,
					lineWidth);
			shape.rect((x + width) - (lineWidth / 2), (y + height)
					- (lineWidth / 2), lineWidth, lineWidth);
			shape.rect((x + width) - (lineWidth / 2), y - (lineWidth / 2),
					lineWidth, lineWidth);
			shape.rect(x - (lineWidth / 2), (y + height) - (lineWidth / 2),
					lineWidth, lineWidth);
		}
	}

	public TextBounds drawString(final String str, final float x, float y) {

		switchMode(new DrawMode(DrawMode.IMAGE_MODE, null));

		y += font.getDescent() + font.getLineHeight() + 3;

		if (str.contains("\n")) {
			return font.drawMultiLine(sprite, str, x, y);
		} else {
			return font.draw(sprite, str, x, y);
		}
	}

	public TextBounds drawStringWrapped(final String str, final float x,
			float y, final float width, final HAlignment alignment) {

		switchMode(new DrawMode(DrawMode.IMAGE_MODE, null));
		y = y + font.getDescent();
		return font.drawWrapped(sprite, str, x, y, width, alignment);
	}

	public void drawTriangle(final float x1, final float y1, final float x2,
			final float y2, final float x3, final float y3) {

		switchMode(new DrawMode(DrawMode.SHAPE_MODE, ShapeType.Line));

		shape.triangle(x1, y1, x2, y2, x3, y3);

	}

	public void drawUI(final Drawable d, final float x, final float y,
			final float width, final float height) {

		switchMode(new DrawMode(DrawMode.IMAGE_MODE, null));

		d.draw(sprite, x, y, width, height);
	}

	public void endDrawing() {

		switchMode(new DrawMode(DrawMode.NO_DRAWING, null));
	}

	public void fillCircle(final float x, final float y, final float radius) {

		switchMode(new DrawMode(DrawMode.SHAPE_MODE, ShapeType.Filled));

		shape.circle(x, y, radius);

	}

	// TEXT DRAWING

	public void fillRect(final float x, final float y, final float width,
			final float height) {

		switchMode(new DrawMode(DrawMode.SHAPE_MODE, ShapeType.Filled));

		shape.rect(x, y, width, height);

	}

	public void fillRect(final float x, final float y, final float width,
			final float height, final Color c1, final Color c2, final Color c3,
			final Color c4) {

		switchMode(new DrawMode(DrawMode.SHAPE_MODE, ShapeType.Filled));

		shape.rect(x, y, width, height, c1, c2, c3, c4);

	}

	public void fillTriangle(final float x1, final float y1, final float x2,
			final float y2, final float x3, final float y3) {

		switchMode(new DrawMode(DrawMode.SHAPE_MODE, ShapeType.Filled));

		shape.triangle(x1, y1, x2, y2, x3, y3);

	}

	public Color getColor() {
		return color;
	}

	public BitmapFont getFont() {
		return font;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public Matrix4 getTransformMatrix() {
		return transform;
	}

	private void hardDrawImage(final TextureRegion region, final float x,
			final float y, final float originX, final float originY,
			final float width, final float height, final float scaleX,
			final float scaleY, final float rotation) {
		switchMode(new DrawMode(DrawMode.IMAGE_MODE, null));
		sprite.draw(region, x, y, originX, originY, width, height, scaleX,
				scaleY, rotation);

	}

	public void reset() {

		setLineWidth(1);
		setColor(Color.WHITE);
		setFont(new BitmapFont());
	}

	public void setColor(final Color color) {
		this.color = color;
		sprite.setColor(color);
		shape.setColor(color);

	}

	public void setFont(final BitmapFont font) {
		this.font = font;
	}

	public void setLineWidth(final float lineWidth) {
		this.lineWidth = lineWidth;
		Gdx.gl.glLineWidth(lineWidth);
	}

	public void setProjectionMatrix(final Matrix4 matrix) {

		sprite.setProjectionMatrix(matrix);
		shape.setProjectionMatrix(matrix);
	}

	public void setTransformMatrix(final Matrix4 transform) {
		this.transform = transform;
		sprite.setTransformMatrix(transform);
		shape.setTransformMatrix(transform);
	}

	private void switchMode(final DrawMode mode) {

		// if this is to be switched to no-drawing mode
		if ((mode.getMode() == DrawMode.NO_DRAWING)
				&& (this.mode.getMode() != DrawMode.NO_DRAWING)) {

			if (this.mode.getMode() == DrawMode.IMAGE_MODE) {

				sprite.end();
			}
			if (this.mode.getMode() == DrawMode.SHAPE_MODE) {

				shape.end();
			}
			this.mode = mode;
		}

		// if this is to be switched to image mode and it has been without mode
		// or in shape mode before
		if ((mode.getMode() == DrawMode.IMAGE_MODE)
				&& (this.mode.getMode() != DrawMode.IMAGE_MODE)) {

			// switch to image mode

			if (this.mode.getMode() == DrawMode.SHAPE_MODE) {

				shape.end();
			}
			this.mode = mode;
			sprite.begin();

		}
		// if this is to be switched to shape mode and it has been without mode
		// or in image mode or in different shape mode
		if (mode.getMode() == DrawMode.SHAPE_MODE) {

			// switch to shape mode
			if (this.mode.getMode() == DrawMode.IMAGE_MODE) {

				sprite.end();
			}
			if (this.mode.getMode() == DrawMode.SHAPE_MODE) {

				if (!this.mode.getShape().equals(mode.getShape())) {

					shape.end();
					this.mode = mode;
					shape.begin(mode.getShape());
				}
			} else {

				this.mode = mode;
				shape.begin(mode.getShape());
			}
		}
	}

}