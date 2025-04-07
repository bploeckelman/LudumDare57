package lando.systems.ld57.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.particles.ParticleManager;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.particles.effects.ShapeEffect;
import lando.systems.ld57.ui.TitleScreenUI;

public class TitleScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x121212ff);
    private final TextureRegion logo;
    private final TitleScreenUI titleScreenUI = new TitleScreenUI();
    private final Stage uiStage = new Stage();

    public TitleScreen() {
        this.logo = assets.atlas.findRegion("libgdx");
        game.audioManager.playMusic(Musics.Type.SHOW);
        initializeUI();
    }

    @Override
    public void initializeUI() {
         uiStage.addActor(titleScreenUI);
         Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            var mousePos = vec3.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            worldCamera.unproject(mousePos);
            particleManager.spawn(ParticleEffect.Type.SHAPE, new ShapeEffect.Params(mousePos.x, mousePos.y, Color.WHITE));
        }
        particleManager.update(delta);
        uiStage.act(delta);
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
        uiStage.draw();
    }
}
