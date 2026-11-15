package ru.mipt.bit.platformer.input;

import java.util.ArrayList;
import java.util.List;

public final class TankControls {
    private final List<TankControlStrategy> strategies = new ArrayList<>();
    private final List<Runnable> commands = new ArrayList<>();

    public void addStrategy(TankControlStrategy strategy) {
        strategies.add(strategy);
    }

    /**
     * Входной порт. Ставит команду в очередь.
     * @param command команда для следующего вызова {@link #controlTanks()}
     */
    public void submit(Runnable command) {
        commands.add(command);
    }

    public void controlTanks() {
        List<Runnable> frameCommands = new ArrayList<>(commands);
        commands.clear();
        collectCommands(frameCommands);
        executeCommands(frameCommands);
    }

    private void collectCommands(List<Runnable> frameCommands) {
        for (TankControlStrategy strategy : strategies) {
            strategy.collectCommands(frameCommands::add);
        }
    }

    private void executeCommands(List<Runnable> frameCommands) {
        for (Runnable command : frameCommands) {
            command.run();
        }
    }
}
