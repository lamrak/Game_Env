package net.validcat.game.cannon;

import net.validcat.framework.game2d.GameObject;

public class Cannon extends GameObject {
	public float angle;
	public static final int CAPACITY = 20;

	public static final int STATE_READY = 0x00;
	public static final int STATE_CHARGING = 0x01;
	public static final int STATE_NO_BULLETS = 0x02;
	
	public int currentBullet = 0;
	public int state;
	public float chargingTime;
	
	public Cannon(float x, float y, float width, float height) {
		super(x, y, width, height);
		angle = 0;
		
		state = STATE_READY;
	}
	
	public void charge() {
		currentBullet = 0;
	}
	
	public void fire() {
		currentBullet++;
		state = currentBullet >= CAPACITY ? STATE_NO_BULLETS : STATE_CHARGING;
	}

	public void charging(float deltaTime) {
		chargingTime += deltaTime;
		if (chargingTime > 2) {
			chargingTime = 0;
			state = STATE_READY;
		}
	}

}
