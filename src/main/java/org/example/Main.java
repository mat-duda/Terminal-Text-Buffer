package org.example;
public class Main {
    public static void main(String[] args) {
        TerminalBuffer buffer = new TerminalBuffer();
        buffer.setup(5,1,5);

        buffer.setAttributes(1,0,true,false ,false);
        buffer.write("hello");
        buffer.print();
    }
}