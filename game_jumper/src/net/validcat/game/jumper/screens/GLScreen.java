package net.validcat.game.jumper.screens;

import net.validcat.framework.IGame;
import net.validcat.framework.Screen;
import net.validcat.framework.gl.GLGame;
import net.validcat.framework.gl.GLGraphics;

public abstract class GLScreen extends Screen {
	protected final GLGame glGame;
	protected final GLGraphics glGraphics;

	public GLScreen(IGame game) {
		super(game);
		glGame = (GLGame)game;
		glGraphics = ((GLGame)game).getGLGraphics();
	}

}
