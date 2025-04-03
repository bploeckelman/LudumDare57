package lando.systems.ld57.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import lando.systems.ld57.Config;
import lando.systems.ld57.assets.Icons;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.utils.Util;
import lando.systems.ld57.world.ScenePlatformer;
import lando.systems.ld57.world.SceneTest;

public class GameScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x131711ff);
    private final Stage stage;

    private Scene<?> scene;

    public GameScreen() {
        this.scene = new ScenePlatformer(this);
        this.stage = new Stage();

        var table = new VisTable();
        table.setFillParent(true);
        table.align(Align.top);
        table.add(new VisTextButton("Switch Scene", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                scene = (scene instanceof ScenePlatformer)
                    ? new SceneTest(GameScreen.this)
                    : new ScenePlatformer(GameScreen.this);
            }
        })).pad(10f).expandX().top().center();

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
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
            if (Config.Flag.GLOBAL.isEnabled()) {
                renderConfigFlagIcons();
            }
        }
        batch.end();
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
            Config.Flag.RENDER.toggle();
        }

        var toggleUI = Gdx.input.isKeyJustPressed(Input.Keys.NUM_3);
        if (toggleUI) {
            Config.Flag.UI.toggle();
        }

        var toggleLog = Gdx.input.isKeyJustPressed(Input.Keys.NUM_4);
        if (toggleLog) {
            Config.Flag.UI.toggle();
        }

        var toggleFrameStep = Gdx.input.isKeyJustPressed(Input.Keys.NUM_0);
        if (toggleFrameStep) {
            Config.Flag.FRAME_STEP.toggle();
        }

        if (Config.Flag.FRAME_STEP.isEnabled()) {
            Config.stepped_frame = Gdx.input.isKeyJustPressed(Input.Keys.NUM_9);
            return !Config.stepped_frame;
        }
        return false;
    }

    // TODO(brian): handle these as checkboxes in the ui instead
    private void renderConfigFlagIcons() {
        float size = 32f;
        float margin = 20f;
        float x = 0;
        float y = windowCamera.viewportHeight - margin - size;

        Color iconTint;
        Icons.Type iconType;
        TextureRegion icon;

        var rect = Util.rect.obtain();
        if (Config.Flag.FRAME_STEP.isEnabled()) {
            x += margin + size;
            rect.set(x, y, size, size);

            iconTint = Config.stepped_frame ? Color.LIME : Color.ORANGE;
            iconType = Config.stepped_frame ? Icons.Type.PERSON_PLAY : Icons.Type.PERSON_X;
            icon = iconType.get();
            Util.draw(batch, icon, rect, iconTint);
        } else {
            x += margin + size;
            rect.set(x, y, size, size);

            iconTint = Color.LIME;
            iconType = Icons.Type.PERSON_PLAY;
            icon = iconType.get();
            Util.draw(batch, icon, rect, iconTint);
        }
        if (Config.Flag.RENDER.isEnabled()) {
            x += margin + size;
            rect.set(x, y, size, size);

            iconTint = Color.SCARLET;
            iconType = Icons.Type.CARD_STACK;
            icon = iconType.get();
            Util.draw(batch, icon, rect, iconTint);
        }
        if (Config.Flag.UI.isEnabled()) {
            x += margin + size;
            rect.set(x, y, size, size);

            iconTint = Color.CYAN;
            iconType = Icons.Type.PUZZLE;
            icon = iconType.get();
            Util.draw(batch, icon, rect, iconTint);
        }
        if (Config.Flag.LOG.isEnabled()) {
            x += margin + size;
            rect.set(x, y, size, size);

            iconTint = Color.GOLDENROD;
            iconType = Icons.Type.NOTEPAD;
            icon = iconType.get();
            Util.draw(batch, icon, rect, iconTint);
        }
        Util.free(rect);
    }
}
