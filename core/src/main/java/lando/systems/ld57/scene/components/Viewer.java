package lando.systems.ld57.scene.components;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Util;

public class Viewer extends Component {

    public final OrthographicCamera camera;
    private final Rectangle bounds;

    public Viewer(Entity entity, OrthographicCamera camera) {
        super(entity);
        this.camera = camera;
        this.bounds = new Rectangle();
    }

    @Override
    public void update(float delta) {
        camera.update();
    }

    public float width() {
        return camera.viewportWidth * camera.zoom;
    }

    public float height() {
        return camera.viewportHeight * camera.zoom;
    }

    public float left() {
        return camera.position.x - width() / 2f;
    }

    public float right() {
        return camera.position.x + width() / 2f;
    }

    public float bottom() {
        return camera.position.y - height() / 2f;
    }

    public float top() {
        return camera.position.y + height() / 2f;
    }

    public Rectangle paddedBounds(float padding) {
        bounds.x = left() - padding;
        bounds.y = bottom() - padding;
        bounds.width = width() + 2 * padding;
        bounds.height = height() + 2 * padding;
        return bounds;
    }
}
