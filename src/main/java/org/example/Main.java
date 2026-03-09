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
            } else {
                buffer.write(input);
            }
        }
    }
}