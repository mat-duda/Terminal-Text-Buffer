package org.example;

import java.util.Scanner;

public class CommandProcessor {
    private final TerminalBuffer buffer;
    private final Scanner scanner;

    public CommandProcessor(TerminalBuffer buffer, Scanner scanner) {
        this.buffer = buffer;
        this.scanner = scanner;
    }

    public void execute(String input) {
        if (input.isBlank()) return;
        String[] parts = input.split(" ");
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "goto" -> buffer.setCursor(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));

                case "move" -> {
                    TerminalBuffer.Directions dir = TerminalBuffer.Directions.valueOf(parts[1].toUpperCase());
                    buffer.moveCursor(dir, Integer.parseInt(parts[2]));
                }
                case "fill" -> buffer.fillLine(parts[1].charAt(0));

                case "insert" -> buffer.insert(input.substring(7));

                case "clearall" -> buffer.clearAll();

                case "clear" -> {
                    for (Lines line : buffer.getActiveScreen()) {
                        line.clear();
                    }
                }

                case "emptyinsert" -> buffer.insertEmpty();

                case "getcharat" -> {
                    System.out.println("Char: [" + buffer.getCharAt(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])) + "]");
                    waitForUser();
                }

                case "getattat" -> {
                    System.out.println("Attributes: " + buffer.getAttAt(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                    waitForUser();
                }

                case "getline" -> {
                    System.out.println("Line " + parts[1] + ": " + buffer.getLine(Integer.parseInt(parts[1])));
                    waitForUser();
                }

                case "getscreenandscroll" -> {
                    System.out.println("--- FULL CONTENT ---");
                    System.out.println(buffer.getScreenAndScroll());
                    waitForUser();
                }

                case "getscreen" -> {
                    System.out.println("--- SCREEN CONTENT ---");
                    System.out.println(buffer.getScreen());
                    waitForUser();
                }

                case "resize" -> buffer.resize(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                default -> buffer.write(input);
            }
        } catch (Exception e) {
            System.err.println("Error executing command '" + command + "': " + e.getMessage());
            waitForUser();
        }
    }

    private void waitForUser() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}