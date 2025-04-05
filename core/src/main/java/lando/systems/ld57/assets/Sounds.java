package lando.systems.ld57.assets;

import com.badlogic.gdx.audio.Sound;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld57.assets.framework.AssetContainer;
import lando.systems.ld57.assets.framework.AssetEnum;
import lando.systems.ld57.utils.Util;

public class Sounds extends AssetContainer<Sounds.Type, Sound> {

    public static AssetContainer<Type, Sound> container;

    private static final String folder = "audio/sounds/";

    public enum Type implements AssetEnum<Sound> {
          BOARD_CLICK("board_click.ogg")
        ;

        private final String path;

        public final float volume = 1;

        Type(String filename) {
            this.path = folder + filename;
        }

        @Override
        public Sound get() {
            return Sounds.container.get(this);
        }
    }

    public Sounds() {
        super(Sounds.class, Sound.class);
        Sounds.container = this;
    }

    @Override
    public void load(Assets assets) {
        var mgr = assets.mgr;
        for (var type : Type.values()) {
            mgr.load(type.path, Sound.class);
        }
    }

    @Override
    public void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : Type.values()) {
            var sound = mgr.get(type.path, Sound.class);
            if (sound == null) {
                Util.log(containerClassName, Stringf.format("init(): sound '%s' not found for type '%s'", type.path, type.name()));
                continue;
            }
            resources.put(type, sound);
        }
    }
}
