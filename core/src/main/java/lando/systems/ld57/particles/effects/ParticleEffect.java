package lando.systems.ld57.particles.effects;

import lando.systems.ld57.particles.ParticleManager;

public abstract class ParticleEffect {
    ParticleManager particleManager;
    public ParticleEffect(ParticleManager particleManager) {
        this.particleManager = particleManager;
    }
    public abstract void spawn(ParticleEffectParams params);

    public enum Type {
        DIRT(DirtEffect.class),
        SPARK(SparkEffect.class),
        SHAPE(ShapeEffect.class),
        BLOOD_SPLAT(BloodSplatEffect.class),
        BLOOD(BloodEffect.class),
        BLOOD_FOUNTAIN(BloodFountainEffect.class),
        ;

        public Class particleEffect;
        Type(Class particleEffect) {
            this.particleEffect = particleEffect;
        }
    }
}
