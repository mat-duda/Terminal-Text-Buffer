package org.example;

public class TextColor {
    public static final String RESET = "\u001B[0m";

    public static String setColor(int foregroundColor, int backgroundColor, boolean bold, boolean italic, boolean underlined) {
        String base = "\u001B[";
        StringBuilder sb = new StringBuilder(base);
        if (foregroundColor < 8) sb.append(30 + foregroundColor);
        else sb.append(90 + (foregroundColor - 8));

        sb.append(";");
        if (backgroundColor < 8) sb.append(40 + backgroundColor);
        else sb.append(100 + (backgroundColor - 8));

        if (bold) sb.append(";1");
        if (italic) sb.append(";3");
        if (underlined) sb.append(";4");



        return sb.append("m").toString();
    }

}
