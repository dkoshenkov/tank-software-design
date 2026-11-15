package ru.mipt.bit.platformer.model.tank;

/**
 * По запасу здоровья определяет, как танк едет и может ли стрелять.
 */
enum DamageState implements TankState {
    LIGHT(1),
    MEDIUM(2),
    /**
     * Танк едет втрое медленнее и не может стрелять.
     */
    HEAVY(3) {
        @Override
        public void shoot(Runnable fire) {
        }
    };

    private final float moveDurationMultiplier;

    DamageState(float moveDurationMultiplier) {
        this.moveDurationMultiplier = moveDurationMultiplier;
    }

    static DamageState forHealth(int health, int maxHealth) {
        long scaledHealth = 100L * health;
        if (health <= 0 || scaledHealth < 15L * maxHealth) {
            return HEAVY;
        }
        if (scaledHealth <= 70L * maxHealth) {
            return MEDIUM;
        }
        return LIGHT;
    }

    @Override
    public float movementIncrement(float deltaSeconds, float baseMoveDurationSeconds) {
        return deltaSeconds / baseMoveDurationSeconds / moveDurationMultiplier;
    }

    @Override
    public void shoot(Runnable fire) {
        fire.run();
    }
}
