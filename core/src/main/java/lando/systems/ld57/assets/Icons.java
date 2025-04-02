package lando.systems.ld57.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld57.assets.framework.AssetContainer;
import lando.systems.ld57.assets.framework.AssetEnum;
import lando.systems.ld57.utils.Util;
import text.formic.Stringf;

public class Icons extends AssetContainer<Icons.Type, TextureRegion> {

    public static AssetContainer<Type, TextureRegion> container;

    private static final String folder = "icon/";

    public enum Type implements AssetEnum<TextureRegion> {
        CARD_STACK("card-stack"),
        CIRCLE_CHECK("circle-check"),
        CIRCLE_X("circle-x"),
        HEART("heart"),
        HEART_BROKEN("heart-broken"),
        NOTEPAD("notepad"),
        PERSON_PLAY("person-play"),
        PERSON_X("person-x"),
        PUZZLE("puzzle");

        private final String regionName;

        Type(String regionName) {
            this.regionName = folder + regionName;
        }

        @Override
        public TextureRegion get() {
            return container.get(this);
        }
    }

    public Icons() {
        super(Icons.class, TextureRegion.class);
        Icons.container = this;
    }

    @Override
    public void init(Assets assets) {
        var atlas = assets.atlas;
        for (var type : Type.values()) {
            var region = atlas.findRegion(type.regionName);
            if (region == null) {
                Util.log(containerClassName, Stringf.format("init(): atlas region '%s' not found for type '%s'", type.regionName, type.name()));
                continue;
            }
            resources.put(type, region);
        }
    }
}
