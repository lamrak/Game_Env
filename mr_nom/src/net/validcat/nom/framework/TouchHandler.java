package net.validcat.nom.framework;

import java.util.List;

import net.validcat.nom.framework.IInput.TouchEvent;

import android.view.View.OnTouchListener;

public interface TouchHandler extends OnTouchListener {
	public boolean isTouchDown(int pointer);
	public int getTouchX(int pointer);
	public int getTouchY(int pointer);
	
	public List<TouchEvent> getTouchEvents();
}
