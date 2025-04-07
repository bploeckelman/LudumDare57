package lando.systems.ld57.scene.scenes.components;

import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.Timer;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Util;

public class MonkeyBehavior extends EnemyBehavior {
    enum STATE {
        WALK,
        JUMP,
        RUNAWAY,
    }
    private STATE state = STATE.WALK;

    public MonkeyBehavior(Entity entity) {
        super(entity);

    }

    @Override
    public void update(float dt) {
        Util.log("Monkey State:", state.toString());
        var mover = entity.get(Mover.class);
        var collider = entity.get(Collider.class);
        var animator = entity.get(Animator.class);
        if (state == STATE.WALK) {
            mover.velocity.x = 30f;
            turnAroundAtEdge(mover, collider);
            state = jumpTowardPlayer(mover, animator) ? STATE.JUMP : STATE.WALK;
            if (state == STATE.JUMP) {
                Util.log("jumping");
            }
        }
        else if (state == STATE.JUMP && mover.onGround()) {
            Util.log("Just landed");
            state = STATE.RUNAWAY;
            mover.velocity.x = -60f * animator.facing;
            var timer = new Timer(entity, 2f, () -> {
                state = STATE.WALK;
                entity.destroy(Timer.class);
            });
        }
    }
}
