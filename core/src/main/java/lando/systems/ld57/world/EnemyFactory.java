package lando.systems.ld57.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.DebugRender;
import lando.systems.ld57.scene.components.Health;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.ParticleEmitter;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.components.Timer;
import lando.systems.ld57.scene.components.WaitToMove;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.components.AngrySunBehavior;
import lando.systems.ld57.scene.scenes.components.BulletBillBehavior;
import lando.systems.ld57.scene.scenes.components.CastleBatBehavior;
import lando.systems.ld57.scene.scenes.components.EagleBehavior;
import lando.systems.ld57.scene.scenes.components.GoombaBehavior;
import lando.systems.ld57.scene.scenes.components.KoopaBehavior;
import lando.systems.ld57.scene.scenes.components.MegaBatBehavior;
import lando.systems.ld57.scene.scenes.components.MonkeyBehavior;
import lando.systems.ld57.scene.scenes.components.SkeletonBehavior;
import lando.systems.ld57.screens.BaseScreen;

public class EnemyFactory {
    static Entity flyingChasingEnemy(Scene<? extends BaseScreen> scene, float x, float y, float width, float height, float scale, float speed, Anims.Type animType) {
        var entity = scene.createEntity();
        var pos = new Position(entity, x, y);
        new Health(entity, 2f);
        new ParticleEmitter(entity);
        new WaitToMove(entity);

        var animator = new Animator(entity, animType);
        animator.origin.set(scale * width, 0);
        animator.size.scl(scale);

        var collider = Collider.makeRect(entity, Collider.Mask.enemy, -.5f * scale * width, 0, width * scale, height * scale);
        var mover = new Mover(entity, collider);
        mover.velocity.set(-speed, 0f);
        mover.setCollidesWith(Collider.Mask.player);
        mover.setOnHit((params) -> {
            if (params.hitCollider.mask == Collider.Mask.player) {
                // bounce back
                mover.velocity.scl(-2f);
                mover.velocity.y = MathUtils.clamp(mover.velocity.y, -speed * 2, speed * 2);
                damagePlayerOnHit(params.hitCollider.entity, 1f);
            }
        });
        var timer = new Timer(entity);
        timer.onEnd = () -> {
            var playerPos = entity.scene.player.get(Position.class);
            var playerAnim = entity.scene.player.get(Animator.class);
            var direction = new Vector2(playerPos.x(), playerPos.y() + playerAnim.size.y / 2f).sub(pos.x(), pos.y()).nor();
            if (direction == Vector2.Zero) {
                mover.velocity.setToRandomDirection().scl(speed);
            } else {
                mover.velocity.set(direction).scl(speed);
            }
            timer.start(2f);
        };
        timer.start(2f);

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity angrySun(Scene<? extends BaseScreen> scene, float x, float y) {
        var WIDTH = 16f;
        var HEIGHT = 16f;
        var SPEED = 20f;
        var entity = scene.createEntity();
        var pos = new Position(entity, x, y);
        new Health(entity, 2f);
        new ParticleEmitter(entity);
        new AngrySunBehavior(entity);

        var scale = 2f;
        var animator = new Animator(entity, Anims.Type.ANGRY_SUN);
        animator.origin.set(scale * WIDTH, 0);
        animator.size.scl(scale);

        var collider = Collider.makeRect(entity, Collider.Mask.enemy, -.5f * scale * WIDTH, 0, WIDTH * scale, HEIGHT * scale - 10f);

        var mover = new Mover(entity, collider);
        mover.velocity.set(-SPEED, 0f);
        mover.setCollidesWith(Collider.Mask.player);

        mover.setOnHit((params) -> {
            if (params.hitCollider.mask == Collider.Mask.player) {
                damagePlayerOnHit(params.hitCollider.entity, 1f);
            }
        });
        var timer = new Timer(entity);
        timer.onEnd = () -> {
            var camera = entity.scene.screen.worldCamera;
            var direction = new Vector2(camera.position.x, camera.position.y + 30f).sub(pos.x(), pos.y()).nor();
            if (direction.x == 0) {
                direction = MathUtils.randomBoolean() ? new Vector2(1, 0) : new Vector2(-1, 0);
            }
            mover.velocity.set(direction).scl(SPEED);
            timer.start(2f);
        };
        timer.start(2f);

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity bulletBill(Scene<? extends BaseScreen> scene, float x, float y) {
        var WIDTH = 16f;
        var HEIGHT = 10f;
        var SPEED = 30f;
        var entity = scene.createEntity();
        var pos = new Position(entity, x, y);
        new Health(entity, 2f);
        new ParticleEmitter(entity);
        new BulletBillBehavior(entity);

        var scale = 3f;
        var animator =  new Animator(entity, Anims.Type.BULLET_BILL);
        animator.origin.set(scale * WIDTH, 0);
        animator.size.scl(scale); // X inverted to flip

        var collider = Collider.makeRect(entity, Collider.Mask.enemy,  -.5f * scale * WIDTH, 0, WIDTH * scale, HEIGHT * scale - 10f);
        var mover = new Mover(entity, collider);
        mover.velocity.set(-SPEED, 0f);
        mover.setCollidesWith(Collider.Mask.player);
        mover.setOnHit((params) -> {
            damagePlayerOnHit(params.hitCollider.entity, 1f);
            if (pos.x() < 0) {
                entity.scene.world.destroy(entity);
            }
        });

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity koopa(Scene<? extends BaseScreen> scene, float x, float y) {
        var WIDTH = 16f;
        var HEIGHT = 24f;
        var SPEED = 30f;
        var entity = scene.createEntity();
        new Position(entity, x,y);
        new Health(entity, 2f);
        new ParticleEmitter(entity);
        new KoopaBehavior(entity);

        var scale = .75f;
        var animator =  new Animator(entity, Anims.Type.KOOPA_WALK);
        animator.origin.set(scale * WIDTH, 0);
        animator.size.scl(scale); // X inverted to flip

        var collider = Collider.makeRect(entity, Collider.Mask.enemy,  -.5f * scale * WIDTH, 0, WIDTH * scale, HEIGHT * scale);
        var mover = new Mover(entity, collider);
        var randomDirection = MathUtils.randomBoolean() ? 1 : -1;
        mover.velocity.set(SPEED * randomDirection, 0f);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.addCollidesWith(Collider.Mask.player);

        mover.setOnHit((params) -> {
            if (params.hitCollider.mask == Collider.Mask.solid) {
                switch (params.direction) {
                    case LEFT:
                    case RIGHT:
                        mover.invertX();
                        break;
                }
            }
            else if (params.hitCollider.mask == Collider.Mask.player) {
                animator.play(Anims.Type.KOOPA_REVIVE);
                var hitDuration = 1.5f;
                mover.velocity.set(0f, 0f);
                var timer = entity.get(Timer.class);
                if (timer == null) {
                    // no active timer, create and attach one
                    damagePlayerOnHit(params.hitCollider.entity, 1f);
                    new Timer(entity, hitDuration, () -> {
                        animator.play(Anims.Type.KOOPA_WALK);
                        mover.velocity.set(SPEED * randomDirection, 0f);
                        entity.destroy(Timer.class);
                    });
                } else {
                    // timer was still in progress, reset it
                    timer.start(hitDuration);
                }
            }
        });
        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity goomba(Scene<? extends BaseScreen> scene, float x, float y) {
        var WIDTH = 14f;
        var HEIGHT = 14f;
        var SPEED = 15f;
        var entity = scene.createEntity();
        new Position(entity, x,y);
        new Health(entity, 2f);
        new ParticleEmitter(entity);
        new GoombaBehavior(entity);

        var scale = 1f;
        var animator =  new Animator(entity, Anims.Type.GOOMBA_WALK);
        animator.origin.set(scale * WIDTH / 2f, 0);
        animator.size.scl(scale);

        var collider = Collider.makeRect(entity, Collider.Mask.enemy, -0.5f * scale * WIDTH, 0, WIDTH, HEIGHT);

        var mover = new Mover(entity, collider);
        var randomDirection = MathUtils.randomBoolean() ? 1 : -1;
        mover.velocity.set(randomDirection * SPEED, 0f);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.addCollidesWith(Collider.Mask.player);

        mover.setOnHit((params) -> {
            if (params.hitCollider.mask == Collider.Mask.solid) {
                switch (params.direction) {
                    case LEFT:
                    case RIGHT:
                        mover.invertX();
                        break;
                }
            }
            else if (params.hitCollider.mask == Collider.Mask.player) {
                var hitDuration = 0.5f;
                var timer = entity.get(Timer.class);
                if (timer == null) {
                    damagePlayerOnHit(params.hitCollider.entity, 1f);
                    final float origSizeX = animator.size.x;
                    final float origSizeY = animator.size.y;

                    animator.play(Anims.Type.GOOMBA_DEATH);
                    animator.size.set(animator.size.x, animator.size.y * 0.33f);

                    // no active timer, create and attach one
                    new Timer(entity, hitDuration, () -> {
                        animator.play(Anims.Type.GOOMBA_WALK);
                        animator.size.set(origSizeX, origSizeY);
                        entity.destroy(Timer.class);
                    });
                } else {
                    // timer was still in progress, reset it
                    timer.start(hitDuration);
                }
            }
        });

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity skeleton(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();
        new Position(entity, x,y);
        new SkeletonBehavior(entity);
        new ParticleEmitter(entity);
        new Health(entity, 3f);

        var animator =  new Animator(entity, Anims.Type.SKELETON_MOVE);
        animator.origin.set(16, 1);

        var collider = Collider.makeRect(entity, Collider.Mask.enemy, -5, 0, 10, 28);

        var mover = new Mover(entity, collider);
        var randomDirection = MathUtils.randomBoolean() ? 1 : -1;
        mover.velocity.set(randomDirection * 10f, 0f);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.addCollidesWith(Collider.Mask.player);
        mover.setOnHit((params) -> {
            if (Collider.Mask.solid == params.hitCollider.mask) {
                if (params.direction.isHorizontal()) {
                    mover.invertX();
                }
            } else if (Collider.Mask.player == params.hitCollider.mask) {
                var stunDuration = 0.5f;
                var timer = entity.get(Timer.class);
                if (timer == null) {
                    var origVelX = mover.velocity.x;
                    var origVelY = mover.velocity.y;
                    mover.stopX();
                    // no active timer, create and attach one
                    new Timer(entity, stunDuration, () -> {
                        mover.velocity.set(origVelX, origVelY);
                        entity.destroy(Timer.class);
                    });
                } else {
                    // timer was still in progress, reset it
                    timer.start(stunDuration);
                }
            }
        });

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity megaBat(Scene<? extends BaseScreen> scene, float x, float y) {
        var width = 28f;
        var height = 20f;
        var scale = 1f;
        var speed = 20f;

        var entity = flyingChasingEnemy(scene, x, y, width, height, scale, speed, Anims.Type.BAT_FLYING);
        new MegaBatBehavior(entity);

        var animator = entity.get(Animator.class);
        animator.origin.set(scale * width * .5f, 0f);

        return entity;
    }

    public static Entity eagle(Scene<? extends BaseScreen> scene, float x, float y) {
        var width = 28f;
        var height = 28f;
        var scale = 1f;
        var speed = 20f;

        var entity = flyingChasingEnemy(scene, x, y, width, height, scale, speed, Anims.Type.EAGLE);
        new EagleBehavior(entity);

        var animator = entity.get(Animator.class);
        animator.origin.set(scale * width * .5f, 0f);

        return entity;
    }

    public static Entity castleBat(Scene<? extends BaseScreen> scene, float x, float y) {
        var width = 14f;
        var height = 14f;
        var scale = 1f;
        var speed = 10f;

        var entity = flyingChasingEnemy(scene, x, y, width, height, scale, speed, Anims.Type.BAT);
        new CastleBatBehavior(entity);

        var animator = entity.get(Animator.class);
        animator.origin.set(width / 2f, 0);

        return entity;
    }

    public static Entity monkey(Scene<? extends BaseScreen> scene, float x, float y) {
        var width = 16f;
        var height = 24f;
        var scale = .75f;
        var speed = 30f;

        var entity = scene.createEntity();
        new Position(entity, x,y);
        new Health(entity, 2f);
        new ParticleEmitter(entity);
        new MonkeyBehavior(entity);

        var animator =  new Animator(entity, Anims.Type.MONKEY_WALK);
        animator.origin.set(scale * width / 2f, 0);
        animator.size.scl(scale);

        var collider = Collider.makeRect(entity, Collider.Mask.enemy,  -.5f * scale * width, 0, width * scale, height * scale);

        var randomDirection = MathUtils.randomBoolean() ? 1 : -1;
        var mover = new Mover(entity, collider);
        mover.velocity.set(speed * randomDirection, 0f);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.addCollidesWith(Collider.Mask.player);
        mover.setOnHit((params) -> {
            if (params.hitCollider.mask == Collider.Mask.solid) {
                switch (params.direction) {
                    case LEFT:
                    case RIGHT:
                        mover.invertX();
                        break;
                }
            }
            else if (params.hitCollider.mask == Collider.Mask.player) {
                damagePlayerOnHit(params.hitCollider.entity, 1f);
            }
        });

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }
    private static void damagePlayerOnHit(Entity player, float damage) {
        var playerHealth = player.getIfActive(Health.class);
        if (playerHealth != null) {
            playerHealth.takeDamage(damage);
        }
    }
}
