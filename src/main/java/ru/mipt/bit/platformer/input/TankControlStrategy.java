package ru.mipt.bit.platformer.input;

import java.util.function.Consumer;

/**
 * Через порт {@link TankControls} получает команды игрока или ИИ.
 */
public interface TankControlStrategy {
    void collectCommands(Consumer<Runnable> submit);

    default void controlTank() {
        collectCommands(Runnable::run);
    }
}
