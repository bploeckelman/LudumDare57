package lando.systems.ld57.particles.effects;

import lando.systems.ld57.particles.ParticleManager;

public abstract class ParticleEffect {
    ParticleManager particleManager;
    public ParticleEffect(ParticleManager particleManager) {
        this.particleManager = particleManager;
    }
    public abstract void spawn(ParticleEffectParams params);

    public static enum Type {
        DIRT,
    }
}
