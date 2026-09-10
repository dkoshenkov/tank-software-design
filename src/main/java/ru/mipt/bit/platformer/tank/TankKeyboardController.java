package ru.mipt.bit.platformer.tank;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import static com.badlogic.gdx.Input.Keys.*;

public class TankKeyboardController {
    private static final Map<Direction, int[]> KEYS_BY_DIRECTION = createKeysByDirection();

    private final Tank tank;
    private final Predicate<GridPoint2> canMoveTo;

    public TankKeyboardController(Tank tank, Predicate<GridPoint2> canMoveTo) {
        this.tank = tank;
        this.canMoveTo = canMoveTo;
    }

    public void handleInput(Input input) {
        for (Map.Entry<Direction, int[]> binding : KEYS_BY_DIRECTION.entrySet()) {
            for (int keyCode : binding.getValue()) {
                if (input.isKeyPressed(keyCode)) {
                    tank.tryMove(binding.getKey(), canMoveTo);
                    break;
                }
            }
        }
    }

    private static Map<Direction, int[]> createKeysByDirection() {
        Map<Direction, int[]> keysByDirection = new LinkedHashMap<>();

        keysByDirection.put(Direction.UP, new int[]{UP, W});
        keysByDirection.put(Direction.LEFT, new int[]{LEFT, A});
        keysByDirection.put(Direction.DOWN, new int[]{DOWN, S});
        keysByDirection.put(Direction.RIGHT, new int[]{RIGHT, D});

        return Collections.unmodifiableMap(keysByDirection);
    }
}
