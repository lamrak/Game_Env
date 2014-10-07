package net.validcat.game.jumper;

import javax.microedition.khronos.opengles.GL10;

import net.validcat.framework.game2d.Animation;
import net.validcat.framework.game2d.SpriteBatcher;
import net.validcat.framework.game2d.TextureRegion;
import net.validcat.framework.gl.Camera2D;
import net.validcat.framework.gl.GLGraphics;
import net.validcat.game.jumper.models.Bob;
import net.validcat.game.jumper.models.Castle;
import net.validcat.game.jumper.models.Coin;
import net.validcat.game.jumper.models.Platform;
import net.validcat.game.jumper.models.Spring;
import net.validcat.game.jumper.models.Squirrel;
import net.validcat.game.jumper.models.World;
import net.validcat.game.jumper.util.Assets;

public class WorldRenderer {
	static final float FRUSTUM_WIDTH = 10;
	static final float FRUSTUM_HEIGHT = 15;
	GLGraphics glGraphics;
	World world;
	Camera2D cam;
	SpriteBatcher batcher;
	
	public WorldRenderer(GLGraphics glGraphics, SpriteBatcher batcher, World world) {
		this.glGraphics = glGraphics;
		this.world = world;
		this.cam = new Camera2D(glGraphics, FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
		this.batcher = batcher;
	}

	public void render() {
		if(world.bob.position.y > cam.position.y )
		cam.position.y = world.bob.position.y;
		cam.setViewportAndMatrices();
		
		renderBackground();
		renderObjects();
	}

	private void renderObjects() {
		GL10 gl = glGraphics.getGL();
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		batcher.beginBatch(Assets.items);
		renderBob();
		renderPlatforms();
		renderItems();
		renderSquirrels();
		renderCastle();
		batcher.endBatch();
		gl.glDisable(GL10.GL_BLEND);
	}

	private void renderBob() {
		TextureRegion keyFrame;
		switch (world.bob.state) {
		case Bob.BOB_STATE_FALL:
			keyFrame = Assets.bobFall.getKeyFrame(world.bob.stateTime,
					Animation.ANIMATION_LOOPING);
			break;
		case Bob.BOB_STATE_JUMP:
			keyFrame = Assets.bobJump.getKeyFrame(world.bob.stateTime,
					Animation.ANIMATION_LOOPING);
			break;
		case Bob.BOB_STATE_HIT:
		default:
			keyFrame = Assets.bobHit;
		}
		float side = world.bob.velocity.x < 0 ? -1 : 1;
		batcher.drawSprite(world.bob.position.x, world.bob.position.y, side * 1, 1, keyFrame);
	}

	private void renderPlatforms() {
		for (int i = 0; i < world.platforms.size(); i++) {
			Platform platform = world.platforms.get(i);
			TextureRegion keyFrame = Assets.platform;
			if (platform.state == Platform.PLATFORM_STATE_PULVERIZING)
				keyFrame = Assets.brakingPlatform.getKeyFrame(
						platform.stateTime, Animation.ANIMATION_NONLOOPING);
			batcher.drawSprite(platform.position.x, platform.position.y, 2,
					0.5f, keyFrame);
		}
	}

	private void renderItems() {
		for(int i = 0; i < world.springs.size(); i++) {
			Spring spring = world.springs.get(i);
			batcher.drawSprite(spring.position.x, spring.position.y, 1, 1, Assets.spring);
		}
		
		for(int i = 0; i < world.coins.size(); i++) {
		Coin coin = world.coins.get(i);
			TextureRegion keyFrame = Assets.coinAnim.getKeyFrame(coin.stateTime, Animation.ANIMATION_LOOPING);
			batcher.drawSprite(coin.position.x, coin.position.y, 1, 1,keyFrame);
		}
	}

	private void renderSquirrels() {
		for(int i = 0; i < world.squirrels.size(); i++) {
			Squirrel squirrel = world.squirrels.get(i);
			TextureRegion keyFrame = Assets.squirrelFly.getKeyFrame(squirrel.stateTime, Animation.ANIMATION_LOOPING);
			float side = squirrel.velocity.x < 0?-1:1;
			batcher.drawSprite(squirrel.position.x, squirrel.position.y, side * 1, 1, keyFrame);
		}
	}

	private void renderCastle() {
		Castle castle = world.castle;
		batcher.drawSprite(castle.position.x, castle.position.y, 2, 2, Assets.castle);
	}

	private void renderBackground() {
		batcher.beginBatch(Assets.background);
		batcher.drawSprite(cam.position.x, cam.position.y,
				FRUSTUM_WIDTH, FRUSTUM_HEIGHT, Assets.backgroundRegion);
		batcher.endBatch();
	}

}
