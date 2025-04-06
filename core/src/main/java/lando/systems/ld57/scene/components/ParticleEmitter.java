package lando.systems.ld57.scene.components;

import lando.systems.ld57.particles.ParticleManager;
import lando.systems.ld57.particles.effects.DirtEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public class ParticleEmitter extends Component {

    private final ParticleEffect.Type particleType;
    private final ParticleManager particleManager;

    public ParticleEmitter(Entity entity, ParticleEffect.Type particleType) {
        super(entity);
        this.particleType = particleType;
        this.particleManager = entity.scene.screen.particleManager;
    }

    public void spawnParticle(float x, float y) {
        // TODO(brian): params needs to be passed into constructor along with type
        particleManager.spawn(particleType, new DirtEffect.Params(x, y));
    }
}
