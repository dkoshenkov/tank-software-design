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
        List<String> rows = file.readString("UTF-8").lines().collect(Collectors.toList());
        if (rows.isEmpty() || rows.get(0).isEmpty()) {
            throw new IllegalArgumentException("Level must not be empty");
        }
        int width = rows.get(0).length();
        GridPoint2 playerPosition = null;
        List<GridPoint2> treePositions = new ArrayList<>();
        for (int row = 0; row < rows.size(); row++) {
            String line = rows.get(row);
            if (line.length() != width) {
                throw new IllegalArgumentException("Level rows must have equal width");
            }
            for (int x = 0; x < width; x++) {
                GridPoint2 position = new GridPoint2(x, rows.size() - row - 1);
                char cell = line.charAt(x);
                switch (cell) {
                    case 'T':
                        treePositions.add(position);
                        break;
                    case 'X':
                        if (playerPosition != null) {
                            throw new IllegalArgumentException("Level must have exactly one player");
                        }
                        playerPosition = position;
                        break;
                    case '_':
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown level cell: " + cell);
                }
            }
        }
        if (playerPosition == null) {
            throw new IllegalArgumentException("Level must have exactly one player");
        }
        return new LevelLayout(width, rows.size(), playerPosition, treePositions);
    }
}
