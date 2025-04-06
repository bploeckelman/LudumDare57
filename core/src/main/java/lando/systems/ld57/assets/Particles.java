package lando.systems.ld57.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.tommyettinger.digital.Stringf;
import lando.systems.ld57.assets.framework.AssetContainer;
import lando.systems.ld57.assets.framework.AssetEnum;
import lando.systems.ld57.utils.Util;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class Particles extends AssetContainer<Particles.Type, Animation> {

    public static AssetContainer<Type, Animation> container;

    private static final String folder = "particles/";

    public enum Type implements AssetEnum<Animation> {
        CIRCLE(0.1f, "kenney/circle", Animation.PlayMode.LOOP),
        DIRT(0.1f, "kenney/dirt", Animation.PlayMode.LOOP),
        FIRE(0.1f, "kenney/fire", Animation.PlayMode.LOOP),
        FLAME(0.1f, "kenney/flame", Animation.PlayMode.LOOP),
        FLARE(0.1f, "kenney/flare", Animation.PlayMode.LOOP),
        LIGHT(0.1f, "kenney/light", Animation.PlayMode.LOOP),
        MAGIC(0.1f, "kenney/magic", Animation.PlayMode.LOOP),
        MUZZLE(0.1f, "kenney/muzzle", Animation.PlayMode.LOOP),
        SCORCH(0.1f, "kenney/scorch", Animation.PlayMode.LOOP),
        SCRATCH(0.1f, "kenney/scratch", Animation.PlayMode.LOOP),
        SLASH(0.1f, "kenney/slash", Animation.PlayMode.LOOP),
        SMOKE(0.1f, "kenney/smoke", Animation.PlayMode.LOOP),
        SPARK(0.1f, "kenney/spark", Animation.PlayMode.LOOP),
        STAR(0.1f, "kenney/star", Animation.PlayMode.LOOP),
        SYMBOL(0.1f, "kenney/symbol", Animation.PlayMode.LOOP),
        TRACE(0.1f, "kenney/trace", Animation.PlayMode.LOOP),
        TWIRL(0.1f, "kenney/twirl", Animation.PlayMode.LOOP),
        WINDOW(0.1f, "kenney/window", Animation.PlayMode.LOOP),
        SHAPE(0.1f, "kenney-puzzle/shape", Animation.PlayMode.LOOP),
        SPLAT(0.1f, "splats/splat", Animation.PlayMode.LOOP),
        BLOOD(0.1f, "blood/particle-blood", Animation.PlayMode.LOOP)
        ;
        public final float frameDuration;
        public final String regionsName;
        public final Animation.PlayMode playMode;

        Type(float frameDuration, String regionsName, Animation.PlayMode playMode) {
            this.frameDuration = frameDuration;
            this.regionsName = folder + regionsName;
            this.playMode = playMode;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Animation<TextureRegion> get() {
            return (Animation<TextureRegion>) container.get(this);
        }
    }

    public Particles() {
        super(Particles.class, Animation.class);
        Particles.container = this;
    }

    private static final Map<Type, Animation<TextureRegion>> animations = new HashMap<>();

    @Override
    public void init(Assets assets) {
        var atlas = assets.atlas;
        for (var type : Type.values()) {
            var frames = atlas.findRegions(type.regionsName);
            var animation = new Animation<TextureRegion>(type.frameDuration, frames, type.playMode);
            animations.put(type, animation);
        }
    }

    public Animation<TextureRegion> get(Type type) {
        var animation = animations.get(type);
        if (animation == null) {
            Util.log(Stringf.format("Animations", "Animation type '%s', regions '%s' not found", type.name(), type.regionsName));
        }
        return animation;
    }
}
