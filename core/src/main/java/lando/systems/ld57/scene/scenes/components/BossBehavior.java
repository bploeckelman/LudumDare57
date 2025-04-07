package lando.systems.ld57.scene.scenes.components;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.particles.effects.BloodSplatEffect;
import lando.systems.ld57.particles.effects.DirtEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.PlayerBehavior;
import lando.systems.ld57.utils.Util;

public class BossBehavior extends EnemyBehavior {

    private static final float ATTACK_COOLDOWN = 2f;
    private static final float ATTACK_DURATION = 0.8f;

    private float timer = ATTACK_COOLDOWN;
    private boolean attacking = false;


    Entity body;
    Entity bowser;
    Entity gannon;
    Entity wily;
    Entity dracula;

    private final Array<Entity> parts = new Array<>();

    private int facing = 1;
    private boolean movingRight = false;
    private float speed = 20f;
    private float totalMovement = 0f;
    private final float moveRadius = 80f;
    private final Vector2 startPos;
    private final float widthOffset;
    private int headCount;

    public BossBehavior(Entity entity, Entity body, Entity bowser, Entity gannon, Entity wily, Entity dracula) {
        super(entity);

        var bossPos = entity.get(Position.class);
        startPos = new Vector2(bossPos.x(), bossPos.y());

        this.body = body;
        this.bowser = bowser;
        this.gannon = gannon;
        this.wily = wily;
        this.dracula = dracula;

        parts.add(body);
        parts.add(bowser);
        parts.add(gannon);
        parts.add(wily);
        parts.add(dracula);

        this.headCount = this.parts.size - 1;

        this.widthOffset = this.body.get(Animator.class).keyframe.getRegionWidth() / 4f;

//        new Timer(entity, 3000, ()-> {
//
//        });
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        for (int i = parts.size - 1; i > 0; i--) {
            var part = parts.get(i);
            var health = part.get(Health.class);
            if (health == null || health.health <= 0) {
                parts.removeIndex(i);
                part.selfDestruct();
            }
        }
        facePlayer();
        move(dt);

        speed = (5 - this.headCount) * 20;
    }

    private void facePlayer() {
        var player = entity.scene.player;
        if (player != null) {
            var thisPos = entity.get(Position.class);
            var playerPos = player.get(Position.class);
            // left
            var oldFacing = facing;
            if (facing < 0) {
                facing = (playerPos.x() > thisPos.x() + widthOffset) ? 1 : -1;
            } else {
                facing = (playerPos.x() < thisPos.x() - widthOffset) ? -1 : 1;
            }

            if (facing != oldFacing) {
                for (var entity: parts) {
                    if (entity.get(Collider.class) == null) continue;
                    var collider = entity.get(Collider.class).shape(Collider.CircShape.class).circ;
                    collider.x = -collider.x;
                }
            }
        }
    }

    private void move(float dt) {
         totalMovement += dt;
         float dx = this.speed * dt;
         if (!movingRight) {
             dx *= -1;
         }

         var floatValue = MathUtils.sin(totalMovement * 3);

         var position = entity.get(Position.class);
         float x = position.x() + dx;
         if (x < startPos.x - moveRadius) {
             movingRight = true;
         } else if (x > startPos.x + moveRadius) {
             movingRight = false;
         }

         float y = position.y() + floatValue * 3;
         position.set(x, position.y());

         for (var entity : parts) {
             var animator = entity.get(Animator.class);
             if (animator == null) continue;
             entity.get(Animator.class).facing = facing;
             entity.get(Position.class).set(x, y);
         }
    }

    @Override
    public void attack() {
//        var animator = entity.get(Animator.class);
//        animator.play(Anims.Type.SKELETON_ATTACK);
//        prevFacing = animator.facing;
//
//        var mover = entity.get(Mover.class);
//        prevVelocity.set(mover.velocity);
//        mover.stopX();
//
//        var position = entity.get(Position.class);
//        var emitter = entity.get(ParticleEmitter.class);
//        emitter.spawnParticle(ParticleEffect.Type.DIRT,
//            new DirtEffect.Params(position.x(), position.y() + animator.size.y / 2f));
//
//        throwBone();
    }
}
