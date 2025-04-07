package lando.systems.ld57.scene.components;

import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Callbacks;

public class Timer extends Component {

    private float duration;

    public Callbacks.NoArg onEnd;
    public Callbacks.NoArg onUpdate;

    public Timer(Entity entity) {
        super(entity);
        this.duration = 0;
        this.onEnd = null;
        this.onUpdate = null;
    }

    public Timer(Entity entity, float duration) {
        this(entity, duration, null);
    }

    public Timer(Entity entity, float duration, Callbacks.NoArg onEnd) {
        this(entity, duration, null, onEnd);
    }

    public Timer(Entity entity, float duration, Callbacks.NoArg onUpdate, Callbacks.NoArg onEnd) {
        super(entity);
        this.onEnd = onEnd;
        this.onUpdate = onUpdate;
        start(duration);
    }

    public void start(float duration) {
        this.duration = duration;
    }

    @Override
    public void update(float dt) {
        if (onUpdate != null) {
            onUpdate.run();
        }

        if (duration > 0) {
            duration -= dt;
            if (duration <= 0 && onEnd != null) {
                onEnd.run();
            }
        }
    }
}
