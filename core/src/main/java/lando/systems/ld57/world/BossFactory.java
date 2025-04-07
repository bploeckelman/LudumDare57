package lando.systems.ld57.world;

import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.components.BossBehavior;
import lando.systems.ld57.screens.BaseScreen;

public class BossFactory {

    public static Entity createBoss(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();
        new Position(entity, x, y);
        new Health(entity, 3000f);

        var body = addBossPart(entity, Anims.Type.BOSS_BODY, 15, 95, 30);
        var bowser = addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_BOWSER, 70, 127, 10);
        var wily = addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_WILY, 45, 150, 10);
        var gannon = addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_GANON, 10, 160, 10);
        var dracula = addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_DRACULA, -25, 140, 10);

        new BossBehavior(entity, body, bowser, wily, gannon, dracula);

        return entity;
    }

    private static Entity addBossPart(Entity entity, Anims.Type type, float x, float y, float radius) {
        var part = entity.scene.createEntity();
        var pos = entity.get(Position.class);
        new Position(part, pos.x(), pos.y());

        var scale = 1f;

        var animator = new Animator(part, type);
        animator.origin.set(45, 1);
        animator.defaultScale.scl(scale);

        Collider.makeCirc(part, Collider.Mask.enemy, x * scale, y * scale, radius * scale);
        DebugRender.makeForShapes(part, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return part;
    }
}
