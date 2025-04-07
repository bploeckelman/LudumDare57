package lando.systems.ld57.scene.scenes.components;

import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.framework.Entity;


public class MiniBossBehavior extends EnemyBehavior {
    public enum State {NORMAL, ATTACK, HURT}

    protected Characters.Data charData;
    private State enemyState;

    public MiniBossBehavior(Entity entity, Characters.Data charData) {
        super(entity);
        enemyState = State.NORMAL;
        this.charData = charData;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        var animator = entity.get(Animator.class);
        var mover = entity.get(Mover.class);
        var pos = entity.get(Position.class);
        if (mover == null || animator == null) return;

        if (enemyState == State.ATTACK && animator.stateTime >= animator.animation.getAnimationDuration()) {
            enemyState = State.NORMAL;
        }
        if (enemyState == State.HURT && animator.stateTime >= animator.animation.getAnimationDuration()) {
            enemyState = State.NORMAL;
        }

        switch(enemyState) {
            case NORMAL:
            if (mover.onGround()) {
                var animType = Characters.AnimType.IDLE;
                if (Math.abs(mover.velocity.x) > 20) {
                    animType = Characters.AnimType.WALK;
                }
                animator.play(charData.animByType.get(animType));
            } else {
                if (mover.velocity.y > 0) {
                    var anim = charData.animByType.get(Characters.AnimType.JUMP);
                    animator.play(anim);
                } else if (mover.velocity.y < 0) {
                    var anim = charData.animByType.get(Characters.AnimType.FALL);
                    animator.play(anim);
                }
            }
            break;
            case ATTACK:
            break;
            case HURT:
            animator.play(charData.animByType.get(Characters.AnimType.HURT));

            break;
        }
    }

    protected void beginAttack() {
        var animator = entity.get(Animator.class);
        enemyState = State.ATTACK;
        animator.stateTime = 0;
        animator.play(charData.animByType.get(Characters.AnimType.ATTACK));
    }

    protected void beginPowerAttack() {
        enemyState = State.ATTACK;
        var animator = entity.get(Animator.class);
        animator.stateTime = 0;
        animator.play(charData.animByType.get(Characters.AnimType.POWERATTACK));
    }
}
