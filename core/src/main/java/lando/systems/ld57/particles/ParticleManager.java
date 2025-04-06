package lando.systems.ld57.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.*;
import lando.systems.ld57.particles.effects.DirtEffect;
import lando.systems.ld57.particles.effects.ParticleEffect;
import lando.systems.ld57.particles.effects.ParticleEffectParams;
import lando.systems.ld57.particles.effects.SparkEffect;
import lando.systems.ld57.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class ParticleManager implements Disposable {

    public enum Layer { BACKGROUND, FOREGROUND }

    private static final int MAX_PARTICLES = 5000;

    public Pool<Particle> particlePool = Pools.get(Particle.class, MAX_PARTICLES);
    public ObjectMap<Layer, Array<Particle>> activeParticlesByLayer = new ObjectMap<>();
    public final Map<ParticleEffect.Type, ParticleEffect> effects = new HashMap<ParticleEffect.Type, ParticleEffect>();

    public ParticleManager() {
        int particlesPerLayer = MAX_PARTICLES / Layer.values().length;
        this.activeParticlesByLayer.put(Layer.BACKGROUND, new Array<>(false, particlesPerLayer));
        this.activeParticlesByLayer.put(Layer.FOREGROUND, new Array<>(false, particlesPerLayer));
        initEffects();
    }

    private void initEffects() {
//        for (var effectType : ParticleEffect.Type.values()) {
//            try {
//                ParticleEffect effect = (ParticleEffect) effectType.particleEffect.getConstructor(ParticleManager.class).newInstance(this);
//                effects.put(effectType, effect);
//            } catch(Exception e) {
//                Util.log("Error initiating effects");
//            }
//        }
        effects.put(ParticleEffect.Type.DIRT, new DirtEffect(this));
        effects.put(ParticleEffect.Type.SPARK, new SparkEffect(this));
    }

    public void clear() {
        for (var layer : Layer.values()) {
            particlePool.freeAll(activeParticlesByLayer.get(layer));
            activeParticlesByLayer.get(layer).clear();
        }
    }

    public void spawn(ParticleEffect.Type type, ParticleEffectParams params) {
        effects.get(type).spawn(params);
    }

    public void update(float dt) {
        for (var layer : Layer.values()) {
            for (int i = activeParticlesByLayer.get(layer).size - 1; i >= 0; --i) {
                var particle = activeParticlesByLayer.get(layer).get(i);
                particle.update(dt);
                if (particle.isDead()) {
                    activeParticlesByLayer.get(layer).removeIndex(i);
                    particlePool.free(particle);
                }
            }
        }
    }

    public void render(SpriteBatch batch, Layer layer) {
        for (var particle : activeParticlesByLayer.get(layer)) {
            particle.render(batch);
        }
    }

    @Override
    public void dispose() {
        clear();
    }

}
