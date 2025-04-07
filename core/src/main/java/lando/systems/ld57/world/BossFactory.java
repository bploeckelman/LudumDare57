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
        new Health(entity, 3000f);

        addBossPart(entity, Anims.Type.BOSS_BODY, 15, 95, 30);
        addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_BOWSER, 70, 127, 10);
        addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_WILY, 45, 150, 10);
        addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_GANON, 10, 160, 10);
        addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_DRACULA, -25, 140, 10);



        return entity;
    }

    private static void addBossPart(Entity entity, Anims.Type type, float x, float y, float radius) {
        var part = entity.scene.createEntity();
        var pos = entity.get(Position.class);
        new Position(part, pos.x(), pos.y());

        var scale = 0.2f;

        var animator = new Animator(part, type);
        animator.origin.set(45, 1);
        animator.defaultScale.scl(scale);

        var collider = Collider.makeCirc(part, Collider.Mask.enemy, x * scale, y * scale, radius * scale);
        DebugRender.makeForShapes(part, DebugRender.DRAW_POSITION_AND_COLLIDER);


//        var mover = new Mover(part, collider);
//        mover.velocity.setToRandomDirection().scl(2);
//        mover.addCollidesWith(Collider.Mask.solid);
//        mover.setOnHit((params) -> {
//            // invert speed on the hit axis and add some squash/stretch
//            switch (params.direction) {
//                case LEFT:
//                case RIGHT: {
//                    mover.invertX();
//                    //image.scale.set(0.66f, 1.33f);
//                } break;
//                case UP:
//                case DOWN: {
//                    mover.invertY();
//                    //image.scale.set(1.33f, 0.66f);
//                } break;
//            }
//        });
    }
}
