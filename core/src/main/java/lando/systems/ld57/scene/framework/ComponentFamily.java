package lando.systems.ld57.scene.framework;

import lando.systems.ld57.scene.Scene;

/**
 * Shared superclass for {@link Component} types that share a common interface or behavior.
 * Used as a generics bound for grouping components by optional families,
 * in addition to by their concrete {@link Component} subclass type.
 */
public abstract class ComponentFamily extends Component {

    public ComponentFamily(Entity entity) {
        super(entity);
    }

    public ComponentFamily(Scene<?> scene) {
        super(scene);
    }
}
