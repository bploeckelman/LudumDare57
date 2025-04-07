package lando.systems.ld57.scene.scenes;

import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.Boundary;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.components.Tilemap;
import lando.systems.ld57.scene.components.ViewController;
import lando.systems.ld57.screens.GameScreen;
import lando.systems.ld57.world.EntityFactory;

public class SceneMegaman extends Scene<GameScreen> {

    private static final String TAG = SceneMegaman.class.getSimpleName();
    public static final Musics.Type music = Musics.Type.MEGAMAN2;

    public SceneMegaman(GameScreen screen) {
        super(screen);

        // configure the camera to emulate a low res display
//        var resolution = Resolutions.NES_NATIVE;
        var resolution = Resolutions.NES_NATIVE_4_3;
//        var resolution = Resolutions.NES_SCALED_4_3;
        var camera = screen.worldCamera;
        camera.setToOrtho(false, resolution.x, resolution.y);
        camera.update();

        var map = EntityFactory.map(this, "maps/megaman.tmx", "middle");
        var boundary = map.get(Boundary.class);
        var tilemap = map.get(Tilemap.class);

        makeMapObjects(tilemap);

        var playerPos = player.get(Position.class);

        viewer = EntityFactory.cam(this, boundary);
        viewer.get(ViewController.class).target(playerPos);
    }
}
