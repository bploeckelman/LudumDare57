package lando.systems.ld57.scene.components;

import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Callbacks;
import lando.systems.ld57.utils.Direction;
import lando.systems.ld57.utils.Util;

public class Health extends Component {

    private float maxHealth;
    private float health;
    Callbacks.NoArg onDeath;

    public Health(Entity entity, float maxHealth) {
        this(entity, maxHealth, () -> {
            Util.log(entity.toString(), "is dead");
            entity.scene.world.destroy(entity);
        });
    }

    public Health(Entity entity, float maxHealth, Callbacks.NoArg onDeath) {
        super(entity);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.onDeath = onDeath;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (health <= 0) {
            onDeath.run();
        }
    }

    public void takeDamage(float amount) {
        health -= amount;
    }
}
