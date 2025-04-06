package lando.systems.ld57.scene.ldgame;

import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.PlayerInput;
import lando.systems.ld57.scene.components.ParticleEmitter;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.assets.Sounds;

import java.util.Map;

public class HeroBehavior extends Component {

    public static float COYOTE_TIME = 0.2f;
    public static float MAX_SPEED = 100f;
    public static float MAX_SPEED_AIR = 80f;
    public static float JUMP_SPEED = 300f;
    public static float MOVE_SPEED = 800f;

    private final Animator animator;
    private final Mover mover;
    private final ParticleEmitter particleEmitter;
    private boolean wasOnGround = true;
    private float jumpCoolDown;
    private boolean wasGrounded;
    private boolean isGrounded;
    private float lastOnGround;

    private Character character;

    public HeroBehavior(Entity entity, Animator animator, Mover mover, ParticleEmitter particleEmitter) {
        super(entity);
        this.animator = animator;
        this.mover = mover;
        this.particleEmitter = particleEmitter;
        this.character = Character.BELMONT;
    }

    @Override
    public void update(float dt) {
        jumpCoolDown = Math.max(0, jumpCoolDown - dt);

        var playerInput = entity.get(PlayerInput.class);
        // Handle input
        if (playerInput != null) {
            mover.velocity.x += playerInput.getWalkAmount() * MOVE_SPEED * dt;

            wasGrounded = isGrounded;
            isGrounded = mover.onGround();
            lastOnGround += dt;
            if (isGrounded) {
                lastOnGround = 0;
            }

            if (playerInput.actionJustPressed(PlayerInput.Action.JUMP)
                    && lastOnGround < COYOTE_TIME
                    && jumpCoolDown <= 0) {
                mover.velocity.y = JUMP_SPEED;
                jumpCoolDown = .2f;
            }

            if (playerInput.actionJustPressed(PlayerInput.Action.NEXT_CHAR)) {
                nextCharacter();
            }

            if (playerInput.actionJustPressed(PlayerInput.Action.PREVIOUS_CHAR)) {
                prevCharacter();
            }


            // Cap Velocity
            var maxSpeed = MAX_SPEED;
            if (isGrounded){
                maxSpeed = MAX_SPEED;
            } else {
                maxSpeed = MAX_SPEED_AIR;
            }
            if (Math.abs(mover.velocity.x) > maxSpeed) {
                mover.velocity.x = maxSpeed * (Math.signum(mover.velocity.x));
            }
        }

        if (mover.velocity.x > 0) {
            animator.facing = 1;
        } else if (mover.velocity.x < 0) {
            animator.facing = -1;
        }
        if (mover.onGround()) {
            if (!wasOnGround) {
                entity.scene.screen.game.audioManager.playSound(Sounds.Type.BOARD_CLICK);
            }
            wasOnGround = true;
            if (Math.abs(mover.velocity.x) > 20) {
                var anim = charAnimMap.get(character).get(AnimType.WALK);
                animator.play(anim);
                particleEmitter.spawnParticle(entity.get(Position.class).x(), entity.get(Position.class).y());
            } else {
                var anim = charAnimMap.get(character).get(AnimType.IDLE);
                animator.play(anim);
            }
        } else {
            wasOnGround = false;
            if (mover.velocity.y > 0) {
                var anim = charAnimMap.get(character).get(AnimType.JUMP);
                animator.play(anim);
            } else if (mover.velocity.y < 0) {
                var anim = charAnimMap.get(character).get(AnimType.FALL);
                animator.play(anim);
            }
        }
        // TODO(brian): handle hurt and attack animations
    }

    public void nextCharacter() {
        if      (character == Character.BELMONT) character = Character.LINK;
        else if (character == Character.LINK)    character = Character.MARIO;
        else if (character == Character.MARIO)   character = Character.MEGAMAN;
        else                                     character = Character.BELMONT;
    }

    public void prevCharacter() {
        if (character == Character.BELMONT)      character = Character.MEGAMAN;
        else if (character == Character.MEGAMAN) character = Character.MARIO;
        else if (character == Character.MARIO)   character = Character.LINK;
        else                                     character = Character.BELMONT;
    }

    // ------------------------------------------------------------------------
    // Utilities to convert between generic and character-specific animations
    // ------------------------------------------------------------------------

    // NOTE: hero anim was for testing and doesn't match the sizes of the others, skipping it
    private enum Character { /*HERO,*/ BELMONT, LINK, MARIO, MEGAMAN }

    private enum AnimType { ATTACK, FALL, HURT, IDLE, JUMP, WALK }

    private final Map<Character, Map<AnimType, Anims.Type>> charAnimMap = Map.of(
//          Character.HERO, Map.of(
//              AnimType.IDLE, Anims.Type.HERO_IDLE
//            , AnimType.WALK, Anims.Type.HERO_RUN
//            , AnimType.JUMP, Anims.Type.HERO_JUMP
//            , AnimType.FALL, Anims.Type.HERO_FALL
//            , AnimType.HURT, Anims.Type.HERO_FALL // TODO: missing
//            , AnimType.ATTACK, Anims.Type.HERO_ATTACK)
          Character.BELMONT, Map.of(
              AnimType.ATTACK, Anims.Type.BELMONT_ATTACK
            , AnimType.FALL, Anims.Type.BELMONT_FALL
            , AnimType.HURT, Anims.Type.BELMONT_HURT
            , AnimType.IDLE, Anims.Type.BELMONT_IDLE
            , AnimType.JUMP, Anims.Type.BELMONT_JUMP
            , AnimType.WALK, Anims.Type.BELMONT_WALK) // TODO: missing
        , Character.LINK, Map.of(
              AnimType.ATTACK, Anims.Type.LINK_ATTACK
            , AnimType.FALL, Anims.Type.LINK_FALL
            , AnimType.HURT, Anims.Type.LINK_HURT
            , AnimType.IDLE, Anims.Type.LINK_IDLE
            , AnimType.JUMP, Anims.Type.LINK_JUMP
            , AnimType.WALK, Anims.Type.LINK_WALK)
        , Character.MARIO, Map.of(
              AnimType.ATTACK, Anims.Type.MARIO_ATTACK
            , AnimType.FALL, Anims.Type.MARIO_FALL
            , AnimType.HURT, Anims.Type.MARIO_FALL // TODO: missing
            , AnimType.IDLE, Anims.Type.MARIO_IDLE
            , AnimType.JUMP, Anims.Type.MARIO_JUMP
            , AnimType.WALK, Anims.Type.MARIO_WALK)
        , Character.MEGAMAN, Map.of(
              AnimType.ATTACK, Anims.Type.MEGAMAN_ATTACK
            , AnimType.FALL, Anims.Type.MEGAMAN_FALL
            , AnimType.HURT, Anims.Type.MEGAMAN_HURT
            , AnimType.IDLE, Anims.Type.MEGAMAN_IDLE
            , AnimType.JUMP, Anims.Type.MEGAMAN_JUMP
            , AnimType.WALK, Anims.Type.MEGAMAN_WALK)
    );
}
