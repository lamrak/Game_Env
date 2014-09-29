package net.validcat.game.jumper.models;

import net.validcat.framework.game2d.GameObject;

public class Castle extends GameObject {
	public static float CASTLE_WIDTH = 1.7f;
	public static float CASTLE_HEIGHT = 1.7f;
	
	public Castle(float x, float y) {
		super(x, y, CASTLE_WIDTH, CASTLE_HEIGHT);
	}
	
}
