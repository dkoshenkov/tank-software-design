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
        List<GridPoint2> treePositions = generateTrees(playerPosition);
        return new LevelLayout(width, height, playerPosition, treePositions);
    }

    private List<GridPoint2> generateTrees(GridPoint2 playerPosition) {
        List<GridPoint2> treePositions = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            addTreesInRow(y, playerPosition, treePositions);
        }
        return treePositions;
    }

    private void addTreesInRow(int y, GridPoint2 playerPosition, List<GridPoint2> trees) {
        for (int x = 0; x < width; x++) {
            GridPoint2 position = new GridPoint2(x, y);
            boolean nearPlayer = Math.abs(x - playerPosition.x) <= 1 && Math.abs(y - playerPosition.y) <= 1;
            if (!nearPlayer && random.nextFloat() < 0.2f) {
                trees.add(position);
            }
        }
    }

}
