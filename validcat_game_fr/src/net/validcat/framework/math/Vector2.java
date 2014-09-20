package net.validcat.framework.math;

import android.annotation.SuppressLint;
import android.util.FloatMath;

@SuppressLint("FloatMath")
public class Vector2 {
	public static float TO_RADIANS = (1 / 180.0f) * (float) Math.PI;
	public static float TO_DEGREES = (1 / (float) Math.PI) * 180;
	public float x, y;

	public Vector2() {
	}

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(Vector2 other) {
		this.x = other.x;
		this.y = other.y;
	}
	
	public Vector2 copy() {
		return new Vector2(x, y);
	}
	
	public Vector2 set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2 set(Vector2 other) {
		this.x = other.x;
		this.y = other.y;
		return this;
	}
	
	/**
	 * Vector1 + Vector2
	 */
	public Vector2 add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	/**
	 * Vector1 + Vector2
	 */
	public Vector2 add(Vector2 other) {
		this.x += other.x;
		this.y += other.y;
		return this;
	}

	/**
	 * Vector1 - Vector2
	 */
	public Vector2 sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	/**
	 * Vector1 - Vector2
	 */
	public Vector2 sub(Vector2 other) {
		this.x -= other.x;
		this.y -= other.y;
		return this;
	}
	
	public Vector2 mul(float scalar) {
		this.x *= scalar;
		this.y *= scalar;
		return this;
	}
	
	public float len() {
		return FloatMath.sqrt(x * x + y * y);
	}
	
	/**
	 * Метод nor() нормализирует вектор до единичной длины.
	 * @return
	 */
	public Vector2 nor() {
		float len = len();
		if (len != 0) {
			this.x /= len;
			this.y /= len;
		}
		return this;
	}
	
	/**
	 * Метод angle() вычисляет угол между вектором и осью х, используя метод atan2().
	 * @return
	 */
	public float angle() {
		float angle = (float) Math.atan2(y, x) * TO_DEGREES;
		if (angle < 0)
			angle += 360;
		return angle;
	}
	
	/**
	 * Метод rotate() поворачивает вектор вокруг начала координат на данный угол.
	 * @param angle
	 * @return
	 */
	public Vector2 rotate(float angle) {
		float rad = angle * TO_RADIANS;
		float cos = FloatMath.cos(rad);
		float sin = FloatMath.sin(rad);
		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;
		this.x = newX;
		this.y = newY;
		return this;
	}
	
	/**
	 * Подсчитывают расстояния между этим и другим вектором.
	 * @param other
	 * @return
	 */
	public float dist(Vector2 other) {
		float distX = this.x - other.x;
		float distY = this.y - other.y;
		return FloatMath.sqrt(distX * distX + distY * distY);
	}

	public float dist(float x, float y) {
		float distX = this.x - x;
		float distY = this.y - y;
		return FloatMath.sqrt(distX * distX + distY * distY);
	}
	
	public float distSquared(Vector2 other) {
		float distX = this.x - other.x;
		float distY = this.y - other.y;
		return distX * distX + distY * distY;
	}

	public float distSquared(float x2, float y2) {
		float distX = this.x - x2;
		float distY = this.y - y2;
		return distX * distX + distY * distY;
	}
}
