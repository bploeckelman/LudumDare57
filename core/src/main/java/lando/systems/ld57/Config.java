package lando.systems.ld57;

import lando.systems.ld57.utils.Util;
import text.formic.Stringf;

public class Config {

    public static final String window_title = "Captain N(ostalgia)";
    public static final String preferences_name = "lando-systems-ld57-prefs";
    public static final int window_width = 1280;
    public static final int window_height = 720;
    public static final int framebuffer_width = window_width;
    public static final int framebuffer_height = window_height;

    public static boolean stepped_frame = false;

    /**
     * Flags for enabling/disabling certain features, mostly used for debugging.
     * {@link Flag#GLOBAL} can be disabled to globally ignore any debug flag,
     * intended for disabling all debug features at once in production builds
     */
    public enum Flag {
        //@formatter:off
          GLOBAL(false)
        , LOG(true)
        , UI(false)
        , RENDER(false)
        , MUTE(false)
        , FRAME_STEP(false)
        , START_ON_GAMESCREEN(false)
        , SHOW_LAUNCHSCREEN(false)
        ;
        //@formatter:on

        private static final String TAG = "Config.Flag";

        private boolean isEnabled;

        Flag(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

        public boolean isEnabled() {
            return GLOBAL.isEnabled && isEnabled;
        }

        public boolean isDisabled() {
            return !GLOBAL.isEnabled || !isEnabled;
        }

        /**
         * @return whether the specified flag is enabled or not, taking into account {@link Flag#GLOBAL} for global control
         */
        public static boolean isEnabled(Flag flag) {
            return GLOBAL.isEnabled && flag.isEnabled;
        }

        /**
         * Enables this flag, regardless of its current status
         */
        public boolean enable() {
            isEnabled = true;
            Util.log(TAG, Stringf.format("enabled: %s='%b'", name(), isEnabled));
            return isEnabled;
        }

        /**
         * Disables this flag, regardless of its current status
         */
        public boolean disable() {
            isEnabled = false;
            Util.log(TAG, Stringf.format("disabled: %s='%b'", name(), isEnabled));
            return isEnabled;
        }

        /**
         * Enables or disables this flag, based of the specified value regardless of its current status
         *
         * @param enabled whether to enable or disable this flag
         * @return the new value of this flag, or false if a null flag type was provided
         */
        public boolean set(boolean enabled) {
            isEnabled = enabled;
            Util.log(TAG, Stringf.format("set: %s='%b'", name(), isEnabled));
            return isEnabled;
        }

        /**
         * Toggles whether this flag is enabled or disabled
         *
         * @return the new value of this flag, after toggling
         */
        public boolean toggle() {
            isEnabled = !isEnabled;
            Util.log(TAG, Stringf.format("toggled: %s='%b'", name(), isEnabled));
            return isEnabled;
        }
    }
}
