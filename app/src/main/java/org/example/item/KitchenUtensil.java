package org.example.item;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public abstract boolean canAccept(Ingredient ingredient);

    public abstract void addIngredient(Ingredient ingredient);
}