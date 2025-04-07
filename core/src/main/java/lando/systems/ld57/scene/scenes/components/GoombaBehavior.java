package lando.systems.ld57.scene.scenes.components;

import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.framework.Entity;

public class GoombaBehavior extends EnemyBehavior {

    public GoombaBehavior(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        var mover = entity.get(Mover.class);
        var collider = entity.get(Collider.class);

        turnAroundAtEdge(mover, collider);
    }
}
