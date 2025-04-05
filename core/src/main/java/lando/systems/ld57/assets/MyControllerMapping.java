package lando.systems.ld57.assets;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import de.golfgl.gdx.controllers.mapping.ConfiguredInput;
import de.golfgl.gdx.controllers.mapping.ControllerMappings;

public class MyControllerMapping extends ControllerMappings {
    public static final int BUTTON_JUMP = 0;
    public static final int BUTTON_FIRE = 1;
    public static final int AXIS_VERTICAL = 2;
    public static final int AXIS_HORIZONTAL = 3;
    public static final int D_PAD_AXIS = 4;
    public static final int BUTTON_START = 5;
    public static final int BUTTON_CANCEL = 6;

    public MyControllerMapping() {
        super();

        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BUTTON_JUMP));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BUTTON_FIRE));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BUTTON_START));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, BUTTON_CANCEL));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axis, AXIS_VERTICAL));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axis, AXIS_HORIZONTAL));
        addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axis, D_PAD_AXIS));


        commitConfig();
    }

    @Override
    public boolean getDefaultMapping(MappedInputs defaultMapping, Controller controller) {
        boolean web = Gdx.app.getType() == Application.ApplicationType.WebGL;

        defaultMapping.putMapping(new MappedInput(AXIS_VERTICAL, new ControllerAxis(web ? 1 : 1)));
        defaultMapping.putMapping(new MappedInput(AXIS_HORIZONTAL, new ControllerAxis(web ? 0 : 0)));
        defaultMapping.putMapping(new MappedInput(D_PAD_AXIS, new ControllerButton(web ? 14 : 14), new ControllerButton(web ? 15 : 13) ));
        defaultMapping.putMapping(new MappedInput(BUTTON_JUMP, new ControllerButton(web ? 0 : 1)));
        defaultMapping.putMapping(new MappedInput(BUTTON_FIRE, new ControllerButton(web ? 1 : 0)));
        defaultMapping.putMapping(new MappedInput(BUTTON_START, new ControllerButton(web ? 9 : 6)));
        defaultMapping.putMapping(new MappedInput(BUTTON_CANCEL, new ControllerButton(web ? 8 : 4)));

        return true;

    }
}
