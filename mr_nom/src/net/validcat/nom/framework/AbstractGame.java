package net.validcat.nom.framework;

import net.validcat.nom.MainActivity;

public abstract class AbstractGame extends MainActivity implements IGame {

	@Override
	public IInput getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFileIO getFileIO() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IGraphics getGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAudio getAudio() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setScreen(Screen screen) {
		// TODO Auto-generated method stub

	}

	@Override
	public Screen getCurrentScreen() {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract Screen getStartScreen();

}
