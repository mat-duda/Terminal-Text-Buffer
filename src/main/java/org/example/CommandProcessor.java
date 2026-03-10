package org.example;

import java.util.Scanner;

public class CommandProcessor {
    private final TerminalBuffer buffer;
    private final Scanner scanner;

    public CommandProcessor(TerminalBuffer buffer, Scanner scanner) {
        this.buffer = buffer;
        this.scanner = scanner;
    }
    public void execute(String input){
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
                char character = input.split(" ")[1].charAt(0);
                buffer.fillLine(character);
            }
            else if (input.startsWith("insert")) {
                String text = input.substring(7);
                buffer.insert(text);
            }
            else if (input.startsWith("clearall")) {

                buffer.clearAll();

            }
            else if (input.startsWith("clear")) {
                for(Lines lines: buffer.getActiveScreen())
                    lines.clear();
            } else if (input.startsWith("emptyinsert")) {
                buffer.insertEmpty();
            }

            else if (input.startsWith("getcharat")) {
                String[] parts = input.split(" ");
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                System.out.println(buffer.getCharAt(x, y));
                scanner.nextLine();
            }
            else if (input.startsWith("getattat")) {
                String[] parts = input.split(" ");
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                System.out.println(buffer.getAttAt(x, y));
                scanner.nextLine();
            }
            else if (input.startsWith("getline")) {
                String[] parts = input.split(" ");
                int y = Integer.parseInt(parts[1]);
                System.out.println(buffer.getLine(y));
                scanner.nextLine();
            }
            else if (input.startsWith("getscreenandscroll")) {
                System.out.println("SCREEN AND SCROLL CONTENT");
                System.out.println(buffer.getScreenAndScroll());
                scanner.nextLine();
            }
            else if (input.startsWith("getscreen")) {
                System.out.println("SCREEN");
                System.out.println(buffer.getScreen());
                scanner.nextLine();
            }
            else if (input.startsWith("resize")) {
                String[] parts = input.split(" ");
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                buffer.resize(x,y);
            }
            else {
                buffer.write(input);
            }
        }

    }

