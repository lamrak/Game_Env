package net.validcat.framework;

import net.validcat.framework.impl.AndroidFastRenderView;
import net.validcat.framework.impl.AndroidGraphics;
import net.validcat.framework.impl.AndroidInput;
import net.validcat.framework.impl.Audio;
import net.validcat.framework.impl.FileIO;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public abstract class AndroidGame extends Activity implements IGame {
	AndroidFastRenderView renderView;
	IGraphics graphics;
	IAudio audio;
	IInput input;
	IFileIO fileIO;
	Screen screen;
//	WakeLock wakeLock;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		int frameBufferWidth = isLandscape ? 480 : 320;
		int frameBufferHeight = isLandscape ? 320 : 480;
		Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565);
		
		float scaleX = (float) frameBufferWidth / getWindowManager().getDefaultDisplay().getWidth();
		float scaleY = (float) frameBufferHeight / getWindowManager().getDefaultDisplay().getHeight();
		
		renderView = new AndroidFastRenderView(this, frameBuffer);
		graphics = new AndroidGraphics(getAssets(), frameBuffer);
		fileIO = new FileIO(getAssets());
		audio = new Audio(this);
		input = new AndroidInput(this, renderView, scaleX, scaleY);
		screen = getStartScreen();
		setContentView(renderView);
		
//		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//		wakeLock = powerManager.newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, "GLGame");
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		wakeLock.acquire();
		screen.resume();
		renderView.resume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		wakeLock.release();
		renderView.pause();
		screen.pause();
		if (isFinishing())
			screen.dispose();
	}
	
	@Override
	public IInput getInput() {
		return input;
	}

	@Override
	public IFileIO getFileIO() {
		return fileIO;
	}

	@Override
	public IGraphics getGraphics() {
		return graphics;
	}

	@Override
	public IAudio getAudio() {
		return audio;
	}

	@Override
	public void setScreen(Screen screen) {
		if (screen == null)
			throw new IllegalArgumentException("Screen must not be null");
		this.screen.pause();
		this.screen.dispose();
		this.screen = screen;
		screen.resume();
		screen.update(0);
	}

	@Override
	public Screen getCurrentScreen() {
		return screen;
	}

	public abstract Screen getStartScreen();

}
