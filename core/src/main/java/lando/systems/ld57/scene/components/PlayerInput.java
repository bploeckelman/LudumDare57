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

import java.util.HashMap;
import java.util.Map;

import static lando.systems.ld57.assets.MyControllerMapping.*;

public class PlayerInput extends Component {
    public enum Action {JUMP, ATTACK, POWER_ATTACK, PREVIOUS_CHAR, NEXT_CHAR, WALK}

    public static float CONTROLLER_DEADZONE = 0.1f;


    private MappedController mappedController;
    private final Map<Action, Boolean> pressedThisFrame;
    private final Map<Action, Boolean> pressedLastFrame;
    private float moveX;


    public PlayerInput(Entity entity) {
        super(entity);
        Controller controller = Controllers.getCurrent();
        if (controller != null) {
            mappedController = new MappedController(Controllers.getCurrent(), Main.game.assets.controllerMappings);
        }
        this.pressedThisFrame = new HashMap<>();
        this.pressedLastFrame = new HashMap<>();
    }

    @Override
    public void update(float dt) {
        // store into last Frame for next Frame
        for (Action action : Action.values()) {
            pressedLastFrame.put(action, pressedThisFrame.get(action));
        }

        for (Action action : Action.values()) {
            pressedThisFrame.put(action, false);
        }

        var mover = entity.get(Mover.class);
        if (mover == null) return; // Early Out

        moveX = 0;

        Controller controller = Controllers.getCurrent();
        if (controller != null) {
            if (mappedController == null) {
                mappedController = new MappedController(controller, Main.game.assets.controllerMappings);
            } else {
                mappedController.setController(controller);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            pressedThisFrame.put(Action.JUMP, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            pressedThisFrame.put(Action.ATTACK, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.K)) {
            pressedThisFrame.put(Action.POWER_ATTACK, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            pressedThisFrame.put(Action.PREVIOUS_CHAR, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            pressedThisFrame.put(Action.NEXT_CHAR, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            moveX += -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            moveX += 1;
        }

        if (Controllers.getCurrent() != null) {
            if (mappedController.isButtonPressed(BUTTON_JUMP)) {
                pressedThisFrame.put(Action.JUMP, true);
            }
            if (mappedController.isButtonPressed(BUTTON_FIRE)) {
                pressedThisFrame.put(Action.ATTACK, true);
            }
            if (mappedController.isButtonPressed(BUTTON_POWER_ATTACK)) {
                pressedThisFrame.put(Action.POWER_ATTACK, true);
            }
            float xDir = mappedController.getConfiguredAxisValue(AXIS_HORIZONTAL);
            if (Math.abs(xDir) > CONTROLLER_DEADZONE) {
                moveX += xDir;
            }
            moveX += mappedController.getConfiguredAxisValue(D_PAD_AXIS);
        }


    }

    public boolean actionJustPressed(Action action) {
        return pressedThisFrame.get(action) && !pressedLastFrame.get(action);
    }

    public boolean actionPressed(Action action) {
        return pressedThisFrame.get(action);
    }

    public float getWalkAmount() {
        return moveX;
    }
}
