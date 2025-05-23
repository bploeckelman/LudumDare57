package lando.systems.ld57.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.TypingLabel;
import lando.systems.ld57.Config;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Fonts;
import lando.systems.ld57.assets.Patches;
import lando.systems.ld57.ui.Button;

public class CreditsScreen extends BaseScreen {

    private final TypingLabel titleLabel;
    private final TypingLabel themeLabel;
    private final TypingLabel leftCreditLabel;
    private final TypingLabel rightCreditLabel;
    private final TypingLabel thanksLabel;
    private final TypingLabel disclaimerLabel;

//    private final Animation<TextureRegion> catAnimation;
//    private final Animation<TextureRegion> dogAnimation;
//    private final Animation<TextureRegion> kittenAnimation;
    private final Texture background;

    private final String title = "{GRADIENT=red;gray}Captain Nostalgia{ENDGRADIENT}";
    private final String theme = "{GRADIENT=red;gray}Made for Ludum Dare 57: Depth{ENDGRADIENT}";

    private final String thanks = "{GRADIENT=red;gray}Thank you for playing our game!{ENDGRADIENT}";
    private final String developers = "{COLOR=gray}Developed by:{COLOR=white}\n {GRADIENT=white;gray}Brian Ploeckelman (44){ENDGRADIENT}\n {GRADIENT=white;gray}Doug Graham (43){ENDGRADIENT}\n {GRADIENT=white;gray}Jeffrey Hwang (36){ENDGRADIENT}\n {GRADIENT=white;gray}Brian Rossman (48){ENDGRADIENT}\n {GRADIENT=white;gray}Patrick Harvey (34){ENDGRADIENT}\n {GRADIENT=white;gray}Clayton Mays (33){ENDGRADIENT}\n {GRADIENT=white;gray}Melissa Erman (22){ENDGRADIENT}";
    private final String artists = "{COLOR=gray}Art by:{COLOR=white}\n {GRADIENT=white;gray}Matt Neumann (43){ENDGRADIENT}\n {GRADIENT=white;gray}Luke Bain (43){ENDGRADIENT}\n";
    private final String emotionalSupport = "{COLOR=cyan}Emotional Support:{COLOR=white}\n Asuka(10), Osha(9), Cherry (8), \n Obi (6), Yoda (8), Nova (3), and Roxie (2)";
    private final String music = "{COLOR=gray}Music, Design and Narration:{COLOR=white}\n " +
        "{GRADIENT=white;gray}Pete V (41){ENDGRADIENT}\n";
    private final String libgdx = "Made with {COLOR=red}<3{COLOR=white}\nand {RAINBOW}LibGDX (15){ENDRAINBOW}";
    private final String disclaimer = "{GRADIENT=black;gray}Disclaimer:{ENDGRADIENT}  {GRADIENT=gold;yellow}{JUMP=.27} No donuts were creamed in the making of this game{ENDJUMP}{ENDGRADIENT}";

    private float accum = 0f;
    private boolean showPets = false;

    private Button afterCreditsButton;

    public CreditsScreen() {
        super();

        var extraLargeFont = Fonts.Type.ROUNDABOUT.getVariant("extra-large");
        var extraLargeTypingFont = new Font(extraLargeFont);
        var largeFont = Fonts.Type.ROUNDABOUT.getVariant("large");
        var largeTypingFont = new Font(largeFont);
        var font = Fonts.Type.ROUNDABOUT.getVariant("medium");
        var typingFont = new Font(font);
        var smallFont = Fonts.Type.ROUNDABOUT.getVariant("small");
        var smallTypingFont = new Font(smallFont);

        font.setColor(Color.WHITE);

        titleLabel = new TypingLabel(title, extraLargeTypingFont);
        titleLabel.setPosition(0f, Config.window_height - 90f);
        titleLabel.setWidth(Config.window_width);
        titleLabel.setAlignment(Align.center);
        titleLabel.setScale(3f);

        themeLabel = new TypingLabel(theme, largeTypingFont);
        themeLabel.setWidth(Config.window_width);
        themeLabel.setPosition(0f, Config.window_height - 180f);
        themeLabel.setAlignment(Align.center);

        leftCreditLabel = new TypingLabel(developers.toLowerCase() + "\n\n" + emotionalSupport.toLowerCase() + "\n\n", typingFont);
        leftCreditLabel.setWidth(Config.window_width / 2f - 150f);
        leftCreditLabel.setPosition(75f, Config.window_height / 2f - 70f);

        background = Main.game.assets.titleScreen;

        rightCreditLabel = new TypingLabel(artists.toLowerCase() + "\n" + music.toLowerCase() + "\n" + libgdx.toLowerCase(), typingFont);
        rightCreditLabel.setPosition(Config.window_width / 2 + 75f, Config.window_height / 2f);
        rightCreditLabel.setWidth(Config.window_width / 2f - 150f);

        thanksLabel = new TypingLabel(thanks, typingFont);
        thanksLabel.setWidth(Config.window_width);
        thanksLabel.setPosition(0f, 105f);
        thanksLabel.setAlignment(Align.center);

        disclaimerLabel = new TypingLabel(disclaimer, smallTypingFont);
        disclaimerLabel.setPosition(0f, 75f);
        disclaimerLabel.setWidth(Config.window_width);
        disclaimerLabel.setAlignment(Align.center);

        var bounds = new Rectangle((windowCamera.viewportWidth /3), 10, (windowCamera.viewportWidth /3), 50);
        afterCreditsButton = new Button(bounds, "Done", Patches.Type.PLAIN.get(), Patches.Type.PLAIN_DIM.get(), font);
        afterCreditsButton.setOnClickAction(() -> {
            if (transitioning) return;
            game.setScreen(new TitleScreen());
            transitioning = true;
        });
    }

//    @Override
//    public void alwaysUpdate(float delta) {

//    }

    @Override
    public void update(float dt) {
        if (transitioning) { return; }
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            windowCamera.unproject(mousePos);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isTouched()) {
            if (!transitioning && afterCreditsButton.getBounds().contains(mousePos.x, mousePos.y)) {
               afterCreditsButton.onClick();
                transitioning = true;
               return;
            }
            var allDone = titleLabel.hasEnded() && themeLabel.hasEnded() && leftCreditLabel.hasEnded() && rightCreditLabel.hasEnded() && thanksLabel.hasEnded() && disclaimerLabel.hasEnded();
            Gdx.app.log("CreditScreen", "allDone: " + allDone);
            if (!allDone && accum > 1) {
                titleLabel.skipToTheEnd();
                themeLabel.skipToTheEnd();
                leftCreditLabel.skipToTheEnd();
                rightCreditLabel.skipToTheEnd();
                thanksLabel.skipToTheEnd();
                disclaimerLabel.skipToTheEnd();
                showPets = true;
                return;
            }
        }
        accum += dt;
        titleLabel.act(dt);
        themeLabel.act(dt);
        leftCreditLabel.act(dt);
        rightCreditLabel.act(dt);
        thanksLabel.act(dt);
        disclaimerLabel.act(dt);
        afterCreditsButton.update(mousePos.x, mousePos.y);
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(.0f, .0f, .1f, 1f);

        batch.enableBlending();
        batch.setProjectionMatrix(windowCamera.combined);
        batch.begin();
        {
            batch.setColor(.5f, .5f, .5f, .9f);
            batch.draw(background, 0, 0, Config.window_width, Config.window_height);

            batch.setColor(0f, 0f, 0f, .6f);
            batch.draw(assets.pixelRegion, 25f, 130f, Config.window_width / 2f - 50f, 400f);
            batch.draw(assets.pixelRegion, Config.window_width / 2f + 25f, 130f, Config.window_width / 2f - 50f, 400f);

            batch.setColor(Color.WHITE);
            titleLabel.draw(batch, 1f);
            themeLabel.draw(batch, 1f);
            leftCreditLabel.draw(batch, 1f);
            rightCreditLabel.draw(batch, 1f);
            thanksLabel.draw(batch, 1f);
            disclaimerLabel.draw(batch, 1f);
            if (accum > 7.5 || showPets) {
//                TextureRegion cherryTexture = assets.cherry.getKeyFrame(accum);
//                TextureRegion asukaTexture = assets.asuka.getKeyFrame(accum);
//                TextureRegion oshaTexture = assets.osha.getKeyFrame(accum);
//                batch.draw(oshaTexture, 450f, 175f);
//                batch.draw(asukaTexture, 500f, 175f);
//                batch.draw(cherryTexture, 550f, 175f);
            }
            if (accum > 8.5 || showPets) {
//                TextureRegion obiTexture = assets.obi.getKeyFrame(accum);
//                TextureRegion yodaTexture = assets.yoda.getKeyFrame(accum);
//                batch.draw(obiTexture, 475f, 125f);
//                batch.draw(yodaTexture, 525f, 125f);
            }
            batch.setColor(Color.WHITE);
            afterCreditsButton.draw(batch);
        }
        batch.end();
    }

}
