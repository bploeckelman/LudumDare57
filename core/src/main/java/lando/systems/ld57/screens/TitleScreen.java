package lando.systems.ld57.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.particles.ParticleManager;

public class TitleScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x121212ff);
    private final TextureRegion logo;
    private final ParticleManager particleManager;

    public TitleScreen() {
        this.logo = assets.atlas.findRegion("libgdx");
        game.audioManager.playMusic(Musics.Type.TEST);
        this.particleManager = new ParticleManager();
    }

    @Override
    public void update(float delta) {
        particleManager.update(delta);
        var shouldExit = Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY);
        if (shouldExit && !transitioning) {
            game.setScreen(new GameScreen());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(backgroundColor);

        var camera = windowCamera;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(logo,
            (camera.viewportWidth  - logo.getRegionWidth())  / 2f,
            (camera.viewportHeight - logo.getRegionHeight()) / 2f);
        particleManager.render(batch, ParticleManager.Layer.FOREGROUND);
        batch.end();
    }
}
