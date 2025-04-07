package lando.systems.ld57.world;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.screens.BaseScreen;

public class BossFactory {
    public static Entity createBoss(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();
        new Position(entity, x, y);
        new Health(entity, 3f);

        addBossPart(entity, Anims.Type.BOSS_BODY);
        addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_BOWSER);
        addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_WILY);
        addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_GANON);
        addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_DRACULA);

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    private static void addBossPart(Entity entity, Anims.Type type) {
        var part = entity.scene.createEntity();
        var pos = entity.get(Position.class);
        new Position(part, pos.x(), pos.y());

        var animator = new Animator(part, type);
        animator.defaultScale.scl(0.3f);


    }
}
