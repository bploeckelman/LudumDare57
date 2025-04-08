package lando.systems.ld57.scene.scenes.components;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld57.particles.effects.BulletExplosionEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.particles.effects.ShapeEffect;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.Health;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.ParticleEmitter;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.framework.ComponentFamily;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Time;
import lando.systems.ld57.utils.Util;

public abstract class EnemyBehavior extends ComponentFamily {

    private static float INVINCIBILITY_TIME = 0.5f;
    static Vector2 tempVec1 = new Vector2();
    static Vector2 tempVec2 = new Vector2();

    private float accum;

    public float invincibilityTime = 0f;

    public EnemyBehavior(Entity entity) {
        super(entity);
        new ParticleEmitter(entity);
    }

    public void attack() {

    }

    public void die() {
        Util.log(entity.toString(), "is dead");
        var emitter = entity.get(ParticleEmitter.class);
        var pos = entity.get(Position.class);
        if (emitter != null) {
            entity.scene.screen.particleManager.spawn(ParticleEffect.Type.SHAPE, new ShapeEffect.Params(pos.x(), pos.y(), Util.randomColor()));
        }
        entity.active = false;
        entity.scene.world.destroy(entity);
    }

    public void hurt(float damageAmount) {
        if (invincibilityTime > 0) return;
        invincibilityTime = INVINCIBILITY_TIME;

        var health = entity.get(Health.class);
        if (health != null) {
            health.takeDamage(damageAmount);
        }

        Time.pause_for(0.1f);

        // TODO(brian): knockback
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        accum += delta;
        var health = entity.get(Health.class);

        if (health != null) {
            if (health.gethealth() <= 0) {
                die();
                return;
            }
        }


        invincibilityTime -= delta;
        invincibilityTime = Math.max(0, invincibilityTime);


        var anim = entity.get(Animator.class);
        if (health != null && anim != null) {
            if (invincibilityTime > 0) {
                anim.fillColor.set(1f, 1f, 1f, .8f * MathUtils.sin(accum * 30));
            } else {
                anim.fillColor.set(1f, 1f, 1f, .0f);
            }
        }
    }

    /**
     * Turns the entity around if it is near an edge.
     */
    public static void turnAroundAtEdge(Mover mover, Collider collider) {
        if (mover == null || collider == null) {
            return;
        }

        var rectShape = collider.shape(Collider.RectShape.class);
        var size = (rectShape != null) ? rectShape.rect.width : 0;
        var xDir = mover.velocity.x > 0 ? 1 : -1;
        var xOffset = (int) (xDir * size * (3f / 4f));

        var isSafe = collider.check(Collider.Mask.solid, xOffset, -1);
        if (!isSafe) {
            mover.invertX();
        }
    }

    public static boolean jumpTowardPlayer(Mover mover, Animator animator) {
        if (mover == null) {
            return false;
        }
        var player = mover.entity.scene.player;
        if (player != null) {
            var thisPos = mover.entity.get(Position.class);
            var playerPos = player.get(Position.class);
            tempVec1.set(thisPos.x(), thisPos.y());
            tempVec2.set(playerPos.x(), playerPos.y());
            var distance = tempVec1.dst2(tempVec2);
            if (distance < 1000f) {
                mover.velocity.y = 130f;
                mover.velocity.x = animator.facing * 150f;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void facePlayer(Animator animator) {
        if (animator == null) {
            return;
        }

        var player = animator.entity.scene.player;
        if (player != null) {
            var thisPos = animator.entity.get(Position.class);
            var playerPos = player.get(Position.class);
            var dx = playerPos.x() - thisPos.x();
            animator.facing = (dx > 0) ? 1 : -1;
            animator.autoFacing = false;
        }
    }


    protected void destroyBulletParticle(Entity bulletEntity) {
        var particleEmitter = entity.get(ParticleEmitter.class);
        var bulletPos = bulletEntity.get(Position.class);
        particleEmitter.spawnParticle(
            ParticleEffect.Type.BULLET_EXPLOSION,
            new BulletExplosionEffect.Params(bulletPos.x(), bulletPos.y(), bulletEntity.get(Animator.class).keyframe)
        );
    }
}
