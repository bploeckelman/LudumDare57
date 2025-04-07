package lando.systems.ld57.audio;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.Config;
import lando.systems.ld57.assets.Assets;
import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.assets.Sounds;

public class AudioManager {
    private static final float DEFAULT_VOLUME = 0.5f;

    private Musics.Type currentMusicType;
    public Music currentMusic = null;

    public MutableFloat musicVolume;
    public MutableFloat soundVolume;

    public AudioManager(Assets assets) {
        musicVolume = new MutableFloat(DEFAULT_VOLUME);
        soundVolume = new MutableFloat(DEFAULT_VOLUME);
    }

    public void update(float dt) {
        if (currentMusic == null) return;
        var volume = Config.Flag.MUTE.isEnabled() ? 0f : musicVolume.floatValue();
        currentMusic.setVolume(volume);
        currentMusic.play();
    }

    public void setMusicVolume(float level) {
        musicVolume.setValue(level);
    }
    public void setSoundVolume(float level) {
        soundVolume.setValue(level);
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
        var sounds = soundType.get();
        var volume = Config.Flag.MUTE.isEnabled() ? 0f : soundVolume.floatValue();
        var sound = sounds[MathUtils.random(sounds.length-1)];
        sound.play(soundType.volume * volume, 1f, pan);
    }

    private void stopCurrentMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    private void startNewMusic(Musics.Type musicType) {
        var volume = Config.Flag.MUTE.isEnabled() ? 0f : musicVolume.floatValue();

        currentMusicType = musicType;
        currentMusic = currentMusicType.get();
        currentMusic.setLooping(true);
        currentMusic.setVolume(musicType.volume * volume);
        currentMusic.play();
    }
}
