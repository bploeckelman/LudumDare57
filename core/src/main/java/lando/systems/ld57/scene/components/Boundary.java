package lando.systems.ld57.scene.components;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public class Boundary extends Component {

    // TODO(brian): support other shapes through a shared interface if needed
    public final Rectangle bounds = new Rectangle();

    private final Vector2 center = new Vector2();

    public Boundary(Entity entity, Rectangle bounds) {
        super(entity);
        this.bounds.set(bounds);
    }

    public Boundary(Entity entity, float x, float y, float width, float height) {
        super(entity);
        this.bounds.set(x, y, width, height);
    }

    public float left()   { return bounds.x; }
    public float bottom() { return bounds.y; }
    public float right()  { return bounds.x + bounds.width; }
    public float top()    { return bounds.y + bounds.height; }

    public float halfWidth() { return bounds.width / 2; }
    public float halfHeight() { return bounds.height / 2; }

    public Vector2 center() {
        return center.set(left() + halfWidth(), bottom() + halfHeight());
    }
}
