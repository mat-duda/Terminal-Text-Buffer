package org.example;
public class Main {
    public static void main(String[] args) {
        TerminalBuffer buffer = new TerminalBuffer();
        buffer.setup(2,3,5);

        buffer.setAttributes(3,0,true,true ,true);
        buffer.write("hello");
        buffer.print();
    }
}