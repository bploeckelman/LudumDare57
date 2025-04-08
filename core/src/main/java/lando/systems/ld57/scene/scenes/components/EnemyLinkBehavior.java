package lando.systems.ld57.scene.scenes.components;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.PlayerBehavior;

public class EnemyLinkBehavior extends MiniBossBehavior {

    public static final float MOVEMENT_SPEED = 40f;

    private float lastAttack;
    private float lastThrow;
    private float lastVelocity;

    public EnemyLinkBehavior(Entity entity) {
        super(entity, Characters.Type.LINK.get());
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        lastAttack -= dt;
        lastThrow -= dt;
        lastVelocity -= dt;

        var anim = entity.get(Animator.class);
        var bossMover = entity.get(Mover.class);
        var bossPos = entity.get(Position.class);


        var playerPos = entity.scene.player.get(Position.class);

        if (anim == null || bossMover == null || bossPos == null || playerPos == null) {
            return;
        }

        if (lastVelocity <= 0) {
            lastVelocity = MathUtils.random(.5f, 2f);
            if (MathUtils.randomBoolean(.99f)) {
                if (playerPos.x() < bossPos.x()){
                    bossMover.velocity.x = -MOVEMENT_SPEED;
                } else {
                    bossMover.velocity.x = MOVEMENT_SPEED;
                }
            } else {
                if (playerPos.x() < bossPos.x()){
                    bossMover.velocity.x = MOVEMENT_SPEED;
                } else {
                    bossMover.velocity.x = -MOVEMENT_SPEED;
                }
            }
        }

        if (lastAttack <= 0 && enemyState == State.NORMAL) {
            linkAttack();
            lastAttack = MathUtils.random(1f, 2f);
        }

        if (lastThrow <= 0 && enemyState == State.NORMAL)
        {
            linkPowerAttack();
            lastThrow = MathUtils.random(2f, 4f);
        }

    }


    public Entity linkAttack() {
        beginAttack();
        var playerPos = entity.get(Position.class);
        var playerAnim = entity.get(Animator.class);

        var scene = entity.scene;
        var attackEntity = scene.createEntity();

        var position = new Position(attackEntity,
            playerPos.x() + 5 * playerAnim.facing,
            playerPos.y() + 20);

        // lookup a attack animation / collider data for character
        var charData = Characters.Type.LINK.get();
        var attackAnim = charData.animByType.get(Characters.AnimType.ATTACK).get();
        var attackDuration = attackAnim.getAnimationDuration();
        var attackColliderRects = charData.attackColliderRects;
        if (attackColliderRects.isEmpty()) {
            entity.scene.destroy(attackEntity);
            return null;
        }

        // create the attack collider, updating its rect on each attack animation frame
        var colliderRect = attackColliderRects.get(0);
        var collider = Collider.makeRect(attackEntity, Collider.Mask.enemy_projectile, colliderRect);

        var meleeDamage = new MeleeDamage(attackEntity);
        meleeDamage.setOnHit((params -> {
            var collidedEntity = params.hitCollider.entity;
            var enemyBehavior = collidedEntity.get(PlayerBehavior.class);
            if (enemyBehavior != null) {
                var damageAmount = charData.attackInfo.attackDamage;
                enemyBehavior.hurt(damageAmount);
            }
        }));
        new Timer(attackEntity, attackDuration,
            () -> {
                // 'follow' the player's position in case they're moving
                position.set(
                    playerPos.x() + 5 * playerAnim.facing,
                    playerPos.y() + 20);

                // update the collider rect based on the animation frame
                // so that the attack collider matches the location of
                // the weapon in the attack animation for each frame
                var frameIndex = attackAnim.getKeyFrameIndex(playerAnim.stateTime);
                if (frameIndex < attackColliderRects.size()) {
                    var currentRect = attackColliderRects.get(frameIndex);
                    var rectShape = collider.shape(Collider.RectShape.class);
                    if (rectShape != null) {
                        // mirror the attack collider rect for this frame
                        // if the player is facing to the left instead of right
                        // since the rect values were written for right facing
                        currentRectFacing.set(currentRect);
                        if (playerAnim.facing == -1) {
                            currentRectFacing.x = -(currentRectFacing.x + currentRectFacing.width);
                        }
                        rectShape.rect.set(currentRectFacing);
                    }
                }
            },
            () -> entity.scene.destroy(attackEntity)
        );

        DebugRender.makeForShapes(attackEntity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return attackEntity;
    }

    public Entity linkPowerAttack() {
        beginPowerAttack();
        float size = 12f;
        var scene = entity.scene;
        var charAnimator = entity.get(Animator.class);
        var charPos = entity.get(Position.class);

        var powerAttackEntity = scene.createEntity();
        new Position(powerAttackEntity, charPos.x() + 14 * charAnimator.facing, charPos.y() + 20);
        var collider = Collider.makeRect(powerAttackEntity, Collider.Mask.enemy_projectile, -size/2f, -size/2f, size, size);
        var mover = new Mover(powerAttackEntity, collider);
        mover.velocity.x = 100 * charAnimator.facing;
        mover.addCollidesWith(Collider.Mask.player);
        mover.setOnHit((params -> {
                var collidedEntity = params.hitCollider.entity;
                var enemyBehavior = collidedEntity.get(PlayerBehavior.class);
                if (enemyBehavior != null) {
                    var damageAmount = charData.attackInfo.powerAttackDamage;
                    enemyBehavior.hurt(damageAmount);
                }
                destroyBulletParticle(powerAttackEntity);
                powerAttackEntity.selfDestruct();
            })
        );

        var animator = new Animator(powerAttackEntity, Anims.Type.LINK_SWORD);
        animator.size.set(size, size);
        animator.origin.set(size/2f, size/2f);
        animator.outlineColor.a = 0;

        new Timer(powerAttackEntity, 5, () -> {
            destroyBulletParticle(powerAttackEntity);
            powerAttackEntity.destroy(Timer.class);
            powerAttackEntity.selfDestruct();
            // TODO particle effect
        });

        return powerAttackEntity;
    }

}
