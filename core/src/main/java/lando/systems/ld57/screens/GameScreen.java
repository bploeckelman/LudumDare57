package lando.systems.ld57.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import lando.systems.ld57.Config;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.world.ScenePlatformer;
import lando.systems.ld57.world.SceneTest;

import java.util.HashMap;
import java.util.Map;

public class GameScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x131711ff);
    private final Stage stage;

    private Scene<?> scene;

    public GameScreen() {
        this.scene = new ScenePlatformer(this);
        this.stage = new Stage();

        initializeUI();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void update(float dt) {
        handleExit();

        var shouldSkipFrame = handleDebugFlags();
        if (shouldSkipFrame) {
            return;
        }

        scene.update(dt);
        stage.act(dt);

        super.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(backgroundColor);

        var shapes = assets.shapes;
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            scene.render(batch);
            scene.render(shapes);
        }
        batch.end();

        stage.draw();

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            // ... add any 'overlay' rendering here that would go on top of any scene2d ui
        }
        batch.end();
    }

    @Override
    public void initializeUI() {
        ConfigUI.init();

        var table = new VisTable();
        table.setFillParent(true);
        table.align(Align.top);

        var configTable = new VisTable();
        configTable.setBackground("grey");
        configTable.setHeight(40f);
        configTable.setWidth(windowCamera.viewportWidth);
        configTable.align(Align.top);

        configTable.add(new VisTextButton("Switch Scene", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                scene = (scene instanceof ScenePlatformer)
                    ? new SceneTest(GameScreen.this)
                    : new ScenePlatformer(GameScreen.this);
            }
        })).pad(10f).expandX().top();

        ConfigUI.init();
        for (var flag : ConfigUI.checkboxes.keySet()) {
            var checkbox = ConfigUI.checkboxes.get(flag);
            configTable.add(checkbox).pad(10f).expandX().top();
        }

        table.add(configTable);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    private void handleExit() {
        var shouldExit = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
        var shouldQuit = shouldExit && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        if (shouldQuit) {
            Gdx.app.exit();
        } else if (shouldExit && !transitioning) {
            game.setScreen(new TitleScreen());
        }
    }

    private boolean handleDebugFlags() {
        var toggleGlobal = Gdx.input.isKeyJustPressed(Input.Keys.NUM_1);
        if (toggleGlobal) {
            Config.Flag.GLOBAL.toggle();
        }

        var toggleRender = Gdx.input.isKeyJustPressed(Input.Keys.NUM_2);
        if (toggleRender) {
            Config.Flag.LOG.toggle();
        }

        var toggleUI = Gdx.input.isKeyJustPressed(Input.Keys.NUM_3);
        if (toggleUI) {
            Config.Flag.UI.toggle();
        }

        var toggleLog = Gdx.input.isKeyJustPressed(Input.Keys.NUM_4);
        if (toggleLog) {
            Config.Flag.RENDER.toggle();
        }

        var toggleFrameStep = Gdx.input.isKeyJustPressed(Input.Keys.NUM_0);
        if (toggleFrameStep) {
            Config.Flag.FRAME_STEP.toggle();
        }

        if (Config.Flag.FRAME_STEP.isEnabled()) {
            Config.stepped_frame = Gdx.input.isKeyJustPressed(Input.Keys.NUM_9);
            return !Config.stepped_frame;
        }

        stage.setDebugAll(Config.Flag.UI.isEnabled());

        return false;
    }

    private static class ConfigUI {
        static final Map<Config.Flag, VisCheckBox> checkboxes = new HashMap<>();

        static void init() {
            for (var flag : Config.Flag.values()) {
                // this is a launch flag, no need to show it in the UI
                if (Config.Flag.START_ON_GAMESCREEN == flag) {
                    continue;
                }
                var checkbox = new VisCheckBox(flag.name(), "small");
                checkbox.setChecked(flag.isEnabled());
                checkbox.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        flag.toggle();
                    }
                });
                checkboxes.put(flag, checkbox);
            }
        }
    }
}
