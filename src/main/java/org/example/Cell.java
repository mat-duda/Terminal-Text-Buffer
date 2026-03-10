package org.example;

public class Cell {
    char character = ' ';
    int foreGroundColor;
    int backgroundColor;
    boolean bold, italic, underline;
    public Cell(char character, TextAttributes attrs) {
        this.character = character;
        this.foreGroundColor = attrs.foregroundColor();
        this.backgroundColor = attrs.backgroundColor();
        this.bold = attrs.isBold();
        this.italic = attrs.isItalic();
        this.underline = attrs.isUnderline();
    }

    public void update(char c, TextAttributes attrs) {
        this.character = c;
        this.foreGroundColor = attrs.foregroundColor();
        this.backgroundColor = attrs.backgroundColor();
        this.bold = attrs.isBold();
        this.italic = attrs.isItalic();
        this.underline = attrs.isUnderline();
    }

    public String render(boolean isCursor) {
        int fg = isCursor ? backgroundColor : foreGroundColor;
        int bg = isCursor ? foreGroundColor : backgroundColor;

        String style = TextColor.setColor(fg, bg, bold, italic, underline);
        return style + character + TextColor.RESET;
    }

    @Override
    public String toString() {
        return String.valueOf(character);

    }
}