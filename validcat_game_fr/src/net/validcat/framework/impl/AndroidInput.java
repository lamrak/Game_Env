package net.validcat.framework.impl;

import java.util.List;

import android.content.Context;
import android.view.View;

import net.validcat.framework.IInput;
import net.validcat.framework.TouchHandler;

public class AndroidInput implements IInput {
	AccelerometerHandler accelHandler;
	KeyboardHandler keyHandler;
	TouchHandler touchHandler;
	
	public AndroidInput(Context context, View view, float scaleX, float scaleY) {
		accelHandler = new AccelerometerHandler(context);
		keyHandler = new KeyboardHandler(view);
		touchHandler = new TouchEventHandler(view, scaleX, scaleY);
	}
	@Override
	public boolean isKeyPressed(int keyCode) {
		return keyHandler.isKeyPressed(keyCode);
	}

	@Override
	public boolean isTouchDown(int pointer) {
		return touchHandler.isTouchDown(pointer);
	}

	@Override
	public int getTouchX(int pointer) {
		return touchHandler.getTouchX(pointer);
	}

	@Override
	public int getTouchY(int pointer) {
		return touchHandler.getTouchY(pointer);
	}

	@Override
	public float getAccelX() {
		return accelHandler.getAccelX();
	}

	@Override
	public float getAccelY() {
		return accelHandler.getAccelY();
	}

	@Override
	public float getAccelZ() {
		return accelHandler.getAccelZ();
	}

	@Override
	public List<KeyEvent> getKeyEvents() {
		return keyHandler.getKeyEvents();
	}

	@Override
	public List<TouchEvent> getTouchEvents() {
		return touchHandler.getTouchEvents();
	}

}
