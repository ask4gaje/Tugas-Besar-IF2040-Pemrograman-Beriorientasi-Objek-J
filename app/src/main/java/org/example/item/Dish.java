package org.example.item;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dish extends Item {
    private List<Ingredient> components;

    public Dish() {
        super("Unfinished Dish");
        this.components = new ArrayList<>();
    }

    public void addIngredient(Ingredient ingredient) {
        components.add(ingredient);
        updateName();
    }

    public List<Ingredient> getComponents() {
        return components;
    }

    private void updateName() {
        if (components.isEmpty()) {
            this.name = "Empty Dish";
            return;
        }

        boolean hasBun = this.contains(IngredientType.BUN);
        boolean hasCookedMeat = false;
        boolean hasChoppedCheese = false;
        boolean hasChoppedTomatoes = false;
        boolean hasChoppedLettuce = false;

        for (Ingredient i : this.getComponents()) {
            if (i.getType() == IngredientType.MEAT && i.getState() == IngredientState.COOKED) {
                hasCookedMeat = true;
            }
            if (i.getType() == IngredientType.CHEESE && i.getState() == IngredientState.CHOPPED) {
                hasChoppedCheese = true;
            }
            if (i.getType() == IngredientType.TOMATO && i.getState() == IngredientState.CHOPPED) {
                hasChoppedTomatoes = true;
            }
            if (i.getType() == IngredientType.LETTUCE && i.getState() == IngredientState.CHOPPED) {
                hasChoppedLettuce = true;
            }
        }

        if (this.getComponents().size() == 2 && hasBun && hasCookedMeat) {
            this.name = "Classic Burger";
        }

        if (this.getComponents().size() == 3 && hasBun && hasCookedMeat && hasChoppedCheese) {
            this.name = "Cheese Burger";
        }

        if (this.getComponents().size() == 4 && hasBun && hasCookedMeat && hasChoppedTomatoes && hasChoppedLettuce) {
            this.name = "BLT Burger";
        }

        if (this.getComponents().size() == 4 && hasBun && hasCookedMeat && hasChoppedCheese && hasChoppedLettuce) {
            this.name = "Deluxe Burger";
        }

        else {
            StringBuilder sb = new StringBuilder("Burger [");
            for (Ingredient i : components) {
                sb.append(i.getType().label).append(", ");
            }
            sb.setLength(sb.length() - 2);
            sb.append("]");
            this.name = sb.toString();
        }
    }
    
    public boolean contains(IngredientType type) {
        for (Ingredient i : components) {
            if (i.getType() == type) return true;
        }
        return false;
    }
}