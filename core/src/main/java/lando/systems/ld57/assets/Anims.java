package lando.systems.ld57.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld57.assets.framework.AssetContainer;
import lando.systems.ld57.assets.framework.AssetEnum;

/**
 * {@link AssetContainer} implementation that contains an {@link Animation} asset
 * NOTE(brian): tried using java.lang.reflect.ParameterizedType to encapsulate the Animation type param
 *  but it didn't seems like there was a way to use the type params to get a non-raw class Animation out
 *  so we're just using the raw type and suppressing the warning
 */
@SuppressWarnings("rawtypes")
public class Anims extends AssetContainer<Anims.Type, Animation> {

    public static AssetContainer<Type, Animation> container;

    private static class Path {
        private static final String OLDMAN = "character/oldman/";
        private static final String HERO = "character/hero/";
        private static final String BELMONT = "character/belmont/";
        private static final String LINK = "character/link/";
        private static final String MARIO = "character/mario/";
        private static final String MEGAMAN = "character/megaman/";
        private static final String GOOMBA = "enemies/goomba/";
    }

    public enum Type implements AssetEnum<Animation> {
        // hero animations ------------------------------------------
          HERO_LAND_EFFECT(Path.HERO)
        , HERO_ATTACK_EFFECT(Path.HERO)
        , HERO_ATTACK(Path.HERO)
        , HERO_DEATH(Path.HERO)
        , HERO_FALL(Path.HERO)
        , HERO_IDLE(Path.HERO)
        , HERO_JUMP(Path.HERO)
        , HERO_RUN(Path.HERO)
        // ----------------------------------------------------------
        , OLDMAN_IDLE(Path.OLDMAN)
        , OLDMAN_WALK(Path.OLDMAN, new AnimData(0.15f, Animation.PlayMode.LOOP))
        , OLDMAN_JUMP(Path.OLDMAN, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , OLDMAN_FALL(Path.OLDMAN, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , OLDMAN_HURT(Path.OLDMAN, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , OLDMAN_ATTACK(Path.OLDMAN)
        // ----------------------------------------------------------
        , BELMONT_IDLE(Path.BELMONT)
        , BELMONT_WALK(Path.BELMONT, new AnimData(0.15f, Animation.PlayMode.LOOP))
        , BELMONT_JUMP(Path.BELMONT)
        , BELMONT_FALL(Path.BELMONT)
        , BELMONT_HURT(Path.BELMONT)
        , BELMONT_ATTACK(Path.BELMONT)
        // ----------------------------------------------------------
        , LINK_IDLE(Path.LINK)
        , LINK_WALK(Path.LINK)
        , LINK_JUMP(Path.LINK, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , LINK_FALL(Path.LINK)
        , LINK_HURT(Path.LINK, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , LINK_ATTACK(Path.LINK)
        // ----------------------------------------------------------
        , MARIO_IDLE(Path.MARIO)
        , MARIO_WALK(Path.MARIO)
        , MARIO_JUMP(Path.MARIO, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , MARIO_FALL(Path.MARIO)
        , MARIO_HURT(Path.MARIO, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , MARIO_ATTACK(Path.MARIO)
        // ----------------------------------------------------------
        , MEGAMAN_IDLE(Path.MEGAMAN, new AnimData(0.25f, Animation.PlayMode.LOOP_RANDOM))
        , MEGAMAN_WALK(Path.MEGAMAN)
        , MEGAMAN_JUMP(Path.MEGAMAN, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , MEGAMAN_FALL(Path.MEGAMAN)
        , MEGAMAN_HURT(Path.MEGAMAN, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , MEGAMAN_ATTACK(Path.MEGAMAN)
        // enemies ----------------------------------------------------
        , GOOMBA_WALK(Path.GOOMBA)
        , GOOMBA_DEATH(Path.GOOMBA)
        ;

        private final String path;
        private final String name;
        private final AnimData data;

        Type(String path) {
            this(path, null, null);
        }

        Type(String path, AnimData data) {
            this(path, null, data);
        }

        Type(String path, String name) {
            this(path, name, null);
        }

        Type(String path, String name, AnimData data) {
            this.path = path;
            this.name = (name != null) ? name : name().toLowerCase().replace("_", "-");
            this.data = (data != null) ? data : new AnimData();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Animation<TextureRegion> get() {
            return (Animation<TextureRegion>) container.get(this);
        }
    }

    public Anims() {
        super(Anims.class, Animation.class);
        Anims.container = this;
    }

    @Override
    public void init(Assets assets) {
        // TODO(brian): does AssetContainer.initInternal get called without an explicit super.initInternal call?
        var atlas = assets.atlas;
        for (var type : Type.values()) {
            var data = type.data;
            var regions = atlas.findRegions(type.path + type.name);
            var anim = new Animation<TextureRegion>(data.frameDuration, regions, data.playMode);
            resources.put(type, anim);
        }
    }

    public static class AnimData {
        private static final float DEFAULT_FRAME_DURATION = 0.1f;
        private static final Animation.PlayMode DEFAULT_PLAY_MODE = Animation.PlayMode.LOOP;

        public final float frameDuration;
        public final Animation.PlayMode playMode;

        public AnimData() {
            this(DEFAULT_FRAME_DURATION, DEFAULT_PLAY_MODE);
        }

        public AnimData(float frameDuration) {
            this(frameDuration, DEFAULT_PLAY_MODE);
        }

        public AnimData(float frameDuration, Animation.PlayMode playMode) {
            if (frameDuration <= 0) {
                frameDuration = DEFAULT_FRAME_DURATION;
            }
            if (playMode == null) {
                playMode = DEFAULT_PLAY_MODE;
            }
            this.frameDuration = frameDuration;
            this.playMode = playMode;
        }
    }
}
