package org.example;

import java.util.ArrayList;
import java.util.List;

public class TerminalBuffer {
    private int width;
    private int height;
    private int maxScrollbackLines;
    private List<Lines> activeScreen = new ArrayList<>();

    public int getWidth() {
        return width;
    }

    public void  setup(int width, int height, int maxScrollbackLines){
        this.width = width;
        this.height = height;
        this.maxScrollbackLines = maxScrollbackLines;
        this.activeScreen = new ArrayList<>(height);

        for(int i = 0; i < height; i++){
            activeScreen.add(new Lines(width));

        }

    }
    public List<Lines> getActiveScreen() {
        return activeScreen;
    }
    public void add(Lines line){
        activeScreen.add(line);
        if(activeScreen.size() > maxScrollbackLines){
            activeScreen.remove(0);
        }
    }

}
