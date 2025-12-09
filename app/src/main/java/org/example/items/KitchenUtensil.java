package org.example.items;

import java.util.ArrayList;
import java.util.List;

public abstract class KitchenUtensil extends Item {
    protected List<Preparable> contents; 

    public KitchenUtensil(String name) {
        super(name);
        this.contents = new ArrayList<>();
    }

    public List<Preparable> getContents() {
        return contents;
    }
    
    public void clearContents() {
        contents.clear();
    }
}