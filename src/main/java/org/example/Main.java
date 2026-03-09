package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TerminalBuffer buffer = new TerminalBuffer();
        buffer.setAttributes(1,6,true,false ,false);
        buffer.setup(6,2,2);


        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (!input.equals("exit")) {
            buffer.print();
            //buffer.refresh();
            input = scanner.nextLine().toLowerCase();

            if (input.startsWith("goto")) {
                String[] parts = input.split(" ");
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                buffer.setCursor(x, y);
            } else if (input.startsWith("move")) {
                String[] parts = input.split(" ");
                TerminalBuffer.Directions dir = TerminalBuffer.Directions.valueOf(parts[1].toUpperCase());
                int n = Integer.parseInt(parts[2]);
                buffer.moveCursor(dir, n);
            }
            else if (input.startsWith("fill")) {
                String[] parts = input.split(" ");
                int lineNumber = Integer.parseInt(parts[1]);
                char character = ' ';
                if (parts.length >= 3 && !parts[2].isEmpty()) {
                    character = parts[2].charAt(0);
                }
                buffer.fillLine(lineNumber,character);

            }
            //TODO: BELOW
            else if (input.startsWith("insert")) {}
            else if (input.startsWith("clear")) {}
            else if (input.startsWith("clearall")) {}
            else if (input.startsWith("getcharat")) {}
            else if (input.startsWith("getattat")) {}
            else if (input.startsWith("getline")) {}
            else if (input.startsWith("getscreen")) {}
            else if (input.startsWith("getscreenandscroll")) {}
            //---------------------
            else {
                buffer.write(input);
            }
        }
    }
}