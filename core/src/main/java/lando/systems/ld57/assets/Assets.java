package lando.systems.ld57.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import lando.systems.ld57.Config;
import lando.systems.ld57.assets.framework.AssetContainer;
import lando.systems.ld57.utils.Util;
import lando.systems.ld57.utils.controllers.mapping.ControllerMappings;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Assets implements Disposable {

    private static final String TAG = Assets.class.getSimpleName();

    public enum Load {SYNC, ASYNC}

    public boolean loaded = false;

    public final ObjectMap<Class<? extends AssetContainer<?, ?>>, AssetContainer<?, ?>> containers;
    public final Preferences prefs;
    public final AssetManager mgr;
    public final SpriteBatch batch;
    public final ShapeDrawer shapes;
    public final GlyphLayout layout;
    public final Array<Disposable> disposables;
    public Texture titleScreen;

    public TextureAtlas atlas;
    public I18NBundle strings;

    public final Texture pixel;
    public ShaderProgram outlineShader;

    public TextureRegion pixelRegion;

    public ControllerMappings controllerMappings;

    public Assets() {
        this(Load.SYNC);
    }

    public Assets(Load load) {
        prefs = Gdx.app.getPreferences(Config.preferences_name);

        disposables = new Array<>();
        containers = new ObjectMap<>();
        containers.put(Anims.class, new Anims());
        containers.put(Icons.class, new Icons());
        containers.put(Fonts.class, new Fonts());
        containers.put(Musics.class, new Musics());
        containers.put(Sounds.class, new Sounds());
        containers.put(Patches.class, new Patches());
        containers.put(Particles.class, new Particles());
        containers.put(Characters.class, new Characters());
        containers.put(ScreenTransitions.class, new ScreenTransitions());

        // create a single pixel texture and associated region
        var pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        {
            pixmap.setColor(Color.WHITE);
            pixmap.drawPixel(0, 0);
            pixmap.drawPixel(1, 0);
            pixmap.drawPixel(0, 1);
            pixmap.drawPixel(1, 1);

            pixel = new Texture(pixmap);
            pixelRegion = new TextureRegion(pixel);
        }
        disposables.add(pixmap);
        disposables.add(pixel);

        controllerMappings = new MyControllerMapping();

        mgr = new AssetManager();
        batch = new SpriteBatch();
        shapes = new ShapeDrawer(batch, pixelRegion);
        layout = new GlyphLayout();
        disposables.add(mgr);
        disposables.add(batch);

        // setup asset manager to support ttf/otf fonts
        var internalFileResolver = new InternalFileHandleResolver();
        var fontLoader = new FreetypeFontLoader(internalFileResolver);
        mgr.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(internalFileResolver));
        mgr.setLoader(BitmapFont.class, ".ttf", fontLoader);
        mgr.setLoader(BitmapFont.class, ".otf", fontLoader);

        // populate asset manager
        {
            // one-off items
            mgr.load("sprites/sprites.atlas", TextureAtlas.class);
            mgr.load("i18n/strings", I18NBundle.class);
            mgr.load("ui/uiskin.json", Skin.class);
            mgr.load("images/title-background.png", Texture.class);

            // textures
            mgr.load("images/libgdx.png", Texture.class);

            // fonts
            containers.get(Fonts.class).load(this);

            // music
            containers.get(Musics.class).load(this);

            // sounds
            containers.get(Sounds.class).load(this);

            // shaders
        }

        if (load == Load.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (loaded) return 1;
        if (!mgr.update()) {
            return mgr.getProgress();
        }

        outlineShader = Util.loadShader("shaders/default.vert", "shaders/outline.frag");
        atlas = mgr.get("sprites/sprites.atlas");
        strings = mgr.get("i18n/strings");
        titleScreen = mgr.get("images/title-background.png", Texture.class);

        for (var container : containers.values()) {
            container.init(this);
        }

        loaded = true;
        return 1;
    }

    @Override
    public void dispose() {
        disposables.forEach(Disposable::dispose);
    }
}
