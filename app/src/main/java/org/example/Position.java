// File: app/src/main/java/org/example/Position.java
package org.example;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    // Untuk pengembangan ke depan, Anda disarankan mengimplementasikan equals() dan hashCode()
}