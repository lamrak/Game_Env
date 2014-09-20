package net.validcat.framework.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.validcat.framework.IAudio;
import net.validcat.framework.IFileIO;
import net.validcat.framework.IGame;
import net.validcat.framework.IGraphics;
import net.validcat.framework.IInput;
import net.validcat.framework.Screen;
import net.validcat.framework.impl.AndroidInput;
import net.validcat.framework.impl.Audio;
import net.validcat.framework.impl.FileIO;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

public abstract class GLGame extends Activity implements IGame, Renderer {
	GLSurfaceView glView;
	GLGraphics glGraphics;
	IAudio audio;
	IInput input;
	IFileIO fileIO;
	Screen screen;
	GLGameState state = GLGameState.Initialized;
	Object stateChanged = new Object();
	long startTime = System.nanoTime();
	WakeLock wakeLock;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		glView = new GLSurfaceView(this);
		glView.setRenderer(this);
		setContentView(glView);
		
		glGraphics = new GLGraphics(glView);
		fileIO = new FileIO(getAssets());
		audio = new Audio(this);
		input = new AndroidInput(this, glView, 1, 1);
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
	}
	
	public void onResume() {
		super.onResume();
		glView.onResume();
		wakeLock.acquire();
	}
	
	@Override
	public void onPause() {
		synchronized (stateChanged) {
			state = isFinishing() ? GLGameState.Finished : GLGameState.Paused;
			while (true) {
				try {
					stateChanged.wait();
					break;
				} catch (InterruptedException e) {}
			}
		}
		wakeLock.release();
		glView.onPause();
		super.onPause();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		GLGameState state = null;
		
		synchronized (stateChanged) {
			state = this.state;
		}
		if (state == GLGameState.Running) {
			float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
			startTime = System.nanoTime();
			screen.update(deltaTime);
			screen.present(deltaTime);
		}
		if (state == GLGameState.Paused) {
			screen.pause();
			synchronized (stateChanged) {
				this.state = GLGameState.Idle;
				stateChanged.notifyAll();
			}
		}
		if (state == GLGameState.Finished) {
			screen.pause();
			screen.dispose();
			synchronized (stateChanged) {
				this.state = GLGameState.Idle;
				stateChanged.notifyAll();
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glGraphics.setGL(gl);
		synchronized (stateChanged) {
			if (state == GLGameState.Initialized)
				screen = getStartScreen();
			state = GLGameState.Running;
			screen.resume();
			startTime = System.nanoTime();
		}
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
		throw new IllegalStateException("We are using OpenGL!");
	}
	
	public GLGraphics getGLGraphics() {
		return glGraphics;
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
		screen.resume();
		screen.update(0);
		
		this.screen = screen;
	}

	@Override
	public Screen getCurrentScreen() {
		return screen;
	}
	
	public abstract Screen getStartScreen();
	
	enum GLGameState {
		Initialized,
		Running,
		Paused,
		Finished,
		Idle
	}

}
