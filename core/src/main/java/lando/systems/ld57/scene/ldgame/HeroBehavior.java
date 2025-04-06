package lando.systems.ld57.scene.ldgame;

import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.assets.Sounds;
import lando.systems.ld57.math.Calc;
import lando.systems.ld57.particles.effects.DirtEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.ParticleEmitter;
import lando.systems.ld57.scene.components.PlayerInput;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public class HeroBehavior extends Component {

    public static float COYOTE_TIME = 0.2f;
    public static float MAX_SPEED = 100f;
    public static float MAX_SPEED_AIR = 80f;
    public static float JUMP_SPEED = 300f;
    public static float MOVE_SPEED = 800f;

    private final Animator animator;
    private final Mover mover;
    private boolean wasOnGround = true;
    private float jumpCoolDown;
    private boolean wasGrounded;
    private boolean isGrounded;
    private float lastOnGround;

    private Characters.Type character;

    public HeroBehavior(Entity entity, Animator animator, Mover mover) {
        super(entity);
        this.animator = animator;
        this.mover = mover;
        this.character = Characters.Type.OLDMAN;
    }

    @Override
    public void update(float dt) {
        jumpCoolDown = Math.max(0, jumpCoolDown - dt);

        var playerInput = entity.get(PlayerInput.class);
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
            var pos = entity.get(Position.class);

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

            // set facing direction based on velocity
            var moveDir = (int) Calc.sign(mover.velocity.x);
            if (moveDir != 0) {
                animator.facing = moveDir;
            }
        }

        var charData = character.get();
        if (mover.onGround()) {
            if (!wasOnGround) {
                entity.scene.screen.game.audioManager.playSound(Sounds.Type.BOARD_CLICK);
            }
            wasOnGround = true;

            var animType = Characters.AnimType.IDLE;
            if (Math.abs(mover.velocity.x) > 20) {
                animType = Characters.AnimType.WALK;
                var pos = entity.get(Position.class);
                var particleEmitter = entity.get(ParticleEmitter.class);
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
}
