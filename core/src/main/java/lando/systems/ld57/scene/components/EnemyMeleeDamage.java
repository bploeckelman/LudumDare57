package lando.systems.ld57.scene.components;

import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;
import lando.systems.ld57.utils.Callbacks;

import java.util.EnumSet;

public class EnemyMeleeDamage extends Component {

    public static class OnHitParam implements Callbacks.TypedArg.Params {
        public final Collider hitCollider;

        public OnHitParam(Collider hitCollider) {
            this.hitCollider = hitCollider;
        }
    }

    public Callbacks.TypedArg<OnHitParam> onHit;
    private final EnumSet<Collider.Mask> collidesWith = EnumSet.of(Collider.Mask.solid);


    public EnemyMeleeDamage(Entity entity) {
        this(entity, null);
    }

    public EnemyMeleeDamage(Entity entity, Callbacks.TypedArg<OnHitParam> onHit) {
        super(entity);
        this.onHit = onHit;
        collidesWith.clear();
        collidesWith.add(Collider.Mask.player);
    }

    public void setOnHit(Callbacks.TypedArg<OnHitParam> onHit) {
        this.onHit = onHit;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        var collider = entity.get(Collider.class);
        if (collider == null) return; // Early out
        var otherCollider = collider.checkAndGet(collidesWith, 0,0 );
        if (otherCollider != null) {
            var param = new OnHitParam(otherCollider);
            onHit.run(param);
        }
    }


}
