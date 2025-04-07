package lando.systems.ld57.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Fonts;
import lando.systems.ld57.assets.Patches;
import lando.systems.ld57.screens.EndingScreen;
import lando.systems.ld57.screens.GameScreen;
import lando.systems.ld57.screens.IntroScreen;

public class TitleScreenUI extends Group {

    private TextButton startGameButton;
    private TextButton creditButton;
    private TextButton settingsButton;
    private final float BUTTON_WIDTH = 220f;
    private final float BUTTON_HEIGHT = 50f;
    private final float BUTTON_PADDING = 10f;

    public TitleScreenUI() {
        SettingsUI settingsUI = new SettingsUI();
        var skin = VisUI.getSkin();
        TextButton.TextButtonStyle titleScreenButtonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        titleScreenButtonStyle.font = Fonts.Type.DOGICA.getDefault();
        titleScreenButtonStyle.fontColor = Color.WHITE;
        titleScreenButtonStyle.up = Patches.Type.PLAIN.getDrawable();
        titleScreenButtonStyle.down = Patches.Type.PLAIN_DIM.getDrawable();
        titleScreenButtonStyle.over = Patches.Type.PLAIN_DIM.getDrawable();

        float left = Main.game.windowCamera.viewportWidth * .8f;
        float top = Main.game.windowCamera.viewportHeight * (1f / 4f);

        startGameButton = new TextButton("Start Game", titleScreenButtonStyle);
        startGameButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        startGameButton.setPosition(left, top);
        startGameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.game.currentScreen.transitioning = true;
                Gdx.input.setInputProcessor(null);
                Main.game.setScreen(new IntroScreen());
            }
        });

        settingsButton = new TextButton("Settings", titleScreenButtonStyle);
        settingsButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        settingsButton.setPosition(left, startGameButton.getY() - startGameButton.getHeight() - BUTTON_PADDING);
        settingsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsUI.showSettings();
            }
        });


        creditButton = new TextButton("Credits", titleScreenButtonStyle);
        creditButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        creditButton.setPosition(left, settingsButton.getY() - settingsButton.getHeight() - BUTTON_PADDING);
        creditButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(null);
                Main.game.currentScreen.transitioning = true;
                Main.game.setScreen(new EndingScreen());
            }
        });


        addActor(startGameButton);
        addActor(settingsButton);
        addActor(creditButton);
        addActor(settingsUI);
    }
}
