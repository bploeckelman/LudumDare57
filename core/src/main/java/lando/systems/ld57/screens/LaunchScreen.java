package lando.systems.ld57.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Fonts;

public class LaunchScreen extends BaseScreen {

    @Override
    public void update(float dt) {
        if (!transitioning && Gdx.input.justTouched()){
            transitioning = true;
            Main.game.setScreen(new TitleScreen());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.LIGHT_GRAY);

        var camera = windowCamera;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            var font = Fonts.Type.ROUNDABOUT.getDefault();
            font.getData().setScale(1f);
            assets.layout.setText(font, "Click to Begin", Color.WHITE, camera.viewportWidth, Align.center, false);
            font.draw(batch, assets.layout, 0, camera.viewportHeight / 2f + assets.layout.height);
            font.getData().setScale(1f);
        }
        batch.end();
    }
}
