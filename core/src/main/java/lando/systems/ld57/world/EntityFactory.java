package lando.systems.ld57.world;

import lando.systems.ld57.assets.Patches;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.Boundary;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.DebugRender;
import lando.systems.ld57.scene.components.Patch;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.components.Tilemap;
import lando.systems.ld57.scene.components.ViewController;
import lando.systems.ld57.scene.components.Viewer;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.screens.BaseScreen;

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
