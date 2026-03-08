package org.example;

public class Cell {
    char character = ' ';
    int foreGroundColor;
    int backgroundColor;
    boolean[] styles = new boolean[3];

    public Cell(char character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return String.valueOf(character);
    }
}
