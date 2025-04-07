package lando.systems.ld57.audio;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld57.Config;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Assets;
import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.assets.Sounds;

public class AudioManager {
    private static final float DEFAULT_VOLUME = 0.5f;

    private Musics.Type currentMusicType;
    public Music currentMusic = null;

    public MutableFloat musicVolume;
    public MutableFloat soundVolume;

    private Preferences prefs = Main.game.assets.prefs;

    public AudioManager(Assets assets) {
        musicVolume = new MutableFloat(prefs.getFloat("musicVolume", DEFAULT_VOLUME));
        soundVolume = new MutableFloat(prefs.getFloat("soundVolume", DEFAULT_VOLUME));
        prefs.flush();
    }

    public void update(float dt) {
        if (currentMusic == null) return;
        var volume = Config.Flag.MUTE.isEnabled() ? 0f : musicVolume.floatValue();
        currentMusic.setVolume(volume);
        currentMusic.play();
    }

    public void setMusicVolume(float level) {
        musicVolume.setValue(level);
        prefs.putFloat("musicVolume", level);
        prefs.flush();
    }
    public void setSoundVolume(float level) {
        soundVolume.setValue(level);
        prefs.putFloat("soundVolume", level);
        prefs.flush();
    }

    public void playMusic(Musics.Type musicType) {
        if (musicType == currentMusicType) {
            return; // Continue current music
        }
        stopCurrentMusic();
        currentMusicType = musicType;
        startNewMusic(musicType);
    }

    public long playSound(Sounds.Type soundType) {
        return playSound(soundType, 1f, 0f);
    }

    public long playSound(Sounds.Type soundType, float dynamicVolume, float pan) {
        var sounds = soundType.get();
        var volume = Config.Flag.MUTE.isEnabled() ? 0f : soundVolume.floatValue() * dynamicVolume;
        var sound = sounds[MathUtils.random(sounds.length-1)];
        return sound.play(soundType.volume * volume, 1f, pan);
    }

    public long playSound(Sounds.Type soundType, float dynamicVolume) {
        var sounds = soundType.get();
        var volume = Config.Flag.MUTE.isEnabled() ? 0f : soundVolume.floatValue() * dynamicVolume;
        var sound = sounds[MathUtils.random(sounds.length-1)];
        return sound.play(soundType.volume * volume, 1f, 0f);
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
