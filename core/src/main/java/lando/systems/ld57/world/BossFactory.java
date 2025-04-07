package lando.systems.ld57.world;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.components.SkeletonBehavior;
import lando.systems.ld57.screens.BaseScreen;

public class BossFactory {
    public static Entity createBoss(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();
        new Position(entity, x, y);
        new ParticleEmitter(entity);
        new SkeletonBehavior(entity);
        new Health(entity, 3f);

        var animator = new Animator(entity, Anims.Type.SKELETON_MOVE);
        animator.origin.set(16, 1);

        var collider = Collider.makeRect(entity, Collider.Mask.enemy, -5, 0, 10, 28);

        var mover = new Mover(entity, collider);
        var randomDirection = MathUtils.randomBoolean() ? 1 : -1;
        mover.velocity.set(randomDirection * 10f, 0f);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.addCollidesWith(Collider.Mask.player);
        mover.setOnHit((params) -> {
            if (Collider.Mask.solid == params.hitCollider.mask) {
                if (params.direction.isHorizontal()) {
                    mover.invertX();
                }
            } else if (Collider.Mask.player == params.hitCollider.mask) {
                var stunDuration = 0.5f;
                var timer = entity.get(Timer.class);
                if (timer == null) {
                    var origVelX = mover.velocity.x;
                    var origVelY = mover.velocity.y;
                    mover.stopX();
                    // no active timer, create and attach one
                    new Timer(entity, stunDuration, () -> {
                        mover.velocity.set(origVelX, origVelY);
                        entity.destroy(Timer.class);
                    });
                } else {
                    // timer was still in progress, reset it
                    timer.start(stunDuration);
                }
            }
        });

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }
}
