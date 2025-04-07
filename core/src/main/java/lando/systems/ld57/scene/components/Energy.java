package lando.systems.ld57.scene.components;

import com.badlogic.gdx.math.MathUtils;
import lando.systems.ld57.scene.framework.Component;
import lando.systems.ld57.scene.framework.Entity;

public class Energy extends Component {
    public static final float MAX_ENERGY = 100f;
    public static final float ENERGY_RECHARGE_RATE = 1f;
    private float currentEnergy;

    public Energy(Entity entity) {
        super(entity);
        currentEnergy = MAX_ENERGY;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        currentEnergy = MathUtils.clamp(currentEnergy + ENERGY_RECHARGE_RATE * dt, 0f, MAX_ENERGY);
    }

    public void useEnergy(float amount) {
       currentEnergy =  MathUtils.clamp(currentEnergy - amount, 0, MAX_ENERGY);
    }

    public void addEnergy(float amount) {
        currentEnergy = MathUtils.clamp(currentEnergy + amount, 0, MAX_ENERGY);
    }

    public float getCurrentEnergy() {
        return currentEnergy;
    }

    public float getCurrentEnergyPercent() {
        return currentEnergy / MAX_ENERGY;
    }
}
