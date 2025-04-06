package lando.systems.ld57.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.Particles;
import lando.systems.ld57.particles.Particle;
import lando.systems.ld57.particles.ParticleManager;


public class BulletExposionEffect extends ParticleEffect {

    public BulletExposionEffect(ParticleManager particleManager) {
        super(particleManager);
    }

    public static class Params implements ParticleEffectParams {
        public float startX;
        public float startY;
        public TextureRegion textureRegion;

        public Params(float x, float y, TextureRegion textureRegion) {
            startX = x;
            startY = y;
            this.textureRegion = textureRegion;
        }
    }

    @Override
    public void spawn(ParticleEffectParams parameters) {
        var params = (Params) parameters;
        var layer = particleManager.activeParticlesByLayer.get(ParticleManager.Layer.FOREGROUND);
        var pool = particleManager.particlePool;

        var amount = 10;

        for (int i = 0; i < amount; i++) {
            var angle = MathUtils.random(0f, 360f);
            var speed = MathUtils.random(50f, 100f);
            var endRotation = MathUtils.random(angle - 360f, angle + 360f);
            var startSize = MathUtils.random(5f, 10f);
            var ttl = .25f;

            layer.add(Particle.initializer(pool.obtain())
                .keyframe(params.textureRegion)
                .startPos(params.startX, params.startY)
                .startRotation(angle)
                .endRotation(endRotation)
                .velocity(
                MathUtils.cosDeg(angle) * speed,
                MathUtils.sinDeg(angle) * speed
                )
                .startSize(startSize)
                .endAlpha(0f)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
