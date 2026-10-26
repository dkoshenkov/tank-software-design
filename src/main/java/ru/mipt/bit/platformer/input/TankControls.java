package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.input.ai.RandomTankController;
import ru.mipt.bit.platformer.input.command.MoveCommand;
import ru.mipt.bit.platformer.input.keyboard.TankKeyboardController;
import ru.mipt.bit.platformer.model.tank.Tank;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public final class TankControls {
    private final List<TankControlStrategy> strategies = new ArrayList<>();

    public TankControls(Input input, Tank player, List<Tank> computerTanks,
                        Predicate<GridPoint2> canMoveTo, Random random) {
        strategies.add(new TankKeyboardController(input, direction ->
                new MoveCommand(player, direction, canMoveTo).run()));
        for (Tank tank : computerTanks) {
            strategies.add(new RandomTankController(tank, canMoveTo, random));
        }
    }

    public void controlTanks() {
        for (TankControlStrategy strategy : strategies) {
            strategy.controlTank();
        }
    }
}
