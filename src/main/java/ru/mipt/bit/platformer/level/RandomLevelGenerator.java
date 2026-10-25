package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.level.LevelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class RandomLevelGenerator implements LevelGenerator {
    private final int width;
    private final int height;
    private final Random random;

    public RandomLevelGenerator(int width, int height, Random random) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.random = random;
    }

    @Override
    public LevelLayout generate() {
        GridPoint2 playerPosition = new GridPoint2(random.nextInt(width), random.nextInt(height));
        List<GridPoint2> treePositions = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                GridPoint2 position = new GridPoint2(x, y);
                if (!position.equals(playerPosition) && random.nextFloat() < 0.2f) {
                    treePositions.add(position);
                }
            }
        }
        return new LevelLayout(width, height, playerPosition, treePositions);
    }
}
