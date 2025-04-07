package lando.systems.ld57.assets;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld57.assets.framework.AssetContainer;
import lando.systems.ld57.assets.framework.AssetEnum;
import lando.systems.ld57.utils.Util;

public class Sounds extends AssetContainer<Sounds.Type, Sound[]> {

    public static AssetContainer<Type, Sound[]> container;

    private static final String folder = "audio/sounds/";

    public enum Type implements AssetEnum<Sound[]> {
          BOARD_CLICK("board_click.ogg", "error-buzz.ogg")
        ;

        private final Array<String> path;

        public final float volume;


        Type(String... filanames) {
            this(1f, filanames);
        }

        Type(float volume, String... filenames) {
            this.path = new Array<>();
            for (String s : filenames) {
                path.add(folder + s);
            }
            this.volume = volume;
        }

        @Override
        public Sound[] get() {
            return Sounds.container.get(this);
        }
    }

    public Sounds() {
        super(Sounds.class, Sound[].class);
        Sounds.container = this;
    }

    @Override
    public void load(Assets assets) {
        var mgr = assets.mgr;
        for (var type : Type.values()) {
            for (String filename : type.path) {
                mgr.load(filename, Sound.class);
            }
        }
    }

    @Override
    public void init(Assets assets) {
        var mgr = assets.mgr;
        for (var type : Type.values()) {
            Sound[] sounds = new Sound[type.path.size];
            for (int i = 0; i < type.path.size; i++) {
                sounds[i] = mgr.get(type.path.get(i), Sound.class);
            }
            if (sounds == null) {
                Util.log(containerClassName, Stringf.format("init(): sound '%s' not found for type '%s'", type.path, type.name()));
                continue;
            }
            resources.put(type, sounds);
        }
    }
}
