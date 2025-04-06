package lando.systems.ld57.scene.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.math.Calc;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.framework.families.RenderableComponent;
import lando.systems.ld57.utils.Util;

public class Animator extends RenderableComponent {

    public Animation<TextureRegion> animation;
    public TextureRegion keyframe;
    public float stateTime;
    public int facing;

    public Animator(Entity entity, Anims.Type type) {
        this(entity, type.get());
    }

    public Animator(Entity entity, Animation<TextureRegion> animation) {
        this(entity, animation.getKeyFrame(0));
        this.animation = animation;
    }

    public Animator(Entity entity, TextureRegion keyframe) {
        super(entity);
        this.animation = null;
        this.keyframe = keyframe;
        this.size.set(keyframe.getRegionWidth(), keyframe.getRegionHeight());
        this.stateTime = 0;
        this.facing = 1;
    }

    @SuppressWarnings("unchecked")
    public float play(Anims.Type type) {
        var anim = (Animation<TextureRegion>) Anims.container.get(type);
        return play(anim);
    }

    public float play(Animation<TextureRegion> anim) {
        if (anim == null) return 0;
        this.animation = anim;
        return this.animation.getAnimationDuration();
    }

    @Override
    public void update(float dt) {
        if (animation == null) return;

        stateTime += dt;
        keyframe = animation.getKeyFrame(stateTime);

        float sx = Calc.approach(Calc.abs(scale.x), defaultScale.x, dt * scaleReturnSpeed);
        float sy = Calc.approach(Calc.abs(scale.y), defaultScale.y, dt * scaleReturnSpeed);
        scale.set(facing * sx, sy);
    }

    Color testColor = new Color(Color.WHITE);
    @Override
    public void render(SpriteBatch batch) {
        if (keyframe == null) return;

        ShaderProgram shader = Main.game.assets.outlineShader;
        batch.setShader(shader);
        shader.setUniformf("u_time", stateTime);
        shader.setUniformf("u_color1", Util.hsvToRgb(stateTime, 1f, 1f, testColor ));
        shader.setUniformf("u_thickness", 8);

        var rect = obtainPooledRectBounds();
        Util.draw(batch, keyframe, rect, tint);
        Util.free(rect);

        batch.setShader(null);
    }
}
