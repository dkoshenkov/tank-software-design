package ru.mipt.bit.platformer.model.tank;

/**
 * Определяет скорость танка и возможность выстрела.
 */
interface TankState {
    float movementIncrement(float deltaSeconds, float baseMoveDurationSeconds);
    void shoot(Runnable fire);
}
