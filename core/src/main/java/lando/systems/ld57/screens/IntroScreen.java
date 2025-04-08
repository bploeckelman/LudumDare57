package lando.systems.ld57.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.TypingLabel;
import lando.systems.ld57.Config;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Fonts;
import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.assets.ScreenTransitions;
import lando.systems.ld57.assets.Sounds;
import lando.systems.ld57.particles.ParticleManager;

public class IntroScreen extends BaseScreen {

    Texture backgroundTexture;
    Texture parchmentTexture;
    BitmapFont font;
    ParticleManager particles;
    Rectangle skipButton = new Rectangle(Gdx.graphics.getWidth() - 200, 20, 180, 80);
    long currentSoundId;
    String page1 =
        "{COLOR=white}" +
//            "--------------------------------------------------" +
            "Life comes at ya fast, ya know? \n\n" +

            "When you're a kid, no one wants anything from ya " +
            "except to sit down and be quiet \n\n" +

            "Nothing but junk food and video games, all day every day\n\n" +

            "Sure, the games made you wanna pull your hair out, " +
            "what with the punishing difficulty level and near total lack " +
            "of save points. But times were simpler then, ya know?\n\n" +

            "Ain't got much hair left to pull out these days...\n\n" +

            "Instead of junk food, it's heartburn and bills\n\n" +

            "Easy to get lost in the {GRADIENT=black;gray}depths{ENDGRADIENT} of nostalgia, " +
            "but it feels like things are never quite how you remember them..."
            ;


//    String page2 =
//        "{COLOR=white}" +
//            "For many of these diminutive animals, the best defense "+
//            "is a good offense." +
//            "And the destruction is the main tool in their toolbox. \n\n" +
//            "Let us see what kind of tricks these GRADIENT=black;gray}tiny creatures{ENDGRADIENT}" +
//            " have up their sleeves as they set out on their tiny rampage!";
//
//    String page3 =
//        "{COLOR=white}" +
//            "Exact revenge! Raze their buildings, exter{GRADIENT=black;gray}mini{ENDGRADIENT}ate them!";

    int currentPage = 0;
    float elapsedTime = 0f;
    TypingLabel typingLabel;
    float transitionAlpha = 0f;

    public IntroScreen() {
//        backgroundTexture = assets.introBackground;
//        parchmentTexture = assets.parchment;
        font = Fonts.Type.DOGICA.getDefault();

//        Main.game.audioManager.playMusic(Musics.Type.SHOW);

        particles = Main.game.particleManager;

        typingLabel = new TypingLabel(page1, new Font(font));
        typingLabel.setPosition(worldCamera.viewportWidth * .1f,
            worldCamera.viewportHeight * .5f);
        typingLabel.setWidth(Config.window_width * .8f);
        typingLabel.wrap = true;
        typingLabel.setScale(.6f);
        typingLabel.getFont().adjustLineHeight(2f);
        currentSoundId = Main.game.audioManager.playSound(Sounds.Type.JUMP);
    }

    @Override
    public void alwaysUpdate(float delta) {

    }

    @Override
    public void update(float dt) {
        elapsedTime += dt;
        if (Gdx.input.justTouched() && elapsedTime > .5f) {
            elapsedTime = 0;
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            worldCamera.unproject(touchPos);
//            particles.levelUpEffect(touchPos.x, touchPos.y);

            if (transitionAlpha < 1f) {
                transitionAlpha = 1f;
            } else if (!typingLabel.hasEnded()) {
                typingLabel.skipToTheEnd();
            } else {
                launchGame();
//                currentPage++;
//                if (currentPage == 1) {
//                    typingLabel.restart(page2);
//                } else if (currentPage == 2) {
//                    typingLabel.restart(page3);
                }
//                else if (currentPage == 3) {
//                    typingLabel.restart(page4);
//                }
//                else {
//                    launchGame();
//                }
//            }
        }
        particles.update(dt);
        if (transitionAlpha < 1f) {
            transitionAlpha = MathUtils.clamp(transitionAlpha + dt, 0f, 1f);
        } else {
            typingLabel.act(dt);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(.0f, .0f, .1f, 1f);

        batch.enableBlending();
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, transitionAlpha);
        //batch.draw(backgroundTexture, 0, 0, windowCamera.viewportWidth, windowCamera.viewportHeight);

        // Center parchment calculation (adjust offsets if needed)
        //batch.draw(parchmentTexture, windowCamera.viewportWidth * .1f, windowCamera.viewportHeight * .1f, windowCamera.viewportWidth * .9f, windowCamera.viewportHeight * .9f * transitionAlpha);

        typingLabel.draw(batch, 1f);

        particles.render(batch, ParticleManager.Layer.FOREGROUND);

//        Patches.get(Patches.Type.PLAIN).draw(batch, skipButton.x, skipButton.y, skipButton.width, skipButton.height);
        batch.end();
    }

    void launchGame() {
        if (!transitioning){
            transitioning = true;
            game.setScreen(new GameScreen(), ScreenTransitions.Type.DOOMDRIP, true);
        }
    }
}
