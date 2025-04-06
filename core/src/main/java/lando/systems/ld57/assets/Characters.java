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

    public enum AnimType { ATTACK, FALL, HURT, IDLE, JUMP, WALK }

    public enum Type implements AssetEnum<Data> {
          OLDMAN  (new Vector2(16, 0), new Rectangle(-5, 0, 10, 28), List.of(), Color.WHITE)
        , BELMONT (new Vector2(16, 0), new Rectangle(-5, 0, 10, 28), List.of(), Color.ORANGE)
        , LINK    (new Vector2(16, 0), new Rectangle(-5, 0, 10, 32), List.of(), Color.GREEN)
        , MARIO   (new Vector2(16, 0), new Rectangle(-5, 0, 10, 28), List.of(), Color.RED)
        , MEGAMAN (new Vector2(16, 0), new Rectangle(-5, 0, 10, 20), List.of(), Color.BLUE)
        ;

        private final Data data;

        Type(Vector2 origin, Rectangle colliderOffset, List<Rectangle> attackColliderOffsets, Color primaryColor) {
            this.data = new Data(origin, colliderOffset, attackColliderOffsets, primaryColor);
        }

        @Override
        public Data get() {
            return data;
        }
    }

    public static class Data {
        public final Vector2 origin;
        public final Rectangle colliderOffset;
        public final List<Rectangle> attackColliderOffsets;
        public final Color primaryColor;

        public Map<AnimType, Anims.Type> animByType = new HashMap<>();

        public Data(
            Vector2 origin,
            Rectangle colliderOffset,
            List<Rectangle> attackColliderOffsets,
            Color primaryColor
        ) {
            this.origin = origin;
            this.colliderOffset = colliderOffset;
            this.attackColliderOffsets = attackColliderOffsets;
            this.primaryColor = primaryColor;
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
        }
    }
}
