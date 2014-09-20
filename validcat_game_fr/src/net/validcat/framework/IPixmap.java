package net.validcat.framework;

import net.validcat.framework.IGraphics.PixmapFormat;

public interface IPixmap {
	
	public int getWidth();
	public int getHeight();
	public PixmapFormat getFormat();
	public void dispose();

}
