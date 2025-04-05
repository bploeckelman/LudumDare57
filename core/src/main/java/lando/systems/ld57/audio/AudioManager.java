package lando.systems.ld57.audio;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.audio.Music;
import lando.systems.ld57.assets.Assets;
import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.assets.Sounds;

public class AudioManager {
    private static final float DEFAULT_VOLUME = 0.5f;

    private Musics.Type currentMusicType;
    private Music currentMusic = null;

    public MutableFloat musicVolume;
    public MutableFloat soundVolume;

    public AudioManager(Assets assets) {
        musicVolume = new MutableFloat(DEFAULT_VOLUME);
        soundVolume = new MutableFloat(DEFAULT_VOLUME);
    }

    public void update(float dt) {
        if (currentMusic == null) return;
        currentMusic.setVolume(musicVolume.floatValue());
        currentMusic.play();
    }

    public void playMusic(Musics.Type musicType) {
        if (musicType == currentMusicType) {
            return; // Continue current music
        }
        stopCurrentMusic();
        currentMusicType = musicType;
        startNewMusic(musicType);
    }

    public void playSound(Sounds.Type soundType) {
        playSound(soundType, 0f);
    }

    public void playSound(Sounds.Type soundType, float pan) {
        var sound = soundType.get();
        sound.play(soundType.volume * soundVolume.floatValue(), 1f, pan);
    }

    private void stopCurrentMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    private void startNewMusic(Musics.Type musicType) {
        currentMusicType = musicType;
        currentMusic = currentMusicType.get();
        currentMusic.setLooping(true);
        currentMusic.setVolume(musicType.volume * musicVolume.floatValue());
        currentMusic.play();
    }
}
