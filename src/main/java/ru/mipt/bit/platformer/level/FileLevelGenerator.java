package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.level.LevelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class FileLevelGenerator implements LevelGenerator {
    private final FileHandle file;

    public FileLevelGenerator(FileHandle file) {
        this.file = file;
    }

    @Override
    public LevelLayout generate() {
        List<String> rows = file
                .readString("UTF-8")
                .lines()
                .collect(Collectors.toList()
                );
        return parseLayout(rows);
    }

    private static LevelLayout parseLayout(List<String> rows) {
        if (rows.isEmpty() || rows.get(0).isEmpty()) {
            throw new IllegalArgumentException("Level must not be empty");
        }
        int width = rows.get(0).length();

        List<GridPoint2> treePositions = new ArrayList<>();
        GridPoint2 playerPosition = getPlayerPosition(rows, width, treePositions);
        if (playerPosition == null) {
            throw new IllegalArgumentException("Level must have exactly one player");
        }

        return new LevelLayout(width, rows.size(), playerPosition, treePositions);
    }

    private static GridPoint2 getPlayerPosition(List<String> rows, int width, List<GridPoint2> treePositions) {
        GridPoint2 playerPosition = null;

        for (int row = 0; row < rows.size(); row++) {
            String line = rows.get(row);
            if (line.length() != width) {
                throw new IllegalArgumentException("Level rows must have equal width");
            }
            playerPosition = parseRow(line, rows.size() - row - 1, treePositions, playerPosition);
        }
        return playerPosition;
    }

    private static GridPoint2 parseRow(String line, int y, List<GridPoint2> treePositions, GridPoint2 playerPosition) {
        for (int x = 0; x < line.length(); x++) {
            GridPoint2 position = new GridPoint2(x, y);
            playerPosition = parseCell(line.charAt(x), position, treePositions, playerPosition);
        }
        return playerPosition;
    }

    private static GridPoint2 parseCell(char cell, GridPoint2 position, List<GridPoint2> treePositions, GridPoint2 playerPosition) {
        switch (cell) {
            case 'T':
                treePositions.add(position);
                break;
            case 'X':
                if (playerPosition != null) {
                    throw new IllegalArgumentException("Level must have exactly one player");
                }
                return position;
            case '_':
                break;
            default:
                throw new IllegalArgumentException("Unknown level cell: " + cell);
        }
        return playerPosition;
    }
}
