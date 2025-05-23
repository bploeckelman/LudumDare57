package lando.systems.ld57.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import lando.systems.ld57.Config;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Assets;
import lando.systems.ld57.assets.ScreenTransitions;
import lando.systems.ld57.utils.Time;

public class Transition {

    private static class FrameBufferObjects {
        final FrameBuffer original;
        final FrameBuffer transition;
        final Texture originalTexture;
        final Texture transitionTexture;

        FrameBufferObjects() {
            var format = Pixmap.Format.RGB888;
            var width = Config.window_width;
            var height = Config.window_height;
            original = new FrameBuffer(format, width, height, false);
            transition = new FrameBuffer(format, width, height, false);
            originalTexture = original.getColorBufferTexture();
            transitionTexture = transition.getColorBufferTexture();
        }
    }

    private static FrameBufferObjects fbo;
    private static ShaderProgram shader;
    private static BaseScreen current;
    private static BaseScreen next;
    private static float percent;
    private static boolean instant;

    public static void init(Assets assets) {
        fbo = new FrameBufferObjects();
        shader = ScreenTransitions.Type.random().get();
        current = null;
        next = null;

        // NOTE: must be 1 on construction to indicate that there's not a transition in progress
        percent = 1;
    }

    public static boolean inProgress() {
        return percent < 1;
    }

    public static void to(BaseScreen newScreen, ScreenTransitions.Type type, boolean immediate) {
        if (inProgress()) return;

        percent = 0;
        instant = immediate;
        next = newScreen;
        current = Main.game.currentScreen;

        if (type == null) {
            type = ScreenTransitions.Type.random();
        }
        shader = type.get();

        next.transitioning = true;
        current.transitioning = true;
    }

    public static void update(float dt) {
        if (!inProgress()) return;

        if (instant) {
            percent = 1;
        } else {
            percent += dt;
        }

        if (percent >= 1) {
            percent = 1;

            next.transitioning = false;
            current.transitioning = false;

            current = next;
            next = null;

            Main.game.currentScreen = current;
        }
    }

    public static void render(SpriteBatch batch) {
        // update transition between current and next screens
        next.update(Time.delta);
        next.renderOffscreenBuffers(batch);

        // render next screen to a buffer
        fbo.transition.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        next.render(batch);
        fbo.transition.end();

        // render current screen to a buffer
        fbo.original.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Main.game.currentScreen.render(batch);
        fbo.original.end();

        // combine next and current screen buffers with the transition shader, drawing into on-screen buffer
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setShader(shader);
        {
            var camera = Main.game.windowCamera;
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            {
                fbo.originalTexture.bind(1);
                fbo.transitionTexture.bind(0);

                shader.setUniformi("u_texture1", 1);
                shader.setUniformf("u_percent", percent);

                batch.setColor(Color.WHITE);
                batch.draw(fbo.transitionTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
            }
            batch.end();
        }
        batch.setShader(null);
    }
}
