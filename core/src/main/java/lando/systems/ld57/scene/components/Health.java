package lando.systems.ld57.scene.components;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.particles.effects.BloodEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.particles.effects.ShapeEffect;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.PlayerBehavior;
import lando.systems.ld57.utils.Callbacks;
import lando.systems.ld57.utils.Util;

public class Health extends Component {

    private float maxHealth;
    private float health;
    private float immunityTime;
    Callbacks.NoArg onDeath;

    public Health(Entity entity, float maxHealth) {
        this(entity, maxHealth, () -> {
            Util.log(entity.toString(), "is dead");
            var emitter = entity.get(ParticleEmitter.class);
            var pos = entity.get(Position.class);
            if (emitter != null) {
                entity.scene.screen.particleManager.spawn(ParticleEffect.Type.SHAPE, new ShapeEffect.Params(pos.x(), pos.y(), Util.randomColor()));
            }
            entity.scene.world.destroy(entity);
        });
    }

    public Health(Entity entity, float maxHealth, Callbacks.NoArg onDeath) {
        super(entity);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.onDeath = onDeath;
        this.immunityTime = 0;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        immunityTime = MathUtils.clamp(immunityTime-delta, 0f, 1f);
        if (health <= 0) {
            onDeath.run();
        }
    }

    public void takeDamage(float amount) {
        if (immunityTime > 0) return;
        Util.log(entity.toString(), "Damage " + amount + " Health: " + health);
        health -= amount;
        immunityTime = .3f;
        var emitter = entity.get(ParticleEmitter.class);
        var pos = entity.get(Position.class);
        var anim = entity.get(Animator.class);
        var playerBehavior = entity.get(PlayerBehavior.class);
        if (playerBehavior != null) {
            //Player got damaged
            playerBehavior.knockBack(1f);
        }
        if (emitter != null) {
            entity.scene.screen.particleManager.spawn(ParticleEffect.Type.BLOOD, new BloodEffect.Params(pos.x() + anim.size.x / 2f, pos.y() + anim.size.x / 2f));
        }
    }

    public void setHealth(float health) {
        this.health = health;
    }
}
