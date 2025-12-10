package org.example.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ingredient extends Item implements Preparable {
    private final IngredientType type;
    private IngredientState state;

    public Ingredient(IngredientType type) {
        super(type.label + " (" + IngredientState.RAW + ")");
        this.type = type;
        this.state = IngredientState.RAW;
    }

    public Ingredient(IngredientType type, IngredientState state) {
        super(type.label + " (" + state + ")");
        this.type = type;
        this.state = state;
    }

    @Override
    public boolean canBeChopped() {
        return state == IngredientState.RAW && type != IngredientType.BUN;
    }

    @Override
    public boolean canBeCooked() {
        return (state == IngredientState.CHOPPED || state == IngredientState.RAW) 
                && type == IngredientType.MEAT;
    }

    @Override
    public boolean canBePlacedOnPlate() {
        if (type == IngredientType.MEAT) return state == IngredientState.COOKED;
        if (type == IngredientType.BUN) return state == IngredientState.RAW; 
        return state == IngredientState.CHOPPED; 
    }

    @Override
    public void chop() {
        if (canBeChopped()) {
            this.state = IngredientState.CHOPPED;
            updateName();
        }
    }

    @Override
    public void cook() {
        this.state = IngredientState.COOKED;
        updateName();
    }

    @Override
    public void burn() {
        this.state = IngredientState.BURNED;
        updateName();
    }

    public IngredientState getState() { return state; }
    public IngredientType getType() { return type; }

    private void updateName() {
        this.name = type.label + " (" + state + ")";
    }
}