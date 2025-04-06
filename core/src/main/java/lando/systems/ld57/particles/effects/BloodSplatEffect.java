package lando.systems.ld57.particles.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.Particles;
import lando.systems.ld57.particles.Particle;
import lando.systems.ld57.particles.ParticleManager;

public class BloodSplatEffect extends ParticleEffect {

    public BloodSplatEffect(ParticleManager particleManager) {
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

        var amount = 3;

        for (int i = 0; i < amount; i++) {
            var keyframe = Particles.Type.SPLAT.get().getKeyFrame(MathUtils.random());
            var startSize = MathUtils.random(50f, 75f);
            var ttl = 5f;
            var startingColor = new Color(MathUtils.random(.8f, 1f), MathUtils.random(.2f), MathUtils.random(.2f), 1);
            layer.add(Particle.initializer(pool.obtain())
                .keyframe(keyframe)
                .startPos(params.startX, params.startY)
                .targetPos(params.startX - 5f, params.startY - 25f)
                .startColor(startingColor)
                .endColor(Color.CLEAR)
                .startSize(startSize)
                .endSize(startSize - 10f, startSize + 50f)
                .timeToLive(ttl)
                .init()
            );
        }
    }
}
