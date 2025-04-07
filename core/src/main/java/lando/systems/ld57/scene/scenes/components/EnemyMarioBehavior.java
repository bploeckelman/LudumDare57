package lando.systems.ld57.scene.scenes.components;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Direction;

public class EnemyMarioBehavior extends MiniBossBehavior {
    public static final float MOVEMENT_SPEED = 40f;


    float lastJumpTime;
    float lastFireballTime;
    float lastDirectionChangeTime;
    float accum;

    public EnemyMarioBehavior(Entity entity) {
        super(entity, Characters.Type.MARIO.get());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        var player = entity.scene.player;
        var playerPos = player.get(Position.class);
        var bossPos = entity.get(Position.class);
        var bossMover = entity.get(Mover.class);
        var bossAnimator = entity.get(Animator.class);

        var wait = entity.get(WaitToMove.class);
        if (wait != null) {
            // NO-OP until we start to move
            return;
        }

        if (bossAnimator == null || bossMover == null) {
            return;
        }

        lastDirectionChangeTime -= delta;
        lastJumpTime -= delta;
        lastFireballTime -= delta;
        accum += delta;

        bossAnimator.outlineColor.set(1f, 0, 0, MathUtils.sin(accum * 20f));


        if (lastDirectionChangeTime < 0) {
            lastDirectionChangeTime = MathUtils.random(1f, 2f);
            if (playerPos.x() < bossPos.x()){
                bossMover.velocity.x = -MOVEMENT_SPEED;
            } else {
                bossMover.velocity.x = MOVEMENT_SPEED;
            }
        }

        if (lastJumpTime < 0){
            if (Math.abs(playerPos.x() - bossPos.x()) < 30) {
                bossMover.velocity.y = 300;
                lastJumpTime = 4f;
            } else {
                lastJumpTime = MathUtils.random(1f, 3f);
            }
        }

        if (lastFireballTime <= 0) {
            beginPowerAttack();

            var size = 12f;
            lastFireballTime = MathUtils.random(3f, 6f);
            var fireball = entity.scene.createEntity();
            new FireParticle(fireball);
            new Position(fireball, bossPos.x() + 15 * bossAnimator.facing, bossPos.y() + 13);
            var collider = Collider.makeRect(fireball, Collider.Mask.enemy_projectile, -size/2f, -size/2f, size, size);
            var fireballMover = new Mover(fireball, collider);
            fireballMover.velocity.x = 100 * bossAnimator.facing;
            fireballMover.gravity = Mover.BASE_GRAVITY;
            fireballMover.addCollidesWith(Collider.Mask.player, Collider.Mask.solid);
            fireballMover.setOnHit((params -> {
                    if (params.hitCollider.mask == Collider.Mask.solid){
                        var move = fireball.get(Mover.class);
                        if (params.direction == Direction.Relative.DOWN) {
                            move.velocity.y = 100;
                        }

                    } else {
                        var collidedEntity = params.hitCollider.entity;
                        var health = collidedEntity.get(Health.class);
                        if (health != null) {
                            health.takeDamage(1f);
                        }
                        destroyBulletParticle(fireball);
                        fireball.scene.world.destroy(fireball);
                    }
                })
            );

            var animator = new Animator(fireball, Anims.Type.MARIO_FIREBALL);
            animator.size.set(size, size);
            animator.origin.set(size/2f, size/2f);
            animator.outlineColor.a = 0;


            var timer = new Timer(fireball, 1, () -> {
                destroyBulletParticle(fireball);
                fireball.destroy(Timer.class);
                fireball.scene.world.destroy(fireball);
                // TODO particle effect
            });

        }

    }


}
