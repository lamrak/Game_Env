package net.validcat.game.cannon;

import net.validcat.framework.game2d.DynamicGameObject;

public class Bullet extends DynamicGameObject {
	public boolean isFire = false;
	
	public Bullet(float x, float y, float width, float height) {
		super(x, y, width, height);
	}
	
}
