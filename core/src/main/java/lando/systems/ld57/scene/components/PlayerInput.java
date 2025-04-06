package lando.systems.ld57.scene.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import lando.systems.ld57.Main;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.ldgame.HeroBehavior;
import lando.systems.ld57.utils.controllers.mapping.MappedController;

import static lando.systems.ld57.assets.MyControllerMapping.*;

public class PlayerInput extends Component {
    public static float COYOTE_TIME = 0.2f;
    public static float MAX_SPEED = 100f;
    public static float MAX_SPEED_AIR = 80f;
    public static float JUMP_SPEED = 300f;
    public static float MOVE_SPEED = 800f;
    public static float CONTROLLER_DEADZONE = 0.1f;

    private float jumpCoolDown;
    private boolean wasGrounded;
    private boolean isGrounded;
    private boolean jumpButtonLastFrame;
    private float lastOnGround;
    private MappedController mappedController;


    public PlayerInput(Entity entity) {
        super(entity);
        jumpCoolDown = 0;
        lastOnGround = 0;
        wasGrounded = false;
        isGrounded = false;
        jumpButtonLastFrame = false;
        Controller controller = Controllers.getCurrent();
        if (controller != null) {
            mappedController = new MappedController(Controllers.getCurrent(), Main.game.assets.controllerMappings);
        }


    }

    @Override
    public void update(float dt) {
        jumpCoolDown = Math.max(0, jumpCoolDown - dt);


        var mover = entity.get(Mover.class);
        if (mover == null) return; // Early Out

        Controller controller = Controllers.getCurrent();
        if (controller != null) {
            if (mappedController == null) {
                mappedController = new MappedController(controller, Main.game.assets.controllerMappings);
            } else {
                mappedController.setController(controller);
            }
        }

        wasGrounded = isGrounded;
        isGrounded = mover.onGround();
        lastOnGround += dt;
        if (isGrounded) {
            lastOnGround = 0;
        }

        float moveDirX = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            moveDirX += -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            moveDirX += 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            && lastOnGround < COYOTE_TIME
            && jumpCoolDown <= 0){
            mover.velocity.y = JUMP_SPEED;
            jumpCoolDown = .2f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            var heroBehavior = entity.get(HeroBehavior.class);
            if (heroBehavior != null) {
                heroBehavior.nextCharacter();
            }
        }


        if (controller != null) {
            boolean jumpButton = mappedController.isButtonPressed(BUTTON_JUMP);
            if (jumpButton
                && lastOnGround < COYOTE_TIME
                && jumpCoolDown <= 0
                && !jumpButtonLastFrame) {
                mover.velocity.y = JUMP_SPEED;
                jumpCoolDown = .2f;
            }
            float xDir = mappedController.getConfiguredAxisValue(AXIS_HORIZONTAL);
            if (Math.abs(xDir) > CONTROLLER_DEADZONE) {
                moveDirX += xDir;
            }
            moveDirX += mappedController.getConfiguredAxisValue(D_PAD_AXIS);

            if (mappedController.isButtonPressed(BUTTON_CANCEL)) {
                var heroBehavior = entity.get(HeroBehavior.class);
                if (heroBehavior != null) {
                    heroBehavior.nextCharacter();
                }
            }

            jumpButtonLastFrame = jumpButton;
        }

        mover.velocity.x += moveDirX * MOVE_SPEED * dt;




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
}
