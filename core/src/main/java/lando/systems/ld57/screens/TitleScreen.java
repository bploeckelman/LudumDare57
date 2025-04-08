package lando.systems.ld57.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import lando.systems.ld57.Config;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.assets.Fonts;
import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.particles.ParticleManager;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.particles.effects.ShapeEffect;
import lando.systems.ld57.ui.TitleScreenUI;
import lando.systems.ld57.utils.accessors.Vector2Accessor;

public class TitleScreen extends BaseScreen {

    private final Color backgroundColor = new Color(0x121212ff);
    private final float charScale = 10f;
    private TextureRegion logo;
    private TextureRegion oldGuy;
    private TextureRegion megaMan;
    private TextureRegion mario;
    private TextureRegion zelda;
    private TextureRegion belmont;
    private final TitleScreenUI titleScreenUI = new TitleScreenUI();
    private final Stage uiStage = new Stage();
    private final Texture background;
    private boolean drawUI = false;
    private Vector2 logoPosition = new Vector2(80, -100);
    private Vector2 megaPosition = new Vector2(Config.window_width / 5f - 300f, 750);
    private Vector2 marioPosition = new Vector2(Config.window_width / 5f * 2f - 300f, 750);
    private Vector2 oldGuyPosition = new Vector2(Config.window_width / 5f * 3f - 250f, 750);
    private Vector2 zeldaPosition = new Vector2(Config.window_width / 5f * 4f - 300f, 750);
    private Vector2 castlePosition = new Vector2(Config.window_width / 5f * 5f - 350f, 750);
    float accum = 0f;
    private boolean oldGuyHurt = false;
    float oldGuyAccum = 0f;
    private TextureRegion cartridgeTexture;


    public TitleScreen() {
        background = Main.game.assets.titleScreen;
        logo = Anims.Type.TITLE_LOGO.get().getKeyFrame(0f);
        game.audioManager.playMusic(Musics.Type.MEGAMAN3);
        initializeUI();

        cartridgeTexture = assets.atlas.findRegion("captain-n-ostalgia-nes-cart");

        Timeline.createSequence()
            .delay(.1f)
            .push(Tween.to(logoPosition, Vector2Accessor.Y, 1.5f)
                .target(550).ease(Bounce.OUT))
            .push(Tween.to(megaPosition, Vector2Accessor.Y, .7f)
                .target(100).ease(Elastic.OUT))
            .push(Tween.to(marioPosition, Vector2Accessor.Y, .7f)
                .target(100).ease(Elastic.OUT))
            .push(Tween.to(oldGuyPosition, Vector2Accessor.Y, .7f)
                .target(100).ease(Elastic.OUT))
            .push(Tween.to(zeldaPosition, Vector2Accessor.Y, .7f)
                .target(100).ease(Elastic.OUT))
            .push(Tween.to(castlePosition, Vector2Accessor.Y, .7f)
                .target(100).ease(Elastic.OUT))
            .pushPause(.1f)
            .push(Tween.call((type, source) -> {
                drawUI = true;
            }))
            .start(Main.game.tween);

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
            Main.game.tween.update(100f);
        }
        particleManager.update(delta);
        uiStage.act(delta);
        accum += delta;
        oldGuy = Anims.Type.OLDMAN_IDLE.get().getKeyFrame(accum);
        megaMan = Anims.Type.MEGAMAN_IDLE.get().getKeyFrame(accum);
        mario = Anims.Type.MARIO_IDLE.get().getKeyFrame(accum);
        zelda = Anims.Type.LINK_IDLE.get().getKeyFrame(accum);
        belmont = Anims.Type.BELMONT_IDLE.get().getKeyFrame(accum);
        if (drawUI) {
            oldGuyAccum += delta;
            oldGuy = Anims.Type.OLDMAN_HURT.get().getKeyFrame(oldGuyAccum);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(backgroundColor);

        var camera = windowCamera;
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, windowCamera.viewportWidth, windowCamera.viewportHeight);
        batch.draw(logo, logoPosition.x, logoPosition.y-20, logo.getRegionWidth(), logo.getRegionHeight());
        batch.draw(cartridgeTexture, (camera.viewportWidth - cartridgeTexture.getRegionWidth()) / 2f, 60);
        batch.draw(oldGuy, oldGuyPosition.x, oldGuyPosition.y, oldGuy.getRegionWidth() * charScale, oldGuy.getRegionHeight() * charScale);
        batch.draw(zelda, zeldaPosition.x, zeldaPosition.y, zelda.getRegionWidth() * charScale, zelda.getRegionHeight() * charScale);
        batch.draw(megaMan, megaPosition.x, megaPosition.y, megaMan.getRegionWidth() * charScale, megaMan.getRegionHeight() * charScale);
        batch.draw(mario, marioPosition.x, marioPosition.y, mario.getRegionWidth() * charScale, mario.getRegionHeight() * charScale);
        batch.draw(belmont, castlePosition.x, castlePosition.y, belmont.getRegionWidth() * charScale, belmont.getRegionHeight() * charScale);
        particleManager.render(batch, ParticleManager.Layer.FOREGROUND);
        if (drawUI) {
            var font = Fonts.Type.DOGICA.getDefault();
            var layout = Main.game.assets.layout;
            layout.setText(font, "Ow, my back!");
            font.draw(batch, layout, Config.window_width / 5f * 3f - 180f, 390f);
        }
        batch.end();
        if (drawUI) {
            uiStage.draw();
        }
    }
}
