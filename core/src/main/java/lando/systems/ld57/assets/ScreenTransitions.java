package lando.systems.ld57.assets;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.framework.AssetContainer;
import lando.systems.ld57.assets.framework.AssetEnum;
import lando.systems.ld57.utils.Util;

public class ScreenTransitions extends AssetContainer<ScreenTransitions.Type, ShaderProgram> {

    public static AssetContainer<Type, ShaderProgram> container;

    public enum Type implements AssetEnum<ShaderProgram> {
          BLINDS
        , CIRCLECROP
        , CROSSHATCH
        , CUBE
        , DISSOLVE
        , DOOMDRIP
        , DOORWAY
        , DREAMY
        , HEART
        , PIXELIZE
        , RADIAL
        , RIPPLE
        , SIMPLEZOOM
        , STEREO
        ;

        @Override
        public ShaderProgram get() {
            return container.get(this);
        }

        public static Type random() {
            var index = MathUtils.random(Type.values().length - 1);
            return Type.values()[index];
        }
    }

    public ScreenTransitions() {
        super(ScreenTransitions.class, ShaderProgram.class);
        ScreenTransitions.container = this;
    }

    @Override
    public void init(Assets assets) {
        var prefix = "shaders/transitions/";
        var vertex = prefix + "default.vert";
        for (var type : Type.values()) {
            var filename = type.name().toLowerCase() + ".frag";
            var fragment = prefix + filename;
            var shader = Util.loadShader(vertex, fragment);
            resources.put(type, shader);
        }
    }
}
