package lando.systems.ld57.utils.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;

/**
 * Brian Ploeckelman created on 7/22/2014.
 */
public class ColorAccessor implements TweenAccessor<Color> {

    public static final int R = 1;
    public static final int G = 2;
    public static final int B = 3;
    public static final int A = 4;
    public static final int RGB = 5;
    public static final int RGBA = 6;

    @Override
    public int getValues(Color target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case R:  returnValues[0] = target.r; return 1;
            case G:  returnValues[0] = target.g; return 1;
            case B:  returnValues[0] = target.b; return 1;
            case A:  returnValues[0] = target.a; return 1;
            case RGB:
                returnValues[0] = target.r;
                returnValues[1] = target.g;
                returnValues[2] = target.b;
                return 3;
            case RGBA:
                returnValues[0] = target.r;
                returnValues[1] = target.g;
                returnValues[2] = target.b;
                returnValues[3] = target.a;
                return 4;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Color target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case R:  target.r = newValues[0]; break;
            case G:  target.g = newValues[0]; break;
            case B:  target.b = newValues[0]; break;
            case A:  target.a = newValues[0]; break;
            case RGB:
                target.r = newValues[0];
                target.g = newValues[1];
                target.b = newValues[2];
                break;
            case RGBA:
                target.r = newValues[0];
                target.g = newValues[1];
                target.b = newValues[2];
                target.a = newValues[3];
                break;
            default: assert false;
        }
    }

}
