package lando.systems.ld57.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld57.Config;
import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.particles.ParticleManager;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.framework.World;
import lando.systems.ld57.scene.framework.families.RenderableComponent;
import lando.systems.ld57.scene.scenes.PlayerBehavior;
import lando.systems.ld57.scene.scenes.SceneCastlevania;
import lando.systems.ld57.scene.scenes.SceneMario;
import lando.systems.ld57.scene.scenes.SceneMegaman;
import lando.systems.ld57.scene.scenes.SceneZelda;
import lando.systems.ld57.screens.BaseScreen;
import lando.systems.ld57.utils.Util;
import lando.systems.ld57.world.EntityFactory;
import space.earlygrey.shapedrawer.ShapeDrawer;
import text.formic.Stringf;

/**
 * An arrangement of {@link Entity} instances from an associated {@link World},
 * setup for them to be created and interact in a particular way to produce
 * a given gameplay or narrative form.
 */
public class Scene<ScreenType extends BaseScreen> {

    private static final String TAG = Scene.class.getSimpleName();

    public final ScreenType screen;
    public final World<ScreenType> world;

    public Entity player;
    public Entity viewer;

    public Scene(ScreenType screen) {
        this.screen = screen;
        this.world = new World<>(this);

        // reset the screen's world camera to default for each new scene
        var camera = screen.worldCamera;
        camera.setToOrtho(false, Config.framebuffer_width, Config.framebuffer_height);
        camera.update();
    }

    public Entity createEntity() {
        return world.create(this);
    }

    public void destroy(Entity entity) {
        world.destroy(entity);
    }

    public void update(float dt) {
        world.update(dt);
    }

    public void render(SpriteBatch batch) {
        world.getFamily(RenderableComponent.class)
            .forEach(component -> {
                if (component.active) {
                    component.render(batch);
                    if (component instanceof Tilemap) {
                        screen.particleManager.render(screen.batch, ParticleManager.Layer.BACKGROUND);
                    }
                }
            });
    }

    public void render(ShapeDrawer shapes) {
        world.getFamily(RenderableComponent.class)
            .forEach(component -> {
                if (component.active) {
                    component.render(shapes);
                }
            });
    }

    protected Entity spawnPlayer(Characters.Type charType, float x, float y) {
        var entity = createEntity();

        new Position(entity, x, y);
        new PlayerInput(entity);
        new PlayerBehavior(entity, charType);
        new ParticleEmitter(entity);
        new Health(entity, 500);

        var animType = charType.get().animByType.get(Characters.AnimType.IDLE);
        var animator = new Animator(entity, animType);
        animator.origin.set(charType.get().origin);

        var collider = Collider.makeRect(entity, Collider.Mask.player, charType.get().colliderOffset);

        var mover = new Mover(entity, collider);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.velocity.set(0, 0);
        mover.friction = .001f;

        new Energy(entity);

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    protected void makeMapObjects(Tilemap tilemap) {
        var objectLayerName = "objects";

        var layer = tilemap.map.getLayers().get(objectLayerName);
        var objects = layer.getObjects();

        for (var object : objects) {
            Util.log(TAG, object, obj -> Stringf.format(
                "parsing map object: %s[name='%s', pos=(%.1f, %.1f)]...",
                obj.getClass().getSimpleName(),
                object.getName(),
                object.getProperties().get("x", Float.class),
                object.getProperties().get("y", Float.class)));

            // NOTE: 'name' field doesn't get set in the tmx <object> when
            //  creating when creating an object from a template,
            //  so don't count on it for parsing
            var name = object.getName();
            var props = object.getProperties();
            var type = props.get("type", "unknown", String.class);
            var x = props.get("x", Float.class);
            var y = props.get("y", Float.class);

            if ("spawn".equals(type)) {
                var character = props.get("character", String.class);
                if (character != null) {
                    switch (character) {
                        case "player":   player = spawnPlayer(getSceneCharType(), x, y); break;
                        case "goomba":   EntityFactory.goomba(this, x, y); break;
                        case "koopa":    EntityFactory.koopa(this, x, y);  break;
                        case "skeleton": EntityFactory.skeleton(this, x, y);  break;
                        case "castleBat": EntityFactory.castleBat(this, x, y); break;
                        case "eagle": EntityFactory.eagle(this, x, y); break;
                        case "megaBat": EntityFactory.megaBat(this, x, y); break;
                    }
                }
            } else if ("unknown".equals(type)) {
                Util.log(TAG, "map object: unknown type ", obj -> Stringf.format(
                    "name='%s', type='%s', x=%.1f, y=%.1f", name, type, x, y));
            }
        }
    }

    protected static class Resolutions {
        public static final Vector2 NES_NATIVE = new Vector2(256, 240);
        public static final Vector2 NES_NATIVE_4_3 = new Vector2(256 * 4f / 3f, 240);
        public static final Vector2 NES_NATIVE_16_9 = new Vector2(256, 240 * 16f / 9f);
        public static final Vector2 NES_SCALED_4_3 = new Vector2(292, 224);
    }

    private Characters.Type getSceneCharType() {
        if      (this instanceof SceneCastlevania) return Characters.Type.BELMONT;
        else if (this instanceof SceneMario)       return Characters.Type.MARIO;
        else if (this instanceof SceneMegaman)     return Characters.Type.MEGAMAN;
        else if (this instanceof SceneZelda)       return Characters.Type.LINK;
        return Characters.Type.OLDMAN;
    }
}
