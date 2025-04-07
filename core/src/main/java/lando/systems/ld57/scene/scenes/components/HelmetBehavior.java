package lando.systems.ld57.scene.scenes.components;

import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.Timer;
import lando.systems.ld57.scene.framework.Entity;

public class HelmetBehavior extends EnemyBehavior {
    enum State {
        IDLE,
        WALK,
    }
    private State state = State.WALK;
    private final float duration = 2f;
    private Timer timer;
    public HelmetBehavior(Entity entity) {
        super(entity);
        timer = new Timer(entity, duration);
        var animator = entity.get(Animator.class);
        var mover = entity.get(Mover.class);
        timer.onEnd = () -> {
            switch(state) {
                case IDLE:
                    state = State.WALK;
                    animator.animation = Anims.Type.HELMET_WALK.get();
                    mover.velocity.x = 30f * animator.facing;
                    break;
                case WALK:
                    state = State.IDLE;
                    animator.animation = Anims.Type.HELMET_IDLE.get();
                    mover.velocity.x = 0f;
                    break;
            }
            timer.start(duration);
        };
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        var mover = entity.get(Mover.class);
        var collider = entity.get(Collider.class);
        if (state == State.WALK) {
            turnAroundAtEdge(mover, collider);
        }
    }
}
