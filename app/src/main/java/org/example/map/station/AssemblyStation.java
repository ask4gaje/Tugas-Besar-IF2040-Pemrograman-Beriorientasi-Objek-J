package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.FryingPan;
import org.example.item.Ingredient;
import org.example.item.IngredientState;
import org.example.item.Item;
import org.example.item.Plate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssemblyStation extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssemblyStation.class);

    public AssemblyStation(Position position) {
        super(position);
    }

    @Override
    public void interact(Chef chef) {};

    @Override
    public void pickUp(Chef chef) {
        Item heldItem = chef.getInventory();
        Item itemOnStation = this.itemOnTile;

        if (heldItem instanceof Plate plate && itemOnTile instanceof Ingredient ingredient) {
            if (ingredient.canBePlacedOnPlate()) {
                plate.addDishComponent(ingredient);
                this.itemOnTile = null;
                LOGGER.info("{} added {} from station to their plate.", chef.getName(), ingredient.getName());
                return;
            } else {
                LOGGER.warn("Cannot combine: {} is not prepared.", ingredient.getName());
                return;
            }
        }

        if (heldItem instanceof Ingredient ingredient && itemOnTile instanceof Plate plate) {
            if (ingredient.canBePlacedOnPlate()) {
                plate.addDishComponent(ingredient);
                chef.dropItem();
                LOGGER.info("{} placed {} onto the plate on station.", chef.getName(), ingredient.getName());
                return;
            } else {
                LOGGER.warn("Cannot place {} on plate: not prepared.", ingredient.getName());
                return;
            }
        }

        if (heldItem instanceof FryingPan pan) {
            if (!pan.getContents().isEmpty() && pan.getContents().get(0) instanceof Ingredient meat) {
                
                if (meat.getState() == IngredientState.COOKED) {
                    
                    if (itemOnStation instanceof Plate plate) {
                        plate.addDishComponent(meat);
                        pan.getContents().clear(); 
                        LOGGER.info("Menuang daging matang dari Pan ke Piring.");
                        return; 
                    }
                    
                    else if (itemOnStation == null) {
                        this.itemOnTile = meat; 
                        pan.getContents().clear();
                        LOGGER.info("Menuang daging matang ke Assembly Station.");
                        return; 
                    }
                }
            }
        }

        if (itemOnStation != null && heldItem == null) {
            chef.setInventory(this.itemOnTile);
            this.itemOnTile = null;
            LOGGER.info("{} took {} from Assembly Station.", chef.getName(), chef.getInventory().getName());
            return;
        }

        else if (itemOnStation == null && heldItem != null) {
            this.itemOnTile = chef.dropItem();
            LOGGER.info("{} placed {} on Assembly Station.", chef.getName(), itemOnTile.getName());
            return;
        }

        LOGGER.warn("{} tried to interact, but nothing happened.", chef.getName());
    }
}