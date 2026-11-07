package ru.mipt.bit.platformer.input;

import java.util.ArrayList;
import java.util.List;

public final class TankControls {
    private final List<TankControlStrategy> strategies = new ArrayList<>();
    private final List<Runnable> commands = new ArrayList<>();

    public void addStrategy(TankControlStrategy strategy) {
        strategies.add(strategy);
    }

    public void submit(Runnable command) {
        commands.add(command);
    }

    public void controlTanks() {
        commands.clear();
        for (TankControlStrategy strategy : strategies) {
            strategy.controlTank();
        }
        for (Runnable command : commands) {
            command.run();
        }
        commands.clear();
    }
}
