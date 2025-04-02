package lando.systems.ld57.assets.framework;

import com.badlogic.gdx.utils.GdxRuntimeException;
import text.formic.Stringf;

public interface AssetEnum<ResourceType> {

    class MethodImplementationMissingException extends GdxRuntimeException {
        private static final long serialVersionUID = -8967847562533119940L;
        public MethodImplementationMissingException(String methodName) {
            super(Stringf.format("override %s to use param in concrete AssetType enum", methodName));
        }
    }

    default ResourceType get() {
        throw new MethodImplementationMissingException("resourceType");
    }
}
