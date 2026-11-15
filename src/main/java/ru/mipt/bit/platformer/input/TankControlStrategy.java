package ru.mipt.bit.platformer.input;

import java.util.function.Consumer;

public interface TankControlStrategy {
    void collectCommands(Consumer<Runnable> submit);

    default void controlTank() {
        collectCommands(Runnable::run);
    }
}
