package lando.systems.ld57.scene.components;

import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Util;

public class WaitToMove extends Component {

    Rectangle viewRect = new Rectangle();

    public WaitToMove(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float dt) {
        var mover = entity.get(Mover.class);
        var pos = entity.get(Position.class);
        if (mover == null || pos == null) {
            Util.log("Trying to stop a no moving object");
            return;
        }
        mover.active = false;
        var viewer = entity.scene.viewer.get(Viewer.class);
        if (viewer == null) {
            Util.log("No viewer found?, this is probably a bug");
            return;
        }

        if (viewer.paddedBounds(30).contains(pos.x(), pos.y())){
            mover.active = true;
            entity.destroy(WaitToMove.class);
        }
    }
}
