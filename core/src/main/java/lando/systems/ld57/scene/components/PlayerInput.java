package lando.systems.ld57.scene.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public class PlayerInput extends Component {
    public static float MAX_SPEED = 100f;
    public static float JUMP_SPEED = 350f;
    public static float MOVE_SPEED = 8f;

    public PlayerInput(Entity entity) {
        super(entity);

    }

    @Override
    public void update(float dt) {
        var mover = entity.get(Mover.class);
        if (mover == null) return; // Early Out

        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            mover.velocity.x -= MOVE_SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            mover.velocity.x += MOVE_SPEED;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && mover.onGround()){
            mover.velocity.y = JUMP_SPEED;
        }



        // Cap Velocity
        if (Math.abs(mover.velocity.x) > MAX_SPEED) {
            mover.velocity.x = MAX_SPEED * (Math.signum(mover.velocity.x));
        }

    }
}
