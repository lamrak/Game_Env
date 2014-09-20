package net.validcat.framework.impl;

import android.graphics.Bitmap;
import net.validcat.framework.IPixmap;
import net.validcat.framework.IGraphics.PixmapFormat;

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
