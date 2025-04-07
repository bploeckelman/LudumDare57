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
        private static final String GOOMBA = "enemies/mario/goomba/";
        private static final String KOOPA = "enemies/mario/koopa/";
        private static final String ANGRY_SUN = "enemies/mario/angry-sun/";
        private static final String BULLET_BILL = "enemies/mario/bullet-bill/";
        private static final String SKELETON = "enemies/castlevania/skeleton/";
        private static final String CASTLE_BAT = "enemies/castlevania/bat/";
        private static final String EAGLE = "enemies/castlevania/eagle/";
        private static final String MONKEY = "enemies/castlevania/monkey/";
        private static final String MEDUSA = "enemies/castlevania/medusa/";
        private static final String MEGA_BAT_CLOSED = "enemies/megaman/bat-closed/";
        private static final String MEGA_BAT_FLYING = "enemies/megaman/bat-flying/";
        private static final String HELMET = "enemies/megaman/helmet/";
        private static final String HOTHEAD_IDLE = "enemies/megaman/hothead_idle/";
        private static final String HOTHEAD_THROWING = "enemies/megaman/hothead_throwing/";
        private static final String BOSS = "boss/";
        private static final String ITEMS = "items/";
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
        , OLDMAN_ATTACK(Path.OLDMAN, new AnimData(0.03f, Animation.PlayMode.NORMAL))
        , OLDMAN_POWERATTACK(Path.OLDMAN, new AnimData(0.06f, Animation.PlayMode.NORMAL))
        // ----------------------------------------------------------
        , BELMONT_IDLE(Path.BELMONT)
        , BELMONT_WALK(Path.BELMONT, new AnimData(0.15f, Animation.PlayMode.LOOP))
        , BELMONT_JUMP(Path.BELMONT)
        , BELMONT_FALL(Path.BELMONT)
        , BELMONT_HURT(Path.BELMONT, new AnimData(.5f, Animation.PlayMode.NORMAL))
        , BELMONT_ATTACK(Path.BELMONT, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , BELMONT_POWERATTACK(Path.BELMONT, new AnimData(0.3f, Animation.PlayMode.NORMAL))
        // ----------------------------------------------------------
        , LINK_IDLE(Path.LINK)
        , LINK_WALK(Path.LINK)
        , LINK_JUMP(Path.LINK, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , LINK_FALL(Path.LINK)
        , LINK_HURT(Path.LINK, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , LINK_ATTACK(Path.LINK, new AnimData(.1f, Animation.PlayMode.NORMAL))
        , LINK_POWERATTACK(Path.LINK, new AnimData(.2f, Animation.PlayMode.NORMAL))
        // ----------------------------------------------------------
        , MARIO_IDLE(Path.MARIO)
        , MARIO_WALK(Path.MARIO)
        , MARIO_JUMP(Path.MARIO, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , MARIO_FALL(Path.MARIO)
        , MARIO_HURT(Path.MARIO, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , MARIO_ATTACK(Path.MARIO)
        , MARIO_POWERATTACK(Path.MARIO)
        // ----------------------------------------------------------
        , MEGAMAN_IDLE(Path.MEGAMAN, new AnimData(0.25f, Animation.PlayMode.LOOP_RANDOM))
        , MEGAMAN_WALK(Path.MEGAMAN)
        , MEGAMAN_JUMP(Path.MEGAMAN, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , MEGAMAN_FALL(Path.MEGAMAN)
        , MEGAMAN_HURT(Path.MEGAMAN, new AnimData(0.1f, Animation.PlayMode.NORMAL))
        , MEGAMAN_ATTACK(Path.MEGAMAN, new AnimData(.2f, Animation.PlayMode.NORMAL))
        , MEGAMAN_POWERATTACK(Path.MEGAMAN)
        // enemies ----------------------------------------------------
        , GOOMBA_WALK(Path.GOOMBA)
        , GOOMBA_DEATH(Path.GOOMBA)
        , KOOPA_WALK(Path.KOOPA)
        , KOOPA_REVIVE(Path.KOOPA, new AnimData(0.4f, Animation.PlayMode.LOOP))
        , KOOPA_SHELL_SPIN(Path.KOOPA)
        , ANGRY_SUN(Path.ANGRY_SUN)
        , BULLET_BILL(Path.BULLET_BILL)
        , SKELETON_MOVE(Path.SKELETON, new AnimData(0.33f, Animation.PlayMode.LOOP))
        , SKELETON_ATTACK(Path.SKELETON, new AnimData(0.4f, Animation.PlayMode.NORMAL))
        , SKELETON_BONE(Path.SKELETON, new AnimData(0.1f, Animation.PlayMode.LOOP))
        , BAT(Path.CASTLE_BAT)
        , EAGLE(Path.EAGLE)
        , MONKEY_WALK(Path.MONKEY)
        , MONKEY_JUMP(Path.MONKEY)
        , MEDUSA(Path.MEDUSA)
        , BAT_CLOSED(Path.MEGA_BAT_CLOSED)
        , BAT_FLYING(Path.MEGA_BAT_FLYING)
        , HELMET(Path.HELMET)
        , HOTHEAD_IDLE(Path.HOTHEAD_IDLE)
        , HOTHEAD_THROWING(Path.HOTHEAD_THROWING)
        // boss -------------------------------------------------------
        , BOSS_BODY(Path.BOSS, new AnimData(0.15f, Animation.PlayMode.LOOP))
        , BOSS_HEAD_BOWSER(Path.BOSS, new AnimData(0.15f, Animation.PlayMode.LOOP))
        , BOSS_NECK_HEAD_BOWSER(Path.BOSS, new AnimData(0.15f, Animation.PlayMode.LOOP))
        , BOSS_HEAD_DRACULA(Path.BOSS, new AnimData(0.15f, Animation.PlayMode.LOOP))
        , BOSS_NECK_HEAD_DRACULA(Path.BOSS, new AnimData(0.15f, Animation.PlayMode.LOOP))
        , BOSS_HEAD_WILY(Path.BOSS, new AnimData(0.15f, Animation.PlayMode.LOOP))
        , BOSS_NECK_HEAD_WILY(Path.BOSS, new AnimData(0.15f, Animation.PlayMode.LOOP))
        , BOSS_HEAD_GANON(Path.BOSS, new AnimData(0.15f, Animation.PlayMode.LOOP))
        , BOSS_NECK_HEAD_GANON(Path.BOSS, new AnimData(0.15f, Animation.PlayMode.LOOP))
        // projectiles ------------------------------------------------
        , MEGAMAN_SHOT(Path.MEGAMAN)
        , MEGAMAN_POWERSHOT(Path.MEGAMAN)
        , MARIO_FIREBALL(Path.MARIO)
        , BELMONT_AXE(Path.BELMONT)
        , LINK_SWORD(Path.LINK)
        // items ------------------------------------------------------
        , ENERGY_CAPSULE(Path.ITEMS)
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
