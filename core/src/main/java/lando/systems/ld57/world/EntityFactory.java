package lando.systems.ld57.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.assets.Fonts;
import lando.systems.ld57.assets.Icons;
import lando.systems.ld57.assets.Patches;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.components.EnemyMarioBehavior;
import lando.systems.ld57.scene.scenes.components.GoombaBehavior;
import lando.systems.ld57.scene.scenes.components.MonkeyBehavior;
import lando.systems.ld57.scene.scenes.components.SkeletonBehavior;
import lando.systems.ld57.screens.BaseScreen;
import lando.systems.ld57.utils.Time;
import lando.systems.ld57.utils.Util;

public class EntityFactory {

    public static Entity boundary(Scene<? extends BaseScreen> scene, float x, float y, float w, float h) {
        var entity = scene.createEntity();

        var halfWidth = w / 2f;
        var halfHeight = h / 2f;

        new Position(entity, x + halfWidth, y + halfHeight);
        Collider.makeRect(entity, Collider.Mask.solid, -halfWidth, -halfHeight, w, h);

        var patch = new Patch(entity, Patches.Type.PLAIN);
        patch.origin.set(halfWidth, halfHeight);
        patch.size.set(w, h);

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity map(Scene<? extends BaseScreen> scene, String tmxFilePath, String collisionLayer) {
        return map(scene, 0, 0, tmxFilePath, collisionLayer);
    }

    public static Entity map(Scene<? extends BaseScreen> scene, float x, float y, String tmxFilePath, String collisionLayer) {
        var entity = scene.createEntity();

        new Position(entity, x, y);

        var tilemap = new Tilemap(entity, tmxFilePath, scene.screen.worldCamera,  scene.screen.batch);
        tilemap.makeGridCollider(collisionLayer);
        tilemap.makeBoundary();

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity cam(Scene<? extends BaseScreen> scene, Boundary boundary) {
        var entity = scene.createEntity();

        new Viewer(entity, scene.screen.worldCamera);
        new ViewController(entity, boundary);

        return entity;
    }
}
