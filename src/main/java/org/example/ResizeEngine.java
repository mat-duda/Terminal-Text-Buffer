package org.example;

import java.util.ArrayList;
import java.util.List;

public class ResizeEngine {
    public static class ResizeResult {
        public List<Lines> inactive;
        public List<Lines> active;
        public int cursorX;
        public int cursorY;
    }

    public static ResizeResult reflow(List<Lines> inactive, List<Lines> active, int oldWidth, int newWidth, int newHeight, int oldCursorX, int oldCursorY, TextAttributes attr) {
        List<Cell> flattened = new ArrayList<>();
        for (Lines line : inactive) flattened.addAll(line.getCells());
        for (Lines line : active) flattened.addAll(line.getCells());
        int totalCellsBeforeCursor = (inactive.size() + oldCursorY) * oldWidth + oldCursorX;
        List<Lines> allNewLines = new ArrayList<>();
        for (int i = 0; i < flattened.size(); i += newWidth) {
            Lines newLine = new Lines(newWidth, attr);
            newLine.getCells().clear();
            for (int j = 0; j < newWidth; j++) {
                if (i + j < flattened.size()) {
                    newLine.getCells().add(flattened.get(i + j));
                } else {
                    newLine.getCells().add(new Cell(' ', attr));
                }
            }
            allNewLines.add(newLine);
        }

        ResizeResult result = new ResizeResult();
        int screenStart = Math.max(0, allNewLines.size() - newHeight);

        result.inactive = new ArrayList<>(allNewLines.subList(0, screenStart));
        result.active = new ArrayList<>(allNewLines.subList(screenStart, allNewLines.size()));

        while (result.active.size() < newHeight) {
            result.active.add(new Lines(newWidth, attr));
        }
        int newTotalY = totalCellsBeforeCursor / newWidth;
        result.cursorX = Math.min(totalCellsBeforeCursor % newWidth, newWidth - 1);
        result.cursorY = Math.max(0, Math.min(newTotalY - result.inactive.size(), newHeight - 1));

        return result;
    }
}