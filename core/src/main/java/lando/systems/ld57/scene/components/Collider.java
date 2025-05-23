package lando.systems.ld57.scene.components;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import lando.systems.ld57.math.Calc;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Util;
import text.formic.Stringf;

import java.util.EnumSet;

public class Collider extends Component {

    private static final String TAG = Collider.class.getSimpleName();

    // ------------------------------------------------------------------------
    // Collider specific types and values
    // ------------------------------------------------------------------------

    public enum Mask { solid, npc, player, object, projectile, effect, player_projectile, enemy, enemy_projectile}

    public interface Shape {
        boolean overlaps(Collider other, int xOffset, int yOffset);
    }

    // ------------------------------------------------------------------------
    // Data
    // ------------------------------------------------------------------------

    public final Mask mask;
    public final Shape shape;

    // ------------------------------------------------------------------------
    // Factory methods and private constructors
    // ------------------------------------------------------------------------

    public static Collider makeRect(Entity entity, Mask mask, Rectangle rect) {
        return makeRect(entity, mask, rect.x, rect.y, rect.width, rect.height);
    }

    public static Collider makeRect(Entity entity, Mask mask, float x, float y, float w, float h) {
        if (w <= 0 || h <= 0) {
            Util.log(TAG, "WARN: collider created with degenerate shape size");
        }
        return new Collider(entity, mask, x, y, w, h);
    }

    public static Collider makeCirc(Entity entity, Mask mask, float x, float y, float radius) {
        if (radius <= 0) {
            Util.log(TAG, "WARN: collider created with degenerate shape size");
        }
        return new Collider(entity, mask, x, y, radius);
    }

    public static Collider makeGrid(Entity entity, Mask mask, int tileSize, int cols, int rows) {
        if (tileSize <= 0 || cols <= 0 || rows <= 0) {
            Util.log(TAG, "WARN: collider created with degenerate shape size");
        }
        return new Collider(entity, mask, tileSize, cols, rows);
    }

    private Collider(Entity entity, Mask mask, float x, float y, float w, float h) {
        super(entity);
        this.mask = mask;
        this.shape = new RectShape(this, x, y, w, h);
    }

    private Collider(Entity entity, Mask mask, float x, float y, float radius) {
        super(entity);
        this.mask = mask;
        this.shape = new CircShape(this, x, y, radius);
    }

    private Collider(Entity entity, Mask mask, int tileSize, int cols, int rows) {
        super(entity);
        this.mask = mask;
        this.shape = new GridShape(this, tileSize, cols, rows);
    }

    // ------------------------------------------------------------------------
    // Public interface
    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <T extends Shape> T shape(Class<T> shapeClass) {
        if (ClassReflection.isInstance(shapeClass, shape)) {
            return (T) shape;
        }
        return null;
//        throw new GdxRuntimeException("Collider shape is not the specified type: " + shapeClass);
    }

    public boolean check(Mask mask) {
        return check(mask, 0, 0);
    }

    public boolean check(Mask mask, int xOffset, int yOffset) {
        var hitCollider = checkAndGet(mask, xOffset, yOffset);
        return hitCollider != null;
    }

    public Collider checkAndGet(Mask mask, int xOffset, int yOffset) {
        var colliders = entity.scene.world.getComponents(Collider.class);
        for (var other : colliders) {
            if (other == this) continue;
            if (other.inactive()) continue;
            if (mask != other.mask) continue;

            if (shape.overlaps(other, xOffset, yOffset)) {
                return other;
            }
        }
        return null;
    }

    public Collider checkAndGet(EnumSet<Mask> masks, int xOffset, int yOffset) {
        var colliders = entity.scene.world.getComponents(Collider.class);
        for (var other : colliders) {
            if (other == this) continue;
            if (other.inactive()) continue;
            if (!masks.contains(other.mask)) continue;

            if (shape.overlaps(other, xOffset, yOffset)) {
                return other;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    // Concrete shape types and shape-shape collision implementations
    // ------------------------------------------------------------------------

    public static final class RectShape implements Shape {

        private final Collider collider;

        public final Rectangle rect;

        public RectShape(Collider collider, float x, float y, float w, float h) {
            this.collider = collider;
            this.rect = new Rectangle(x, y, w, h);
        }

        @Override
        public boolean overlaps(Collider other, int xOffset, int yOffset) {
            var aRect = Util.rect.obtain().set(0, 0, 0, 0);
            var aPos = Util.vec2.obtain().setZero();
            var bPos = Util.vec2.obtain().setZero();

            var aPosition = collider.entity.get(Position.class);
            var bPosition = other.entity.get(Position.class);
            if (aPosition != null && aPosition.active) aPos.set(aPosition.value);
            if (bPosition != null && bPosition.active) bPos.set(bPosition.value);

            aRect.set(
                rect.x + aPos.x + xOffset,
                rect.y + aPos.y + yOffset,
                rect.width, rect.height);

            var overlaps = false;
            if (other.shape instanceof RectShape) {
                var otherShape = (RectShape) other.shape;
                var bRect = Util.rect.obtain();

                bRect.set(
                    otherShape.rect.x + bPos.x,
                    otherShape.rect.y + bPos.y,
                    otherShape.rect.width,
                    otherShape.rect.height
                );
                overlaps = aRect.overlaps(bRect);

                Util.free(bRect);
            } else if (other.shape instanceof CircShape) {
                var otherShape = (CircShape) other.shape;
                var bCirc = Util.circ.obtain();

                bCirc.set(
                    otherShape.circ.x + bPos.x,
                    otherShape.circ.y + bPos.y,
                    otherShape.circ.radius
                );
                overlaps = Intersector.overlaps(bCirc, aRect);

                Util.free(bCirc);
            } else if (other.shape instanceof GridShape) {
                var otherGrid = (GridShape) other.shape;
                var rows = otherGrid.rows;
                var cols = otherGrid.cols;
                var tileSize = otherGrid.tileSize;

                // construct the rectangle describing the boundary of the grid
                var gridBounds = Util.rect.obtain().set(
                    bPos.x, bPos.y,
                    cols * tileSize,
                    rows * tileSize
                );

                // only worth checking against the grid tiles if the rectangle is within the grid bounds
                if (aRect.overlaps(gridBounds)) {
                    // calc the rectangular extents of the rectangle relative to the grid (instead of relative to the world)
                    // this is needed so that we can determine what horiz/vert ranges of tiles could have an overlap
                    var rectRelativeX = rect.x + aPos.x + xOffset - bPos.x;
                    var rectRelativeY = rect.y + aPos.y + yOffset - bPos.y;
                    var rectLeft   = rectRelativeX;
                    var rectRight  = rectRelativeX + rect.width;
                    var rectTop    = rectRelativeY + rect.height;
                    var rectBottom = rectRelativeY;

                    // get the range of grid tiles that the rectangle overlaps on each axis
                    int left   = Calc.clampInt((int) Calc.floor  (rectLeft   / (float) tileSize), 0, cols);
                    int right  = Calc.clampInt((int) Calc.ceiling(rectRight  / (float) tileSize), 0, cols);
                    int top    = Calc.clampInt((int) Calc.ceiling(rectTop    / (float) tileSize), 0, rows);
                    int bottom = Calc.clampInt((int) Calc.floor  (rectBottom / (float) tileSize), 0, rows);

                    // check each tile in the possible overlap range for solidity
                    for (int y = bottom; y < top; y++) {
                        for (int x = left; x < right; x++) {
                            var i = x + y * cols;
                            var solid = otherGrid.tiles[i].solid;
                            if (solid) {
                                overlaps = true;
                                break;
                            }
                        }
                    }
                }

                Util.free(gridBounds);
            }

            Util.free(bPos);
            Util.free(aPos);
            Util.free(aRect);
            return overlaps;
        }
    }

    public static final class CircShape implements Shape {

        private final Collider collider;

        public final Circle circ;

        public CircShape(Collider collider, float x, float y, float radius) {
            this.collider = collider;
            this.circ = new Circle(x, y, radius);
        }

        @Override
        public boolean overlaps(Collider other, int xOffset, int yOffset) {
            var aCirc = Util.circ.obtain();
            var aPos = Util.vec2.obtain().setZero();
            var bPos = Util.vec2.obtain().setZero();

            var aPosition = collider.entity.get(Position.class);
            var bPosition = other.entity.get(Position.class);
            if (aPosition != null && aPosition.active) aPos.set(aPosition.value);
            if (bPosition != null && bPosition.active) bPos.set(bPosition.value);

            aCirc.set(
                circ.x + aPos.x + xOffset,
                circ.y + aPos.y + yOffset,
                circ.radius
            );

            var overlaps = false;
            if (other.shape instanceof RectShape) {
                var otherShape = (RectShape) other.shape;
                var bRect = Util.rect.obtain();

                bRect.set(
                    otherShape.rect.x + bPos.x,
                    otherShape.rect.y + bPos.y,
                    otherShape.rect.width,
                    otherShape.rect.height
                );
                overlaps = Intersector.overlaps(aCirc, bRect);

                Util.free(bRect);
            } else if (other.shape instanceof CircShape) {
                var otherShape = (CircShape) other.shape;
                var bCirc = Util.circ.obtain();

                bCirc.set(
                    otherShape.circ.x + bPos.x,
                    otherShape.circ.y + bPos.y,
                    otherShape.circ.radius
                );
                overlaps = aCirc.overlaps(bCirc);

                Util.circ.free(bCirc);
            } else if (other.shape instanceof GridShape) {
                var otherGrid = (GridShape) other.shape;
                var gridBounds = Util.rect.obtain().set(0, 0, 0, 0);

                var rows = otherGrid.rows;
                var cols = otherGrid.cols;
                var tileSize = otherGrid.tileSize;

                // construct the rectangle describing the boundary of the grid
                gridBounds.set(bPos.x, bPos.y, cols * tileSize, rows * tileSize);

                // only worth checking against the grid tiles if the circle is within the grid bounds
                if (Intersector.overlaps(aCirc, gridBounds)) {
                    // calc the rectangular extents of the circle relative to the grid (instead of relative to the world)
                    // this is needed so that we can determine what horiz/vert ranges of tiles could have an overlap
                    var circRelativeX = circ.x + aPos.x + xOffset - bPos.x;
                    var circRelativeY = circ.y + aPos.y + yOffset - bPos.y;
                    var circLeft   = circRelativeX - circ.radius;
                    var circRight  = circRelativeX + circ.radius;
                    var circTop    = circRelativeY + circ.radius;
                    var circBottom = circRelativeY - circ.radius;

                    // get the range of grid tiles that the circle overlaps on each axis
                    int left   = Calc.clampInt((int) Calc.floor(  circLeft   / (float) tileSize), 0, cols);
                    int right  = Calc.clampInt((int) Calc.ceiling(circRight  / (float) tileSize), 0, cols);
                    int top    = Calc.clampInt((int) Calc.ceiling(circTop    / (float) tileSize), 0, rows);
                    int bottom = Calc.clampInt((int) Calc.floor(  circBottom / (float) tileSize), 0, rows);

                    // check each tile in the possible overlap range for solidity
                    var tileRect = Util.rect.obtain();
                    for (int y = bottom; y < top; y++) {
                        for (int x = left; x < right; x++) {
                            var i = x + y * cols;
                            var solid = otherGrid.tiles[i].solid;
                            if (solid) {
                                tileRect.set(
                                    gridBounds.x + x * tileSize,
                                    gridBounds.y + y * tileSize,
                                    tileSize, tileSize
                                );

                                overlaps = Intersector.overlaps(aCirc, tileRect);
                                if (overlaps) {
                                    break;
                                }
                            }
                        }
                    }
                    Util.free(tileRect);
                }

                Util.free(gridBounds);
            }

            Util.free(bPos);
            Util.free(aPos);
            Util.free(aCirc);
            return overlaps;
        }
    }

    public static final class GridShape implements Shape {

        private final Collider collider;

        public final int tileSize;
        public final int cols;
        public final int rows;
        public final Tile[] tiles;

        public static class Tile  {
            public boolean solid;
        }

        public GridShape(Collider collider, int tileSize, int cols, int rows) {
            this.collider = collider;
            this.tileSize = tileSize;
            this.cols = cols;
            this.rows = rows;
            this.tiles = new Tile[cols*rows];
            for (int i = 0; i < cols*rows; i++) {
                tiles[i] = new Tile();
            }
        }

        public void set(int x, int y, boolean solid) {
            var inRangeX = Calc.between(x, 0, cols - 1);
            var inRangeY = Calc.between(y, 0, rows - 1);
            if (!inRangeX || !inRangeY) {
                Util.log(TAG, Stringf.format("Collider.grid.set(%d, %d, %b) called with out of bounds coords, ignored", x, y, solid));
                return;
            }
            int index = x + y * cols;
            tiles[index].solid = solid;
        }

        @Override
        public boolean overlaps(Collider other, int xOffset, int yOffset) {
            throw new UnsupportedOperationException("grid->* overlap checks are not supported, such checks should go in the other direction");
        }
    }
}
