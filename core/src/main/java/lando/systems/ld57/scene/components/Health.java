package lando.systems.ld57.scene.components;

import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Sounds;
import lando.systems.ld57.particles.effects.BloodEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.particles.effects.ShapeEffect;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.PlayerBehavior;
import lando.systems.ld57.utils.Callbacks;
import lando.systems.ld57.utils.Util;

public class Health extends Component {
    public static float MAX_IMMUNITIY_TIME = .4f;

    private float maxHealth;
    public float health;
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
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (health <= 0) {
            // TODO(brian): use player/enemyBehavior.die();
        }
    }

    public void takeDamage(float amount) {
        Util.log(entity.toString(), "Damage " + amount + " Health: " + health);
        health -= amount;

        var pos = entity.get(Position.class);
        var anim = entity.get(Animator.class);
        var playerBehavior = entity.get(PlayerBehavior.class);
        if (playerBehavior != null) {
            //Player got damaged
            playerBehavior.knockBack(1f);
        }
        else {
            Main.game.audioManager.playSound(Sounds.Type.DAMAGE);
        }

        var emitter = entity.get(ParticleEmitter.class);
        if (emitter != null) {
            entity.scene.screen.particleManager.spawn(ParticleEffect.Type.BLOOD, new BloodEffect.Params(pos.x() + anim.size.x / 2f, pos.y() + anim.size.x / 2f));
        }
    }

    public void setHealth(float health) {
        this.health = health;
        // TODO(brian): check for death
    }

    public float gethealth() {
        return health;
    }

    public float getHealthPercent() {
        return health / maxHealth;
    }

}
