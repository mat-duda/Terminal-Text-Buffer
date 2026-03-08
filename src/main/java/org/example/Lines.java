package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Lines {
    private int width;
    private List<Cell> cells;
    public Lines(int width) {
        this.width = width;
        this.cells = new ArrayList<>(width);
        for(int i = 0; i < width; i++){
            cells.add(new Cell('.'));
        }
    }

    @Override
    public String toString() {
        return cells.stream()
                .map(Cell::toString)
                .collect(Collectors.joining(" "));
    }
}
