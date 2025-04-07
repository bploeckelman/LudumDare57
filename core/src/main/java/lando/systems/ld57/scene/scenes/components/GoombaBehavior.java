package lando.systems.ld57.scene.scenes.components;

import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public class GoombaBehavior extends Component {

    public GoombaBehavior(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float dt) {
        var mover = entity.get(Mover.class);
        var collider = entity.get(Collider.class);

        if (mover != null && collider != null) {
            // turn around if we're near an edge
            var rectShape = collider.shape(Collider.RectShape.class);
            var size = (rectShape != null) ? rectShape.rect.width : 0;
            var xDir = mover.velocity.x > 0 ? 1 : -1;
            var xOffset = (int) (xDir * size * (3f / 4f));

            var isSafe = collider.check(Collider.Mask.solid, xOffset, -1);
            if (!isSafe) {
                mover.invertX();
            }
        }
    }
}
