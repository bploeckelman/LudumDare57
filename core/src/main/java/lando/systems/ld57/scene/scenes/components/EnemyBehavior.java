package lando.systems.ld57.scene.scenes.components;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld57.scene.components.Animator;
import lando.systems.ld57.scene.components.Collider;
import lando.systems.ld57.scene.components.Mover;
import lando.systems.ld57.scene.components.Position;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public abstract class EnemyBehavior extends Component {

    public EnemyBehavior(Entity entity) {
        super(entity);
    }

    public void attack() {

    }

    public void die() {

    }

    public void hurt() {

    }

    /**
     * Turns the entity around if it is near an edge.
     */
    public static void turnAroundAtEdge(Mover mover, Collider collider) {
        if (mover == null || collider == null) {
            return;
        }

        var rectShape = collider.shape(Collider.RectShape.class);
        var size = (rectShape != null) ? rectShape.rect.width : 0;
        var xDir = mover.velocity.x > 0 ? 1 : -1;
        var xOffset = (int) (xDir * size * (3f / 4f));

        var isSafe = collider.check(Collider.Mask.solid, xOffset, -1);
        if (!isSafe) {
            mover.invertX();
        }
    }

    public static boolean jumpTowardPlayer(Mover mover, Animator animator) {
        if (mover == null) {
            return false;
        }
        Vector2 tempVec1 = new Vector2();
        Vector2 tempVec2 = new Vector2();
        var player = mover.entity.scene.player;
        if (player != null) {
            var thisPos = mover.entity.get(Position.class);
            var playerPos = player.get(Position.class);
            tempVec1.set(thisPos.x(), thisPos.y());
            tempVec2.set(playerPos.x(), playerPos.y());
            var distance = tempVec1.dst2(tempVec2);
            if (distance < 1000f) {
                mover.velocity.y = 130f;
                mover.velocity.x = animator.facing * 100f;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void facePlayer(Animator animator) {
        if (animator == null) {
            return;
        }

        var player = animator.entity.scene.player;
        if (player != null) {
            var thisPos = animator.entity.get(Position.class);
            var playerPos = player.get(Position.class);
            var dx = playerPos.x() - thisPos.x();
            animator.facing = (dx > 0) ? 1 : -1;
            animator.autoFacing = false;
        }
    }
}
