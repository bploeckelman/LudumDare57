package lando.systems.ld57.scene.scenes;

import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.assets.Sounds;
import lando.systems.ld57.math.Calc;
import lando.systems.ld57.particles.effects.BulletExposionEffect;
import lando.systems.ld57.particles.effects.DirtEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.particles.effects.SparkEffect;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Util;

public class PlayerBehavior extends Component {

    public enum State {NORMAL, ATTACK}

    public static float COYOTE_TIME = 0.2f;
    public static float MAX_SPEED = 100f;
    public static float MAX_SPEED_AIR = 80f;
    public static float JUMP_SPEED = 300f;
    public static float MOVE_SPEED = 800f;

    private boolean wasOnGround = true;
    private float jumpCoolDown;
    private float attackCoolDown;
    private boolean wasGrounded;
    private boolean isGrounded;
    private float lastOnGround;
    private State playerState;

    private Characters.Type character;

    public PlayerBehavior(Entity entity, Characters.Type character) {
        super(entity);
        this.character = character;
        playerState = State.NORMAL;
    }

    @Override
    public void update(float dt) {
        var particleEmitter = entity.get(ParticleEmitter.class);
        var animator = entity.get(Animator.class);
        var mover = entity.get(Mover.class);

        jumpCoolDown = Math.max(0, jumpCoolDown - dt);
        attackCoolDown = Math.max(0, attackCoolDown - dt);

        if (playerState == State.ATTACK && animator.stateTime >= animator.animation.getAnimationDuration()) {
            playerState = State.NORMAL;
        }

        var playerInput = entity.get(PlayerInput.class);
        if (playerInput != null) {
            mover.velocity.x += playerInput.getWalkAmount() * MOVE_SPEED * dt;

            wasGrounded = isGrounded;
            isGrounded = mover.onGround();
            lastOnGround += dt;
            if (isGrounded) {
                lastOnGround = 0;
            }

            if (playerInput.actionPressed(PlayerInput.Action.ATTACK) && attackCoolDown <= 0) {
                animator.stateTime = 0;
                playerState = State.ATTACK;
                animator.play(character.get().animByType.get(Characters.AnimType.ATTACK));
                attackCoolDown = character.get().attackInfo.attackCooldown;
                // spawn attack
            }

            if (playerInput.actionJustPressed(PlayerInput.Action.POWER_ATTACK) && attackCoolDown <= 0) {
                playerState = State.ATTACK;
                animator.stateTime = 0;
                animator.play(character.get().animByType.get(Characters.AnimType.POWERATTACK));
                attackCoolDown = character.get().attackInfo.powerAttackCooldown;
                spawnPowerAttack();
            }

            if (playerInput.actionJustPressed(PlayerInput.Action.JUMP)
                && lastOnGround < COYOTE_TIME
                && jumpCoolDown <= 0) {
                mover.velocity.y = JUMP_SPEED;
                jumpCoolDown = .2f;
                entity.scene.screen.game.audioManager.playSound(Sounds.Type.BOARD_CLICK);

            }

            var pos = entity.get(Position.class);
            if (playerInput.actionJustPressed(PlayerInput.Action.NEXT_CHAR)) {
                nextCharacter();
                particleEmitter.spawnParticle(ParticleEffect.Type.SPARK, new SparkEffect.Params(pos.x(), pos.y(), character.get().primaryColor));
            }

            if (playerInput.actionJustPressed(PlayerInput.Action.PREVIOUS_CHAR)) {
                prevCharacter();
                particleEmitter.spawnParticle(ParticleEffect.Type.SPARK, new SparkEffect.Params(pos.x(), pos.y(), character.get().primaryColor));
            }

            // Cap Velocity
            var maxSpeed = isGrounded ? MAX_SPEED : MAX_SPEED_AIR;
            if (Math.abs(mover.velocity.x) > maxSpeed) {
                mover.velocity.x = maxSpeed * (Math.signum(mover.velocity.x));
            }

            // set facing direction based on velocity
            var moveDir = (int) Calc.sign(mover.velocity.x);
            if (moveDir != 0) {
                animator.facing = moveDir;
            }
        }

        var charData = character.get();
        switch (playerState) {
            case NORMAL:
                if (mover.onGround()) {

                    wasOnGround = true;

                    var animType = Characters.AnimType.IDLE;
                    if (Math.abs(mover.velocity.x) > 20) {
                        animType = Characters.AnimType.WALK;
                        var pos = entity.get(Position.class);
                        particleEmitter.spawnParticle(ParticleEffect.Type.DIRT, new DirtEffect.Params(pos.x(), pos.y()));
                    }
                    animator.play(charData.animByType.get(animType));
                } else {
                    wasOnGround = false;
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
//                animator.play(charData.animByType.get(Characters.AnimType.ATTACK));
                break;
        }

        // TODO(brian): handle hurt and attack animations
    }

    public void nextCharacter() {
        if      (character == Characters.Type.OLDMAN)  character = Characters.Type.BELMONT;
        else if (character == Characters.Type.BELMONT) character = Characters.Type.LINK;
        else if (character == Characters.Type.LINK)    character = Characters.Type.MARIO;
        else if (character == Characters.Type.MARIO)   character = Characters.Type.MEGAMAN;
        else                                           character = Characters.Type.OLDMAN;
    }

    public void prevCharacter() {
        if      (character == Characters.Type.OLDMAN)  character = Characters.Type.BELMONT;
        else if (character == Characters.Type.BELMONT) character = Characters.Type.MEGAMAN;
        else if (character == Characters.Type.MEGAMAN) character = Characters.Type.MARIO;
        else if (character == Characters.Type.MARIO)   character = Characters.Type.LINK;
        else                                           character = Characters.Type.OLDMAN;
    }

    private void spawnPowerAttack() {
        Util.log("Launch Power Attack");
        Entity powerAttackEntity  = null;
        switch (character) {
            case OLDMAN:
                break;
            case BELMONT:
                break;
            case LINK:
                break;
            case MARIO:
                break;
            case MEGAMAN:
                powerAttackEntity = megamanPowerAttack();
                break;
        }


        if (powerAttackEntity != null) {
            DebugRender.makeForShapes(powerAttackEntity, DebugRender.DRAW_POSITION_AND_COLLIDER);
        }

    }

    public Entity megamanPowerAttack() {
        float size = 12f;
        var scene = entity.scene;
        var charAnimator = entity.get(Animator.class);
        var charPos = entity.get(Position.class);

        var powerAttackEntity = scene.createEntity();
        new Position(powerAttackEntity, charPos.x() + 15 * charAnimator.facing, charPos.y() + 13);
        var collider = Collider.makeRect(powerAttackEntity, Collider.Mask.player_projectile, -size/2f, -size/2f, size, size);
        var mover = new Mover(powerAttackEntity, collider);
        mover.velocity.x = 100 * charAnimator.facing;
        mover.addCollidesWith(Collider.Mask.enemy);
        mover.setOnHit((params -> {
                var collidedEntity = params.hitCollider.entity;
                var health = collidedEntity.get(Health.class);
                if (health != null) {
                    health.takeDamage(character.get().attackInfo.powerAttackDamage);
                }
                destroyBulletParticle(powerAttackEntity);
                powerAttackEntity.scene.world.destroy(powerAttackEntity);
            })
        );

        var animator = new Animator(powerAttackEntity, Anims.Type.GOOMBA_WALK);
        animator.size.set(size, size);
        animator.origin.set(size/2f, size/2f);

        var timer = new Timer(powerAttackEntity, 1, () -> {
            destroyBulletParticle(powerAttackEntity);
            powerAttackEntity.destroy(Timer.class);
            powerAttackEntity.scene.world.destroy(powerAttackEntity);
            // TODO particle effect
        });

        return powerAttackEntity;
    }

    private void destroyBulletParticle(Entity bulletEntity) {
        var particleEmitter = entity.get(ParticleEmitter.class);
        var bulletPos = bulletEntity.get(Position.class);
        particleEmitter.spawnParticle(
            ParticleEffect.Type.BULLET_EXPLOSION,
            new BulletExposionEffect.Params(bulletPos.x(), bulletPos.y(), bulletEntity.get(Animator.class).keyframe)
        );
    }
}
