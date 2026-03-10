package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TerminalBuffer buffer = new TerminalBuffer();
        buffer.setAttributes(1, 3, false, true, false);
        buffer.setup(5,2,2);

        Scanner scanner = new Scanner(System.in);
        CommandProcessor processor = new CommandProcessor(buffer,scanner );

        String input = "";

        while(!input.equals("exit")) {
            buffer.print();
            System.out.print("> ");
            input = scanner.nextLine().toLowerCase();

            if (!input.equals("exit")) {
                processor.execute(input);
            }
        }
    }
}