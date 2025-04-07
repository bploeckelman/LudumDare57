package lando.systems.ld57.utils;

import text.formic.Stringf;

public class Direction {

    public enum Axis { X, Y, Z }

    public enum Rotation { CW, CCW }

    public enum Relative {
        UP, DOWN, LEFT, RIGHT;

        public static Relative from(int sign, Axis axis) {
            Relative dir = null;
            switch (axis) {
                case X: switch (sign) {
                    case -1: dir = Relative.LEFT;  break;
                    case +1: dir = Relative.RIGHT; break;
                } break;
                case Y: switch (sign) {
                    case -1: dir = Relative.DOWN; break;
                    case +1: dir = Relative.UP;   break;
                } break;
                case Z: {
                    Util.log(Relative.class.getSimpleName(),
                        Stringf.format("from() not supported for %s", Axis.Z));
                } break;
            }
            return dir;
        }

        public boolean isHorizontal() {
            return this == LEFT || this == RIGHT;
        }

        public boolean isVertical() {
            return this == UP || this == DOWN;
        }
    }

    public enum Movement { FORWARD, BACKWARD, LEFT, RIGHT, UP, DOWN }

    public enum Cardinal { NORTH, SOUTH, EAST, WEST }

    public enum Compass  {
        NORTH, NORTH_EAST,
        EAST, SOUTH_EAST,
        SOUTH, SOUTH_WEST,
        WEST, NORTH_WEST
    }

    public enum Angle {
        DEG_0, DEG_45, DEG_90,
        DEG_135, DEG_180, DEG_225,
        DEG_270, DEG_315, DEG_360
    }
}
