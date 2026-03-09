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

            if (input.equals("\u001B[A")) {
                buffer.moveCursor(TerminalBuffer.Directions.UP, 1);
            } else if (input.equals("\u001B[B")) {
                buffer.moveCursor(TerminalBuffer.Directions.DOWN, 1);
            } else if (input.equals("\u001B[D")) {
                buffer.moveCursor(TerminalBuffer.Directions.LEFT, 1);
            } else if (input.equals("\u001B[C")) {
                buffer.moveCursor(TerminalBuffer.Directions.RIGHT, 1);
            } else  {
                buffer.write(input);
            }
        }
    }
}