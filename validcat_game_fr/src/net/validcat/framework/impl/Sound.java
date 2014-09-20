package net.validcat.framework.impl;

import android.media.SoundPool;
import net.validcat.framework.ISound;

public class Sound implements ISound {
	SoundPool soundPool;
	int soundId;
	
	public Sound(SoundPool soundPool, int soundId) {
		this.soundPool = soundPool;
		this.soundId = soundId; 
	}

	@Override
	public void play(float volume) {
		soundPool.play(soundId, volume, volume, 0, 0, 1);
	}

	@Override
	public void dispose() {
		soundPool.unload(soundId);
	}

}
