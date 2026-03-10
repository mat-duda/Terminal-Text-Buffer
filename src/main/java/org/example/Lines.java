package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Lines {
    private final List<Cell> cells;
    private TextAttributes attributes;
    public Lines(int width, TextAttributes attributes) {
        this.attributes = attributes;
        this.cells = new ArrayList<>(width);
        for (int i = 0; i < width; i++) {
            cells.add(new Cell(' ',attributes));
        }
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void insertCell(int index, Cell newCell) {
        if (index >= 0 && index <= cells.size()) {
            cells.add(index, newCell);
        }
    }

    public String render(int cursorX) {
        return IntStream.range(0, cells.size())
                .mapToObj(i -> cells.get(i).render(i == cursorX))
                .collect(Collectors.joining(""));
    }

    public void clear() {
       for(Cell cell: cells){
           cell.update(' ', attributes);
       }
    }

    @Override
    public String toString() {
        return cells.toString();
    }
}