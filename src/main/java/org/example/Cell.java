package org.example;

public class Cell {
    char character = ' ';
    int foreGroundColor;
    int backgroundColor;
    boolean bold, italic, underline;
    public Cell(){}
    public Cell(char character, int foreGroundColor, int backgroundColor, boolean b, boolean i, boolean u) {
        this.character = character;
        this.foreGroundColor = foreGroundColor;
        this.backgroundColor = backgroundColor;
        this.bold = b;
        this.italic = i;
        this.underline = u;
    }
    public void update(char c, int fg, int bg, boolean b, boolean i, boolean u) {
        this.character = c;
        this.foreGroundColor = fg;
        this.backgroundColor = bg;
        this.bold = b;
        this.italic = i;
        this.underline = u;
    }


    public String render(boolean isCursor) {
        int fg = isCursor ? backgroundColor : foreGroundColor;
        int bg = isCursor ? foreGroundColor : backgroundColor;

        String style = TextColor.setColor(fg, bg, bold, italic, underline);
        return style + character + TextColor.RESET;
    }}