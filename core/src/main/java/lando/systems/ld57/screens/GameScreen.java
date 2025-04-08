package lando.systems.ld57.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
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
import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.assets.Fonts;
import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.assets.Patches;
import lando.systems.ld57.particles.ParticleManager;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.scenes.SceneBoss;
import lando.systems.ld57.scene.scenes.SceneCastlevania;
import lando.systems.ld57.scene.scenes.SceneIntro;
import lando.systems.ld57.scene.scenes.SceneMario;
import lando.systems.ld57.scene.scenes.SceneMegaman;
import lando.systems.ld57.scene.scenes.SceneZelda;
import lando.systems.ld57.ui.Meter;

import java.util.HashMap;
import java.util.Map;

public class GameScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x131711ff);
    private final Stage stageDebugUI;
    private TextraLabel posLabel;

    private Vector3 screenPos;
    private Scene<?> scene;
    private VisTextButton switchSceneButton;

    public Meter playerHealthMeter;
    public Meter bossHealthMeter;

    public Musics.Type music;
    private final float MODAL_WIDTH = 640f;
    private final float MODAL_HEIGHT = 480f;
    private final Rectangle modal = new Rectangle(Config.window_width / 2f  - MODAL_WIDTH / 2f, Config.window_height / 2f - MODAL_HEIGHT / 2f, MODAL_WIDTH, MODAL_HEIGHT);
    public String modalText = "You defeated Mario! Now you can switch to him.";
    public boolean showModal = false;

    public GameScreen() {
        this.playerHealthMeter = Meter.forPlayer(assets,
            10f, 40f,
            20f, windowCamera.viewportHeight - 80f);
        this.bossHealthMeter = Meter.forBoss(assets,
            40f, windowCamera.viewportHeight - 50f,
            windowCamera.viewportWidth - 80f, 40f);

        this.scene = new SceneMario(this);
        this.stageDebugUI = new Stage();
        this.screenPos = new Vector3();
        initializeUI();
    }

    @Override
    public void dispose() {
        stageDebugUI.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stageDebugUI.getViewport().update(width, height, true);
    }

    @Override
    public void init() {
        showModal = false;
    }

    @Override
    public void update(float dt) {
        handleExit();
        if (showModal) {
            if (scene instanceof SceneIntro) {
                modalText="You defeated Intro! Now you can switch to him.";
            }
            else if (scene instanceof SceneMario) {
                modalText="It's a-you, Mario! \n\nYou can now change characters with Q and E!";
            }
            else if (scene instanceof SceneZelda) {
                modalText="Well excuuuse me, Link! \n\nLink is now available Q and E.";
            }
            else if (scene instanceof SceneCastlevania) {
                modalText="Belmont Stake-d! \n\nHe is now available via Q and E.";
            }
            else if (scene instanceof SceneMegaman) {
                modalText="Megaman is mega-dead!\n\nSelect him with Q and E.";
            }
            else if (scene instanceof SceneBoss) {
                Main.game.setScreen(new CreditsScreen());
            }
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (scene instanceof SceneIntro) {
                    scene = new SceneMario(GameScreen.this);
                }
                else if (scene instanceof SceneMario) {
                    scene = new SceneZelda(GameScreen.this);
                }
                else if (scene instanceof SceneZelda) {
                    scene = new SceneCastlevania(GameScreen.this);
                }
                else if (scene instanceof SceneCastlevania) {
                    scene = new SceneMegaman(GameScreen.this);
                }
                else if (scene instanceof SceneMegaman) {
                    scene = new SceneBoss(GameScreen.this);
                }
                else if (scene instanceof SceneBoss) {
                    Main.game.setScreen(new CreditsScreen());
                }
            }
            return;
        }

        var shouldSkipFrame = handleDebugFlags();
        if (shouldSkipFrame) {
            return;
        }

        screenPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        worldCamera.unproject(screenPos);

        posLabel.setText("Mouse: "+ (int)screenPos.x +", " + (int)screenPos.y);
        var currentScene = scene.getClass().getSimpleName().replace("Scene", "");
        switchSceneButton.setText("Switch Scene (" + currentScene + ")");

        scene.update(dt);
//        stageDebugUI.act(dt);
        particleManager.update(dt);

        playerHealthMeter.update(dt);
        if (bossHealthMeter != null) {
            bossHealthMeter.update(dt);
        }

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

        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
//            playerHealthMeter.render(batch);
            // TODO(brian): player energy
            bossHealthMeter.render(batch);

            if (showModal) {
                Patches.Type.PLAIN.get().draw(batch, modal.x, modal.y, modal.width, modal.height);
                var font = Fonts.Type.ROUNDABOUT.getDefault();
                font.getData().setScale(2f);
                var layout = Main.game.assets.layout;
                layout.setText(font, modalText, Color.WHITE, modal.width - 30f, Align.center, true);
                font.draw(batch, layout, modal.x + 15f, modal.y + modal.height / 2f + layout.height/2f);
                font.getData().setScale(1f);
            }
        }
        batch.end();

//        stageDebugUI.draw();
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
                else if (scene instanceof SceneMario)       scene = new SceneZelda(GameScreen.this);
                else if (scene instanceof SceneZelda)       scene = new SceneCastlevania(GameScreen.this);
                else if (scene instanceof SceneCastlevania) scene = new SceneMegaman(GameScreen.this);
                else if (scene instanceof SceneMegaman)     scene = new SceneBoss(GameScreen.this);
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

        stageDebugUI.addActor(table);
        Gdx.input.setInputProcessor(stageDebugUI);
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

        stageDebugUI.setDebugAll(Config.Flag.UI.isEnabled());

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
