package lando.systems.ld57.scene.framework;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.PlayerInput;
import lando.systems.ld57.scene.framework.families.RenderableComponent;
import lando.systems.ld57.scene.scenes.components.EnemyBehavior;
import lando.systems.ld57.screens.BaseScreen;
import lando.systems.ld57.utils.Util;
import text.formic.Stringf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Container for {@link Entity} and {@link Component} instances.
 */
public class World<ScreenType extends BaseScreen> {

    private static final String TAG = World.class.getSimpleName();

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    public final Scene<ScreenType> scene;

    // ------------------------------------------------------------------------
    // Internal collections
    // ------------------------------------------------------------------------

    private final IntMap<Entity> entitiesById = new IntMap<>();
    private final Array<Class<? extends Component>> componentClasses = new Array<>();
    private final Map<Class<? extends Component>, Array<? extends Component>> componentsByClass = new HashMap<>();
    private final Map<Class<? extends ComponentFamily>, Array<? extends Component>> componentsByFamilyClass = new HashMap<>();

    public World(Scene<ScreenType> scene) {
        this.scene = scene;
        componentClasses.add(PlayerInput.class);
        componentClasses.add(Mover.class);
    }

    /**
     * Updates all active components
     */
    public void update(float dt) {
        for (var clazz : componentClasses) {
            var components = getComponents(clazz);
            for (var component : components) {
                if (component.active) {
                    component.update(dt);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Entity methods
    // ------------------------------------------------------------------------

    /**
     * Lookup an entity by id
     *
     * @param id the integer id of the requested entity
     * @return optional containing the entity instance if found, empty optional otherwise
     */
    public Optional<Entity> get(int id) {
        var entity = entitiesById.get(id);
        if (entity == null) {
            Util.log(TAG, Stringf.format("Entity %d not found", id));
        }
        return Optional.ofNullable(entity);
    }

    /**
     * Instantiate a new {@link Entity}
     * TODO(brian): add pooling
     */
    public Entity create(Scene<ScreenType> scene) {
        var entity = new Entity(scene);
        entitiesById.put(entity.id, entity);
        return entity;
    }

    /**
     * Destroys the specified {@link Entity} along with any attached {@link Component} instances
     * TODO(brian): add pooling
     */
    public void destroy(Entity entity) {
        if (entity == null) {
            Util.log(TAG, "destroy() called with null Entity value");
            return;
        }

        // all entity instances should be tracked here, double check and warn if not found
        if (!entitiesById.containsKey(entity.id)) {
            Util.log(TAG, Stringf.format("Entity %d not found, may indicate dangling references", entity.id));
        }

        // detach and destroy all components attached to this entity
        var components = new ArrayList<>(entity.componentsByClass.values());
        entity.componentsByClass.clear();
        components.forEach(component -> destroy(component, component.getClass()));

        // remove the entity itself
        entitiesById.remove(entity.id);
    }

    /**
     * Remove <strong>all</strong> {@link Entity} instances and their attached {@link Component} instances
     */
    public void clear() {
        Util.log(TAG, "Destroying all entities and their attached components!");
        for (int i = entitiesById.size - 1; i >= 0; i--) {
            var entity = entitiesById.get(i);
            destroy(entity);
        }
        entitiesById.clear();
    }

    // ------------------------------------------------------------------------
    // Component methods
    // ------------------------------------------------------------------------

    /**
     * Get a {@link Stream<Component>} of all components in the world
     */
    public Stream<Component> stream() {
        return componentsByClass.values().stream()
            .flatMap(array -> Arrays.stream(array.items));
    }

    /**
     * Get all components of the specified type
     *
     * @param clazz the {@link Class} of the {@link Component} to get (eg. {@code MyComponent.class})
     * @param <C>   generic type of the component to get
     * @return non-null array containing all components of the given type, if any
     */
    @SuppressWarnings("unchecked")
    public <C extends Component> Array<C> getComponents(Class<C> clazz) {
        if (!componentClasses.contains(clazz, true)) {
            componentClasses.add(clazz);
        }

        componentsByClass.putIfAbsent(clazz, new Array<>());
        return (Array<C>) componentsByClass.get(clazz);
    }

    /**
     * Add a new component to the global collection, keyed by type
     *
     * @param component the {@link Component} to add
     * @param clazz     the {@link Class} of the {@link Component} to add (eg. {@code MyComponent.class})
     * @param <C>       generic type of the component to add
     */
    @SuppressWarnings("unchecked")
    public <C extends Component> void add(Component component, Class<C> clazz) {
        // validate that there's a component to add
        if (component == null) {
            Util.log(TAG, "add() called with null Component value");
            return;
        }

        // validate that the component matches the class type
        if (!ClassReflection.isInstance(clazz, component)) {
            Util.log(TAG, Stringf.format("add(): component %s is not of the specified class %s, ignoring",
                component.getClass().getSimpleName(), clazz.getSimpleName()));
            return;
        }

        // add by family if applicable
        // NOTE(brian): handling each family manually for now, there aren't many
        if (component instanceof RenderableComponent) {
            var renderable = (RenderableComponent) component;
            var components = getFamily(RenderableComponent.class);
            components.add(renderable);
        }
        if (component instanceof EnemyBehavior) {
            var enemyBehavior = (EnemyBehavior) component;
            var components = getFamily(EnemyBehavior.class);
            components.add(enemyBehavior);
        }

        // add by type
        var components = getComponents(clazz);
        components.add((C) component);
    }

    /**
     * Remove the specified component from the global collection, if they're in it
     *
     * @param component the {@link Component} to destroy
     * @param clazz     the {@link Class} of the {@link Component} to destroy (eg. {@code MyComponent.class})
     * @param <C>       generic type of the component to destroy
     */
    @SuppressWarnings("unchecked")
    public <C extends Component> void destroy(Component component, Class<C> clazz) {
        // validate that there's a component to destroy
        if (component == null) {
            Util.log(TAG, "destroy(): component null, ignoring");
            return;
        }

        // validate that the component matches the class type
        if (!ClassReflection.isInstance(clazz, component)) {
            Util.log(TAG, Stringf.format("destroy(): component %s is not of the specified class %s, ignoring",
                component.getClass().getSimpleName(), clazz.getSimpleName()));
            return;
        }

        // ensure that the component is detached from its entity before destroying
        var entity = component.entity;
        if (entity != Entity.NONE) {
            Util.log(TAG, Stringf.format("destroy(%s): component attached to entity %d, detaching first",
                component.getClass().getSimpleName(), entity.id));
            entity.detach(clazz);
        }

        // remove by family if applicable
        // NOTE(brian): handling each family manually for now, there aren't many
        if (component instanceof RenderableComponent) {
            var renderable = (RenderableComponent) component;
            var components = getFamily(RenderableComponent.class);
            components.removeValue(renderable, true);
        }
        if (component instanceof EnemyBehavior) {
            var enemyBehavior = (EnemyBehavior) component;
            var components = getFamily(EnemyBehavior.class);
            components.removeValue(enemyBehavior, true);
        }

        // remove by type
        var components = getComponents(clazz);
        components.removeValue((C) component, true);
    }

    // ------------------------------------------------------------------------
    // Facade.Families implementation
    // ------------------------------------------------------------------------

    /**
     * Get all components of the specified family type
     *
     * @param clazz the {@link Class} of the {@link ComponentFamily} to get (eg. {@code MyFamily.class})
     * @param <F>   generic type of the family to get
     * @return non-null array containing all components of the given family, if any
     */
    @SuppressWarnings("unchecked")
    public <F extends ComponentFamily> Array<F> getFamily(Class<F> clazz) {
        return (Array<F>) componentsByFamilyClass.computeIfAbsent(clazz, (key) -> new Array<>());
    }
}
