package net.validcat.nom;

import net.validcat.nom.framework.AndroidGame;
import net.validcat.nom.framework.Screen;
import net.validcat.nom.screens.LoadingScreen;

public class MrNomGame extends AndroidGame {

	@Override
	public Screen getStartScreen() {
		return new LoadingScreen(this);
	}

}
