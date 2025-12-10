package org.example.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Item {
    protected String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return name;
    }
}