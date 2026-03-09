package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Lines {
    private int width;
    private List<Cell> cells;

    public Lines(int width, int defaultBackgroundColor) {
        this.width = width;
        this.cells = new ArrayList<>(width);
        for (int i = 0; i < width; i++) {
            cells.add(new Cell(' ',7,defaultBackgroundColor,false,false,false));
        }
    }

    public List<Cell> getCells() {
        return cells;
    }

    public Cell getCell(int i) {
        return cells.get(i);
    }

    public String render(int cursorX) {
        return IntStream.range(0, cells.size())
                .mapToObj(i -> {
                    return cells.get(i).render(i == cursorX);
                })
                .collect(Collectors.joining(""));
    }
}