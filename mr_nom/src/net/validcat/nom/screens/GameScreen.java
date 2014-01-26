package net.validcat.nom.screens;

import java.util.List;

import android.graphics.Color;

import net.validcat.nom.Assets;
import net.validcat.nom.Settings;
import net.validcat.nom.World;
import net.validcat.nom.framework.IGame;
import net.validcat.nom.framework.IGraphics;
import net.validcat.nom.framework.IInput.TouchEvent;
import net.validcat.nom.framework.IPixmap;
import net.validcat.nom.framework.Screen;
import net.validcat.nom.model.Snake;
import net.validcat.nom.model.SnakePart;
import net.validcat.nom.model.Stain;

public class GameScreen extends Screen {
	GameState state = GameState.READY;
	World world;
	int oldScore = 0;
	String score = "0";

	public GameScreen(IGame game) {
		super(game);
		world = new World();
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();
		if(state == GameState.READY) 	 updateReady(touchEvents);
		if(state == GameState.RUNNING)   updateRunning(touchEvents, deltaTime);
		if(state == GameState.PAUSED)	 updatePaused(touchEvents);
		if(state == GameState.GAME_OVER) updateGameOver(touchEvents);
	}

	private void updateGameOver(List<TouchEvent> touchEvents) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (event.x >= 128 && event.x <= 192 && event.y >= 200 && event.y <= 264) {
					if (Settings.soundEnabled) Assets.click.play(1);
					game.setScreen(new MainMenuScreen(game));
					return;
				}
			}
		}
	}

	private void updatePaused(List<TouchEvent> touchEvents) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (event.x > 80 && event.x <= 240) {
					if (event.y > 100 && event.y <= 148) {
						if (Settings.soundEnabled) Assets.click.play(1);
						state = GameState.RUNNING;
						return;
					}
					if (event.y > 148 && event.y < 196) {
						if (Settings.soundEnabled) Assets.click.play(1);
						game.setScreen(new MainMenuScreen(game));
						return;
					}
				}
			}
		}
	}

	private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
		int len = touchEvents.size();
		for(int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if(event.type == TouchEvent.TOUCH_UP) {
					if(event.x < 64 && event.y < 64) {
						if(Settings.soundEnabled) Assets.click.play(1);
						state = GameState.PAUSED;
						return;
					}
			}
			if(event.type == TouchEvent.TOUCH_DOWN) {
				if(event.x < 64  && event.y > 416) world.snake.turnLeft();
				if(event.x > 256 && event.y > 416) world.snake.turnRight();
			}
		}
		
		world.update(deltaTime);
		
		if(world.gameOver) {
			if(Settings.soundEnabled) Assets.bitten.play(1);
			state = GameState.GAME_OVER;
		}
		
		if(oldScore != world.score) {
			oldScore = world.score;
			score = "" + oldScore;
			if(Settings.soundEnabled) Assets.eat.play(1);
		}
	}

	private void updateReady(List<TouchEvent> touchEvents) {
		if(touchEvents.size() > 0)
			state = GameState.RUNNING;
	}

	@Override
	public void present(float deltaTime) {
		IGraphics g = game.getGraphics();
		g.drawPixmap(Assets.background, 0, 0);
		drawWorld(world);
		
		if(state == GameState.READY) 	 drawReadyUI();
		if(state == GameState.RUNNING) 	 drawRunningUI();
		if(state == GameState.PAUSED) 	 drawPausedUI();
		if(state == GameState.GAME_OVER) drawGameOverUI();
		drawText(g, score, g.getWidth() / 2 - score.length()*20 / 2, g.getHeight() - 42);
	}

	private void drawText(IGraphics g, String line, int x, int y) {
		int len = line.length();
		for (int i = 0; i < len; i++) {
			char character = line.charAt(i);
			if (character == ' ') {
				x += 20;
				continue;
			}
			
			int srcX = 0;
			int srcWidth = 0;
			if (character == '.') {
				srcX = 200;
				srcWidth = 10;
			} else {
				srcX = (character - '0') * 20;
				srcWidth = 20; 
			}
			g.drawPixmap(Assets.numbers, x, y, srcX, 0, srcWidth, 32);
			x += srcWidth;
		}
	}

	private void drawWorld(World world2) {
		IGraphics g = game.getGraphics();
		Snake snake = world.snake;
		SnakePart head = snake.parts.get(0);
		Stain stain = world.stain;
		IPixmap stainPixmap = null;
		if(stain.type == Stain.TYPE_1) stainPixmap = Assets.stain1;
		if(stain.type == Stain.TYPE_2) stainPixmap = Assets.stain2;
		if(stain.type == Stain.TYPE_3) stainPixmap = Assets.stain3;
		
		int x = stain.x * 32;
		int y = stain.y * 32;
		g.drawPixmap(stainPixmap, x, y);
		
		int len = snake.parts.size();
		for(int i = 1; i < len; i++) {
			SnakePart part = snake.parts.get(i);
			x = part.x * 32;
			y = part.y * 32;
			g.drawPixmap(Assets.tail, x, y);
		}
		
		IPixmap headPixmap = null;
		if(snake.direction == Snake.UP)    headPixmap = Assets.headUp;
		if(snake.direction == Snake.LEFT)  headPixmap = Assets.headLeft;
		if(snake.direction == Snake.DOWN)  headPixmap = Assets.headDown;
		if(snake.direction == Snake.RIGHT) headPixmap = Assets.headRight;
		x = head.x * 32 + 16;
		y = head.y * 32 + 16;
		g.drawPixmap(headPixmap, x - headPixmap.getWidth() / 2, y - headPixmap.getHeight() / 2);
	}

	private void drawGameOverUI() {
		IGraphics g = game.getGraphics();
		
		g.drawPixmap(Assets.gameOver, 62, 100);
		g.drawPixmap(Assets.buttons, 128, 200, 0, 128, 64, 64);
		g.drawLine(0, 416, 480, 416, Color.BLACK);
	}

	private void drawPausedUI() {
		IGraphics g = game.getGraphics();
		g.drawPixmap(Assets.pause, 80, 100);
		g.drawLine(0, 416, 480, 416, Color.BLACK);
	}

	private void drawRunningUI() {
		IGraphics g = game.getGraphics();
		
		g.drawPixmap(Assets.buttons, 0, 0, 64, 128, 64, 64);
		g.drawLine(0, 416, 480, 416, Color.BLACK);
		g.drawPixmap(Assets.buttons, 0, 416, 64, 64, 64, 64);
		g.drawPixmap(Assets.buttons, 256, 416, 0, 64, 64, 64);
	}

	private void drawReadyUI() {
		IGraphics g = game.getGraphics();
		g.drawPixmap(Assets.ready, 47, 100);
		g.drawLine(0, 416, 480, 416, Color.BLACK);
	}

	@Override
	public void pause() {
		if(state == GameState.RUNNING)
			state = GameState.PAUSED;
		if(world.gameOver) {
			Settings.addScore(world.score);
			Settings.save(game.getFileIO());
		}
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
	
	enum GameState {
		READY,
		RUNNING,
		PAUSED,
		GAME_OVER
	}

}
