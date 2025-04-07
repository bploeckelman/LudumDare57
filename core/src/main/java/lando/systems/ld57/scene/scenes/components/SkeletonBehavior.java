package lando.systems.ld57.scene.scenes.components;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.particles.effects.BloodSplatEffect;
import lando.systems.ld57.particles.effects.DirtEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.DebugRender;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.ParticleEmitter;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.PlayerBehavior;

public class SkeletonBehavior extends EnemyBehavior {

    private static final float ATTACK_COOLDOWN = 2f;
    private static final float ATTACK_DURATION = 0.8f;

    private float timer = ATTACK_COOLDOWN;
    private boolean attacking = false;

    private final Vector2 prevVelocity = new Vector2();
    private int prevFacing = 1;

    public SkeletonBehavior(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        var mover = entity.get(Mover.class);
        var collider = entity.get(Collider.class);
        var animator = entity.get(Animator.class);

        facePlayer(animator);
        turnAroundAtEdge(mover, collider);

        timer -= dt;
        if (timer <= 0) {
            if (attacking) {
                attacking = false;
                timer = ATTACK_COOLDOWN;
                animator.play(Anims.Type.SKELETON_MOVE);
                animator.facing = prevFacing;
                mover.velocity.set(prevVelocity);
            } else {
                timer = ATTACK_DURATION;
                attack();
                attacking = true;
            }
        }
    }

    @Override
    public void attack() {
        var animator = entity.get(Animator.class);
        animator.play(Anims.Type.SKELETON_ATTACK);
        prevFacing = animator.facing;

        var mover = entity.get(Mover.class);
        prevVelocity.set(mover.velocity);
        mover.stopX();

        var position = entity.get(Position.class);
        var emitter = entity.get(ParticleEmitter.class);
        emitter.spawnParticle(ParticleEffect.Type.DIRT,
            new DirtEffect.Params(position.x(), position.y() + animator.size.y / 2f));

        throwBone();
    }

    private void throwBone() {
        var position = entity.get(Position.class);
        var animator = entity.get(Animator.class);
        var emitter = entity.get(ParticleEmitter.class);
        var size = 18f;

        final var bone = entity.scene.createEntity();
        new Position(bone, position.x(), position.y() + animator.size.y / 2f);
        new ParticleEmitter(bone);

        var boneAnim = new Animator(bone, Anims.Type.SKELETON_BONE);
        boneAnim.origin.set(size / 2f, size / 2f);

        var collider = Collider.makeRect(bone, Collider.Mask.player_projectile, -5, -5, 10, 10);

        var mover = new Mover(bone, collider);
        mover.velocity.set(100f * animator.facing, 300f);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.addCollidesWith(Collider.Mask.player);
        mover.setOnHit(params -> {
            var hitMask = params.hitCollider.mask;
            if (hitMask == Collider.Mask.solid) {
                var bonePos = bone.get(Position.class);
                emitter.spawnParticle(ParticleEffect.Type.BLOOD_SPLAT,
                    new BloodSplatEffect.Params(bonePos.x(), bonePos.y()));
                bone.selfDestruct();
            } else if (hitMask == Collider.Mask.player) {
                var player = params.hitCollider.entity;

                var playerBehavior = player.get(PlayerBehavior.class);
                if (playerBehavior != null) {
                    playerBehavior.hurt(1f);
                }

                var bonePos = bone.get(Position.class);
                emitter.spawnParticle(ParticleEffect.Type.BLOOD_SPLAT,
                    new BloodSplatEffect.Params(bonePos.x(), bonePos.y()));

                bone.selfDestruct();
            }
        });

        DebugRender.makeForShapes(bone, DebugRender.DRAW_POSITION_AND_COLLIDER);
    }
}
