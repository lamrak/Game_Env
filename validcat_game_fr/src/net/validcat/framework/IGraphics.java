package net.validcat.framework;

public interface IGraphics {

	public static enum PixmapFormat {
		ARGB8888, ARGB4444, RGB565
	}

	public IPixmap newPixmap(String fileName, PixmapFormat format);

	public void clear(int color);

	public void drawPixel(int x, int y, int color);

	public void drawLine(int x, int y, int x2, int y2, int color);

	public void drawRect(int x, int y, int width, int height, int color);

	public void drawPixmap(IPixmap pixmap, int x, int y, int srcX, int srcY,
			int srcWidth, int srcHeight);

	public void drawPixmap(IPixmap pixmap, int x, int y);

	public int getWidth();

	public int getHeight();

}
