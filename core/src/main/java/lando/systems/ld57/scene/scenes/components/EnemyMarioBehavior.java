package lando.systems.ld57.scene.scenes.components;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.scene.components.WaitToMove;
import lando.systems.ld57.scene.framework.Entity;

public class EnemyMarioBehavior extends EnemyBehavior {

    float lastJumpTime;
    float lastFireballTime;
    float lastDirectionChangeTime;

    public EnemyMarioBehavior(Entity entity) {
        super(entity);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        var wait = entity.get(WaitToMove.class);
        if (wait != null) {
            // NO-OP until we start to move
            return;
        }
        lastDirectionChangeTime -= delta;
        lastJumpTime -= delta;
        lastFireballTime -= delta;

        if (lastDirectionChangeTime < 0) {
            lastDirectionChangeTime = MathUtils.random(1f, 2f);

        }

    }
}
