package lando.systems.ld57.scene.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld57.Config;
import lando.systems.ld57.assets.Fonts;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.framework.families.RenderableComponent;
import lando.systems.ld57.utils.Callbacks;
import lando.systems.ld57.utils.Util;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class DebugRender extends RenderableComponent {

    // TODO(brian): update to leverage Shape component for some of these

    // ------------------------------------------------------------------------
    // Inner types and constants
    // ------------------------------------------------------------------------

    private static final boolean DRAW_FILLED = false;

    /**
     * Base class for optional render callback parameters. Extend this class
     * and include fields for data needed for an onRender callback for your use case.
     * The base class maintains references to {@link SpriteBatch} and {@link ShapeDrawer}
     * as well as this {@link DebugRender} component for use in callbacks if needed.
     */
    public abstract static class Params implements Callbacks.TypedArg.Params {
        public DebugRender self;
        public SpriteBatch batch;
        public ShapeDrawer shapes;
    }

    /**
     * Custom parameters for debug rendering text
     */
    public static class TextParams extends Params {

        public Fonts.Type fontType;
        public String fontVariant;
        public String text;

        public TextParams(String text) {
            this(Fonts.Type.ROUNDABOUT, Fonts.Variant.DEFAULT_NAME, text);
        }

        public TextParams(Fonts.Type fontType, String fontVariant, String text) {
            this.fontType = fontType;
            this.fontVariant = fontVariant;
            this.text = text;
        }
    }

    /**
     * Default render callback that draws a filled circle at the entity's position.
     * TODO(brian): could be convenient to have a way to compose multiple callbacks,
     *   define a standard set for typical things; position, collider, renderable bounds, etc...
     *   then mix and match for a given entity without needing to reimplement them
     */
    public static final Callbacks.TypedArg<Params> DRAW_POSITION = (params) -> {
        var shapes = params.shapes;
        var entity = params.self.entity;
        if (entity == Entity.NONE) return;
        var position = entity.get(Position.class);
        if (position == null) return;

        // draw position
        var outer = 4f;
        var inner = outer * (3f / 4f);
        shapes.filledCircle(position.value, outer, Color.CYAN);
        shapes.filledCircle(position.value, inner, Color.YELLOW);
    };

    /**
     * Default render callback that draws a filled circle at the entity's position
     * and a rectangle for the entity's collider if it has one.
     */
    public static final Callbacks.TypedArg<Params> DRAW_POSITION_AND_COLLIDER = (params) -> {
        var shapes = params.shapes;
        if (params.self == null) return; // TODO(brian): related to a bug here where field isn't always set even though it should be
        var entity = params.self.entity;
        if (entity == Entity.NONE) return;
        var position = entity.get(Position.class);
        if (position == null) return;

        var color = Color.MAGENTA;
        var colorFill = color.cpy(); colorFill.a = 0.25f;
        var lineWidth = 1.1f;

        // draw collider
        var collider = entity.get(Collider.class);
        if (collider != null) {
            if (collider.shape instanceof Collider.RectShape) {
                var shape = (Collider.RectShape) collider.shape;
                var rect = Util.rect.obtain().set(
                    shape.rect.x + position.x(),
                    shape.rect.y + position.y(),
                    shape.rect.width,
                    shape.rect.height
                );
                if (DRAW_FILLED) {
                    shapes.filledRectangle(rect, colorFill);
                } else {
                    shapes.rectangle(rect, color, lineWidth);
                }
                Util.free(rect);
            } else if (collider.shape instanceof Collider.CircShape) {
                var shape = (Collider.CircShape) collider.shape;
                var circ = Util.circ.obtain();
                circ.set(
                    shape.circ.x + position.x(),
                    shape.circ.y + position.y(),
                    shape.circ.radius
                );
                if (DRAW_FILLED) {
                    shapes.setColor(0f, 1f, 1f, 0.5f);
                    shapes.filledCircle(circ.x, circ.y, circ.radius);
                    shapes.setColor(Color.WHITE);
                } else {
                    shapes.setColor(Color.YELLOW);
                    shapes.circle(circ.x, circ.y, circ.radius, lineWidth);
                    shapes.setColor(Color.WHITE);
                }
                Util.free(circ);
            } else if (collider.shape instanceof Collider.GridShape) {
                var shape = (Collider.GridShape) collider.shape;
                var rect = Util.rect.obtain();

                int numTiles = shape.cols * shape.rows;
                for (int i = 0; i < numTiles; i++) {
                    var tile = shape.tiles[i];
                    if (tile.solid) {
                        int x = i % shape.cols;
                        int y = i / shape.cols;
                        int size = shape.tileSize;
                        rect.set(
                            position.x() + x * size,
                            position.y() + y * size,
                            size, size
                        );
                        if (DRAW_FILLED) {
                            shapes.filledRectangle(rect, colorFill);
                        } else {
                            shapes.rectangle(rect, color, lineWidth);
                        }
                    }
                }

                var boundary = collider.entity.get(Boundary.class);
                if (boundary != null) {
                    rect.set(boundary.bounds);
                    if (DRAW_FILLED) {
                        shapes.setColor(1f, 1f, 0f, 0.1f);
                        shapes.filledRectangle(rect);
                        shapes.setColor(Color.WHITE);
                    } else {
                        shapes.rectangle(rect, Color.YELLOW, lineWidth);
                    }
                }

                Util.free(rect);
            }
        }

        // draw position
        var outer = 1.5f;
        var inner = outer * (3f / 4f);
        shapes.filledCircle(position.value, outer, Color.CYAN);
        shapes.filledCircle(position.value, inner, Color.YELLOW);
    };

    /**
     * Default {@link Params} instance so {@link DebugRender} callbacks
     * can be used without requiring custom params to be created.
     */
    private final Params DEFAULT_PARAMS = new Params() {};

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    public Callbacks.TypedArg<Params> onBatchRender;
    public Callbacks.TypedArg<Params> onShapeRender;
    public Params onBatchRenderParams = DEFAULT_PARAMS;
    public Params onShapeRenderParams = DEFAULT_PARAMS;

    // ------------------------------------------------------------------------
    // Factory methods
    // ------------------------------------------------------------------------

    public static DebugRender makeForBatch(Entity entity, Callbacks.TypedArg<Params> onRender) {
        return new DebugRender(entity, onRender, null, null, null);
    }

    public static DebugRender makeForBatch(Entity entity, Callbacks.TypedArg<Params> onRender, Params params) {
        return new DebugRender(entity, onRender, params, null, null);
    }

    public static DebugRender makeForShapes(Entity entity, Callbacks.TypedArg<Params> onRender) {
        return new DebugRender(entity, null, null, onRender, null);
    }

    public static DebugRender makeForShapes(Entity entity, Callbacks.TypedArg<Params> onRender, Params params) {
        return new DebugRender(entity, null, null, onRender, params);
    }

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    public DebugRender(Entity entity) {
        super(entity);
    }

    private DebugRender(Entity entity,
                        Callbacks.TypedArg<Params> onBatchRender, Params batchParams,
                        Callbacks.TypedArg<Params> onShapeRender, Params shapeParams) {
        super(entity);
        this.onBatchRender = onBatchRender;
        this.onShapeRender = onShapeRender;
        this.onBatchRenderParams = (batchParams != null) ? batchParams : DEFAULT_PARAMS;
        this.onShapeRenderParams = (shapeParams != null) ? shapeParams : DEFAULT_PARAMS;
    }

    // ------------------------------------------------------------------------
    // Component implementation
    // ------------------------------------------------------------------------

    @Override
    public void update(float dt) {
        // TODO(brian): don't really like this, but callbacks defined outside a scope
        //  where the relevant entity is available still may need to access it through
        //  a component reference, and this is a simple way to ensure the self ref is set.
        //  See the DebugRender.DRAW_POSITION for an example where this wouldn't be needed
        //  if the callback was defined in the Factory method that creates the entity
        //  and its position component.
        if (onBatchRenderParams != null) {
            onBatchRenderParams.self = this;
        }
        if (onShapeRenderParams != null) {
            onShapeRenderParams.self = this;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (Config.Flag.RENDER.isDisabled()) return;

        if (onBatchRenderParams != null) {
            onBatchRenderParams.batch = batch;
        }

        if (onBatchRender != null) {
            onBatchRender.run(onBatchRenderParams);
        }
    }

    @Override
    public void render(ShapeDrawer shapes) {
        if (Config.Flag.RENDER.isDisabled()) return;

        if (onShapeRenderParams != null) {
            onShapeRenderParams.shapes = shapes;
        }

        if (onShapeRender != null) {
            onShapeRender.run(onShapeRenderParams);
        }
    }
}
