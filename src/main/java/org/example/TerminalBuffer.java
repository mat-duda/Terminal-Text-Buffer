package org.example;

import java.util.ArrayList;
import java.util.List;

public class TerminalBuffer {
    public enum Directions{
        UP,DOWN,LEFT,RIGHT
    }
    private int width;
    private int height;
    private int maxScrollback;
    private List<Lines> activeScreen = new ArrayList<>();
    private List<Lines> inactiveScreen = new ArrayList<>();
    private TextAttributes textAttributes = TextAttributes.defaultAttributes();
    private int cursorX = 0;
    private int cursorY = 0;

    public void setup(int width, int height, int maxScrollback) {
        this.width = width;
        this.height = height;
        this.maxScrollback = maxScrollback;
        activeScreen.clear();
        for (int i = 0; i < height; i++) {
            activeScreen.add(new Lines(width, textAttributes));
        }
    }
    public void setAttributes(int fg, int bg, boolean bold, boolean italic, boolean underline) {
        this.textAttributes = new TextAttributes(fg, bg, bold, italic, underline);
    }

    private void pushLinesToInactiveScreen() {
        if (activeScreen.isEmpty()) return;

        Lines topLine = activeScreen.removeFirst();
        inactiveScreen.add(topLine);

        if (inactiveScreen.size() > maxScrollback) {
            inactiveScreen.removeFirst();
        }
        activeScreen.add(new Lines(width, textAttributes));
    }

    public List<Lines> getActiveScreen() {
        return activeScreen;
    }
    public void add(Lines line){
        activeScreen.add(line);
        if(activeScreen.size() > maxScrollback){
            activeScreen.removeFirst();
        }
    }
    public void write(String text){
        for (char c : text.toCharArray()) {
            Lines currentLine = activeScreen.get(cursorY);
            Cell currentCell = currentLine.getCells().get(cursorX);
            currentCell.update(c, textAttributes);
            cursorForward();
        }
    }
    private Lines getLineFromTotalBuffer(int index) {
        int scrollbackSize = inactiveScreen.size();
        if (index < 0 || index >= scrollbackSize + height) {
            throw new IndexOutOfBoundsException("Buffer index out of bounds");
        }

        if (index < scrollbackSize) {
            return inactiveScreen.get(index);
        } else {
            return activeScreen.get(index - scrollbackSize);
        }
    }
    public char getCharAt(int x, int y) {
        return getLineFromTotalBuffer(y).getCells().get(x).character;
    }
    public String getAttAt(int x, int y) {
        Lines line = getLineFromTotalBuffer(y);
        if (x < 0 || x >= width) return String.valueOf(' ');
        Cell cell = line.getCells().get(x);
        return String.format("FG: %d, BG: %d, Bold: %b, Italic: %b, Underline: %b",
                cell.foreGroundColor, cell.backgroundColor, cell.bold, cell.italic, cell.underline);
    }
    public void cursorForward() {
        if (cursorX < width - 1) {
            cursorX += 1;
        } else {
            cursorX = 0;
            if (cursorY < height - 1) {
                cursorY += 1;
            } else {
                    pushLinesToInactiveScreen();
            }
        }
    }

    public void print(){
        System.out.println(inactiveScreen.toString());
        System.out.print("\033[H\033[2J");

        System.out.flush();
        for (int y = 0; y < activeScreen.size(); y++) {
            Lines line = activeScreen.get(y);
            int highlightColumn = (y == cursorY) ? cursorX : -1;

            System.out.println(line.render(highlightColumn )+TextColor.RESET);

        }
        System.out.println(cursorX +" "+ cursorY );
    }
    public void moveCursor(Directions direction, int n){
        switch (direction){
            case UP -> cursorY = Math.max(0, cursorY - n);
            case DOWN -> cursorY = Math.min(height - 1, cursorY + n);
            case LEFT -> cursorX = Math.max(0, cursorX - n);
            case RIGHT -> cursorX = Math.min(width - 1, cursorX + n);
        }

    }
    public void insertEmpty() {
        Lines shiftedLine = activeScreen.removeFirst();
        inactiveScreen.add(shiftedLine);
        if (inactiveScreen.size() > maxScrollback) {
            inactiveScreen.removeFirst();
        }
        activeScreen.add(new Lines(this.width, this.textAttributes));
    }
    public void fillLine(char character){
        Lines lineToChange = activeScreen.get(cursorY);

        for (Cell cell : lineToChange.getCells()) {
            cell.update(character, textAttributes);
        }
    }

    public void clearAll(){
        for (Lines line : activeScreen) {
            line.clear();
        }
        for (Lines line : inactiveScreen) {
            line.clear();
        }
        inactiveScreen.clear();
        cursorX = 0;
        cursorY = 0;
    }
    public void insert(String text) {
        for (char c : text.toCharArray()) {
            Lines currentLine = activeScreen.get(cursorY);

            currentLine.insertCell(cursorX, new Cell(c, textAttributes));
            if (currentLine.getCells().size() > width) {
                Cell overflowCell = currentLine.getCells().remove(width);

                int nextY = cursorY + 1;
                if (nextY >= height) {
                    pushLinesToInactiveScreen();
                    nextY = height - 1;
                }

                activeScreen.get(nextY).getCells().addFirst(overflowCell);

                if (activeScreen.get(nextY).getCells().size() > width) {
                    activeScreen.get(nextY).getCells().remove(width);
                }
            }

            cursorForward();
        }
    }
    public String getLine(int y) {
        return getLineFromTotalBuffer(y).toString();
    }
    public String getScreenAndScroll(){
        StringBuilder sb = new StringBuilder();
        for (Lines line : inactiveScreen) {
            sb.append(line.toString()).append("\n");
        }
        for (Lines line : activeScreen) {
            sb.append(line.toString()).append("\n");
        }
        return sb.toString();
    }
    public String getScreen(){
        StringBuilder sb = new StringBuilder();
        for (Lines line : activeScreen) {
            sb.append(line.toString()).append("\n");
        }
        return sb.toString();
    }
    public void setCursor(int x, int y) {
        this.cursorX = Math.max(0, Math.min(x, width - 1));
        this.cursorY = Math.max(0, Math.min(y, height - 1));
    }
    public void resize(int newWidth, int newHeight) {
        int totalCellsBeforeCursor = (inactiveScreen.size() + cursorY) * width + cursorX;
        List<Cell> allCells = new ArrayList<>();
        for (Lines line : inactiveScreen) {
            allCells.addAll(line.getCells());
        }
        for (Lines line : activeScreen) {
            allCells.addAll(line.getCells());
        }
        this.width = newWidth;
        this.height = newHeight;

        List<Lines> allNewLines = new ArrayList<>();
        for (int i = 0; i < allCells.size(); i += newWidth) {
            Lines newLine = new Lines(newWidth, textAttributes);
            newLine.getCells().clear();

            for (int j = 0; j < newWidth; j++) {
                if (i + j < allCells.size()) {
                    newLine.getCells().add(allCells.get(i + j));
                } else {
                    newLine.getCells().add(new Cell(' ', textAttributes));
                }
            }
            allNewLines.add(newLine);
        }

        int screenStart = Math.max(0, allNewLines.size() - height);

        this.activeScreen = new ArrayList<>(allNewLines.subList(screenStart, allNewLines.size()));
        this.inactiveScreen = new ArrayList<>(allNewLines.subList(0, screenStart));

        while (activeScreen.size() < height) {
            activeScreen.add(new Lines(width, textAttributes));
        }
        this.cursorY = (totalCellsBeforeCursor / newWidth) - inactiveScreen.size();
        this.cursorX = totalCellsBeforeCursor % newWidth;

        this.cursorY = Math.max(0, Math.min(cursorY, height - 1));
        this.cursorX = Math.max(0, Math.min(cursorX, width - 1));
    }

}
