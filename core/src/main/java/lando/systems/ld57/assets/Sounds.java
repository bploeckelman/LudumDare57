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
        BOARD_CLICK("board_click.ogg", "error-buzz.ogg"),
        JUMP("jump1.ogg"),
        FIREBALL("fireball1.ogg"),

        LINKPAIN("linkPain1.ogg"),
        LINKATTACK("linkAttack1.ogg", "linkAttack2.ogg","linkAttack3.ogg"),
        LINKSWORD("linksword1.ogg"),
        PUNCHHIT("punch_hit1.ogg"),
        PUNCH("punch1.ogg"),

        MEGABUSTER("megabuster1.ogg"),
        BIGBUSTER("bigbuster1.ogg"),
        MEGADAMAGE("megadamage1.ogg"),

        SWIPE1("swipe1.ogg"),
        SWIPE2("swipe2.ogg"),
        GRUNT("grunt1.ogg", "grunt2.ogg", "grunt3.ogg", "grunt4.ogg", "grunt5.ogg", "grunt6.ogg"
            , "grunt7.ogg", "grunt8.ogg", "grunt9.ogg", "grunt10.ogg", "grunt11.ogg"),
        TVSHOW("tvshow1.ogg"),
        STEELYDAN1("steelydan1.ogg"),
        BITCOIN1("bitcoin1.ogg"),
        OZEMPIC("ozempic1.ogg"),

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
