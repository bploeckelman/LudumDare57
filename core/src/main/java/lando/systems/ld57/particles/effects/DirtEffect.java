package lando.systems.ld57.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.Particles;
import lando.systems.ld57.particles.Particle;
import lando.systems.ld57.particles.ParticleManager;


public class DirtEffect extends ParticleEffect {

    public DirtEffect(ParticleManager particleManager) {
        super(particleManager);
    }

    public static class Params implements ParticleEffectParams {
        public float startX;
        public float startY;

        public Params(float x, float y) {
            startX = x;
            startY = y;
        }
    }

    @Override
    public void spawn(ParticleEffectParams parameters) {
        var params = (Params) parameters;
        var layer = particleManager.activeParticlesByLayer.get(ParticleManager.Layer.BACKGROUND);
        var pool = particleManager.particlePool;

        var amount = 1;
        var keyframe = Particles.Type.DIRT.get().getKeyFrame(MathUtils.random(1f));

        for (int i = 0; i < amount; i++) {
            var angle = MathUtils.random(30f, 140f);
            var speed = MathUtils.random(10f, 20f);
            var startSize = MathUtils.random(5f, 15f);
            var ttl = MathUtils.random(.25f, .5f);
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX, params.startY)
                .velocity(MathUtils.cosDeg(angle) * speed, MathUtils.sinDeg(angle) * speed)
                .startColor(Color.BROWN)
                .endColor(Color.CLEAR)
                .startSize(startSize)
                .endSize(startSize * 2f)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
