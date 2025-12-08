package com.burger.nimonscooked.model;

public class Chef {
    public String name;
    public int x, y;
    public Direction facing = Direction.DOWN;
    public Item holding;

    public Chef(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
}
