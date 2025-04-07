package lando.systems.ld57.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld57.assets.framework.AssetContainer;
import lando.systems.ld57.assets.framework.AssetEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Characters extends AssetContainer<Characters.Type, Characters.Data> {

    public static AssetContainer<Type, Data> container;

    public enum AnimType { ATTACK, FALL, HURT, IDLE, JUMP, WALK, POWERATTACK }

    public enum Type implements AssetEnum<Data> {
          OLDMAN  (new Vector2(16, 1),  new Rectangle(-5, 0, 10, 28), Color.WHITE)
        , BELMONT (new Vector2(25, 3), new Rectangle(-5, 0, 10, 28), Color.ORANGE)
        , LINK    (new Vector2(16, 1),  new Rectangle(-5, 0, 10, 32), Color.GREEN)
        , MARIO   (new Vector2(16, 1),  new Rectangle(-5, 0, 10, 28), Color.RED)
        , MEGAMAN (new Vector2(16, 1),  new Rectangle(-5, 0, 10, 16), Color.BLUE)
        ;

        private final Data data;

        Type(Vector2 origin, Rectangle colliderOffset, Color primaryColor) {
            this.data = new Data(origin, colliderOffset, primaryColor);
        }

        @Override
        public Data get() {
            return data;
        }
    }

    public static class AttackInfo {
        public float attackCooldown;
        public float powerAttackCooldown;
        public float attackDamage;
        public float powerAttackDamage;

        public AttackInfo() {}
    }


    public static class Data {
        public final Vector2 origin;
        public final Rectangle colliderOffset;
        public final Color primaryColor;

        public Map<AnimType, Anims.Type> animByType = new HashMap<>();
        public List<Rectangle> attackColliderRects;
        public AttackInfo attackInfo;
        public Vector2 size;

        public Data(
            Vector2 origin,
            Rectangle colliderOffset,
            Color primaryColor
        ) {
            this.origin = origin;
            this.colliderOffset = colliderOffset;
            this.attackColliderRects = List.of();
            this.primaryColor = primaryColor;
            this.attackInfo = new AttackInfo();
            this.size = new Vector2();
        }
    }

    public Characters() {
        super(Characters.class, Data.class);
        Characters.container = this;
    }

    @Override
    public void init(Assets assets) {
        for (var type : Type.values()) {
            var name = type.name();
            var data = type.get();
            data.animByType.put(AnimType.ATTACK, Anims.Type.valueOf(name + "_ATTACK"));
            data.animByType.put(AnimType.FALL,   Anims.Type.valueOf(name + "_FALL"));
            data.animByType.put(AnimType.HURT,   Anims.Type.valueOf(name + "_HURT"));
            data.animByType.put(AnimType.IDLE,   Anims.Type.valueOf(name + "_IDLE"));
            data.animByType.put(AnimType.JUMP,   Anims.Type.valueOf(name + "_JUMP"));
            data.animByType.put(AnimType.WALK,   Anims.Type.valueOf(name + "_WALK"));
            data.animByType.put(AnimType.POWERATTACK, Anims.Type.valueOf(name + "_POWERATTACK"));

            switch(type) {
                case OLDMAN:
                    data.attackInfo.attackCooldown = .5f;
                    data.attackInfo.powerAttackCooldown = .7f;
                    data.attackInfo.attackDamage = 1f;
                    data.attackInfo.powerAttackDamage = 2f;
                    data.size.set(34, 34);
                    break;
                case BELMONT:
                    data.attackInfo.attackCooldown = .6f;
                    data.attackInfo.powerAttackCooldown = .25f;
                    data.attackInfo.attackDamage = 1f;
                    data.attackInfo.powerAttackDamage = 2f;
                    data.size.set(78, 34);
                    data.attackColliderRects = List.of(
                          new Rectangle(-28, -23, 8, 29)
                        , new Rectangle(-28, -15, 15, 20)
                        , new Rectangle(0, -8, 48, 8)
                    );
                    break;
                case LINK:
                    data.attackInfo.attackCooldown = .5f;
                    data.attackInfo.powerAttackCooldown = .1f;
                    data.attackInfo.attackDamage = 1f;
                    data.attackInfo.powerAttackDamage = 2f;
                    data.size.set(34, 34);
                    data.attackColliderRects = List.of(
                        new Rectangle(-18, 0, 8, 15)
                        , new Rectangle(0, -8, 15, 18)
                    );
                    break;
                case MARIO:
                    data.attackInfo.attackCooldown = .1f;
                    data.attackInfo.powerAttackCooldown = .25f;
                    data.attackInfo.attackDamage = 1f;
                    data.attackInfo.powerAttackDamage = 2f;
                    data.size.set(34, 34);
                    break;
                case MEGAMAN:
                    data.attackInfo.attackCooldown = .15f;
                    data.attackInfo.powerAttackCooldown = .3f;
                    data.attackInfo.attackDamage = .2f;
                    data.attackInfo.powerAttackDamage = 2f;
                    data.size.set(34, 34);
                    break;
            }
        }


    }
}
