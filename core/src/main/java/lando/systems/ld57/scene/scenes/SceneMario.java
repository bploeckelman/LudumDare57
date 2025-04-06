package lando.systems.ld57.scene.scenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.assets.Fonts;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.Boundary;
import lando.systems.ld57.scene.components.ViewController;
import lando.systems.ld57.screens.GameScreen;
import lando.systems.ld57.world.EntityFactory;

public class SceneMario extends Scene<GameScreen> {

    private static final String TAG = SceneMario.class.getSimpleName();

    public SceneMario(GameScreen screen) {
        super(screen);

        // configure the camera to emulate a low res display
        var width = 240;
        var height = 160;
        var camera = screen.worldCamera;
        camera.setToOrtho(false, width, height);
        camera.update();

        var margin = 5f;
        var thickness = 2f;

        // NOTE(brian): this is a clunky way to setup an enclosed region
        //  of colliders, but it works well enough for testing purposes
        EntityFactory.boundary(this, margin + thickness, margin, thickness, height - 2 * margin);
        EntityFactory.boundary(this, width - margin - thickness * 2f, margin, thickness, height - 2 * margin);
        EntityFactory.boundary(this, margin + thickness, margin, width - 2 * margin - 2 * thickness, thickness);
        EntityFactory.boundary(this, margin + thickness, height - margin - thickness, width - 2 * margin - 2 * thickness, thickness);

        var boundsEntity = createEntity();
        var boundary = new Boundary(boundsEntity, new Rectangle(
            margin + thickness,
            margin + thickness,
            width   - 2 * (margin + thickness),
            height - 2 * (margin + thickness)));

        var x = camera.viewportWidth / 2f;
        var y = camera.viewportHeight / 2f;
        spawnPlayer(Characters.Type.MARIO, x, y);

        var cam = EntityFactory.cam(this, boundary);
        cam.get(ViewController.class).target(boundary.center());
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        var assets = screen.assets;
        var layout = assets.layout;
        var font = Fonts.Type.DOGICA.getDefault();
        var camera = screen.game.windowCamera;
        var x = camera.viewportWidth / 2f;
        var y = camera.viewportHeight / 2f;

        batch.setProjectionMatrix(screen.windowCamera.combined);
        layout.setText(font, TAG);
        font.draw(batch, layout, x - layout.width / 2f, y);
        batch.setProjectionMatrix(screen.worldCamera.combined);
    }
}
