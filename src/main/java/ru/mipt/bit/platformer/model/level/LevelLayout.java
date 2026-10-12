package ru.mipt.bit.platformer.model.level;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.List;

public final class LevelLayout {
    private final int width;
    private final int height;
    private final GridPoint2 playerPosition;
    private final List<GridPoint2> treePositions;

    public LevelLayout(int width, int height, GridPoint2 playerPosition, List<GridPoint2> treePositions) {
        LevelMap bounds = new LevelMap(width, height);
        if (!bounds.containsTile(playerPosition)) {
            throw new IllegalArgumentException("Player must be inside the level");
        }
        for (GridPoint2 position : treePositions) {
            if (!bounds.containsTile(position) || position.equals(playerPosition)) {
                throw new IllegalArgumentException("Tree must be inside the level and outside the player tile");
            }
        }
        this.width = width;
        this.height = height;
        this.playerPosition = new GridPoint2(playerPosition);
        this.treePositions = copyPositions(treePositions);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public GridPoint2 getPlayerPosition() {
        return new GridPoint2(playerPosition);
    }

    public List<GridPoint2> getTreePositions() {
        return copyPositions(treePositions);
    }

    private List<GridPoint2> copyPositions(List<GridPoint2> positions) {
        List<GridPoint2> copy = new ArrayList<>();
        for (GridPoint2 position : positions) {
            copy.add(new GridPoint2(position));
        }
        return copy;
    }
}
