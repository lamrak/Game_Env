package net.validcat.game.jumper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.validcat.framework.Screen;
import net.validcat.framework.gl.GLGame;
import net.validcat.game.jumper.screens.MainMenuScreen;
import net.validcat.game.jumper.util.Assets;
import net.validcat.game.jumper.util.Settings;

public class SuperJumper extends GLGame {
	boolean firstTimeCreate = true;

	@Override
	public Screen getStartScreen() {
		 return new MainMenuScreen(this);
	}
	
	 @Override
	    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	        super.onSurfaceCreated(gl, config);
	        if(firstTimeCreate) {
	            Settings.load(getFileIO());
	            Assets.load(this);
	            firstTimeCreate = false;
	        } else {
	            Assets.reload();
	        }
	    }
	    @Override
	    public void onPause() {
	        super.onPause();
	        if(Settings.soundEnabled)
	            Assets.music.pause();
	    }


}
