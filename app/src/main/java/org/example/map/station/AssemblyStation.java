package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.Ingredient;
import org.example.item.Item;
import org.example.item.Plate;
import org.example.item.FryingPan; // ADDED
import org.example.item.KitchenUtensil;
import org.example.item.Preparable;
import java.util.List; // ADDED
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
        Item itemOnStation = itemOnTile;

        if (heldItem instanceof FryingPan fryingPan && itemOnStation == null) {
            List<Preparable> contents = fryingPan.getContents(); // Retrieve contents
            if (!contents.isEmpty()) {
                Ingredient ingredientToDump = (Ingredient) contents.get(0);

                this.itemOnTile = ingredientToDump;

                fryingPan.clearContents();

                LOGGER.info("{} dumped {} from Frying Pan onto Assembly Station.", chef.getName(), ingredientToDump.getName());
                return;
            }
        }

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

        if (itemOnTile != null && heldItem == null) {
            chef.setInventory(this.itemOnTile);
            this.itemOnTile = null;
            LOGGER.info("{} took {} from Assembly Station.", chef.getName(), chef.getInventory().getName());
            return;
        }

        else if (itemOnTile == null && heldItem != null) {
            this.itemOnTile = chef.dropItem();
            LOGGER.info("{} placed {} on Assembly Station.", chef.getName(), itemOnTile.getName());
            return;
        }

        LOGGER.warn("{} tried to interact, but nothing happened.", chef.getName());
    }
}