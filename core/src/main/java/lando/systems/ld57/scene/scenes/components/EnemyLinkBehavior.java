package lando.systems.ld57.scene.scenes.components;

import lando.systems.ld57.assets.Characters;
import lando.systems.ld57.scene.framework.Entity;

public class EnemyLinkBehavior extends MiniBossBehavior {
    public EnemyLinkBehavior(Entity entity) {
        super(entity, Characters.Type.LINK.get());
    }

}
