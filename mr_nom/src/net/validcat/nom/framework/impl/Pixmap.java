package net.validcat.nom.framework.impl;

import android.graphics.Bitmap;
import net.validcat.nom.framework.IGraphics.PixmapFormat;
import net.validcat.nom.framework.IPixmap;

public class Pixmap implements IPixmap{
	Bitmap bitmap;
	PixmapFormat format;
	
	public Pixmap(Bitmap bitmap, PixmapFormat format) {
		this.bitmap = bitmap;
		this.format = format;
	}

	@Override
	public int getWidth() {
		return bitmap.getWidth();
	}

	@Override
	public int getHeight() {
		return bitmap.getHeight();
	}

	@Override
	public PixmapFormat getFormat() {
		return format;
	}

	@Override
	public void dispose() {
		bitmap.recycle();
	}

}
