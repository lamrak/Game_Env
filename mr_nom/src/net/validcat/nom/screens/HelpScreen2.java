package net.validcat.nom.screens;

import java.util.List;

import android.util.Log;

import net.validcat.nom.Assets;
import net.validcat.nom.Settings;
import net.validcat.nom.framework.IGame;
import net.validcat.nom.framework.IGraphics;
import net.validcat.nom.framework.Screen;
import net.validcat.nom.framework.IInput.TouchEvent;

public class HelpScreen2 extends Screen {
	private static final String LOG_TAG = "HelpScreen2";

	public HelpScreen2(IGame game) {
		super(game);
		Log.d(LOG_TAG, "init");
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (event.x > 256 && event.y > 416) {
					game.setScreen(new HelpScreen3(game));
					if (Settings.soundEnabled) Assets.click.play(1);
					return;
				}
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		IGraphics g = game.getGraphics();
		g.drawPixmap(Assets.background, 0, 0);
		g.drawPixmap(Assets.help2, 64, 100);
		g.drawPixmap(Assets.buttons, 256, 416, 0, 64, 64, 64);
	}
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
