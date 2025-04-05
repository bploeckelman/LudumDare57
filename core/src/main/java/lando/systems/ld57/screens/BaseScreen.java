package lando.systems.ld57.screens;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import lando.systems.ld57.Config;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Assets;
import lando.systems.ld57.scene.Scene;

public abstract class BaseScreen implements Disposable {

    public final Main game;
    public final Assets assets;
    public final SpriteBatch batch;
    public final TweenManager tween;
    public final OrthographicCamera windowCamera;

    public Scene<? extends BaseScreen> scene;

    public OrthographicCamera worldCamera;
    public Vector3 vec3 = new Vector3();
    public boolean transitioning = false;

    public BaseScreen() {
        this.game = Main.game;
        this.assets = game.assets;
        this.batch = game.assets.batch;
        this.tween = game.tween;
        this.windowCamera = game.windowCamera;

        this.worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho(false, Config.framebuffer_width, Config.framebuffer_height);
        worldCamera.update();
    }

    @Override
    public void dispose() {}

    public void resize(int width, int height) {}

    public void alwaysUpdate(float delta) {
        game.audioManager.update(delta);
    }

    public void update(float delta) {
        windowCamera.update();
        if (worldCamera != null) {
            worldCamera.update();
        }
    }

    public abstract void render(SpriteBatch batch);

    public void renderOffscreenBuffers(SpriteBatch batch) {}

    public void initializeUI() {}
}
