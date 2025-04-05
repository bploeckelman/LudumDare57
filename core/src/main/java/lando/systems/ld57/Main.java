package lando.systems.ld57;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import lando.systems.ld57.assets.Assets;
import lando.systems.ld57.assets.ScreenTransitions;
import lando.systems.ld57.audio.AudioManager;
import lando.systems.ld57.screens.BaseScreen;
import lando.systems.ld57.screens.GameScreen;
import lando.systems.ld57.screens.TitleScreen;
import lando.systems.ld57.screens.Transition;
import lando.systems.ld57.utils.Time;
import lando.systems.ld57.utils.accessors.CameraAccessor;
import lando.systems.ld57.utils.accessors.CircleAccessor;
import lando.systems.ld57.utils.accessors.ColorAccessor;
import lando.systems.ld57.utils.accessors.PerspectiveCameraAccessor;
import lando.systems.ld57.utils.accessors.RectangleAccessor;
import lando.systems.ld57.utils.accessors.Vector2Accessor;
import lando.systems.ld57.utils.accessors.Vector3Accessor;

public class Main extends ApplicationAdapter {

    public static Main game;

    public Assets assets;
    public TweenManager tween;
    public FrameBuffer frameBuffer;
    public TextureRegion frameBufferRegion;
    public OrthographicCamera windowCamera;
    public AudioManager audioManager;

    public BaseScreen currentScreen;

    public Main() {
        Main.game = this;
    }

    @Override
    public void create() {
        Time.init();

        assets = new Assets();
        Transition.init(assets);
        VisUI.load(assets.mgr.get("ui/uiskin.json", Skin.class));

        tween = new TweenManager();
        Tween.setWaypointsLimit(4);
        Tween.setCombinedAttributesLimit(4);
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(Circle.class, new CircleAccessor());
        Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());
        Tween.registerAccessor(Vector3.class, new Vector3Accessor());
        Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
        Tween.registerAccessor(PerspectiveCamera.class, new PerspectiveCameraAccessor());

        var format = Pixmap.Format.RGBA8888;
        int width = Config.framebuffer_width;
        int height = Config.framebuffer_height;
        var hasDepth = true;

        frameBuffer = new FrameBuffer(format, width, height, hasDepth);
        var frameBufferTexture = frameBuffer.getColorBufferTexture();
        frameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        frameBufferRegion = new TextureRegion(frameBufferTexture);
        frameBufferRegion.flip(false, true);

        windowCamera = new OrthographicCamera();
        windowCamera.setToOrtho(false, Config.window_width, Config.window_height);
        windowCamera.update();

        audioManager = new AudioManager(assets);

        var startingScreen = Config.Flag.START_ON_GAMESCREEN.isEnabled() ? new GameScreen() : new TitleScreen();
        setScreen(startingScreen);
    }

    public void update(float delta) {
        // update things that must update every tick
        Time.update();
        tween.update(Time.delta);
        currentScreen.alwaysUpdate(Time.delta);
        Transition.update(Time.delta);

        // handle a pause
        if (Time.pause_timer > 0) {
            Time.pause_timer -= Time.delta;
            if (Time.pause_timer <= -0.0001f) {
                Time.delta = -Time.pause_timer;
            } else {
                // skip updates if we're paused
                return;
            }
        }
        Time.millis += Time.delta;
        Time.previous_elapsed = Time.elapsed_millis();

        currentScreen.update(delta);
    }

    @Override
    public void render() {
        update(Time.delta);

        ScreenUtils.clear(Color.DARK_GRAY);
        if (Transition.inProgress()) {
            Transition.render(assets.batch);
        } else {
            currentScreen.renderOffscreenBuffers(assets.batch);
            currentScreen.render(assets.batch);
        }
    }

    public void setScreen(BaseScreen newScreen) {
        setScreen(newScreen, null, false);
    }

    public void setScreen(BaseScreen newScreen, ScreenTransitions.Type transitionType) {
        setScreen(newScreen, transitionType, false);
    }

    public void setScreen(BaseScreen newScreen, ScreenTransitions.Type transitionType, boolean instant) {
        // nothing to transition from, just set the current screen
        if (currentScreen == null) {
            currentScreen = newScreen;
            return;
        }

        // only one transition allowed at a time
        if (Transition.inProgress()) {
            return;
        }

        Transition.to(newScreen, transitionType, instant);
    }
}
