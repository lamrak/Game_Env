package com.example.glsurfaceviewtest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.FloatMath;
import android.util.Log;

import net.validcat.framework.IGame;
import net.validcat.framework.IInput.TouchEvent;
import net.validcat.framework.Screen;
import net.validcat.framework.Texture;
import net.validcat.framework.Vertices;
import net.validcat.framework.game2d.DynamicGameObject;
import net.validcat.framework.game2d.GameObject;
import net.validcat.framework.gl.Camera2D;
import net.validcat.framework.gl.GLGame;
import net.validcat.framework.gl.SpatialHashGrid;
import net.validcat.framework.gl.GLGraphics;
import net.validcat.framework.math.OverlapTester;
import net.validcat.framework.math.Vector2;
import net.validcat.framework.utils.FPSCounter;

@SuppressLint("FloatMath")
public class GLGameTest extends GLGame { //TODO 440
	
	@Override
	public Screen getStartScreen() {
		return new Camera2DScreen(this);
//		return new CollisionScreen(this); //420
//		return new CannonGravityScreen(this); // 400
//		return new CannonScreen(this);
//		return new BobScreen(this);
	}
	
	class Camera2DScreen extends Screen {
		final int NUM_TARGETS = 20;
		final float WORLD_WIDTH = 9.6f;
		final float WORLD_HEIGHT = 4.8f;
		GLGraphics glGraphics;
		Cannon cannon;
		DynamicGameObject ball;
		List<GameObject> targets;
		SpatialHashGrid grid;
		Vertices cannonVertices;
		Vertices ballVertices;
		Vertices targetVertices;
		Vector2 touchPos = new Vector2();
		Vector2 gravity = new Vector2(0,-10);
		
		Camera2D camera;
		
		public Camera2DScreen(IGame game) {
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
			camera = new Camera2D(glGraphics, WORLD_WIDTH, WORLD_HEIGHT);
			
			cannon = new Cannon(0, 0, 1, 1);
			ball = new DynamicGameObject(0, 0, 0.2f, 0.2f);
			targets = new ArrayList<GameObject>(NUM_TARGETS);
			grid = new SpatialHashGrid(WORLD_WIDTH, WORLD_HEIGHT, 2.5f);
			for (int i = 0; i < NUM_TARGETS; i++) {
				GameObject target = new GameObject((float) Math.random()
						* WORLD_WIDTH, (float) Math.random() * WORLD_HEIGHT, 0.5f, 0.5f);
				grid.insertStaticObject(target);
				targets.add(target);
			}
			cannonVertices = new Vertices(glGraphics, 3, 0, false, false);
			cannonVertices.setVertices(new float[] { -0.5f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f }, 0, 6);
			ballVertices = new Vertices(glGraphics, 4, 6, false, false);
			ballVertices.setVertices(new float[] { -0.1f, -0.1f, 0.1f, -0.1f, 0.1f, 0.1f, -0.1f, 0.1f }, 0, 8);
			ballVertices.setIndices(new short[] { 0, 1, 2, 2, 3, 0 }, 0, 6);
			targetVertices = new Vertices(glGraphics, 4, 6, false, false);
			targetVertices.setVertices(new float[] { -0.25f, -0.25f, 0.25f, -0.25f, 0.25f, 0.25f, -0.25f, 0.25f }, 0, 8);
			targetVertices.setIndices(new short[] { 0, 1, 2, 2, 3, 0 }, 0, 6);
		}

		@Override
		public void update(float deltaTime) {
			List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
			game.getInput().getKeyEvents();

			int len = touchEvents.size();
			for (int i = 0; i < len; i++) {
				TouchEvent event = touchEvents.get(i);
				camera.touchToWorld(touchPos.set(event.x, event.y));
				cannon.angle = touchPos.sub(cannon.position).angle();
				if (event.type == TouchEvent.TOUCH_UP) {
					float radians = cannon.angle * Vector2.TO_RADIANS;
					float ballSpeed = touchPos.len() * 2;
					ball.position.set(cannon.position);
					ball.velocity.x = FloatMath.cos(radians) * ballSpeed;
					ball.velocity.y = FloatMath.sin(radians) * ballSpeed;
					ball.bounds.lowerLeft.set(ball.position.x - 0.1f, ball.position.y - 0.1f);
				}
			}
			
			ball.velocity.add(gravity.x * deltaTime, gravity.y * deltaTime);
			ball.position.add(ball.velocity.x * deltaTime, ball.velocity.y * deltaTime);
			ball.bounds.lowerLeft.add(ball.velocity.x * deltaTime, ball.velocity.y * deltaTime);
			List<GameObject> colliders = grid.getPotentialColliders(ball);
			len = colliders.size();
			for (int i = 0; i < len; i++) {
				GameObject collider = colliders.get(i);
				if (OverlapTester.overlapRectangles(ball.bounds, collider.bounds)) {
					grid.removeObject(collider);
					targets.remove(collider);
				}
			}
			
			if(ball.position.y > 0) {
				camera.position.set(ball.position);
				camera.zoom = 1 + ball.position.y / WORLD_HEIGHT;
			} else {
				camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
				camera.zoom = 1;
			}
		}

		@Override
		public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			camera.setViewportAndMatrices();
			
			gl.glColor4f(0, 1, 0, 1);
			targetVertices.bind();
			int len = targets.size();
			for (int i = 0; i < len; i++) {
				GameObject target = targets.get(i);
				gl.glLoadIdentity();
				gl.glTranslatef(target.position.x, target.position.y, 0);
				targetVertices.draw(GL10.GL_TRIANGLES, 0, 6);
			}
			targetVertices.unbind();
			
			gl.glLoadIdentity();
			gl.glTranslatef(ball.position.x, ball.position.y, 0);
			gl.glColor4f(1, 0, 0, 1);
			ballVertices.bind();
			ballVertices.draw(GL10.GL_TRIANGLES, 0, 6);
			ballVertices.unbind();
			
			gl.glLoadIdentity();
			gl.glTranslatef(cannon.position.x, cannon.position.y, 0);
			gl.glRotatef(cannon.angle, 0, 0, 1);
			gl.glColor4f(1, 1, 1, 1);
			cannonVertices.bind();
			cannonVertices.draw(GL10.GL_TRIANGLES, 0, 3);
			cannonVertices.unbind();
		}

		@Override public void pause() {}
		@Override public void resume() {}
		@Override public void dispose() {}
	}
	
	class CollisionScreen extends Screen {
		final int NUM_TARGETS = 20;
		final float WORLD_WIDTH = 9.6f;
		final float WORLD_HEIGHT = 4.8f;
		GLGraphics glGraphics;
		Cannon cannon;
		DynamicGameObject ball;
		List<GameObject> targets;
		SpatialHashGrid grid;
		Vertices cannonVertices;
		Vertices ballVertices;
		Vertices targetVertices;
		Vector2 touchPos = new Vector2();
		Vector2 gravity = new Vector2(0,-10);
		
		public CollisionScreen(IGame game) {
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
			cannon = new Cannon(0, 0, 1, 1);
			ball = new DynamicGameObject(0, 0, 0.2f, 0.2f);
			targets = new ArrayList<GameObject>(NUM_TARGETS);
			grid = new SpatialHashGrid(WORLD_WIDTH, WORLD_HEIGHT, 2.5f);
			for (int i = 0; i < NUM_TARGETS; i++) {
				GameObject target = new GameObject((float) Math.random()
						* WORLD_WIDTH, (float) Math.random() * WORLD_HEIGHT, 0.5f, 0.5f);
				grid.insertStaticObject(target);
				targets.add(target);
			}
			cannonVertices = new Vertices(glGraphics, 3, 0, false, false);
			cannonVertices.setVertices(new float[] { -0.5f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f }, 0, 6);
			ballVertices = new Vertices(glGraphics, 4, 6, false, false);
			ballVertices.setVertices(new float[] { -0.1f, -0.1f, 0.1f, -0.1f, 0.1f, 0.1f, -0.1f, 0.1f }, 0, 8);
			ballVertices.setIndices(new short[] { 0, 1, 2, 2, 3, 0 }, 0, 6);
			targetVertices = new Vertices(glGraphics, 4, 6, false, false);
			targetVertices.setVertices(new float[] { -0.25f, -0.25f, 0.25f, -0.25f, 0.25f, 0.25f, -0.25f, 0.25f }, 0, 8);
			targetVertices.setIndices(new short[] { 0, 1, 2, 2, 3, 0 }, 0, 6);
		}

		@Override
		public void update(float deltaTime) {
			List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
			game.getInput().getKeyEvents();

			int len = touchEvents.size();
			for (int i = 0; i < len; i++) {
				TouchEvent event = touchEvents.get(i);
				touchPos.x = (event.x / (float) glGraphics.getWidth()) * WORLD_WIDTH;
				touchPos.y = (1 - event.y / (float) glGraphics.getHeight()) * WORLD_HEIGHT;
				cannon.angle = touchPos.sub(cannon.position).angle();
				if (event.type == TouchEvent.TOUCH_UP) {
					float radians = cannon.angle * Vector2.TO_RADIANS;
					float ballSpeed = touchPos.len() * 2;
					ball.position.set(cannon.position);
					ball.velocity.x = FloatMath.cos(radians) * ballSpeed;
					ball.velocity.y = FloatMath.sin(radians) * ballSpeed;
					ball.bounds.lowerLeft.set(ball.position.x - 0.1f, ball.position.y - 0.1f);
				}
			}
			
			ball.velocity.add(gravity.x * deltaTime, gravity.y * deltaTime);
			ball.position.add(ball.velocity.x * deltaTime, ball.velocity.y * deltaTime);
			ball.bounds.lowerLeft.add(ball.velocity.x * deltaTime, ball.velocity.y * deltaTime);
			List<GameObject> colliders = grid.getPotentialColliders(ball);
			len = colliders.size();
			for (int i = 0; i < len; i++) {
				GameObject collider = colliders.get(i);
				if (OverlapTester.overlapRectangles(ball.bounds, collider.bounds)) {
					grid.removeObject(collider);
					targets.remove(collider);
				}
			}
		}

		@Override
		public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0, WORLD_WIDTH, 0, WORLD_HEIGHT, 1, -1);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			
			gl.glColor4f(0, 1, 0, 1);
			targetVertices.bind();
			int len = targets.size();
			for (int i = 0; i < len; i++) {
				GameObject target = targets.get(i);
				gl.glLoadIdentity();
				gl.glTranslatef(target.position.x, target.position.y, 0);
				targetVertices.draw(GL10.GL_TRIANGLES, 0, 6);
			}
			targetVertices.unbind();
			
			gl.glLoadIdentity();
			gl.glTranslatef(ball.position.x, ball.position.y, 0);
			gl.glColor4f(1, 0, 0, 1);
			ballVertices.bind();
			ballVertices.draw(GL10.GL_TRIANGLES, 0, 6);
			ballVertices.unbind();
			
			gl.glLoadIdentity();
			gl.glTranslatef(cannon.position.x, cannon.position.y, 0);
			gl.glRotatef(cannon.angle, 0, 0, 1);
			gl.glColor4f(1, 1, 1, 1);
			cannonVertices.bind();
			cannonVertices.draw(GL10.GL_TRIANGLES, 0, 3);
			cannonVertices.unbind();
		}

		@Override public void pause() {}
		@Override public void resume() {}
		@Override public void dispose() {}
	}
	
	class CannonGravityScreen extends Screen {
		float FRUSTUM_WIDTH = 9.6f;
		float FRUSTUM_HEIGHT = 6.4f;
		GLGraphics glGraphics;
		Vertices cannonVertices;
		Vertices ballVertices;
		Vector2 cannonPos = new Vector2();
		float cannonAngle = 0;
		Vector2 touchPos = new Vector2();
		Vector2 ballPos = new Vector2(0,0);
		Vector2 ballVelocity = new Vector2(0,0);
		Vector2 gravity = new Vector2(0,-10);
		
		public CannonGravityScreen(IGame game) {
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
			cannonVertices = new Vertices(glGraphics, 3, 0, false, false);
			cannonVertices.setVertices(new float[] { -0.5f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f }, 0, 6);
			ballVertices = new Vertices(glGraphics, 4, 6, false, false);
			ballVertices.setVertices(new float[] { -0.1f, -0.1f, 0.1f, -0.1f, 0.1f, 0.1f, -0.1f, 0.1f }, 0, 8);
			ballVertices.setIndices(new short[] { 0, 1, 2, 2, 3, 0 }, 0, 6);
		}
		
		public void update(float deltaTime) {
			List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
			game.getInput().getKeyEvents();
			int len = touchEvents.size();
			for (int i = 0; i < len; i++) {
				TouchEvent event = touchEvents.get(i);
				touchPos.x = (event.x / (float) glGraphics.getWidth()) * FRUSTUM_WIDTH;
				touchPos.y = (1 - event.y / (float) glGraphics.getHeight()) * FRUSTUM_HEIGHT;
				cannonAngle = touchPos.sub(cannonPos).angle();
				if (event.type == TouchEvent.TOUCH_UP) {
					float radians = cannonAngle * Vector2.TO_RADIANS;
					float ballSpeed = touchPos.len();
					ballPos.set(cannonPos);
					ballVelocity.x = FloatMath.cos(radians) * ballSpeed;
					ballVelocity.y = FloatMath.sin(radians) * ballSpeed;
				}
			}
//			ballVelocity.add(gravity.x * deltaTime, gravity.y * deltaTime);
			ballPos.add(ballVelocity.x * deltaTime, ballVelocity.y * deltaTime);
		}
		
		@Override
		public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0, FRUSTUM_WIDTH, 0, FRUSTUM_HEIGHT, 1, -1);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			
			gl.glLoadIdentity();
			gl.glTranslatef(cannonPos.x, cannonPos.y, 0);
			gl.glRotatef(cannonAngle, 0, 0, 1);
			gl.glColor4f(1, 1, 1, 1);
			cannonVertices.bind();
			cannonVertices.draw(GL10.GL_TRIANGLES, 0, 3);
			cannonVertices.unbind();
			
			gl.glLoadIdentity();
			gl.glTranslatef(ballPos.x, ballPos.y, 0);
			gl.glColor4f(1, 0, 0, 1);
			ballVertices.bind();
			ballVertices.draw(GL10.GL_TRIANGLES, 0, 6);
			ballVertices.unbind();
		}
		
		@Override public void pause() {}
		@Override public void resume() {}
		@Override public void dispose() {}
		
	}
	
	class CannonScreen extends Screen {
		float FRUSTUM_WIDTH = 9.6f;
		float FRUSTUM_HEIGHT = 6.4f;
		GLGraphics glGraphics;
		Vertices vertices;
		Vector2 cannonPos = new Vector2(2.4f, 0.5f);
		float cannonAngle = 0;
		Vector2 touchPos = new Vector2();
		
		public CannonScreen(IGame game) {
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
			vertices = new Vertices(glGraphics, 3, 0, false, false);
			vertices.setVertices(new float[] { -0.5f, -0.5f, 0.5f, 0.0f, -0.5f, 0.5f }, 0, 6);
		}
		
		@Override
		public void update(float deltaTime) {
			List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
			game.getInput().getKeyEvents();
			int len = touchEvents.size();
			for (int i = 0; i < len; i++) {
				TouchEvent event = touchEvents.get(i);
				touchPos.x = (event.x / (float) glGraphics.getWidth()) * FRUSTUM_WIDTH;
				touchPos.y = (1 - event.y / (float) glGraphics.getHeight()) * FRUSTUM_HEIGHT;
				cannonAngle = touchPos.sub(cannonPos).angle();
			}
		}
		
		@Override
		public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0, FRUSTUM_WIDTH, 0, FRUSTUM_HEIGHT, 1, -1);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glTranslatef(cannonPos.x, cannonPos.y, 0);
			gl.glRotatef(cannonAngle, 0, 0, 1);
			vertices.bind();
			vertices.draw(GL10.GL_TRIANGLES, 0, 3);
			vertices.unbind();
		}

		@Override
		public void pause() {}

		@Override
		public void resume() {}

		@Override
		public void dispose() {}
	}
	/**
	 * 
	 * @author user
	 *
	 */
	class BobScreen extends Screen {
		static final int NUM_BOBS = 100;
		GLGraphics glGraphics;
		Texture bobTexture;
		Vertices bobModel;
		Bob[] bobs;
		FPSCounter counter;
		
		public BobScreen(IGame game) {
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
			bobTexture = new Texture((GLGame) game, "bobargb8888-32x32.png");
			bobModel = new Vertices(glGraphics, 4, 12, false, true);
			bobModel.setVertices(new float[] { 
					-16, -16, 0, 1, 
					16,  -16, 1, 1,
					16, 16, 1, 0, 
					-16, 16, 0, 0, 
					}, 0, 16);
			bobModel.setIndices(new short[] { 0, 1, 2, 2, 3, 0 }, 0, 6);
			
			
			bobs = new Bob[NUM_BOBS];
			for (int i = 0; i < NUM_BOBS; i++) {
				bobs[i] = new Bob();
			}
			
			counter = new FPSCounter();
		}
		
		@Override
		public void update(float deltaTime) {
			game.getInput().getTouchEvents();
			game.getInput().getKeyEvents();
			for (int i = 0; i < NUM_BOBS; i++) {
				bobs[i].update(deltaTime);
			}
		}
		
		@Override
		public void present(float deltaTime) {
			counter.logFrame();
			GL10 gl = glGraphics.getGL();
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			
			bobModel.bind();
			for (int i = 0; i < NUM_BOBS; i++) {
				gl.glLoadIdentity();
				gl.glTranslatef(bobs[i].x, bobs[i].y, 0);
//				gl.glRotatef(45, 0, 0, 1);
//				gl.glScalef(2, 0.5f, 0);
				bobModel.draw(GL10.GL_TRIANGLES, 0, 6);
			}
			bobModel.unbind();
		}

		@Override
		public void pause() {}

		@Override
		public void resume() {
			GL10 gl = glGraphics.getGL();
			gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
			gl.glClearColor(1, 0, 0, 1);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0, 320, 0, 480, 1, -1);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			bobTexture.reload();
			gl.glEnable(GL10.GL_TEXTURE_2D);
			bobTexture.bind();
		}

		@Override
		public void dispose() {}
	}	
		
	class BlendingScreen extends Screen {
		GLGraphics glGraphics;
		Vertices vertices;
		Texture textureRgb;
		Texture textureRgba;

		public BlendingScreen(IGame game) {
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
			textureRgb = new Texture((GLGame) game, "bobrgb888.png");
			textureRgba = new Texture((GLGame) game, "bobargb8888.png");
			vertices = new Vertices(glGraphics, 8, 12, true, true);
			float[] rects = new float[] { 
//					100, 100, 1, 1, 1, 0.5f, 0, 1, 
//					228, 100, 1, 1, 1, 0.5f, 1, 1, 
//					228, 228, 1, 1, 1, 0.5f, 1, 0,
//					100, 228, 1, 1, 1, 0.5f, 0, 0, 
					100, 300, 1, 1, 1, 1, 0, 1,
					228, 300, 1, 1, 1, 1, 1, 1, 
					228, 428, 1, 1, 1, 1, 1, 0,
					100, 428, 1, 1, 1, 1, 0, 0 };
			vertices.setVertices(rects, 0, rects.length);
			vertices.setIndices(new short[] { 0, 1, 2, 2, 3, 0/*, 4, 5, 6, 6, 7, 4 */}, 0, 6);
		};
		
		@Override
		public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
			gl.glClearColor(1, 0, 0, 1);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0, 320, 0, 480, 1, -1);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL10.GL_TEXTURE_2D);
//			textureRgb.bind();
			textureRgba.bind();
			vertices.draw(GL10.GL_TRIANGLES, 0, 6);
//			textureRgba.bind();
//			vertices.draw(GL10.GL_TRIANGLES, 6, 6);
		}

		@Override
		public void update(float deltaTime) {}

		@Override
		public void pause() {}

		@Override
		public void resume() {}

		@Override
		public void dispose() {}
	}
	
	class IndexedScreen extends Screen {
		final int VERTEX_SIZE = (2 + 2) * 4;
		GLGraphics glGraphics;
		FloatBuffer vertices;
		ShortBuffer indices;
		Texture texture;

		public IndexedScreen(IGame game) {
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * VERTEX_SIZE);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertices = byteBuffer.asFloatBuffer();
			vertices.put(new float[] { 	100.0f, 100.0f, 0.0f, 1.0f, 
										228.0f, 100.0f, 1.0f, 1.0f, 
										228.0f, 228.0f, 1.0f, 0.0f, 
										100.0f, 228.0f, 0.0f, 0.0f });
			vertices.flip();
			byteBuffer = ByteBuffer.allocateDirect(6 * 2);
			byteBuffer.order(ByteOrder.nativeOrder());
			indices = byteBuffer.asShortBuffer();
			indices.put(new short[] { 0, 1, 2, 2, 3, 0 });
			indices.flip();
			texture = new Texture((GLGame) game, "bobrgb888.png");
		}

		@Override
		public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0, 320, 0, 480, 1, -1);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			texture.bind();
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			vertices.position(0);
			gl.glVertexPointer(2, GL10.GL_FLOAT, VERTEX_SIZE, vertices);
			vertices.position(2);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, VERTEX_SIZE, vertices);
			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT,
					indices);
		}

		@Override public void update(float deltaTime) {}
		@Override public void pause() {}
		@Override public void resume() {}
		@Override public void dispose() {}
	}
	
	class TexturedTriangleScreen extends Screen {
		final int VERTEX_SIZE = (2 + 2) * 4;
		GLGraphics glGraphics;
		FloatBuffer vertices;
		int textureId;
		
		public TexturedTriangleScreen(IGame game) {
			super(game);
			
			glGraphics = ((GLGame) game).getGLGraphics();
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(3 * VERTEX_SIZE);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertices = byteBuffer.asFloatBuffer();
			vertices.put( new float[] { 
					0.0f, 0.0f, 0.0f, 1.0f,
					319.0f, 0.0f, 1.0f, 1.0f,
					160.0f, 479.0f, 0.5f, 0.0f});
			vertices.flip();
			textureId = loadTexture("bobrgb888.png");
		}
		
		public int loadTexture(String fileName) {
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(game.getFileIO().readAsset(fileName));
				GL10 gl = glGraphics.getGL();
				int textureIds[] = new int[1];
				gl.glGenTextures(1, textureIds, 0);
				int textureId = textureIds[0];
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
				bitmap.recycle();
				
				return textureId;
			} catch (IOException e) {
				Log.d("TexturedTriangleTest", "couldn't load asset bobrgb888.png'!");
				throw new RuntimeException("couldn't load asset '" + fileName + "'");
			}
		}

		@Override
		public void update(float deltaTime) {
			game.getInput().getTouchEvents();
			game.getInput().getKeyEvents();
		}

		@Override
		public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0, 320, 0, 480, 1, -1);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			vertices.position(0);
			gl.glVertexPointer(2, GL10.GL_FLOAT, VERTEX_SIZE, vertices);
			vertices.position(2);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, VERTEX_SIZE, vertices);
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
		}

		@Override
		public void pause() {}

		@Override
		public void resume() {}

		@Override
		public void dispose() {}
	}
	
	class FirstTriangleTest extends Screen {
		GLGraphics glGraphics;
		FloatBuffer vertices;
		int VERTEX_SIZE;
		
		public FirstTriangleTest(IGame game) {
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
			
			VERTEX_SIZE = (2 + 4) * 4;
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(3 * VERTEX_SIZE);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertices = byteBuffer.asFloatBuffer();
//			vertices.put( new float[] { 0.0f, 0.0f, 319.0f, 0.0f, 160.0f, 479.0f});
			vertices.put( new float[] { 0.0f, 	0.0f, 	1, 0, 0, 1,
										319.0f, 0.0f, 	1, 1, 1, 1,
										160.0f, 479.0f, 1, 1, 1, 1});
			vertices.flip();
		}

		@Override
		public void update(float deltaTime) {
			game.getInput().getTouchEvents();
			game.getInput().getKeyEvents();
		}

		@Override
		public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			gl.glClear(GL10. GL_COLOR_BUFFER_BIT);
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
		}

		@Override
		public void pause() {}

		@Override
		public void resume() {
			GL10 gl = glGraphics.getGL();
			gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
			
			gl.glClear(GL10. GL_COLOR_BUFFER_BIT);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrthof(0, 320, 0, 480, 1, -1);
			
//			gl.glColor4f(1, 0, 0, 1);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			vertices.position(0);
			gl.glVertexPointer(2, GL10.GL_FLOAT, VERTEX_SIZE, vertices);
			vertices.position(2);
			gl.glColorPointer(4, GL10.GL_FLOAT, VERTEX_SIZE, vertices);
			
//			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}

		@Override
		public void dispose() {}
		
	}
	
	class TestScreen extends Screen {
		GLGraphics glGraphics;
		Random rand = new Random();
		
		public TestScreen(IGame game) {
			super(game);
			glGraphics = ((GLGame) game).getGLGraphics();
		}
		
		@Override
		public void present(float deltaTime) {
			GL10 gl = glGraphics.getGL();
			gl.glClearColor(rand.nextFloat(), rand.nextFloat(),
			rand.nextFloat(), 1);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		}
		
		@Override
		public void pause() {}
		
		@Override
		public void resume() {}
		
		@Override
		public void dispose() {}
		
		@Override
		public void update(float deltaTime) {}
	
	}

}

//TODO 344 page
//gl.glClearColor(0,0,0,1);
//gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//gl.glViewport(0, 0, glGraphics.getWidth(), glGraphics.getHeight());
//gl.glMatrixMode(GL10.GL_PROJECTION);
//gl.glLoadIdentity();
//gl.glOrthof(0, 320, 0, 480, 1, -1);

//Bitmap bitmap = BitmapFactory.decodeStream(game.getFileIO().readAsset("bobrgb888.png"));
//int textureIds[] = new int[1];
//gl.glGenTextures(1, textureIds, 0);
//int textureId = textureIds[0];
//gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
//GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
//gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
//bitmap.recycle();
