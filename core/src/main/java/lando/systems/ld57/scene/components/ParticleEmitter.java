package lando.systems.ld57.scene.components;

import lando.systems.ld57.particles.ParticleManager;
import lando.systems.ld57.particles.effects.DirtEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.particles.effects.ParticleEffectParams;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public class ParticleEmitter extends Component {
    public final ParticleManager particleManager;

    public ParticleEmitter(Entity entity) {
        super(entity);
        this.particleManager = entity.scene.screen.particleManager;
    }

    public void spawnParticle(ParticleEffect.Type particleType, ParticleEffectParams params) {
        particleManager.spawn(particleType, params);
    }
}
