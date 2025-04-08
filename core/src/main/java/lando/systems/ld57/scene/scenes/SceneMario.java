package lando.systems.ld57.scene.scenes;

import lando.systems.ld57.Main;
import lando.systems.ld57.assets.Musics;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.Boundary;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.components.Tilemap;
import lando.systems.ld57.scene.components.ViewController;
import lando.systems.ld57.screens.GameScreen;
import lando.systems.ld57.world.EnemyFactory;
import lando.systems.ld57.world.EntityFactory;

public class SceneMario extends Scene<GameScreen> {

    private static final String TAG = SceneMario.class.getSimpleName();
    public static final Musics.Type music = Musics.Type.MARIO;

    public SceneMario(GameScreen screen) {
        super(screen);
        Main.game.audioManager.playMusic(SceneMario.music);
        screen.music = music;

        // configure the camera to emulate a low res display
        var width = 240;
        var height = 160;
        var camera = screen.worldCamera;
        camera.setToOrtho(false, width, height);
        camera.update();

        var map = EntityFactory.map(this, "maps/mario.tmx", "middle");
        var boundary = map.get(Boundary.class);
        var tilemap = map.get(Tilemap.class);

        makeMapObjects(tilemap);

        var playerPos = player.get(Position.class);

        viewer = EntityFactory.cam(this, boundary);
        viewer.get(ViewController.class).target(playerPos);

        EnemyFactory.bulletBill(this, 2700f,  50f);
        EnemyFactory.angrySun(this, 1200f,  80f);

    }
}
