package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TerminalBufferTest {
    private TerminalBuffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new TerminalBuffer();
        // Width 5, Height 2, Max Scrollback 2
        buffer.setup(5, 2, 2);
    }

    @Test
    void testBasicWrite() {
        buffer.setAttributes(1, 0, false, false, false);
        buffer.write("ABC");

        assertEquals('A', buffer.getCharAt(0, 0));
        assertEquals('B', buffer.getCharAt(1, 0));
        assertEquals('C', buffer.getCharAt(2, 0));
    }

    @Test
    void testLineWrapping() {
        // Write 6 chars into a 5-wide buffer
        buffer.write("123456");

        // '6' should wrap to the second line
        assertEquals('6', buffer.getCharAt(0, 1));
    }

    @Test
    void testScrollingIntoInactiveScreen() {
        // Line 1: fill it
        buffer.write("AAAAA");
        // Line 2: fill it
        buffer.write("BBBBB");
        // This 'C' should trigger a scroll. 'AAAAA' moves to history.
        buffer.write("C");

        // getCharAt(x, y) uses absolute coordinates.
        // 0 = History Line 1, 1 = Screen Line 1, 2 = Screen Line 2
        assertEquals('A', buffer.getCharAt(0, 0), "History should contain the scrolled line");
        assertEquals('C', buffer.getCharAt(0, 2), "New char should be at the start of the last line");
    }

    @Test
    void testAttributesPersistence() {
        buffer.setAttributes(1, 4, true, false, false); // Red on Blue, Bold
        buffer.write("!");

        String attrs = buffer.getAttAt(0, 0);
        assertTrue(attrs.contains("FG: 1"));
        assertTrue(attrs.contains("BG: 4"));
        assertTrue(attrs.contains("Bold: true"));
    }

    @Test
    void testReflowOnResize() {
        // Width 5: "12345" (Line 1), "6" (Line 2)
        buffer.write("123456");

        // Resize to Width 10. "123456" should now all be on Line 1.
        buffer.resize(10, 2);

        assertEquals('6', buffer.getCharAt(5, 0), "Character '6' should have pulled up to the first line");
    }
    @Test
    void testCursorWrapAround() {
        buffer.setup(5, 2, 5); // Small width for easy testing
        buffer.write("123456"); // 6 should wrap to the second line

        // Check first line (index 0)
        assertEquals("12345", buffer.getLine(0).trim());
        // Check second line (index 1)
        assertEquals("6", buffer.getLine(1).trim());
    }
    @Test
    void testCursorBoundaries() {
        buffer.setup(10, 10, 5);

        // Try to move cursor past the right edge
        buffer.setCursor(9, 0);
        buffer.moveCursor(TerminalBuffer.Directions.RIGHT, 5);
    }

    @Test
    void testManualCursorSet() {
        buffer.setup(80, 24, 100);
        buffer.setCursor(5, 5);
        buffer.write("A");

        // Character should be at (5, 5), not (0, 0)
        assertEquals('A', buffer.getCharAt(5, 5));
    }
    @Test
    void testFillAndClearScreen() {
        buffer.setup(10, 2, 5);
        buffer.fillLine('X'); // Fills current line (0)

        assertEquals('X', buffer.getCharAt(0, 0));
        assertEquals('X', buffer.getCharAt(9, 0));

        buffer.clearAll();
        // After clear, it should be empty (default char is ' ')
        assertEquals(' ', buffer.getCharAt(0, 0));
    }
    @Test
    void testGetLineFromTotalBuffer() {
        // 1 line height, 1 line max scrollback
        buffer.setup(5, 1, 1);

        buffer.write("AAAAA"); // Active screen
        buffer.write("BBBBB"); // "AAAAA" moves to scrollback (index 0), "BBBBB" is active (index 1)

        String line0 = buffer.getLine(0);
        String line1 = buffer.getLine(1);

        assertTrue(line0.contains("A"));
        assertTrue(line1.contains("B"));

    }
    @Test
    void testComplexAttributes() {
        buffer.setup(10, 10, 10);
        // FG: 1, BG: 2, Bold: true, Italic: true, Underline: true
        buffer.setAttributes(1, 2, true, true, true);
        buffer.write("Styled");

        String attr = buffer.getAttAt(0, 0);
        assertTrue(attr.contains("Bold: true"));
        assertTrue(attr.contains("Italic: true"));
        assertTrue(attr.contains("Underline: true"));
    }
    @Test
    void testInsertWithWrapping() {
        buffer.setup(5, 2, 5);
        buffer.write("12345"); // Fill first line

        buffer.setCursor(0, 0);
        buffer.insert("0"); // Insert '0' at start

        // Line 0 should now be "01234"
        // The '5' should have wrapped to Line 1
        assertEquals("01234", buffer.getLine(0).trim());
        assertEquals("5", buffer.getLine(1).trim());
    }
}