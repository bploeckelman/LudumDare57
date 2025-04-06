package lando.systems.ld57.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld57.Config;
import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.DebugRender;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.ParticleEmitter;
import lando.systems.ld57.scene.components.PlayerInput;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.framework.World;
import lando.systems.ld57.scene.framework.families.RenderableComponent;
import lando.systems.ld57.scene.scenes.PlayerBehavior;
import lando.systems.ld57.screens.BaseScreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * An arrangement of {@link Entity} instances from an associated {@link World},
 * setup for them to be created and interact in a particular way to produce
 * a given gameplay or narrative form.
 */
public class Scene<ScreenType extends BaseScreen> {

    public final ScreenType screen;
    public final World<ScreenType> world;

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

    public void update(float dt) {
        world.update(dt);
    }

    public void render(SpriteBatch batch) {
        world.getFamily(RenderableComponent.class)
            .forEach(component -> {
                if (component.active) {
                    component.render(batch);
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

    protected void spawnPlayer(Characters.Type charType, float x, float y) {
        var entity = createEntity();

        new Position(entity, x, y);
        new PlayerInput(entity);
        new PlayerBehavior(entity, charType);
        new ParticleEmitter(entity);

        var animType = charType.get().animByType.get(Characters.AnimType.IDLE);
        var animator = new Animator(entity, animType);
        animator.origin.set(charType.get().origin);

        var collider = Collider.makeRect(entity, Collider.Mask.player, charType.get().colliderOffset);

        var mover = new Mover(entity, collider);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.velocity.set(0, 0);
        mover.friction = .001f;

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);
    }
}
