package org.example;
public class Main {
    public static void main(String[] args) {
        TerminalBuffer buffer = new TerminalBuffer();
        buffer.setup(5,5,5);
        Lines line = new Lines(5);

        buffer.add(line);
        if(buffer.getActiveScreen()!=null) {
            buffer.getActiveScreen().forEach(System.out::println);
        }
    }
}