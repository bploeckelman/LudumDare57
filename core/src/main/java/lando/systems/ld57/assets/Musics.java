package lando.systems.ld57.assets;

import com.badlogic.gdx.audio.Music;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld57.assets.framework.AssetContainer;
import lando.systems.ld57.assets.framework.AssetEnum;
import lando.systems.ld57.utils.Util;

public class Musics extends AssetContainer<Musics.Type, Music> {

    public static AssetContainer<Type, Music> container;

    private static final String folder = "audio/musics/";

    public enum Type implements AssetEnum<Music> {
        TEST ("test_music.mp3"),
        ;

        private final String path;

        public final float volume = 1;

        Type(String filename) {
            this.path = folder + filename;
        }

        @Override
        public Music get() {
            return Musics.container.get(this);
        }
    }

    public Musics() {
        super(Musics.class, Music.class);
        Musics.container = this;
    }

    @Override
    public void load(Assets assets) {
        var mgr = assets.mgr;
        for (var type : Type.values()) {
            mgr.load(type.path, Music.class);
        }
    }

    @Override
    public void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : Type.values()) {
            var music = mgr.get(type.path, Music.class);
            if (music == null) {
                Util.log(containerClassName, Stringf.format("init(): music '%s' not found for type '%s'", type.path, type.name()));
                continue;
            }
            resources.put(type, music);
        }
    }
}
