package lando.systems.ld57.scene.components;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld57.math.Calc;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Util;

import java.util.Objects;

public class ViewController extends Component {

    public final Vector2 cameraChaseZoneBounds = new Vector2(20, 20);
    public final Vector2 speed = new Vector2(100f, 400f);

    public final Boundary boundary;

    public Target target;

    private boolean initialized;

    public ViewController(Entity entity, Boundary boundary) {
        this(entity, boundary, null);
    }

    public ViewController(Entity entity, Boundary boundary, Target target) {
        super(entity);
        this.boundary = Objects.requireNonNull(boundary);
        this.target = target;
        this.initialized = false;
    }

    public void target(float x, float y) {
        target(new Vector2(x, y));
    }

    public void target(Position position) {
        var newTarget = new PositionTarget(position);
        target(newTarget);
    }

    public void target(Vector2 vector2) {
        var newTarget = new Vec2Target(vector2);
        target(newTarget);
    }

    public void target(Interpolator interpolator) {
        var newTarget = new ScrollTarget(interpolator);
        target(newTarget);
    }

    public void target(Target newTarget) {
        target = newTarget;
        initialized = false;
    }

    @Override
    public void update(float dt) {
        if (inactive()) return;
        if (target == null) return;

        // get orthographic camera from parent entity's viewer component
        var viewer = entity.get(Viewer.class);
        if (viewer == null) {
            Util.log("ViewController entity missing expected Viewer component");
            return;
        }
        var camera = viewer.camera;

        // set initial values for target position
        if (!initialized) {
            initialized = true;
            camera.position.set(target.x(), target.y(), 0);
            camera.update();
        }

        // TODO(brian): need a way to override this for manual zooming,
        //  or other situations where we don't just want to zoom out
        //  to fit the full boundary width
        // zoom to fit the boundary width
        //camera.zoom = boundary.bounds.width / camera.viewportWidth;

        // get half dimensions of the camera viewport, adjusted for the zoom factor
        var camHalfWidth  = viewer.width() / 2f;
        var camHalfHeight = viewer.height() / 2f;
        var x = camera.position.x;
        var y = camera.position.y;
        var tarX = Float.MIN_VALUE;
        var tarY = Float.MIN_VALUE;

        // follow target
        var dx = target.x() - camera.position.x;
        var dy = target.y() - camera.position.y;

        if (dx < -cameraChaseZoneBounds.x) {
            tarX = target.x() + cameraChaseZoneBounds.x;
        } else if (dx > cameraChaseZoneBounds.x) {
            tarX = target.x() - cameraChaseZoneBounds.x;
        }

        if (dy < -cameraChaseZoneBounds.y) {
            tarY = target.y() + cameraChaseZoneBounds.y;
        } else if (dy > cameraChaseZoneBounds.y) {
            tarY = target.y() - cameraChaseZoneBounds.y;
        }


        if (tarX != Float.MIN_VALUE) {
            x = Calc.approach(camera.position.x, tarX, dt * speed.x);
        }
        if (tarY != Float.MIN_VALUE) {
            y = Calc.approach(camera.position.y, tarY, dt * speed.y);
        }

        // contain within boundary
        var bounds = boundary.bounds;
        var left   = bounds.x + camHalfWidth;
        var bottom = bounds.y + camHalfHeight;
        var right  = bounds.x + bounds.width  - camHalfWidth;
        var top    = bounds.y + bounds.height - camHalfHeight;
        x = Calc.clampf(x, left, right);
        y = Calc.clampf(y, bottom, top);

        // update actual camera position
        camera.position.set(x, y, 0);
    }

    public interface Target {
        float x();
        float y();
    }

    public static class PositionTarget implements Target {
        Position position;
        public PositionTarget(Position position) {
            this.position = position;
        }
        public float x() { return position.x(); }
        public float y() { return position.y(); }
    }

    public static class Vec2Target implements Target {
        Vector2 vec2;
        public Vec2Target(Vector2 vec2) {
            this.vec2 = vec2;
        }
        public float x() { return vec2.x; }
        public float y() { return vec2.y; }
    }

    private final class ScrollTarget implements Target {
        Interpolator interpolator;

        public ScrollTarget(Interpolator interpolator) {
            this.interpolator = interpolator;
        }

        public float x() { return 0; }

        public float y() {
            var viewCtrl = ViewController.this;
            var boundary = viewCtrl.boundary;
            var viewer = viewCtrl.entity.get(Viewer.class);
            if (viewer == null) {
                Util.log("ViewController entity missing expected Viewer component");
                return boundary.bottom();
            }

            // calculate current target y pos for camera, adjusting interp min/max y for viewer's centered origin
            var viewerOffset = viewer.height() / 2f;
            var min = boundary.bottom() + viewerOffset;
            var max = boundary.top()    + viewerOffset;
            return interpolator.apply(min, max);
        }
    }
}
