package org.example;

import java.util.ArrayList;
import java.util.List;

public class Scrollback {
    private final List<Lines> history = new ArrayList<>();
    private final int maxSize;

    public Scrollback(int maxSize) {
        this.maxSize = maxSize;
    }

    public void addLine(Lines line) {
        history.add(line);
        if (history.size() > maxSize) {
            history.removeFirst();
        }
    }


    public int size() {
        return history.size();
    }
}