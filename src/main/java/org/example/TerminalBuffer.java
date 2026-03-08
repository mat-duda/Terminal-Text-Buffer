package org.example;

import java.util.ArrayList;
import java.util.List;

public class TerminalBuffer {
    private int width;
    private int height;
    private int maxScrollbackLines;
    private List<Lines> activeScreen = new ArrayList<>();
    private int currentForegroundColor = 7;
    private int currentBackgroundColor = 0;
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
            activeScreen.add(new Lines(width));

        }

    }

    public int getCursorX() {
        return cursorX;
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
    public void cursorForward() {
        if (cursorX < width - 1) {
            cursorX += 1;
        } else if (cursorY < height - 1) {
            cursorX = 0;
            cursorY += 1;
        }
    }
    public void print(){
        for (int y = 0; y < activeScreen.size(); y++) {
            Lines line = activeScreen.get(y);
            int highlightColumn = (y == cursorY) ? cursorX : -1;

            System.out.println(line.render(highlightColumn));
        }
    }
}
