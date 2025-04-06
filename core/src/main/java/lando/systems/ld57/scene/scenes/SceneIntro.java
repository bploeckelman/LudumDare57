package lando.systems.ld57.scene.scenes;

import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Boundary;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.DebugRender;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.ParticleEmitter;
import lando.systems.ld57.scene.components.PlayerInput;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.components.Tilemap;
import lando.systems.ld57.scene.components.ViewController;
import lando.systems.ld57.screens.GameScreen;
import lando.systems.ld57.utils.Util;
import lando.systems.ld57.world.EntityFactory;
import text.formic.Stringf;

public class SceneIntro extends Scene<GameScreen> {

    private static final String TAG = SceneIntro.class.getSimpleName();

    public SceneIntro(GameScreen screen) {
        super(screen);

        // configure the camera to emulate a low res display
        var width = 240;
        var height = 160;
        var camera = screen.worldCamera;
        camera.setToOrtho(false, width, height);
        camera.update();

        var map = EntityFactory.map(this, "maps/start.tmx", "middle");
        var boundary = map.get(Boundary.class);
        var tilemap = map.get(Tilemap.class);

        makeMapObjects(tilemap);

        var cam = EntityFactory.cam(this, boundary);
        cam.get(ViewController.class).target(boundary.center());

        EntityFactory.goomba(this, (float) width / 2,  height * 0.2f);
    }

    private void makeMapObjects(Tilemap tilemap) {
        var objectLayerName = "objects";

        var layer = tilemap.map.getLayers().get(objectLayerName);
        var objects = layer.getObjects();

        for (var object : objects) {
            Util.log(TAG, object, obj -> Stringf.format(
                    "parsing map object: %s[name='%s', pos=(%.1f, %.1f)]...",
                    obj.getClass().getSimpleName(),
                    object.getName(),
                    object.getProperties().get("x", Float.class),
                    object.getProperties().get("y", Float.class)));

            var name = object.getName();
            var props = object.getProperties();
            var x = props.get("x", Float.class);
            var y = props.get("y", Float.class);

            if (name.equals("spawn")) {
                spawnPlayer(Characters.Type.OLDMAN, x, y);
            }
        }
    }
}
