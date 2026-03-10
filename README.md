# 🚀 Terminal Text Buffer Implementation

A grid-based terminal with text buffer implemented in **Java**
This project contains separated **Active screen / Scrollback mechanisms**, automatic **line wrapping** and **terminal resizing**  

---

# 🏗️ Technical Decisions & Architecture

## 1. Dual-Buffer Strategy

I implemented the buffer using two distinct logical areas:

### Active Screen (`activeScreen`)
A fixed-size list of **Lines** representing the current viewport.  
This area is fully editable and interactive.

### Scrollback History (`inactiveScreen`)
A list of lines that have **scrolled off the top of the screen**.

I chose a **Push-on-Overflow strategy**, where lines move from the active screen to history only when the cursor is forced past the bottom margin.

---

## 2. Unified Coordinate System

To meet the requirement of accessing characters from **both screen and scrollback**, I implemented a translation layer:

```
getLineFromTotalBuffer(int y)
```

Mapping logic:

```
Index 0 → scrollback.size() - 1
    maps to scrollback history

Index scrollback.size() → totalHeight - 1
    maps to active screen
```

This allows the UI (or tests) to treat the entire buffer as **one continuous vertical space**.

---

## 3. Character Cell Data Structure

Each **Cell** is a lightweight object containing:

- A `char` for the content
- A `TextAttributes` value object storing:
  - Colors
  - Bold
  - Italic
  - Underline

This separation keeps the **memory footprint small** while allowing **rich styling capabilities**.

---

# ⚖️ Trade-offs and Challenges

## Logic vs Rendering

The `TerminalBuffer` is intentionally **pure logic and data**.

It does **not store ANSI escape sequences** or rendering information.

Benefits:
- Clean internal data representation
- Easier testing
- Decoupled rendering layer

---

## List vs Array

Rows are stored using `ArrayList`.

Why?

Although arrays can be slightly faster for fixed sizes, `ArrayList` provides flexibility for:

- insert operations
- line wrapping
- scrolling operations

Using methods like:

```
removeFirst()
add()
```

This significantly simplifies the implementation.

---

## The "Scroll Trigger"

One major challenge was determining **when scrolling should occur**.

I implemented **Lazy Scrolling**.

Scrolling happens **only when a character is written beyond the bottom margin**, not when the cursor simply moves there.

This prevents unnecessary empty lines appearing at the bottom of the buffer.

---

# 🔮 Future Improvements (The "Emoji Problem")

## Wide Character Support (UTF-8 / Emojis)

The current implementation uses Java `char` (UTF-16) and assumes every character occupies **exactly one cell**.

### Improvement

Refactor `Cell` to use:

```
String
```

or

```
int codePoint
```

Then implement:

- **Wide character detection**
- Support for **2-cell width characters** (CJK languages, emojis)

---

## Search and Highlight

Add a **search engine** capable of scanning the `inactiveScreen` (scrollback history) without modifying the active screen.

Potential features:

- regex search
- highlight results
- navigation between matches

---

## Performance Optimization

For extremely large scrollback buffers (100,000+ lines), `ArrayList` becomes inefficient when removing the first element because it is **O(N)**.

### Improvement

Replace it with a **Circular Buffer** implementation to support:

- constant-time insertion
- constant-time removal
- bounded memory usage

---

# 🛠️ How to Run

## Prerequisites

- Java **23**
- **Gradle**

---

## Compile the Project

```bash
./gradlew build
```

---

## Run Unit Tests

```bash
./gradlew test
```

---

## Check Test Report

HTML reports are available at:

```
build/reports/tests/test/index.html
```
