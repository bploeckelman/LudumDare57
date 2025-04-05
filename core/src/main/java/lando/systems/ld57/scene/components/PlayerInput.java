package lando.systems.ld57.scene.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public class PlayerInput extends Component {
    public static float COYOTE_TIME = 0.2f;
    public static float MAX_SPEED = 100f;
    public static float JUMP_SPEED = 300f;
    public static float MOVE_SPEED = 800f;
    public static float CONTROLLER_DEADZONE = 0.1f;

    private float jumpCoolDown;
    private boolean wasGrounded;
    private boolean isGrounded;
    private boolean jumpButtonLastFrame;
    private float lastOnGround;


    public PlayerInput(Entity entity) {
        super(entity);
        jumpCoolDown = 0;
        lastOnGround = 0;
        wasGrounded = false;
        isGrounded = false;
        jumpButtonLastFrame = false;
    }

    @Override
    public void update(float dt) {
        jumpCoolDown = Math.max(0, jumpCoolDown - dt);


        var mover = entity.get(Mover.class);
        if (mover == null) return; // Early Out

        wasGrounded = isGrounded;
        isGrounded = mover.onGround();
        lastOnGround += dt;
        if (isGrounded) {
            lastOnGround = 0;
        }

        float moveDirX = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            moveDirX = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            moveDirX = 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            && lastOnGround < COYOTE_TIME
            && jumpCoolDown <= 0){
            mover.velocity.y = JUMP_SPEED;
            jumpCoolDown = .2f;
        }

        Controller controller = Controllers.getCurrent();
        if (controller != null) {
            boolean jumpButton = controller.getButton(controller.getMapping().buttonB);
            if (jumpButton
                && lastOnGround < COYOTE_TIME
                && jumpCoolDown <= 0
                && !jumpButtonLastFrame) {
                mover.velocity.y = JUMP_SPEED;
                jumpCoolDown = .2f;
            }
            float xDir = controller.getAxis(controller.getMapping().axisLeftX);
            if (Math.abs(xDir) > CONTROLLER_DEADZONE) {
                moveDirX = xDir;
            }

            if (controller.getButton(controller.getMapping().buttonDpadRight)) {
                moveDirX = 1f;
            }
            if (controller.getButton(controller.getMapping().buttonDpadLeft)) {
                moveDirX = -1f;
            }

            jumpButtonLastFrame = jumpButton;
        }

        mover.velocity.x += moveDirX * MOVE_SPEED * dt;




        // Cap Velocity
        if (Math.abs(mover.velocity.x) > MAX_SPEED) {
            mover.velocity.x = MAX_SPEED * (Math.signum(mover.velocity.x));
        }

    }
}
