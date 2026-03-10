package org.example;

import java.util.ArrayList;
import java.util.List;

public class TerminalBuffer {
    public enum Directions{
        UP,DOWN,LEFT,RIGHT
    }
    private int width;
    private int height;
    private int maxScrollbackLines;
    private List<Lines> activeScreen = new ArrayList<>();
    private List<Lines> inactiveScreen = new ArrayList<>();
    private int currentForegroundColor  ;
    private int currentBackgroundColor ;
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isUnderline = false;
    private int cursorX = 0;
    private int cursorY = 0;

    public int getWidth() {
        return width;
    }
    public void setAttributes(int fg, int bg, boolean bold, boolean italic, boolean underline) {
        this.currentForegroundColor = fg;
        this.currentBackgroundColor = bg;
        this.isBold = bold;
        this.isItalic = italic;
        this.isUnderline = underline;
    }
    public void  setup(int width, int height, int maxScrollbackLines){
        this.width = width;
        this.height = height;
        this.maxScrollbackLines = maxScrollbackLines;
        this.activeScreen = new ArrayList<>(height);

        for(int i = 0; i < height; i++){
            activeScreen.add(new Lines(width,this.currentBackgroundColor));

        }

    }

    public int getCursorX() {
        return cursorX;
    }
    private void pushLinesToInactiveScreen() {
        if (activeScreen.isEmpty()) return;

        Lines topLine = activeScreen.remove(0);
        inactiveScreen.add(topLine);

        if (inactiveScreen.size() > maxScrollbackLines) {
            inactiveScreen.remove(0);
        }
        activeScreen.add(new Lines(width, this.currentBackgroundColor));
    }
    public int getCursorY() {
        return cursorY;
    }

    public void setCurrentForegroundColor(int currentForegroundColor) {
        this.currentForegroundColor = currentForegroundColor;
    }

    public void setCurrentBackgroundColor(int currentBackgroundColor) {
        this.currentBackgroundColor = currentBackgroundColor;
    }

    public List<Lines> getActiveScreen() {
        return activeScreen;
    }
    public void add(Lines line){
        activeScreen.add(line);
        if(activeScreen.size() > maxScrollbackLines){
            activeScreen.remove(0);
        }
    }
    public void write(String text){
        for (char c : text.toCharArray()) {
            Lines currentLine = activeScreen.get(cursorY);
            Cell currentCell = currentLine.getCells().get(cursorX);
            currentCell.update(c, currentForegroundColor, currentBackgroundColor, isBold, isItalic, isUnderline);
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
        Lines line = getLineFromTotalBuffer(y);
        if (x < 0 || x >= width) return ' ';
        return line.getCells().get(x).character;
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
        Lines shiftedLine = activeScreen.remove(0);
        inactiveScreen.add(shiftedLine);
        if (inactiveScreen.size() > maxScrollbackLines) {
            inactiveScreen.remove(0);
        }
        activeScreen.add(new Lines(this.width, this.currentBackgroundColor));
    }
    public void fillLine(char character){
        Lines lineToChange = activeScreen.get(cursorY);

        for (Cell cell : lineToChange.getCells()) {
            cell.update(character, currentForegroundColor, currentBackgroundColor, isBold, isItalic, isUnderline);
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
    public int getCurrentBackgroundColor() {
        return currentBackgroundColor;
    }

    public List<Lines> getInactiveScreen() {
        return inactiveScreen;
    }

    public void insert(String text) {
        for (char c : text.toCharArray()) {
            Lines currentLine = activeScreen.get(cursorY);

            currentLine.insertCell(cursorX, new Cell(c, currentForegroundColor, currentBackgroundColor, isBold, isItalic, isUnderline));
            if (currentLine.getCells().size() > width) {
                Cell overflowCell = currentLine.getCells().remove(width);

                int nextY = cursorY + 1;
                if (nextY >= height) {
                    pushLinesToInactiveScreen();
                    nextY = height - 1;
                }

                activeScreen.get(nextY).getCells().add(0, overflowCell);

                if (activeScreen.get(nextY).getCells().size() > width) {
                    activeScreen.get(nextY).getCells().remove(width);
                }
            }

            cursorForward();
        }
    }
    public void setCursor(int x, int y) {
        this.cursorX = Math.max(0, Math.min(x, width - 1));
        this.cursorY = Math.max(0, Math.min(y, height - 1));
    }
}
