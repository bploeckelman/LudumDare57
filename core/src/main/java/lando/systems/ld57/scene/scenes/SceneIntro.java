package lando.systems.ld57.scene.scenes;

import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.Boundary;
import lando.systems.ld57.scene.components.Tilemap;
import lando.systems.ld57.scene.components.ViewController;
import lando.systems.ld57.screens.GameScreen;
import lando.systems.ld57.world.BossFactory;
import lando.systems.ld57.world.EnemyFactory;
import lando.systems.ld57.world.EntityFactory;
import lando.systems.ld57.world.ItemFactory;

public class SceneIntro extends Scene<GameScreen> {

    private static final String TAG = SceneIntro.class.getSimpleName();
    public static Musics.Type music = Musics.Type.MEGAMAN3;

    public SceneIntro(GameScreen screen) {
        super(screen);

        // configure the camera to emulate a low res display
        var width = 240;
        var height = 160;
        var camera = screen.worldCamera;
        camera.setToOrtho(false, width, height);
        camera.update();

        var map = EntityFactory.map(this, "maps/test/start.tmx", "middle");
        var boundary = map.get(Boundary.class);
        var tilemap = map.get(Tilemap.class);

        makeMapObjects(tilemap);

        ItemFactory.energyCapsule(this, 30, 20);

        BossFactory.linkBoss(this, 50, 70);

        viewer = EntityFactory.cam(this, boundary);
        viewer.get(ViewController.class).target(boundary.center());

//        screen.playerHealthMeter.setEntity(player);
//        screen.bossHealthMeter.setEntity(boss);
    }
}
