package lando.systems.ld57.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.assets.Anims;
import lando.systems.ld57.assets.Fonts;
import lando.systems.ld57.assets.Icons;
import lando.systems.ld57.assets.Patches;
import lando.systems.ld57.scene.Scene;
import lando.systems.ld57.scene.components.*;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.screens.BaseScreen;
import lando.systems.ld57.utils.Time;
import lando.systems.ld57.utils.Util;

public class EntityFactory {

    public static Entity koopa(Scene<? extends BaseScreen> scene, float x, float y) {
        var WIDTH = 16f;
        var HEIGHT = 24f;
        var entity = scene.createEntity();
        new Position(entity, x,y);
        new Health(entity, 1f);

        var scale = .5f;
        var animator =  new Animator(entity, Anims.Type.KOOPA_WALK);
        animator.origin.set(scale * -WIDTH, 0);
        animator.size.scl(-scale, scale);

        var collider = Collider.makeRect(entity, Collider.Mask.enemy,  -.5f * scale * WIDTH, 0, WIDTH * scale, HEIGHT * scale);
        var mover = new Mover(entity, collider);
        mover.velocity.setToRandomDirection().scl(10f);
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

                var hitDuration = 0.5f;
                var timer = entity.get(Timer.class);
                if (timer == null) {
                    // no active timer, create and attach one
                    new Timer(entity, hitDuration, () -> {
                        animator.play(Anims.Type.KOOPA_WALK);
                        entity.destroy(Timer.class);
                    });
                } else {
                    // timer was still in progress, reset it
                    timer.start(hitDuration);
                }

                switch (params.direction) {
                    case LEFT:
                    case RIGHT:
                        mover.invertX();
                        break;
                }
            }
        });
        DebugRender.makeForShapes(entity, DebugRender.DRAW_POSITION_AND_COLLIDER);

        return entity;
    }

    public static Entity goomba(Scene<? extends BaseScreen> scene, float x, float y) {
        var entity = scene.createEntity();
        new Position(entity, x,y);
        new Health(entity, 2f);

        var scale = 1f;
        var animator =  new Animator(entity, Anims.Type.GOOMBA_WALK);
        animator.origin.set(scale, 0);
        animator.size.scl(scale);

        var collider = Collider.makeRect(entity, Collider.Mask.enemy, -0.5f * scale, 0, 14, 14);

        var mover = new Mover(entity, collider);
        mover.velocity.setToRandomDirection().scl(MathUtils.random(50, 150));
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
                animator.play(Anims.Type.GOOMBA_DEATH);

                var hitDuration = 0.5f;
                var timer = entity.get(Timer.class);
                if (timer == null) {
                    // no active timer, create and attach one
                    new Timer(entity, hitDuration, () -> {
                        animator.play(Anims.Type.GOOMBA_WALK);
                        entity.destroy(Timer.class);
                    });
                } else {
                    // timer was still in progress, reset it
                    timer.start(hitDuration);
                }

                switch (params.direction) {
                    case LEFT:
                    case RIGHT:
                        mover.invertX();
                        break;
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
