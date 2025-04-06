package lando.systems.ld57.scene.components;

import lando.systems.ld57.particles.effects.FireEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.particles.effects.SparkEffect;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public class FireParticle extends ParticleEmitter {
    public FireParticle(Entity entity) {
        super(entity);

    }

    @Override
    public void update(float dt) {
        var pos = entity.get(Position.class);
        if (pos != null) {
            spawnParticle(ParticleEffect.Type.FIRE, new FireEffect.Params(pos.x(), pos.y()));
        }
    }

}
