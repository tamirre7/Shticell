package dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RangeDto {
    private final String name;
    private final String topLeft;
    private final String bottomRight;
    private final List<String> cellsInRange;
    private final boolean isActive;

    public RangeDto(String name, String topLeft, String bottomRight, boolean isActive) {
        this.name = name;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.isActive = isActive;
        this.cellsInRange = new ArrayList<>();
    }

    public RangeDto(String name, String topLeft, String bottomRight, List<String> cellsInRange, boolean isActive) {
        this.name = name;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.cellsInRange = cellsInRange;
        this.isActive = isActive;
    }

    public String getName() {return name;}
    public String getTopLeft() {return topLeft;}
    public String getBottomRight() {return bottomRight;}
    public List<String> getCellsInRange() {return Collections.unmodifiableList(cellsInRange);}
    public boolean isActive() {return isActive;}

}
