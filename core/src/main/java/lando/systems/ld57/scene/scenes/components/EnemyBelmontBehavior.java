package lando.systems.ld57.scene.scenes.components;

import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.scene.framework.Entity;

public class EnemyBelmontBehavior extends MiniBossBehavior {

    private float lastAttack;


    public EnemyBelmontBehavior(Entity entity) {
        super(entity, Characters.Type.BELMONT.get());
    }
}
