package lando.systems.ld57.scene.scenes;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.assets.Sounds;
import lando.systems.ld57.particles.effects.*;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Direction;
import lando.systems.ld57.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class PlayerBehavior extends Component {

    public enum State {NORMAL, ATTACK, HURT}

    public static float COYOTE_TIME = 0.2f;
    public static float MAX_SPEED = 100f;
    public static float MAX_SPEED_AIR = 80f;
    public static float JUMP_SPEED = 300f;
    public static float MOVE_SPEED = 800f;

    private float jumpCoolDown;
    private float attackCoolDown;
    private boolean wasGrounded;
    private boolean isGrounded;
    private float lastOnGround;
    private State playerState;
    public Vector2 lastSafePos;

    private final Rectangle currentRectFacing = new Rectangle();

    public Characters.Type character;

    public PlayerBehavior(Entity entity, Characters.Type character) {
        super(entity);
        this.character = character;
        playerState = State.NORMAL;
        lastSafePos = new Vector2();
    }

    @Override
    public void update(float dt) {
        var particleEmitter = entity.get(ParticleEmitter.class);
        var animator = entity.get(Animator.class);
        var mover = entity.get(Mover.class);
        var pos = entity.get(Position.class);
        var energy = entity.get(Energy.class);
        mover.addCollidesWith(Collider.Mask.enemy);
        jumpCoolDown = Math.max(0, jumpCoolDown - dt);
        attackCoolDown = Math.max(0, attackCoolDown - dt);

        if (pos.y() <  -40) resetToSafePosition();

        if (playerState == State.ATTACK && animator.stateTime >= animator.animation.getAnimationDuration()) {
            playerState = State.NORMAL;
        }
        if (playerState == State.HURT && animator.stateTime >= animator.animation.getAnimationDuration()) {
            Util.log("PlayerBehavior", "Hurt animation is finished, duration " + animator.animation.getAnimationDuration() + " stateTime " + animator.stateTime);
            playerState = State.NORMAL;
        }

        var playerInput = entity.get(PlayerInput.class);
        if (playerInput != null) {
            if (playerInput.codeInput()){
                Util.log("CODE", "CODE INPUT");
            }
            mover.velocity.x += playerInput.getWalkAmount() * MOVE_SPEED * dt;

            wasGrounded = isGrounded;
            isGrounded = mover.onGround();
            lastOnGround += dt;
            if (isGrounded) {
                lastOnGround = 0;
            }

            if (wasGrounded && isGrounded) {
                lastSafePos.set(pos.x(), pos.y());
            }

            if (playerInput.actionPressed(PlayerInput.Action.ATTACK) && attackCoolDown <= 0) {
                if ( energy.getCurrentEnergy() > character.get().attackInfo.attackEnergyCost) {
                    animator.stateTime = 0;
                    energy.useEnergy(character.get().attackInfo.attackEnergyCost);
                    playerState = State.ATTACK;
                    animator.play(character.get().animByType.get(Characters.AnimType.ATTACK));
                    attackCoolDown = character.get().attackInfo.attackCooldown;
                    spawnAttack();
                } else {
                    // TODO: not enough energy to use attack, need sound
                }
            }

            if (playerInput.actionJustPressed(PlayerInput.Action.POWER_ATTACK) && attackCoolDown <= 0) {
                if (energy.getCurrentEnergy() > character.get().attackInfo.powerAttackEnergyCost) {
                    playerState = State.ATTACK;
                    animator.stateTime = 0;
                    energy.useEnergy(character.get().attackInfo.powerAttackEnergyCost);
                    animator.play(character.get().animByType.get(Characters.AnimType.POWERATTACK));
                    attackCoolDown = character.get().attackInfo.powerAttackCooldown;
                    spawnPowerAttack();
                } else {
                    // not enough Energy to do power attack
                    // TODO: sound effect needed
                }

            }

            if (playerInput.actionJustPressed(PlayerInput.Action.JUMP)
                && lastOnGround < COYOTE_TIME
                && jumpCoolDown <= 0) {
                mover.velocity.y = JUMP_SPEED;
                jumpCoolDown = .2f;

                entity.scene.screen.game.audioManager.playSound(Sounds.Type.JUMP, 0.6f, 0f);
//                entity.scene.screen.game.audioManager.playSound(Sounds.Type.JUMP, 1f);

            }

            if (playerState == State.NORMAL) {
                if (playerInput.actionJustPressed(PlayerInput.Action.NEXT_CHAR)) {
                    nextCharacter();
                    particleEmitter.spawnParticle(ParticleEffect.Type.SPARK, new SparkEffect.Params(pos.x(), pos.y(), character.get().primaryColor));
                }

                if (playerInput.actionJustPressed(PlayerInput.Action.PREVIOUS_CHAR)) {
                    prevCharacter();
                    particleEmitter.spawnParticle(ParticleEffect.Type.SPARK, new SparkEffect.Params(pos.x(), pos.y(), character.get().primaryColor));
                }
            }

            // Cap Velocity
            var maxSpeed = isGrounded ? MAX_SPEED : MAX_SPEED_AIR;
            if (Math.abs(mover.velocity.x) > maxSpeed) {
                mover.velocity.x = maxSpeed * (Math.signum(mover.velocity.x));
            }
            if (mover.velocity.y < -300) {
                mover.velocity.y = -300;
            }

        }

        var charData = character.get();
        switch (playerState) {
            case NORMAL:
                if (mover.onGround()) {
                    var animType = Characters.AnimType.IDLE;
                    if (Math.abs(mover.velocity.x) > 20) {
                        animType = Characters.AnimType.WALK;
                        particleEmitter.spawnParticle(ParticleEffect.Type.DIRT, new DirtEffect.Params(pos.x(), pos.y()));
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
//                animator.play(charData.animByType.get(Characters.AnimType.ATTACK));
                break;
            case HURT:
                animator.play(charData.animByType.get(Characters.AnimType.HURT));

                break;
        }

        // TODO(brian): handle hurt and attack animations
    }

    public void knockBack(float strength) {
        var pos = entity.get(Position.class);
        var mover = entity.get(Mover.class);
        var animator = entity.get(Animator.class);
        var emitter = entity.get(ParticleEmitter.class);

        mover.velocity.x = 300f * strength * -animator.facing ;
        mover.velocity.y = 250f * strength;

        animator.stateTime = 0;
        playerState = State.HURT;

        emitter.spawnParticle(ParticleEffect.Type.SPARK, new SparkEffect.Params(pos.x(), pos.y(), character.get().primaryColor));

        switch (character) {
            case BELMONT:
                Main.game.audioManager.playSound(Sounds.Type.PUNCHHIT, .7f);
                break;
            case LINK:
                Main.game.audioManager.playSound(Sounds.Type.LINKPAIN);
                break;
            case MARIO:
                Main.game.audioManager.playSound(Sounds.Type.GRUNT, .7f);
                break;
            case MEGAMAN:
                Main.game.audioManager.playSound(Sounds.Type.MEGADAMAGE, 1f);
                break;
            case OLDMAN:
                Main.game.audioManager.playSound(Sounds.Type.GRUNT, .5f);
                break;
            default:
                Main.game.audioManager.playSound(Sounds.Type.GRUNT, .7f);
                break;
        }
        // disable automatic facing control so the player doesn't turn around
        // TODO(brian): this doesn't really work as a timer, need some sort of 'landed' callback
//        animator.autoFacing = false;
//        var timer = entity.scene.createEntity();
//        new Timer(timer, 0.6f, () -> {
//            animator.autoFacing = true;
//            timer.selfDestruct();
//        });
    }

    public void resetToSafePosition() {
        var pos = entity.get(Position.class);
        var mover = entity.get(Mover.class);
        if (pos != null) {
            pos.set(lastSafePos);
        }
        if (mover != null) {
            mover.velocity.x = 0;
        }
    }

    public void nextCharacter() {
        if      (character == Characters.Type.OLDMAN)  character = Characters.Type.BELMONT;
        else if (character == Characters.Type.BELMONT) character = Characters.Type.LINK;
        else if (character == Characters.Type.LINK)    character = Characters.Type.MARIO;
        else if (character == Characters.Type.MARIO)   character = Characters.Type.MEGAMAN;
        else                                           character = Characters.Type.OLDMAN;

        var anim = entity.get(Animator.class);
        if (anim != null) {
            anim.size.set(character.get().size);
            anim.origin.set(character.get().origin);
        }
        setOnHit();

    }

    public void prevCharacter() {
        if      (character == Characters.Type.OLDMAN)  character = Characters.Type.MEGAMAN;
        else if (character == Characters.Type.BELMONT) character = Characters.Type.OLDMAN;
        else if (character == Characters.Type.MEGAMAN) character = Characters.Type.MARIO;
        else if (character == Characters.Type.MARIO)   character = Characters.Type.LINK;
        else                                           character = Characters.Type.BELMONT;

        var anim = entity.get(Animator.class);
        if (anim != null) {
            anim.size.set(character.get().size);
            anim.origin.set(character.get().origin);
        }
        setOnHit();
    }

    private void spawnAttack() {
        Util.log("Launch Attack Entity");
        Entity attackEntity  = null;
        switch (character) {
            case OLDMAN:
                attackEntity = oldManAttack();
                Main.game.audioManager.playSound(Sounds.Type.SWIPE1);
                break;
            case BELMONT:
                attackEntity = belmontAttack();
                Main.game.audioManager.playSound(Sounds.Type.WHIP);
                break;
            case LINK:
                attackEntity = linkAttack();
                Main.game.audioManager.playSound(Sounds.Type.LINKATTACK);
                break;
            case MARIO:
//                Main.game.audioManager.playSound(Sounds.Type.FIREBALL);
                break;
            case MEGAMAN:
                Main.game.audioManager.playSound(Sounds.Type.MEGABUSTER);
                attackEntity = megamanAttack();
                break;
        }


        if (attackEntity != null) {
            DebugRender.makeForShapes(attackEntity, DebugRender.DRAW_POSITION_AND_COLLIDER);
        }

    }

    private void spawnPowerAttack() {
        Util.log("Launch Power Attack");
        Entity powerAttackEntity  = null;
        switch (character) {
            case OLDMAN:
                powerAttackEntity = oldManPowerAttack();
                Main.game.audioManager.playSound(Sounds.Type.SWIPE2, .8f);
                break;
            case BELMONT:
                powerAttackEntity = belmontPowerAttack();
                Main.game.audioManager.playSound(Sounds.Type.AXE);
                break;
            case LINK:
                powerAttackEntity = linkPowerAttack();
                Main.game.audioManager.playSound(Sounds.Type.LINKSWORD);
                break;
            case MARIO:
                powerAttackEntity = marioPowerAttack();
                Main.game.audioManager.playSound(Sounds.Type.FIREBALL);
                break;
            case MEGAMAN:
                Main.game.audioManager.playSound(Sounds.Type.BIGBUSTER);
                powerAttackEntity = megamanPowerAttack();
                break;
        }


        if (powerAttackEntity != null) {
            DebugRender.makeForShapes(powerAttackEntity, DebugRender.DRAW_POSITION_AND_COLLIDER);
        }

    }

    public Entity marioPowerAttack() {
        float size = 12f;
        var scene = entity.scene;
        var charAnimator = entity.get(Animator.class);
        var charPos = entity.get(Position.class);

        var powerAttackEntity = scene.createEntity();
        new FireParticle(powerAttackEntity);
        new Position(powerAttackEntity, charPos.x() + 15 * charAnimator.facing, charPos.y() + 13);
        var collider = Collider.makeRect(powerAttackEntity, Collider.Mask.player_projectile, -size/2f, -size/2f, size, size);
        var powerAttackEntityMover = new Mover(powerAttackEntity, collider);
        powerAttackEntityMover.velocity.x = 100 * charAnimator.facing;
        powerAttackEntityMover.gravity = Mover.BASE_GRAVITY;
        powerAttackEntityMover.addCollidesWith(Collider.Mask.enemy, Collider.Mask.solid);
        powerAttackEntityMover.setOnHit((params -> {
            if (params.hitCollider.mask == Collider.Mask.solid){
                var move = powerAttackEntity.get(Mover.class);
                if (params.direction == Direction.Relative.DOWN) {
                    move.velocity.y = 100;
                }

            } else {
                var collidedEntity = params.hitCollider.entity;
                var health = collidedEntity.get(Health.class);
                if (health != null) {
                    health.takeDamage(character.get().attackInfo.powerAttackDamage);
                }
                destroyBulletParticle(powerAttackEntity);
                powerAttackEntity.scene.world.destroy(powerAttackEntity);
            }
            })
        );

        var animator = new Animator(powerAttackEntity, Anims.Type.MARIO_FIREBALL);
        animator.size.set(size, size);
        animator.origin.set(size/2f, size/2f);
        animator.outlineColor.a = 0;

        var timer = new Timer(powerAttackEntity, 1, () -> {
            destroyBulletParticle(powerAttackEntity);
            powerAttackEntity.destroy(Timer.class);
            powerAttackEntity.scene.world.destroy(powerAttackEntity);
            // TODO particle effect
        });

        return powerAttackEntity;
    }

    public Entity oldManAttack() {
        var playerPos = entity.get(Position.class);
        var playerAnim = entity.get(Animator.class);

        var scene = entity.scene;
        var attackEntity = scene.createEntity();

        var position = new Position(attackEntity,
            playerPos.x() + 5 * playerAnim.facing,
            playerPos.y() + 20);

        // lookup a attack animation / collider data for character
        var charData = Characters.Type.OLDMAN.get();
        var attackAnim = charData.animByType.get(Characters.AnimType.ATTACK).get();
        var attackDuration = attackAnim.getAnimationDuration();
        var attackColliderRects = List.of(charData.attackColliderRects.get(0));
        if (attackColliderRects.isEmpty()) {
            entity.scene.destroy(attackEntity);
            return null;
        }

        // create the attack collider, updating its rect on each attack animation frame
        var colliderRect = attackColliderRects.get(0);
        var collider = Collider.makeRect(attackEntity, Collider.Mask.player_projectile, colliderRect);
        var meleeDamage = new MeleeDamage(attackEntity);
        meleeDamage.setOnHit((params -> {
            var collidedEntity = params.hitCollider.entity;
            var health = collidedEntity.get(Health.class);
            if (health != null) {
                health.takeDamage(character.get().attackInfo.attackDamage);
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


        return attackEntity;
    }

    public Entity oldManPowerAttack() {
        var playerPos = entity.get(Position.class);
        var playerAnim = entity.get(Animator.class);

        var scene = entity.scene;
        var attackEntity = scene.createEntity();

        var position = new Position(attackEntity,
            playerPos.x() + 5 * playerAnim.facing,
            playerPos.y() + 20);

        // lookup a attack animation / collider data for character
        var charData = Characters.Type.OLDMAN.get();
        var attackAnim = charData.animByType.get(Characters.AnimType.POWERATTACK).get();
        var attackDuration = attackAnim.getAnimationDuration();
        var attackColliderRects = charData.attackColliderRects;
        if (attackColliderRects.isEmpty()) {
            entity.scene.destroy(attackEntity);
            return null;
        }

        // create the attack collider, updating its rect on each attack animation frame
        var colliderRect = attackColliderRects.get(0);
        var collider = Collider.makeRect(attackEntity, Collider.Mask.player_projectile, colliderRect);
        var meleeDamage = new MeleeDamage(attackEntity);
        meleeDamage.setOnHit((params -> {
            var collidedEntity = params.hitCollider.entity;
            var health = collidedEntity.get(Health.class);
            if (health != null) {
                health.takeDamage(character.get().attackInfo.attackDamage);
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


        return attackEntity;
    }

    public Entity belmontAttack() {
        var playerPos = entity.get(Position.class);
        var playerAnim = entity.get(Animator.class);

        var scene = entity.scene;
        var attackEntity = scene.createEntity();

        var position = new Position(attackEntity,
            playerPos.x() + 5 * playerAnim.facing,
            playerPos.y() + 20);

        // lookup a attack animation / collider data for character
        var charData = Characters.Type.BELMONT.get();
        var attackAnim = charData.animByType.get(Characters.AnimType.ATTACK).get();
        var attackDuration = attackAnim.getAnimationDuration();
        var attackColliderRects = charData.attackColliderRects;
        if (attackColliderRects.isEmpty()) {
            entity.scene.destroy(attackEntity);
            return null;
        }

        // create the attack collider, updating its rect on each attack animation frame
        var colliderRect = attackColliderRects.get(0);
        var collider = Collider.makeRect(attackEntity, Collider.Mask.player_projectile, colliderRect);
        var meleeDamage = new MeleeDamage(attackEntity);
        meleeDamage.setOnHit((params -> {
            var collidedEntity = params.hitCollider.entity;
            var health = collidedEntity.get(Health.class);
            if (health != null) {
                health.takeDamage(character.get().attackInfo.attackDamage);
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


        return attackEntity;
    }

    public Entity belmontPowerAttack() {
        float size = 12f;
        var scene = entity.scene;
        var charAnimator = entity.get(Animator.class);
        var charPos = entity.get(Position.class);

        var powerAttackEntity = scene.createEntity();
        new Position(powerAttackEntity, charPos.x() + 15 * charAnimator.facing, charPos.y() + 23);
        var collider = Collider.makeRect(powerAttackEntity, Collider.Mask.player_projectile, -size/2f, -size/2f, size, size);
        var powerAttackEntityMover = new Mover(powerAttackEntity, collider);
        powerAttackEntityMover.velocity.x = 100 * charAnimator.facing;
        powerAttackEntityMover.velocity.y = 220;
        powerAttackEntityMover.gravity = Mover.BASE_GRAVITY;
        powerAttackEntityMover.addCollidesWith(Collider.Mask.enemy, Collider.Mask.solid);
        powerAttackEntityMover.setOnHit((params -> {
                if (params.hitCollider.mask == Collider.Mask.solid){
                    destroyBulletParticle(powerAttackEntity);
                    powerAttackEntity.scene.world.destroy(powerAttackEntity);

                } else {
                    var collidedEntity = params.hitCollider.entity;
                    var health = collidedEntity.get(Health.class);
                    if (health != null) {
                        health.takeDamage(character.get().attackInfo.powerAttackDamage);
                    }
                    destroyBulletParticle(powerAttackEntity);
                    powerAttackEntity.scene.world.destroy(powerAttackEntity);
                }
            })
        );

        var animator = new Animator(powerAttackEntity, Anims.Type.BELMONT_AXE);
        animator.size.set(size, size);
        animator.origin.set(size/2f, size/2f);
        animator.outlineColor.a = 0;

        var timer = new Timer(powerAttackEntity, 1, () -> {
            destroyBulletParticle(powerAttackEntity);
            powerAttackEntity.destroy(Timer.class);
            powerAttackEntity.scene.world.destroy(powerAttackEntity);
            // TODO particle effect
        });

        return powerAttackEntity;
    }


    public Entity linkAttack() {
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
        var collider = Collider.makeRect(attackEntity, Collider.Mask.player_projectile, colliderRect);

        var meleeDamage = new MeleeDamage(attackEntity);
        meleeDamage.setOnHit((params -> {
            var collidedEntity = params.hitCollider.entity;
            var health = collidedEntity.get(Health.class);
            if (health != null) {
                health.takeDamage(character.get().attackInfo.attackDamage);
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
        float size = 12f;
        var scene = entity.scene;
        var charAnimator = entity.get(Animator.class);
        var charPos = entity.get(Position.class);

        var powerAttackEntity = scene.createEntity();
        new Position(powerAttackEntity, charPos.x() + 14 * charAnimator.facing, charPos.y() + 20);
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

        var animator = new Animator(powerAttackEntity, Anims.Type.LINK_SWORD);
        animator.size.set(size, size);
        animator.origin.set(size/2f, size/2f);
        animator.outlineColor.a = 0;

        var timer = new Timer(powerAttackEntity, 5, () -> {
            destroyBulletParticle(powerAttackEntity);
            powerAttackEntity.destroy(Timer.class);
            powerAttackEntity.scene.world.destroy(powerAttackEntity);
            // TODO particle effect
        });

        return powerAttackEntity;
    }

    public Entity megamanAttack() {
        float size = 8f;
        var scene = entity.scene;
        var charAnimator = entity.get(Animator.class);
        var charPos = entity.get(Position.class);

        var attackEntity = scene.createEntity();
        new Position(attackEntity, charPos.x() + 15 * charAnimator.facing, charPos.y() + 13);
        var collider = Collider.makeRect(attackEntity, Collider.Mask.player_projectile, -size/2f, -size/2f, size, size);
        var mover = new Mover(attackEntity, collider);
        mover.velocity.x = 100 * charAnimator.facing;
        mover.addCollidesWith(Collider.Mask.enemy);
        mover.setOnHit(params -> {
            var collidedEntity = params.hitCollider.entity;
            var health = collidedEntity.get(Health.class);
            if (health != null) {
                health.takeDamage(character.get().attackInfo.attackDamage);
            }
            destroyBulletParticle(attackEntity);
            attackEntity.scene.world.destroy(attackEntity);
        });

        var animator = new Animator(attackEntity, Anims.Type.MEGAMAN_SHOT);
        animator.size.set(size, size);
        animator.origin.set(size/2f, size/2f);
        animator.outlineColor.a = 0;

        var timer = new Timer(attackEntity, 4, () -> {
            destroyBulletParticle(attackEntity);
            attackEntity.destroy(Timer.class);
            attackEntity.scene.world.destroy(attackEntity);
            // TODO particle effect
        });

        return attackEntity;
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

        var animator = new Animator(powerAttackEntity, Anims.Type.MEGAMAN_POWERSHOT);
        animator.size.set(size, size);
        animator.origin.set(size/2f, size/2f);
        animator.outlineColor.a = 0;

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
            new BulletExplosionEffect.Params(bulletPos.x(), bulletPos.y(), bulletEntity.get(Animator.class).keyframe)
        );
    }

    private void setOnHit() {
        var mover = entity.get(Mover.class);
        if (character == Characters.Type.MARIO) {
            mover.setOnHit( (params) -> {
                var hitEntity = params.hitCollider.entity;
                if (params.hitCollider.mask == Collider.Mask.enemy && params.direction == Direction.Relative.DOWN) {
                    Util.log("Mario Behavior", "Stomped enemy");
                    hitEntity.getIfActive(Health.class).takeDamage(1);
                    mover.velocity.y = 200f;
                }

                if (params.hitCollider.mask == Collider.Mask.solid && params.direction == Direction.Relative.UP) {
                    mover.velocity.y = 0;
                }
            });
        } else {
            mover.setOnHit( (params) -> {
                if (params.hitCollider.mask == Collider.Mask.solid && params.direction == Direction.Relative.UP) {
                    mover.velocity.y = 0;
                }
            });
        }
    }
}
