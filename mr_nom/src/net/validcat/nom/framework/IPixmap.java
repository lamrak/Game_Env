package net.validcat.nom.framework;

import net.validcat.nom.framework.IGraphics.PixmapFormat;

public interface IPixmap {
	
	public int getWidth();
	public int getHeight();
	public PixmapFormat getFormat();
	public void dispose();

}
