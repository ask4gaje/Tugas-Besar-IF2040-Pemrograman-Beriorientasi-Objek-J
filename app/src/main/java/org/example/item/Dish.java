package items.dishes;

import items.Item;
import items.ingredients.Ingredient;
import items.interfaces.Preparable;
import java.util.ArrayList;
import java.util.List;

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
        StringBuilder sb = new StringBuilder("Burger [");
        for (Ingredient i : components) {
            sb.append(i.getType().label).append(", ");
        }
        sb.setLength(sb.length() - 2); 
        sb.append("]");
        this.name = sb.toString();
    }
    
    public boolean contains(items.ingredients.IngredientType type) {
        for (Ingredient i : components) {
            if (i.getType() == type) return true;
        }
        return false;
    }
}