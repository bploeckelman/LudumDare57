package lando.systems.ld57.scene.components;

import lando.systems.ld57.particles.ParticleManager;
import lando.systems.ld57.particles.effects.DirtEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public class ParticleEmitter extends Component {
    public final ParticleEffect.Type particleType;
    public final ParticleManager particleManager;

    public ParticleEmitter(Entity entity, ParticleEffect.Type particleType, ParticleManager particleManager) {
        super(entity);
        this.particleType = particleType;
        this.particleManager = particleManager;
    }

    public void spawnParticle(float x, float y) {
        particleManager.spawn(ParticleEffect.Type.DIRT, new DirtEffect.Params(x, y));
    }
}
