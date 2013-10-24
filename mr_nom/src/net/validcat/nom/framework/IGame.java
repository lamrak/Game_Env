package net.validcat.nom.framework;


public interface IGame {
	
	public IInput getInput();
	public IFileIO getFileIO();
	public IGraphics getGraphics();
	public IAudio getAudio();
	public void setScreen(Screen screen);
	public Screen getCurrentScreen();
	public Screen getStartScreen();

}
