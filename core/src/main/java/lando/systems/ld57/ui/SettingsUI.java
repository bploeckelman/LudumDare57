package lando.systems.ld57.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisWindow;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.assets.Assets;
import lando.systems.ld57.assets.Patches;
import lando.systems.ld57.assets.Sounds;
import lando.systems.ld57.audio.AudioManager;

public class SettingsUI extends Group {

    private Assets assets;
    private Skin skin;
    public VisWindow settingsWindow;
    public TextButton closeSettingsTextButton;
    public ImageButton closeSettingsButton;
    public VisWindow greyOutWindow;
    private Rectangle settingsPaneBoundsVisible;
    private Rectangle settingsPaneBoundsHidden;
    public boolean isSettingShown;
    public MoveToAction hideSettingsPaneAction;
    public MoveToAction showSettingsPaneAction;
    public MoveToAction showCloseSettingsButtonAction;
    public MoveToAction hideCloseSettingsButtonAction;
    private AudioManager audio;
    private OrthographicCamera windowCamera;

    public SettingsUI() {
        super();
        this.assets = Main.game.assets;
        this.skin = VisUI.getSkin();
        this.audio = Main.game.audioManager;
        this.windowCamera = Main.game.windowCamera;
        initializeUI();

    }

    public void showSettings() {
        showSettingsPaneAction.reset();
        showCloseSettingsButtonAction.reset();
        greyOutWindow.setZIndex(settingsWindow.getZIndex() + 100);
        settingsWindow.setZIndex(settingsWindow.getZIndex() + 100);
        settingsWindow.addAction(showSettingsPaneAction);
        closeSettingsButton.addAction(showCloseSettingsButtonAction);
        greyOutWindow.setVisible(true);
        isSettingShown = true;
    }

    public void hideSettings() {
        hideSettingsPaneAction.reset();
        hideCloseSettingsButtonAction.reset();
        settingsWindow.addAction(hideSettingsPaneAction);
        closeSettingsButton.addAction(hideCloseSettingsButtonAction);
        greyOutWindow.setVisible(false);
        isSettingShown = false;
    }

    public void initializeUI() {
        Window.WindowStyle defaultWindowStyle = skin.get("default", Window.WindowStyle.class);
        Window.WindowStyle glassWindowStyle = new Window.WindowStyle(defaultWindowStyle);
        glassWindowStyle.background = Patches.Type.PLAIN.getDrawable();

        VisSlider.SliderStyle horizontalSliderStyle = skin.get("default-horizontal", VisSlider.SliderStyle.class);
        VisSlider.SliderStyle customCatSliderStyle = new VisSlider.SliderStyle(horizontalSliderStyle);
        customCatSliderStyle.knob = new TextureRegionDrawable(Anims.Type.GOOMBA_WALK.get().getKeyFrame(0));
        customCatSliderStyle.knobDown = customCatSliderStyle.knob;
        customCatSliderStyle.knobOver = customCatSliderStyle.knob;

        VisSlider.SliderStyle customDogSliderStyle = new VisSlider.SliderStyle(horizontalSliderStyle);
        customDogSliderStyle.knob = new TextureRegionDrawable(Anims.Type.MARIO_IDLE.get().getKeyFrame(0));
        customDogSliderStyle.knobDown = customDogSliderStyle.knob;
        customDogSliderStyle.knobOver = customDogSliderStyle.knob;

        settingsPaneBoundsVisible = new Rectangle(windowCamera.viewportWidth / 4, windowCamera.viewportHeight / 3, windowCamera.viewportWidth / 2, windowCamera.viewportHeight / 2);
        settingsPaneBoundsHidden = new Rectangle(settingsPaneBoundsVisible);
        settingsPaneBoundsHidden.y -= settingsPaneBoundsVisible.height + windowCamera.viewportHeight / 3;

        isSettingShown = false;
//        Rectangle bounds = isSettingShown ? settingsPaneBoundsVisible : settingsPaneBoundsHidden;

//        settingsPane = new VisImage(Assets.Patch.glass_active.drawable);
//        settingsPane.setSize(bounds.width, bounds.height);
//        settingsPane.setPosition(bounds.x, bounds.y);
//        settingsPane.setColor(Color.DARK_GRAY);

        greyOutWindow = new VisWindow("", true);
        greyOutWindow.setSize(windowCamera.viewportWidth, windowCamera.viewportHeight);
        greyOutWindow.setPosition(0f, 0f);
        greyOutWindow.setMovable(false);
        greyOutWindow.setColor(1f, 1f, 1f, .8f);
        greyOutWindow.setKeepWithinStage(false);
        greyOutWindow.setVisible(false);
        greyOutWindow.setTouchable(Touchable.disabled);

        settingsWindow = new VisWindow("", glassWindowStyle);
        settingsWindow.setSize(settingsPaneBoundsHidden.width, settingsPaneBoundsHidden.height);
        settingsWindow.setPosition(settingsPaneBoundsHidden.x, settingsPaneBoundsHidden.y);
        settingsWindow.setMovable(false);
        settingsWindow.align(Align.top | Align.center);
        settingsWindow.setModal(false);
        settingsWindow.setKeepWithinStage(false);
        //settingsWindow.setColor(settingsWindow.getColor().r, settingsWindow.getColor().g, settingsWindow.getColor().b, 1f);
        //settingsWindow.setColor(Color.RED);

        Label settingLabel = new Label("Settings", skin);
        Label.LabelStyle style = settingLabel.getStyle();
        style.fontColor = Color.BLACK;
        settingLabel.setStyle(style);
        //settingLabel.setFontScale(3f);
        settingsWindow.add(settingLabel).padBottom(40f).padTop(40f);
        settingsWindow.row();
        Label musicVolumeLabel = new Label("Music Volume", skin);
        musicVolumeLabel.setColor(Color.BLACK);
        musicVolumeLabel.setFontScale(.5f);
        settingsWindow.add(musicVolumeLabel).padBottom(10f);
        settingsWindow.row();
        VisSlider musicSlider = new VisSlider(0f, 1f, .01f, false, customCatSliderStyle);
        musicSlider.setValue(audio.musicVolume.floatValue());
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.setMusicVolume(musicSlider.getValue());
                if (audio.currentMusic != null) { audio.currentMusic.setVolume(musicSlider.getValue()); }
            }
        });
        settingsWindow.add(musicSlider).padBottom(10f).width(settingsWindow.getWidth() - 100f);
        settingsWindow.row();
        Label soundVolumeLevel = new Label("Sound Volume", skin);
        soundVolumeLevel.setColor(Color.BLACK);
        soundVolumeLevel.setFontScale(.5f);
        settingsWindow.add(soundVolumeLevel).padBottom(10f);
        settingsWindow.row();
        VisSlider soundSlider = new VisSlider(0f, 1f, .01f, false, customDogSliderStyle);
        soundSlider.setValue(audio.soundVolume.floatValue());
        soundSlider.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                audio.setSoundVolume(soundSlider.getValue());
                audio.playSound(Sounds.Type.BOARD_CLICK);
            }
        });
        settingsWindow.add(soundSlider).padBottom(5f).width(settingsWindow.getWidth() - 100f);
        settingsWindow.row();

        ImageButton.ImageButtonStyle defaultButtonStyle = skin.get("default", ImageButton.ImageButtonStyle.class);
        ImageButton.ImageButtonStyle closeButtonStyle = new ImageButton.ImageButtonStyle(defaultButtonStyle);
//        closeButtonStyle.imageUp = new TextureRegionDrawable(assets.inputPrompts.get(InputPrompts.Type.button_light_power));
//        closeButtonStyle.imageDown = new TextureRegionDrawable(assets.inputPrompts.get(InputPrompts.Type.button_light_power));
//        closeButtonStyle.down = new TextureRegionDrawable(assets.inputPrompts.get(InputPrompts.Type.button_light_power));
//        closeButtonStyle.up = new TextureRegionDrawable(assets.inputPrompts.get(InputPrompts.Type.button_light_power));
//        closeButtonStyle.disabled = new TextureRegionDrawable(assets.inputPrompts.get(InputPrompts.Type.button_light_power));
        closeSettingsButton = new ImageButton(skin);
        closeSettingsButton.setStyle(closeButtonStyle);
        closeSettingsButton.setWidth(50f);
        closeSettingsButton.setHeight(25f);
        closeSettingsButton.setPosition(settingsPaneBoundsHidden.x + settingsPaneBoundsHidden.width - closeSettingsButton.getWidth(), settingsPaneBoundsHidden.y + settingsPaneBoundsHidden.height - closeSettingsButton.getHeight());
        closeSettingsButton.setClip(false);

        TextButton.TextButtonStyle settingsButtonStyle = VisUI.getSkin().get("default", TextButton.TextButtonStyle.class);
//        settingsButtonStyle.font = assets.smallFont;
        settingsButtonStyle.fontColor = Color.BLACK;
        settingsButtonStyle.up = Patches.Type.PLAIN.getDrawable();
        settingsButtonStyle.down = Patches.Type.PLAIN_GRADIENT.getDrawable();
        settingsButtonStyle.over = Patches.Type.PLAIN_DIM.getDrawable();

        closeSettingsTextButton = new TextButton("Close Settings", settingsButtonStyle);
        settingsWindow.add(closeSettingsTextButton).padTop(5f).padBottom(10f).width(settingsWindow.getWidth() - 100f).height(50f);

        float showDuration = 0.2f;
        float hideDuration = 0.1f;

        hideCloseSettingsButtonAction = new MoveToAction();
        hideCloseSettingsButtonAction.setPosition(settingsPaneBoundsHidden.x + settingsPaneBoundsHidden.width - closeSettingsButton.getWidth(), settingsPaneBoundsHidden.y + settingsPaneBoundsHidden.getHeight() - closeSettingsButton.getHeight());
        ;
        hideCloseSettingsButtonAction.setDuration(hideDuration);
        showCloseSettingsButtonAction = new MoveToAction();
        showCloseSettingsButtonAction.setPosition(settingsPaneBoundsVisible.x + settingsPaneBoundsVisible.width - closeSettingsButton.getWidth(), settingsPaneBoundsVisible.y + settingsPaneBoundsVisible.getHeight() - closeSettingsButton.getHeight());
        showCloseSettingsButtonAction.setDuration(showDuration);

        closeSettingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hideSettings();
            }
        });

        closeSettingsTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hideSettings();
            }
        });

        //addActor(closeSettingsButton);


        hideSettingsPaneAction = new MoveToAction();
        hideSettingsPaneAction.setPosition(settingsPaneBoundsHidden.x, settingsPaneBoundsHidden.y);
        hideSettingsPaneAction.setDuration(hideDuration);
        //hideSettingsPaneAction.setActor(settingsWindow);

        showSettingsPaneAction = new MoveToAction();
        showSettingsPaneAction.setPosition(settingsPaneBoundsVisible.x, settingsPaneBoundsVisible.y);
        showSettingsPaneAction.setDuration(showDuration);
        //showSettingsPaneAction.setActor(settingsWindow);
        //greyOutWindow.addActor(settingsWindow);
        addActor(greyOutWindow);
        addActor(settingsWindow);
        addActor(closeSettingsButton);
    }
}
