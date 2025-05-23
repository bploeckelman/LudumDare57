package lando.systems.ld57.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld57.Config.Flag;
import text.formic.Stringf;

import java.util.List;
import java.util.function.Function;

public class Util {

    // ------------------------------------------------------------------------
    // Object pools
    // ------------------------------------------------------------------------

    public static final Pool<Vector2> vec2 = Pools.get(Vector2.class, 500);
    public static final Pool<Rectangle> rect = Pools.get(Rectangle.class, 500);
    public static final Pool<Circle> circ = Pools.get(Circle.class, 500);

    public static void free(Vector2... objects) {
        for (var object : objects) {
            Util.vec2.free(object);
        }
    }

    public static void free(Rectangle... objects) {
        for (var object : objects) {
            Util.rect.free(object);
        }
    }

    public static void free(Circle... objects) {
        for (var object : objects) {
            Util.circ.free(object);
        }
    }

    // ------------------------------------------------------------------------
    // Logging related
    // ------------------------------------------------------------------------

//    public static final DateTimeFormatter TIME_FMT_SIMPLE = DateTimeFormatter.ofPattern("hh:mm:ss");

    public static void log(String msg) {
        log("", msg);
    }

    public static void log(String tag, Object object, Function<Object, String> toString) {
        log(tag, toString.apply(object));
    }

    public static void log(String tag, String msg) {
        if (Flag.LOG.isDisabled()) return;
        // stupid gwt...
//        var time = TIME_FMT_SIMPLE.format(LocalDateTime.now());
//        Gdx.app.log(Stringf.format("%s %s", time, tag), msg);
        Gdx.app.log(tag, msg);
    }

    // ------------------------------------------------------------------------
    // Shader related
    // ------------------------------------------------------------------------

    public static ShaderProgram loadShader(String vertSourcePath, String fragSourcePath) {
        ShaderProgram.pedantic = false;
        var shaderProgram = new ShaderProgram(
            Gdx.files.internal(vertSourcePath),
            Gdx.files.internal(fragSourcePath));
        var log = shaderProgram.getLog();

        if (!shaderProgram.isCompiled()) {
            if (Flag.LOG.isEnabled()) {
                Gdx.app.error("LoadShader", "compilation failed:\n" + log);
            }
            throw new GdxRuntimeException("LoadShader: compilation failed:\n" + log);
        } else if (Flag.LOG.isEnabled()) {
            Gdx.app.debug("LoadShader", "ShaderProgram compilation log: " + log);
        }

        return shaderProgram;
    }

    // ------------------------------------------------------------------------
    // Color related
    // ------------------------------------------------------------------------

    private static final List<Color> colors = List.of(
        /* grayscale */ Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.BLACK,
        /* reds      */ Color.FIREBRICK, Color.RED, Color.SCARLET, Color.CORAL, Color.SALMON,
        /* greens    */ Color.GREEN, Color.CHARTREUSE, Color.LIME, Color.FOREST, Color.OLIVE,
        /* blues     */ Color.BLUE, Color.NAVY, Color.ROYAL, Color.SLATE, Color.SKY, Color.CYAN, Color.TEAL,
        /* yellows   */ Color.YELLOW, Color.GOLD, Color.GOLDENROD, Color.ORANGE, Color.BROWN, Color.TAN,
        /* purples   */ Color.PINK, Color.MAGENTA, Color.PURPLE, Color.VIOLET, Color.MAROON);

    public static Color randomColor() {
        var index = MathUtils.random(colors.size() - 1);
        return colors.get(index);
    }

    public static Color randomColorPastel() {
        return randomColorHsv(180f, 360f, 0.7f, 0.9f, 0.8f, 0.95f);
    }

    public static Color randomColorHsv() {
        return randomColorHsv(0f, 360f, 0f, 1f, 0f, 1f);
    }

    public static Color randomColorHsv(float minHue0_360, float maxHue0_360, float minSat0_1, float maxSat0_1, float minVal0_1, float maxVal0_1) {
        var hue = MathUtils.random(minHue0_360, maxHue0_360);
        var sat = MathUtils.random(minSat0_1, maxSat0_1);
        var val = MathUtils.random(maxVal0_1, minVal0_1);
        return Color.WHITE.cpy().fromHsv(hue, sat, val);
    }

    public static Color hsvToRgb(float hue, float saturation, float value, Color outColor) {
        if (outColor == null) {
            outColor = new Color();
        }

        // rotate hue into positive range
        while (hue < 0) hue += 10f;

        hue = hue % 1f;
        int h = (int) (hue * 6);
        h = h % 6;

        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        //@formatter:off
        switch (h) {
            case 0: outColor.set(value, t, p, 1f); break;
            case 1: outColor.set(q, value, p, 1f); break;
            case 2: outColor.set(p, value, t, 1f); break;
            case 3: outColor.set(p, q, value, 1f); break;
            case 4: outColor.set(t, p, value, 1f); break;
            case 5: outColor.set(value, p, q, 1f); break;
            default: Util.log("HSV->RGB", Stringf.format("Failed to convert HSV->RGB(h: %f, s: %f, v: %f)", hue, saturation, value));
        }
        return outColor;
        //@formatter:on
    }

    // ------------------------------------------------------------------------
    // Drawing related
    // ------------------------------------------------------------------------
    private static final Color prevColor = Color.WHITE.cpy();

    // Circle convenience methods -------------------------

    public static void draw(SpriteBatch batch, TextureRegion texture, Circle circle, Color tint) {
        draw(batch, texture, circle, tint, 1f);
    }

    public static void draw(SpriteBatch batch, TextureRegion texture, Circle circle, Color tint, float scale) {
        var x = circle.x;
        var y = circle.y;
        var r = circle.radius * scale;
        prevColor.set(batch.getColor());
        batch.setColor(tint);
        batch.draw(texture, x - r, y - r, 2 * r, 2 * r);
        batch.setColor(prevColor);
    }

    // Rectangle convenience methods ----------------------

    public static void draw(SpriteBatch batch, TextureRegion texture, Rectangle rect) {
        draw(batch, texture, rect, Color.WHITE);
    }

    public static void draw(SpriteBatch batch, TextureRegion texture, Rectangle rect, Color tint) {
        draw(batch, texture, rect, tint, 1f, 1f);
    }

    public static void draw(SpriteBatch batch, TextureRegion texture, Rectangle rect, Color tint, float scaleX, float scaleY) {
        draw(batch, texture, rect, tint, rect.width / 2f, rect.height / 2f, scaleX, scaleY, 0f);
    }

    public static void draw(SpriteBatch batch, TextureRegion texture, Rectangle rect, Color tint, float ox, float oy, float sx, float sy, float rot) {
        var x = rect.x;
        var y = rect.y;
        var h = rect.height;
        var w = rect.width;
        prevColor.set(batch.getColor());
        batch.setColor(tint);
        batch.draw(texture, x, y, ox, oy, w, h, sx, sy, rot);
        batch.setColor(prevColor);
    }

    public static void draw(SpriteBatch batch, NinePatch ninePatch, Rectangle rect) {
        draw(batch, ninePatch, rect, Color.WHITE);
    }

    public static void draw(SpriteBatch batch, NinePatch ninePatch, Rectangle rect, Color tint) {
        draw(batch, ninePatch, rect, tint, 1f);
    }

    public static void draw(SpriteBatch batch, NinePatch ninePatch, Rectangle rect, Color tint, float scale) {
        draw(batch, ninePatch, rect, tint, rect.width / 2f, rect.height / 2f, scale, scale, 0f);
    }

    public static void draw(SpriteBatch batch, NinePatch ninePatch, Rectangle rect, Color tint, float ox, float oy, float sx, float sy, float rot) {
        var x = rect.x;
        var y = rect.y;
        var h = rect.height;
        var w = rect.width;
        prevColor.set(batch.getColor());
        batch.setColor(tint);
        ninePatch.draw(batch, x, y, ox, oy, w, h, sx, sy, rot);
        batch.setColor(prevColor);
    }

    public static TextureRegion getColoredTextureRegion(Color color) {
        Pixmap pixMap = new Pixmap(100, 20, Pixmap.Format.RGBA8888);
        pixMap.setColor(color);
        pixMap.fill();
        TextureRegion textureRegion = new TextureRegion(new Texture(pixMap));
        pixMap.dispose();
        return textureRegion;
    }
}
