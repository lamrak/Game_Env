package net.validcat.framework;

import java.util.ArrayList;
import java.util.List;

public class Pool<T> {
	private final List<T> freeObjects;
	private final PoolObjectFactory<T> factory;
	private final int maxSize;
	
	public Pool(PoolObjectFactory<T> factory, int maxSize) {
		this.factory = factory;
		this.maxSize = maxSize;
		this.freeObjects = new ArrayList<T>(maxSize);
	}
	
	public T newObject() {
		return freeObjects.size() == 0 ? factory.createObject() : freeObjects.remove(freeObjects.size() - 1);
	}
	
	public void free(T object) {
		if (freeObjects.size() < maxSize) freeObjects.add(object);
	}
	
	public interface PoolObjectFactory<T> {
		public T createObject();
	}

}
