package org.example;

import java.util.ArrayList;
import java.util.List;
public class TerminalBuffer {
    public enum Directions{
        UP,DOWN,LEFT,RIGHT
    }
    private int width;
    private int height;
    private int maxScrollback;
    private Cursor cursor;
    private List<Lines> activeScreen = new ArrayList<>();
    private List<Lines> inactiveScreen = new ArrayList<>();
    private TextAttributes textAttributes = TextAttributes.defaultAttributes();


    public void setup(int width, int height, int maxScrollback) {
        this.width = width;
        this.height = height;
        this.maxScrollback = maxScrollback;
        this.cursor = new Cursor(width, height);
        activeScreen.clear();
        for (int i = 0; i < height; i++) {
            activeScreen.add(new Lines(width, textAttributes));
        }
    }
    public void setAttributes(int fg, int bg, boolean bold, boolean italic, boolean underline) {
        this.textAttributes = new TextAttributes(fg, bg, bold, italic, underline);
    }

    private void pushLinesToInactiveScreen() {
        if (activeScreen.isEmpty()) return;

        Lines topLine = activeScreen.removeFirst();
        inactiveScreen.add(topLine);

        if (inactiveScreen.size() > maxScrollback) {
            inactiveScreen.removeFirst();
        }
        activeScreen.add(new Lines(width, textAttributes));
    }

    public List<Lines> getActiveScreen() {
        return activeScreen;
    }
    public void add(Lines line){
        activeScreen.add(line);
        if(activeScreen.size() > maxScrollback){
            activeScreen.removeFirst();
        }
    }
    private void scroll() {
        Lines topLine = activeScreen.removeFirst();
        inactiveScreen.add(topLine);
        if (inactiveScreen.size() > maxScrollback) {
            inactiveScreen.removeFirst();
        }
        activeScreen.add(new Lines(width, textAttributes));
    }
    public void write(String text) {
        for (char c : text.toCharArray()) {
            activeScreen.get(cursor.getY()).getCells().get(cursor.getX()).update(c, textAttributes);
            cursor.setX(cursor.getX() + 1);
            if (cursor.getX() >= width) {
                cursor.setX(0);
                if (cursor.getY() < height - 1) {
                    cursor.setY(cursor.getY() + 1);
                } else {
                    scroll();
                }
            }
        }
    }
    private Lines getLineFromTotalBuffer(int index) {
        int scrollbackSize = inactiveScreen.size();
        if (index < 0 || index >= scrollbackSize + height) {
            throw new IndexOutOfBoundsException("Buffer index out of bounds");
        }

        if (index < scrollbackSize) {
            return inactiveScreen.get(index);
        } else {
            return activeScreen.get(index - scrollbackSize);
        }
    }
    public char getCharAt(int x, int y) {
        return getLineFromTotalBuffer(y).getCells().get(x).character;
    }
    public String getAttAt(int x, int y) {
        Lines line = getLineFromTotalBuffer(y);
        if (x < 0 || x >= width) return String.valueOf(' ');
        Cell cell = line.getCells().get(x);
        return String.format("FG: %d, BG: %d, Bold: %b, Italic: %b, Underline: %b",
                cell.foreGroundColor, cell.backgroundColor, cell.bold, cell.italic, cell.underline);
    }
    public void cursorForward() {
        if (cursor.getX() < width - 1) {
            cursor.setX(cursor.getX()+1);
        } else {
            cursor.setX(0);
            if (cursor.getY() < height - 1) {
                cursor.setY(cursor.getY()+1);
            } else {
                    pushLinesToInactiveScreen();
            }
        }
    }

    public void print() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        for (Lines line : inactiveScreen) {
            System.out.println(TextColor.GREY + line.render(-1) + TextColor.RESET);
        }

        if (!inactiveScreen.isEmpty()) {
            System.out.println(TextColor.GREY + "--- Scrollback Above ---" + TextColor.RESET);
        }

        for (int y = 0; y < activeScreen.size(); y++) {
            Lines line = activeScreen.get(y);
            int highlightColumn = (y == cursor.getY()) ? cursor.getX() : -1;

            System.out.println(line.render(highlightColumn) + TextColor.RESET);
        }

        System.out.println("\nCursor: [" + cursor.getX() + ", " + cursor.getY() + "] | " +
                "History: " + inactiveScreen.size() + "/" + maxScrollback);
    }

    public void insertEmpty() {
        Lines shiftedLine = activeScreen.removeFirst();
        inactiveScreen.add(shiftedLine);
        if (inactiveScreen.size() > maxScrollback) {
            inactiveScreen.removeFirst();
        }
        activeScreen.add(new Lines(this.width, this.textAttributes));
    }
    public void fillLine(char character){
        Lines lineToChange = activeScreen.get(cursor.getY());

        for (Cell cell : lineToChange.getCells()) {
            cell.update(character, textAttributes);
        }
    }

    public void clearAll(){
        for (Lines line : activeScreen) {
            line.clear();
        }
        for (Lines line : inactiveScreen) {
            line.clear();
        }
        inactiveScreen.clear();
    }
    public void insert(String text) {
        for (char c : text.toCharArray()) {
            Lines currentLine = activeScreen.get(cursor.getY());

            currentLine.insertCell(cursor.getX(), new Cell(c, textAttributes));
            if (currentLine.getCells().size() > width) {
                Cell overflowCell = currentLine.getCells().remove(width);

                int nextY = cursor.getY() + 1;
                if (nextY >= height) {
                    pushLinesToInactiveScreen();
                    nextY = height - 1;
                }

                activeScreen.get(nextY).getCells().addFirst(overflowCell);

                if (activeScreen.get(nextY).getCells().size() > width) {
                    activeScreen.get(nextY).getCells().remove(width);
                }
            }

            cursorForward();
        }
    }
    public String getLine(int y) {
        return getLineFromTotalBuffer(y).toString();
    }
    public String getScreenAndScroll(){
        StringBuilder sb = new StringBuilder();
        for (Lines line : inactiveScreen) {
            sb.append(line.toString()).append("\n");
        }
        for (Lines line : activeScreen) {
            sb.append(line.toString()).append("\n");
        }
        return sb.toString();
    }
    public String getScreen(){
        StringBuilder sb = new StringBuilder();
        for (Lines line : activeScreen) {
            sb.append(line.toString()).append("\n");
        }
        return sb.toString();
    }

    public void resize(int newWidth, int newHeight) {
        ResizeEngine.ResizeResult result = ResizeEngine.reflow(inactiveScreen, activeScreen, this.width,
                newWidth, newHeight, cursor.getX(), cursor.getY(), textAttributes);

        this.width = newWidth;
        this.height = newHeight;
        this.inactiveScreen = result.inactive;
        this.activeScreen = result.active;
        this.cursor.setX(result.cursorX);
        this.cursor.setY(result.cursorY);
    }
    public void setCursor(int x, int y) {
        cursor.setCursor(x, y);
    }

    public void moveCursor(Directions direction, int n) {
        cursor.moveCursor(direction, n);
    }
}
