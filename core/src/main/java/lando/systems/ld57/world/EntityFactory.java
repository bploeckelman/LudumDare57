package lando.systems.ld57.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld57.assets.*;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Boundary;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.DebugRender;
import lando.systems.ld57.scene.components.Health;
import lando.systems.ld57.scene.components.Image;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.ParticleEmitter;
import lando.systems.ld57.scene.components.Patch;
import lando.systems.ld57.scene.components.PlayerInput;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.components.Tilemap;
import lando.systems.ld57.scene.components.Timer;
import lando.systems.ld57.scene.components.ViewController;
import lando.systems.ld57.scene.components.Viewer;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.scene.scenes.PlayerBehavior;
import lando.systems.ld57.scene.scenes.components.GoombaBehavior;
import lando.systems.ld57.scene.scenes.components.SkeletonBehavior;
import lando.systems.ld57.screens.BaseScreen;
import lando.systems.ld57.utils.Time;
import lando.systems.ld57.utils.Util;

public class EntityFactory {

    public static Entity megaBat(Scene<? extends BaseScreen> scene, float x, float y) {
        var width = 28f;
        var height = 28f;
        var scale = 1f;
        var speed = 20f;
        var animType = Anims.Type.BAT_FLYING;
        var megaBat = flyingChasingEnemy(scene, x, y, width, height, scale, speed, animType);
        var animator = megaBat.get(Animator.class);
        animator.origin.set(scale * width * .5f, 0f);
        return megaBat;
    }

    public static Entity eagle(Scene<? extends BaseScreen> scene, float x, float y) {
        var width = 28f;
        var height = 28f;
        var scale = 1f;
        var speed = 20f;
        var animType = Anims.Type.EAGLE;
        var eagle = flyingChasingEnemy(scene, x, y, width, height, scale, speed, animType);
        var animator = eagle.get(Animator.class);
        animator.origin.set(scale * width * .5f, 0f);
        return eagle;
    }
    public static Entity castleBat(Scene<? extends BaseScreen> scene, float x, float y) {
        var bat = flyingChasingEnemy(scene, x, y, 14f, 14f, 1f, 10f, Anims.Type.BAT);
        var animator = bat.get(Animator.class);
        animator.origin.set(1f * 14f * .5f, 0f);
        return bat;
    }

    private static Entity flyingChasingEnemy(Scene<? extends BaseScreen> scene, float x, float y, float width, float height, float scale, float speed, Anims.Type animType) {
        var entity = scene.createEntity();
        var pos = new Position(entity, x, y);
        new Health(entity, 2f);
        new ParticleEmitter(entity);

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
                var playerBehavior = scene.player.get(PlayerBehavior.class);
                playerBehavior.knockBack(1f);
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

        var scale = 2f;
        var animator = new Animator(entity, Anims.Type.ANGRY_SUN);
        animator.origin.set(scale * WIDTH, 0);
        animator.size.scl(scale);

        var collider = Collider.makeRect(entity, Collider.Mask.enemy, -.5f * scale * WIDTH, 0, WIDTH * scale, HEIGHT * scale - 10f);

        var mover = new Mover(entity, collider);
        mover.velocity.set(-SPEED, 0f);
        mover.setCollidesWith(Collider.Mask.player);
        mover.setOnHit((params) -> {
            if (pos.x() < 0) {
                entity.scene.world.destroy(entity);
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

        var scale = 3f;
        var animator =  new Animator(entity, Anims.Type.BULLET_BILL);
        animator.origin.set(scale * WIDTH, 0);
        animator.size.scl(scale); // X inverted to flip

        var collider = Collider.makeRect(entity, Collider.Mask.enemy,  -.5f * scale * WIDTH, 0, WIDTH * scale, HEIGHT * scale - 10f);
        var mover = new Mover(entity, collider);
        mover.velocity.set(-SPEED, 0f);
        mover.setCollidesWith(Collider.Mask.player);
        mover.setOnHit((params) -> {
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

        new GoombaBehavior(entity);

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

    public static Entity heart(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();

        new Position(entity, x, y);

        var heartFull = Icons.Type.HEART.get();
        var heartBroken = Icons.Type.HEART_BROKEN.get();
        var tintFull = Color.RED.cpy();
        var tintBroken = Color.ORANGE.cpy();
        var width = heartFull.getRegionWidth();
        var height = heartFull.getRegionHeight();

        var image = new Image(entity, heartFull);
        image.tint.set(tintFull);
        image.origin.set(width / 2f, height / 2f);

        var collider = Collider.makeRect(entity, Collider.Mask.effect, -width / 2f, -height / 2f, width, height);

        var mover = new Mover(entity, collider);
        mover.velocity.setToRandomDirection().scl(MathUtils.random(300, 500));
        mover.addCollidesWith(Collider.Mask.npc);
        mover.setOnHit((params) -> {
            // change the image/tint to indicate a hit
            image.set(heartBroken);
            image.tint.set(tintBroken);

            // change the image back to normal after a bit and self-destruct the timer
            var hitDuration = 0.2f;
            var timer = entity.get(Timer.class);
            if (timer == null) {
                // no active timer, create and attach one
                new Timer(entity, hitDuration, () -> {
                    image.set(heartFull);
                    image.tint.set(tintFull);
                    entity.destroy(Timer.class);
                });
            } else {
                // timer was still in progress, reset it
                timer.start(hitDuration);
            }

            var hitEntity = params.hitCollider.entity;
            var hitPatch = hitEntity.getIfActive(Patch.class);

            // invert speed on the hit axis
            switch (params.direction) {
                case LEFT:
                case RIGHT: {
                    mover.invertX();
                    image.scale.set(0.66f, 1.33f);
                    if (hitPatch != null) {
                        hitPatch.scale.set(1.33f, 1f);
                    }
                }
                break;
                case UP:
                case DOWN: {
                    mover.invertY();
                    image.scale.set(1.33f, 0.66f);
                    if (hitPatch != null) {
                        hitPatch.scale.set(1f, 1.33f);
                    }
                }
                break;
            }
        });

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity circle(Scene<? extends BaseScreen> scene, float x, float y, float radius) {
        var entity = scene.createEntity();

        new Position(entity, x, y);

        var assets = scene.screen.assets;
        var region = assets.atlas.findRegion("objects/circle");
        var image = new Image(entity, region);
        image.size.set(2 * radius, 2 * radius);
        image.origin.set(radius, radius);
        image.tint.set(Util.randomColorPastel());

        var collider = Collider.makeCirc(entity, Collider.Mask.object, 0, 0, radius);

        var speed = MathUtils.random(100f, 300f);
        var mover = new Mover(entity, collider);
        mover.velocity.setToRandomDirection().scl(speed);
        mover.addCollidesWith(Collider.Mask.object, Collider.Mask.solid);
        mover.setOnHit((params) -> {
            // invert speed on the hit axis and add some squash/stretch
            switch (params.direction) {
                case LEFT:
                case RIGHT: {
                    mover.invertX();
                    image.scale.set(0.66f, 1.33f);
                } break;
                case UP:
                case DOWN: {
                    mover.invertY();
                    image.scale.set(1.33f, 0.66f);
                } break;
            }
        });

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static class HeroBehavior extends Component {

        private final Animator animator;
        private final Mover mover;

        public HeroBehavior(Entity entity, Animator animator, Mover mover) {
            super(entity);
            this.animator = animator;
            this.mover = mover;
        }

        @Override
        public void update(float dt) {
            if (mover.onGround()) {
                if (mover.velocity.x != 0) {
                    animator.play(Anims.Type.HERO_RUN);
                } else {
                    animator.play(Anims.Type.HERO_IDLE);
                }
            } else {
                if (mover.velocity.y > 0) {
                    animator.play(Anims.Type.HERO_JUMP);
                } else if (mover.velocity.y < 0) {
                    animator.play(Anims.Type.HERO_FALL);
                }
            }
        }
    }

    public static Entity hero(Scene<? extends BaseScreen> scene, float x, float y) {
        return hero(scene, x, y, 4f);
    }

    public static Entity hero(Scene<? extends BaseScreen> scene, float x, float y, float scale) {
        var entity = scene.createEntity();

        new Position(entity, x, y);

        var animator = new Animator(entity, Anims.Type.BELMONT_IDLE);
        animator.origin.set(8 * scale, 0);
        animator.size.scl(scale);

        // hero animation collider size
        var collider = Collider.makeRect(entity, Collider.Mask.npc, -4 * scale, 0, 6 * scale, 12 * scale);

        new PlayerInput(entity);
        new ParticleEmitter(entity);

        var mover = new Mover(entity, collider);
        mover.gravity = Mover.BASE_GRAVITY;
        mover.velocity.set(0, 0);
        mover.friction = .001f;
        mover.setOnHit((params) -> {
            switch (params.direction) {
                case LEFT:
                case RIGHT: {
                    // invert and save the new speed, then stop for a bit
                    mover.invertX();
                    var speedX = mover.velocity.x;
                    mover.stopX();

                    // do an 'oof'
                    Time.pause_for(0.1f);
                    animator.scale.scl(0.66f, 1.33f);

                    // take a moment to recover
                    // NOTE(brian): example use of Timer component for rudimentary game logic, 'self-destructing' when complete
                    var duration = 0.3f;
                    var timer = entity.get(Timer.class);
                    if (timer != null) {
                        // timer was still in progress, reset it
                        timer.start(duration);
                    } else {
                        new Timer(entity, duration, () -> {
                            // turn around
                            animator.facing *= -1;
                            // resume moving in the opposite direction
                            mover.velocity.x = speedX;
                            // jump!
                            mover.velocity.y = 125;

                            // self-destruct the timer
                            entity.destroy(Timer.class);
                        });
                    }
                }
                break;
            }
        });

        // behavior 'component' - example of an anonymous component used to implement simple game logic
        new HeroBehavior(entity, animator, mover);

        // quick test of using fonts from their asset container
        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);
        DebugRender.makeForBatch(entity, (params) -> {
            if (params instanceof DebugRender.TextParams) {
                var textParams = (DebugRender.TextParams) params;
                var batch = textParams.batch;
                var position = entity.get(Position.class);

                var font = textParams.fontType.getVariant(textParams.fontVariant);
                var assets = entity.scene.screen.assets;
                var layout = assets.layout;
                layout.setText(font, textParams.text);
                font.draw(batch, layout,
                    position.x() - layout.width / 2f,
                    position.y() + animator.size.y);
            }
        }, new DebugRender.TextParams(Fonts.Type.ROUNDABOUT, "tiny", "Hero"));

        return entity;
    }

    public static Entity boundary(Scene<? extends BaseScreen> scene, float x, float y, float w, float h) {
        var entity = scene.createEntity();

        var halfWidth = w / 2f;
        var halfHeight = h / 2f;

        new Position(entity, x + halfWidth, y + halfHeight);
        Collider.makeRect(entity, Collider.Mask.solid, -halfWidth, -halfHeight, w, h);

        var patch = new Patch(entity, Patches.Type.PLAIN);
        patch.origin.set(halfWidth, halfHeight);
        patch.size.set(w, h);

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity map(Scene<? extends BaseScreen> scene, String tmxFilePath, String collisionLayer) {
        return map(scene, 0, 0, tmxFilePath, collisionLayer);
    }

    public static Entity map(Scene<? extends BaseScreen> scene, float x, float y, String tmxFilePath, String collisionLayer) {
        var entity = scene.createEntity();

        new Position(entity, x, y);

        var tilemap = new Tilemap(entity, tmxFilePath, scene.screen.worldCamera,  scene.screen.batch);
        tilemap.makeGridCollider(collisionLayer);
        tilemap.makeBoundary();

        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity cam(Scene<? extends BaseScreen> scene, Boundary boundary) {
        var entity = scene.createEntity();

        new Viewer(entity, scene.screen.worldCamera);
        new ViewController(entity, boundary);

        return entity;
    }
}
