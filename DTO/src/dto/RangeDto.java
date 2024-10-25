package dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RangeDto {
    private final String name;                       // Name of the range
    private final String topLeft;                    // Top-left cell reference of the range
    private final String bottomRight;                 // Bottom-right cell reference of the range
    private final List<String> cellsInRange;         // List of cells included in the range
    private final boolean isActive;                   // Indicates whether the range is active

    // Constructor to initialize range with active status and an empty list of cells
    public RangeDto(String name, String topLeft, String bottomRight, boolean isActive) {
        this.name = name;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.isActive = isActive;
        this.cellsInRange = new ArrayList<>();
    }

    // Constructor to initialize range with a list of cells
    public RangeDto(String name, String topLeft, String bottomRight, List<String> cellsInRange, boolean isActive) {
        this.name = name;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.cellsInRange = cellsInRange;
        this.isActive = isActive;
    }

    public String getName() {
        return name;                                   // Returns the name of the range
    }

    public String getTopLeft() {
        return topLeft;                                // Returns the top-left cell reference
    }

    public String getBottomRight() {
        return bottomRight;                            // Returns the bottom-right cell reference
    }

    public List<String> getCellsInRange() {
        return Collections.unmodifiableList(cellsInRange); // Returns an unmodifiable list of cells in the range
    }

    public boolean isActive() {
        return isActive;                               // Returns the active status of the range
    }
}
