package lando.systems.ld57.scene.ldgame;

import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

import java.util.Map;

public class HeroBehavior extends Component {

    private final Animator animator;
    private final Mover mover;

    private Character character;

    public HeroBehavior(Entity entity, Animator animator, Mover mover) {
        super(entity);
        this.animator = animator;
        this.mover = mover;
        this.character = Character.BELMONT;
    }

    @Override
    public void update(float dt) {
        if (mover.velocity.x > 0) {
            animator.facing = 1;
        } else if (mover.velocity.x < 0) {
            animator.facing = -1;
        }
        if (mover.onGround()) {
            if (Math.abs(mover.velocity.x) > 20) {
                var anim = charAnimMap.get(character).get(AnimType.WALK);
                animator.play(anim);
            } else {
                var anim = charAnimMap.get(character).get(AnimType.IDLE);
                animator.play(anim);
            }
        } else {
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
        if (character == Character.BELMONT) {
            character = Character.LINK;
        } else if (character == Character.LINK) {
            character = Character.BELMONT;
        }
    }

    // ------------------------------------------------------------------------
    // Utilities to convert between generic and character-specific animations
    // ------------------------------------------------------------------------

    // NOTE: hero anim was for testing and doesn't match the sizes of the others, skipping it
    private enum Character { /*HERO,*/ BELMONT, LINK }

    private enum AnimType { IDLE, WALK, JUMP, FALL, ATTACK, HURT }

    private final Map<Character, Map<AnimType, Anims.Type>> charAnimMap = Map.of(
//          Character.HERO, Map.of(
//              AnimType.IDLE, Anims.Type.HERO_IDLE
//            , AnimType.WALK, Anims.Type.HERO_RUN
//            , AnimType.JUMP, Anims.Type.HERO_JUMP
//            , AnimType.FALL, Anims.Type.HERO_FALL
//            , AnimType.HURT, Anims.Type.HERO_FALL // TODO: missing
//            , AnimType.ATTACK, Anims.Type.HERO_ATTACK
//        )
          Character.BELMONT, Map.of(
              AnimType.IDLE, Anims.Type.BELMONT_IDLE
            , AnimType.WALK, Anims.Type.BELMONT_WALK
            , AnimType.JUMP, Anims.Type.BELMONT_JUMP
            , AnimType.FALL, Anims.Type.BELMONT_FALL
            , AnimType.HURT, Anims.Type.BELMONT_HURT
            , AnimType.ATTACK, Anims.Type.BELMONT_ATTACK // TODO: missing
        )
        , Character.LINK, Map.of(
              AnimType.IDLE, Anims.Type.LINK_IDLE
            , AnimType.WALK, Anims.Type.LINK_WALK
            , AnimType.JUMP, Anims.Type.LINK_JUMP
            , AnimType.FALL, Anims.Type.LINK_FALL
            , AnimType.HURT, Anims.Type.LINK_HURT
            , AnimType.ATTACK, Anims.Type.LINK_ATTACK
        )
    );
}
