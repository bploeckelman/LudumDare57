package lando.systems.ld57.world;

import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.DebugRender;
import lando.systems.ld57.scene.components.Energy;
import lando.systems.ld57.scene.components.PickUp;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.screens.BaseScreen;
import lando.systems.ld57.utils.Util;

public class ItemFactory {
    public static Entity energyCapsule(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();
        var width = 30f;
        var height = 24f;
        var scale = .5f;

        new Position(entity, x, y);
        var pickup = new PickUp(entity);
        var animator = new Animator(entity, Anims.Type.ENERGY_CAPSULE);
        animator.origin.set(scale * width / 2f, 0);
        Collider.makeRect(entity, Collider.Mask.object, -.5f * scale * width, 0, width * scale, height * scale);
        pickup.onHit = (params) -> {
            Util.log("Pickup", "Collided with player");
            var playerEnergy = scene.player.get(Energy.class);
            if (playerEnergy != null) {
                playerEnergy.addEnergy(5);
            }
            entity.selfDestruct();
        };

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }
}
