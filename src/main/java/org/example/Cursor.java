package org.example;

public class Cursor {
    private int cursorX = 0;
    private int cursorY = 0;
    private final int width;
    private final int height;
    public Cursor(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void moveCursor(TerminalBuffer.Directions direction, int n){
        switch (direction){
            case UP -> cursorY = Math.max(0, cursorY - n);
            case DOWN -> cursorY = Math.min(height - 1, cursorY + n);
            case LEFT -> cursorX = Math.max(0, cursorX - n);
            case RIGHT -> cursorX = Math.min(width - 1, cursorX + n);
        }

    }
    public void setCursor(int x, int y) {
        this.cursorX = Math.max(0, Math.min(x, width - 1));
        this.cursorY = Math.max(0, Math.min(y, height - 1));
    }

    public void setX(int cursorX) {
        this.cursorX = cursorX;
    }

    public void setY(int cursorY) {
        this.cursorY = cursorY;
    }

    public int getX() {
        return cursorX;
    }
    public int getY() {
        return cursorY;
    }

    public boolean advance() {
        if (cursorX < width - 1) {
            cursorX++;
            return false;
        } else {
            cursorX = 0;
            return true;
        }
    }
}
