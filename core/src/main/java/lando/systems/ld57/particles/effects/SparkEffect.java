package lando.systems.ld57.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.Particles;
import lando.systems.ld57.particles.Particle;
import lando.systems.ld57.particles.ParticleManager;


public class SparkEffect extends ParticleEffect {

    public SparkEffect(ParticleManager particleManager) {
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

        var amount = 50;

        for (int i = 0; i < amount; i++) {
            var keyframe = Particles.Type.SPARK.get().getKeyFrame(MathUtils.random(1f));
            var angle = MathUtils.random(0f, 360f);
            var speed = MathUtils.random(50f, 100f);
            var endRotation = MathUtils.random(angle - 360f, angle + 360f);
            var startSize = MathUtils.random(5f, 10f);
            var ttl = MathUtils.random(.25f, .5f);

            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX, params.startY)
                .startRotation(angle)
                .endRotation(endRotation)
                .velocity(
                    MathUtils.cosDeg(angle) * speed, // X velocity
                    MathUtils.sinDeg(angle) * speed  // Y velocity
                )
                .startColor(params.startColor)
                .startSize(startSize)
                .endSize(startSize * 2f)
                .endAlpha(.25f)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
