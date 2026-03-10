package org.example;

public record TextAttributes(
        int foregroundColor,
        int backgroundColor,
        boolean isBold,
        boolean isItalic,
        boolean isUnderline
) {
    public static TextAttributes defaultAttributes() {
        return new TextAttributes(7, 0, false, false, false);
    }

}