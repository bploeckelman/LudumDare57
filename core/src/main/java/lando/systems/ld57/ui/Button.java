package lando.systems.ld57.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

public class Button {
    private BitmapFont font;
    private Rectangle bounds;
    private String text;
    private boolean isHovered;
    private boolean isPressed;
    private NinePatch ninePatchHovered;
    private NinePatch ninePatchDefault;
    private Runnable onClickAction;

    public Button(Rectangle bounds, String text, NinePatch ninePatchDefault, NinePatch ninePatchHovered, BitmapFont font) {
        this.bounds = bounds;
        this.text = text;
        this.ninePatchDefault = ninePatchDefault;
        this.ninePatchHovered = ninePatchHovered;
        this.font = font;
    }

    public Rectangle getBounds() { return bounds; }
    public String getText() { return text; }
    public boolean isHovered() { return isHovered; }

    public void setOnClickAction(Runnable onClickAction) {
        this.onClickAction = onClickAction;
    }

    public void update(float x, float y) {
        isHovered = bounds.contains(x, y);
        isPressed = false; // Reset pressed state each update
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }

    public void onClick() {
        if (onClickAction != null) {
            onClickAction.run();
        }
    }

    public void draw(SpriteBatch batch) {
        if (isHovered) {
            ninePatchHovered.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        } else {
            ninePatchDefault.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        }
        var layout = new GlyphLayout();
        layout.setText(font, text, Color.WHITE, bounds.width, Align.center, true);
        font.draw(batch, layout, bounds.x, bounds.y + bounds.height / 2 + layout.height / 2);
    }
}
