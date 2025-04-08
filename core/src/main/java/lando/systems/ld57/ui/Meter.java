package lando.systems.ld57.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld57.assets.Assets;
import lando.systems.ld57.assets.Patches;
import lando.systems.ld57.scene.components.Health;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Util;

public class Meter {

    private static final String TAG = Meter.class.getSimpleName();
    private static final float PAD_X = 10f;
    private static final float PAD_Y = 5f;

    private final Color barColor = Color.GREEN.cpy();
    private final NinePatch patch;

    public final Assets assets;
    public final Rectangle outerBounds;
    public final Rectangle innerBounds;

    public Entity entity;
    public Health health;
    // TODO(brian): energy?
    public float percent;

    public Meter(Assets assets, Entity entity, float x, float y, float w, float h) {
        this.assets = assets;
        this.outerBounds = new Rectangle(x, y, w, h);
        this.innerBounds = new Rectangle(x + PAD_X, y + PAD_Y, w - (PAD_X * 2), h - (PAD_Y * 2));
        setEntity(entity);
        this.percent = 1f;
        this.patch = Patches.Type.ROUNDED.get();
    }

    public void setEntity(Entity entity) {
        if (entity != null) {
            this.entity = entity;
            this.health = entity.get(Health.class);
            if (health == null) {
                Util.log(TAG, "*** Entity missing health component for meter: " + entity);
            }
        }
        barColor.set(Color.GREEN);
        percent = 1f;
    }

    public void update(float delta) {
        if (health != null) {
            percent = health.getHealthPercent();
        }
        innerBounds.width *= percent;
        colorRamp(percent);
    }

    public void render(SpriteBatch batch) {
        batch.setColor(Color.WHITE);
        patch.draw(batch, outerBounds.x, outerBounds.y, outerBounds.width, outerBounds.height);

        batch.setColor(barColor);
        batch.draw(assets.pixelRegion, innerBounds.x, innerBounds.y, innerBounds.width, innerBounds.height);

        batch.setColor(Color.WHITE);
    }

    public void colorRamp(float percent) {
        // Ensure percent is in 0-1 range
        percent = Math.max(0, Math.min(1, percent));

        if      (percent < 0.25f) barColor.set(Color.RED);
        else if (percent < 0.5f)  barColor.set(Color.ORANGE);
        else if (percent < 0.75f) barColor.set(Color.YELLOW);
        else if (percent < 1f)    barColor.set(Color.GREEN);
    }
}
