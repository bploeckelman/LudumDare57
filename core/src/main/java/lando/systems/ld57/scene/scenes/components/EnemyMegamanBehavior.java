package lando.systems.ld57.scene.scenes.components;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.PlayerBehavior;

public class EnemyMegamanBehavior extends MiniBossBehavior {
    private int shotsToShoot = 0;
    private float shotTimer;
    private float powerShotTime;
    private float lastVelocity;


    public EnemyMegamanBehavior(Entity entity) {
        super(entity, Characters.Type.MEGAMAN.get());
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        shotTimer -= dt;
        powerShotTime -= dt;
        lastVelocity -= dt;

        var anim = entity.get(Animator.class);
        var bossMover = entity.get(Mover.class);
        var bossPos = entity.get(Position.class);


        var playerPos = entity.scene.player.get(Position.class);

        if (anim == null || bossMover == null || bossPos == null || playerPos == null) {
            return;
        }

        if (shotsToShoot > 0 && enemyState == State.NORMAL) {
            megamanAttack();
            shotsToShoot--;
            facePlayer();
            bossMover.velocity.x = 0;

        }

        if (powerShotTime < 0 && enemyState == State.NORMAL) {
            megamanPowerAttack();
            powerShotTime = MathUtils.random(4f, 6);
        }

        if (shotTimer <= 0 ) {
            shotsToShoot = 6;
            shotTimer = MathUtils.random(4f, 7f);
        }

        if (lastVelocity <= 0) {
            lastVelocity = MathUtils.random(.5f, 2f);
            if (MathUtils.randomBoolean(.9f)) {
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

    }

    public Entity megamanAttack() {
        beginAttack();
        float size = 8f;
        var scene = entity.scene;
        var charAnimator = entity.get(Animator.class);
        var charPos = entity.get(Position.class);

        var attackEntity = scene.createEntity();
        new Position(attackEntity, charPos.x() + 15 * charAnimator.facing, charPos.y() + 13);
        var collider = Collider.makeRect(attackEntity, Collider.Mask.enemy_projectile, -size/2f, -size/2f, size, size);
        var mover = new Mover(attackEntity, collider);
        mover.velocity.x = 100 * charAnimator.facing;
        mover.addCollidesWith(Collider.Mask.player);
        mover.setOnHit(params -> {
            var collidedEntity = params.hitCollider.entity;
            var enemyBehavior = collidedEntity.get(PlayerBehavior.class);
            if (enemyBehavior != null) {
                var damageAmount = charData.attackInfo.attackDamage;
                enemyBehavior.hurt(damageAmount);
            }
            destroyBulletParticle(attackEntity);
            attackEntity.selfDestruct();
        });

        var animator = new Animator(attackEntity, Anims.Type.MEGAMAN_SHOT);
        animator.size.set(size, size);
        animator.origin.set(size/2f, size/2f);
        animator.outlineColor.a = 0;

        new Timer(attackEntity, 4, () -> {
            destroyBulletParticle(attackEntity);
            attackEntity.destroy(Timer.class);
            attackEntity.selfDestruct();
            // TODO particle effect
        });

        return attackEntity;
    }

    public Entity megamanPowerAttack() {
        float size = 12f;
        beginPowerAttack();
        var scene = entity.scene;
        var charAnimator = entity.get(Animator.class);
        var charPos = entity.get(Position.class);

        var powerAttackEntity = scene.createEntity();
        new Position(powerAttackEntity, charPos.x() + 15 * charAnimator.facing, charPos.y() + 13);
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

        var animator = new Animator(powerAttackEntity, Anims.Type.MEGAMAN_POWERSHOT);
        animator.size.set(size, size);
        animator.origin.set(size/2f, size/2f);
        animator.outlineColor.a = 0;

        new Timer(powerAttackEntity, 1, () -> {
            destroyBulletParticle(powerAttackEntity);
            powerAttackEntity.destroy(Timer.class);
            powerAttackEntity.selfDestruct();
            // TODO particle effect
        });

        return powerAttackEntity;
    }

}
