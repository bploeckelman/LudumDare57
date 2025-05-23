package lando.systems.ld57.world;

import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.DebugRender;
import lando.systems.ld57.scene.components.Health;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.ParticleEmitter;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.components.WaitToMove;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.components.*;
import lando.systems.ld57.screens.BaseScreen;
import lando.systems.ld57.utils.Direction;

public class BossFactory {

    public static Entity createBoss(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();
        new Position(entity, x, y);
        new ParticleEmitter(entity);

        var body = addBossPart(entity, Anims.Type.BOSS_BODY, 15, 95, 30);
        new Health(body, 30000);
        var bowser = addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_BOWSER, 70, 127, 10);
        new Health(bowser, 3);
        var wily = addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_WILY, 45, 150, 10);
        new Health(wily, 3);
        var gannon = addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_GANON, 10, 160, 10);
        new Health(gannon, 3);
        var dracula = addBossPart(entity, Anims.Type.BOSS_NECK_HEAD_DRACULA, -25, 140, 10);
        new Health(dracula, 3);

        new BossBehavior(entity, body, bowser, wily, gannon, dracula);
        entity.scene.boss = entity;

        return entity;
    }

    private static Entity addBossPart(Entity entity, Anims.Type type, float x, float y, float radius) {
        var part = entity.scene.createEntity();
        var pos = entity.get(Position.class);
        new Position(part, pos.x(), pos.y());
        new ParticleEmitter(part);

        var scale = 1f;

        var animator = new Animator(part, type);
        animator.origin.set(45, 1);
        animator.defaultScale.scl(scale);


        Collider.makeCirc(part, Collider.Mask.enemy, x * scale, y * scale, radius * scale);
        DebugRender.makeForShapes(part, DebugRender.DRAW_POSITION_AND_COLLIDER);
        new AngrySunBehavior(part);
        return part;
    }

    public static Entity marioBoss(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();
        new Position(entity, x, y);
        new EnemyMarioBehavior(entity);
        new WaitToMove(entity);
        new ParticleEmitter(entity);
        new Health(entity, 4f);
        var animator =  new Animator(entity, Anims.Type.MARIO_IDLE);
        animator.origin.set(16, 1);
        animator.facing = -1;
        var collider = Collider.makeRect(entity, Collider.Mask.enemy, -5, 0, 10, 28);

        var mover = new Mover(entity, collider);
        mover.gravity = Mover.BASE_GRAVITY * .9f;
        mover.addCollidesWith(Collider.Mask.player);

        mover.setOnHit((params -> {
            var hitEntity = params.hitCollider.entity;
            if (params.hitCollider.mask == Collider.Mask.player && params.direction == Direction.Relative.DOWN) {
                hitEntity.getIfActive(Health.class).takeDamage(1f);
                mover.velocity.y = 300;
            }
            if (params.hitCollider.mask == Collider.Mask.solid && params.direction == Direction.Relative.UP) {
                mover.velocity.y = 0;
            }
        }));

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        entity.scene.boss = entity;
        return entity;
    }

    public static Entity belmontBoss(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();
        new Position(entity, x, y);
        new EnemyBelmontBehavior(entity);
        new WaitToMove(entity);
        new ParticleEmitter(entity);
        new Health(entity, 4f);
        var animator =  new Animator(entity, Anims.Type.BELMONT_IDLE);
        animator.origin.set(25, 3);
        animator.facing = -1;
        var collider = Collider.makeRect(entity, Collider.Mask.enemy, -5, 0, 10, 28);

        var mover = new Mover(entity, collider);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.addCollidesWith(Collider.Mask.player);

        mover.setOnHit((params -> {
            if (params.hitCollider.mask == Collider.Mask.solid && params.direction == Direction.Relative.UP) {
                mover.velocity.y = 0;
            }
        }));

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        entity.scene.boss = entity;

        return entity;
    }

    public static Entity linkBoss(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();
        new Position(entity, x, y);
        new EnemyLinkBehavior(entity);
        new WaitToMove(entity);
        new ParticleEmitter(entity);
        new Health(entity, 4f);
        var animator =  new Animator(entity, Anims.Type.LINK_IDLE);
        animator.origin.set(16, 1);
        animator.facing = -1;
        var collider = Collider.makeRect(entity, Collider.Mask.enemy, -5, 0, 10, 28);

        var mover = new Mover(entity, collider);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.addCollidesWith(Collider.Mask.player);

        mover.setOnHit((params -> {
            if (params.hitCollider.mask == Collider.Mask.solid && params.direction == Direction.Relative.UP) {
                mover.velocity.y = 0;
            }
        }));

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        entity.scene.boss = entity;

        return entity;
    }

    public static Entity megamanBoss(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();
        new Position(entity, x, y);
        new EnemyMegamanBehavior(entity);
        new WaitToMove(entity);
        new ParticleEmitter(entity);
        new Health(entity, 6f);
        var animator =  new Animator(entity, Anims.Type.MEGAMAN_IDLE);
        animator.origin.set(16, 1);
        animator.facing = -1;
        var collider = Collider.makeRect(entity, Collider.Mask.enemy, -5, 0, 10, 28);

        var mover = new Mover(entity, collider);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.addCollidesWith(Collider.Mask.player);

        mover.setOnHit((params -> {
            if (params.hitCollider.mask == Collider.Mask.solid && params.direction == Direction.Relative.UP) {
                mover.velocity.y = 0;
            }
        }));

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        entity.scene.boss = entity;

        return entity;
    }
}
