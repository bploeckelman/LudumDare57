package lando.systems.ld57.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.TextraLabel;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import lando.systems.ld57.Config;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.particles.ParticleManager;
import lando.systems.ld57.particles.effects.*;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.scenes.SceneBoss;
import lando.systems.ld57.scene.scenes.SceneCastlevania;
import lando.systems.ld57.scene.scenes.SceneIntro;
import lando.systems.ld57.scene.scenes.SceneMario;
import lando.systems.ld57.scene.scenes.SceneMegaman;
import lando.systems.ld57.scene.scenes.SceneZelda;
import lando.systems.ld57.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class GameScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x131711ff);
    private final Stage stage;
    private TextraLabel posLabel;

    private Vector3 screenPos;
    private Scene<?> scene;
    private VisTextButton switchSceneButton;

    public Musics.Type music;

    public GameScreen() {
        this.scene = new SceneIntro(this);
        this.stage = new Stage();
        this.screenPos = new Vector3();
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
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            particleManager.spawn(ParticleEffect.Type.SHAPE, new ShapeEffect.Params(screenPos.x, screenPos.y, Util.randomColor()));
//            particleManager.spawn(ParticleEffect.Type.BLOOD, new BloodEffect.Params(screenPos.x, screenPos.y));
//            particleManager.spawn(ParticleEffect.Type.BLOOD_SPLAT, new BloodSplatEffect.Params(screenPos.x, screenPos.y));
//            particleManager.spawn(ParticleEffect.Type.BLOOD_FOUNTAIN, new BloodFountainEffect.Params(screenPos.x, screenPos.y));
        }

        screenPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        worldCamera.unproject(screenPos);

        posLabel.setText("Mouse: "+ (int)screenPos.x +", " + (int)screenPos.y);
        var currentScene = scene.getClass().getSimpleName().replace("Scene", "");
        switchSceneButton.setText("Switch Scene (" + currentScene + ")");

        scene.update(dt);
        stage.act(dt);
        particleManager.update(dt);

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
            particleManager.render(batch, ParticleManager.Layer.FOREGROUND);
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

        switchSceneButton = new VisTextButton("Switch Scene", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if      (scene instanceof SceneIntro)       scene = new SceneMario(GameScreen.this);
                else if (scene instanceof SceneMario)       scene = new SceneMegaman(GameScreen.this);
                else if (scene instanceof SceneMegaman)     scene = new SceneZelda(GameScreen.this);
                else if (scene instanceof SceneZelda)       scene = new SceneCastlevania(GameScreen.this);
                else if (scene instanceof SceneCastlevania) scene = new SceneBoss(GameScreen.this);
                else if (scene instanceof SceneBoss)        scene = new SceneIntro(GameScreen.this);

                if      (scene instanceof SceneIntro)       game.audioManager.playMusic(SceneIntro.music);
                else if (scene instanceof SceneMario)       game.audioManager.playMusic(SceneMario.music);
                else if (scene instanceof SceneMegaman)     game.audioManager.playMusic(SceneMegaman.music);
                else if (scene instanceof SceneZelda)       game.audioManager.playMusic(SceneZelda.music);
                else if (scene instanceof SceneCastlevania) game.audioManager.playMusic(SceneCastlevania.music);
                else if (scene instanceof SceneBoss)        game.audioManager.playMusic(SceneBoss.music);
            }
        });
        configTable.add(switchSceneButton).pad(10f).expandX().top();

        ConfigUI.init();
        for (var flag : ConfigUI.checkboxes.keySet()) {
            var checkbox = ConfigUI.checkboxes.get(flag);
            configTable.add(checkbox).pad(10f).expandX().top();
        }

        posLabel = new TextraLabel();
        posLabel.setText("Mouse:");
        configTable.add(posLabel).pad(10f).expandX().top();

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
