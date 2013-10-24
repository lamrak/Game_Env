package net.validcat.nom;

/**
 * ��������� �����, ���������� ���������, ������������� ��� ������������ � 
 * ���������� ����� ����� �� ������������� (��� �� ���� �������, ����� ������ �������).
 * @author o.dobrunov
 *
 */
public abstract class Game {
	
	public void run() {
		createWindowAndUIComponent();
		
//		IInput input = new Input();
//		IGraphics graphics = new Graphics();
//		IAudio audio = new Audio();
//		Screen currentScreen = new MainMenu();
		long lastFrameTime = currentTime();
		while(!userQuit()) {
			long deltaTime = currentTime() - lastFrameTime;
			lastFrameTime = currentTime();
//			currentScreen.updateState(input, deltaTime);
//			currentScreen.present(graphics, audio, deltaTime);
		}
		cleanupResources();
	}

	private boolean userQuit() {
		return false;
	}

	private long currentTime() {
		return System.currentTimeMillis();
	}

	abstract void createWindowAndUIComponent();
	abstract void cleanupResources();

}
