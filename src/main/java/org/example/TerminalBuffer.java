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
        Lines topLine = activeScreen.removeFirst();
        inactiveScreen.add(topLine);
        if(inactiveScreen.size() > maxScrollbackLines){
            inactiveScreen.removeFirst();
        }
        activeScreen.add(new Lines(width, this.currentBackgroundColor));    }
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
    public void cursorForward() {
        System.out.println(inactiveScreen.toString());
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
    public void fillLine(int lineNumber, char character){
        Lines lineToChange = activeScreen.get(lineNumber);

        for (Cell cell : lineToChange.getCells()) {
            cell.update(character, currentForegroundColor, currentBackgroundColor, isBold, isItalic, isUnderline);
        }
    }
    public void setCursor(int x, int y) {
        this.cursorX = Math.max(0, Math.min(x, width - 1));
        this.cursorY = Math.max(0, Math.min(y, height - 1));
    }
}
