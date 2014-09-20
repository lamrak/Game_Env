package net.validcat.framework.impl;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import net.validcat.framework.IAudio;
import net.validcat.framework.IMusic;
import net.validcat.framework.ISound;

public class Audio implements IAudio {
	AssetManager assets;
	SoundPool soundPool;
	
	public Audio(Activity activity) {
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.assets = activity.getAssets();
		this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
	}

	@Override
	public IMusic newMusic(String filename) {
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			return new Music(assetDescriptor);
		} catch (IOException e) {
			throw new RuntimeException("Can't load music file" + filename + "'");
		}
	}

	@Override
	public ISound newSound(String filename) {
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			int soundId = soundPool.load(assetDescriptor, 0);
			return new Sound(soundPool, soundId);
		} catch (IOException e) {
			throw new RuntimeException("Can't load sound file '" + filename + "'");
		}
	}

}
