package lando.systems.ld57.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.Particles;
import lando.systems.ld57.particles.Particle;
import lando.systems.ld57.particles.ParticleManager;


public class ShapeEffect extends ParticleEffect {

    public ShapeEffect(ParticleManager particleManager) {
        super(particleManager);
    }

    public static class Params implements ParticleEffectParams {
        public float startX;
        public float startY;
        public Color startColor;

        public Params(float x, float y, Color startColor) {
            startX = x;
            startY = y;
            this.startColor = startColor;
        }
    }

    @Override
    public void spawn(ParticleEffectParams parameters) {
        var params = (Params) parameters;
        var layer = particleManager.activeParticlesByLayer.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;

        var amount = 10;

        for (int i = 0; i < amount; i++) {
            var keyframe = Particles.Type.SHAPE.get().getKeyFrame(MathUtils.random(1f));
            var angle = MathUtils.random(0f, 360f);
            var speed = MathUtils.random(10f, 20f);
            var endRotation = MathUtils.random(angle - 360f, angle + 360f);
            var startSize = MathUtils.random(3f, 5f);
            var ttl = .5f;

            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX, params.startY)
                .startRotation(angle)
                .endRotation(endRotation)
                .velocity(
                MathUtils.cosDeg(angle) * speed,
                MathUtils.sinDeg(angle) * speed
                )
                .startColor(params.startColor)
                .startSize(startSize)
                .endSize(0f, 0f)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
