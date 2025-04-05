package lando.systems.ld57.scene.ldgame;

import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public class HeroBehavior extends Component {


    private final Animator animator;
    private final Mover mover;

    public HeroBehavior(Entity entity, Animator animator, Mover mover) {
        super(entity);
        this.animator = animator;
        this.mover = mover;
    }

    @Override
    public void update(float dt) {
        if (mover.velocity.x > 0) {
            animator.facing = 1;
        } else if (mover.velocity.x < 0) {
            animator.facing = -1;
        }
        if (mover.onGround()) {
            if (Math.abs(mover.velocity.x) > 20) {
                animator.play(Anims.Type.BELMONT_WALK);
            } else {
                animator.play(Anims.Type.BELMONT_IDLE);
            }
        } else {
            if (mover.velocity.y > 0) {
                animator.play(Anims.Type.BELMONT_JUMP);
            } else if (mover.velocity.y < 0) {
                animator.play(Anims.Type.BELMONT_FALL);
            }
        }
    }
}
