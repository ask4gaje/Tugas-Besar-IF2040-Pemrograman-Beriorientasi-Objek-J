package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.CookingDevice;
import org.example.item.FryingPan;
import org.example.item.Ingredient;
import org.example.item.Item;
import org.example.item.KitchenUtensil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookingStation extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(CookingStation.class);

    private static final int COOKING_TIME_SECONDS = 12; 
    private static final int BURN_DELAY_SECONDS = 12;

    private volatile boolean isBurnTimerActive = false; 

    public CookingStation(Position position) {
        super(position);
        this.itemOnTile = new FryingPan();
    }
    
    /**
     * Starts a non-blocking thread to monitor the cooked ingredient for overcooking/burning.
     */
    private void startBurnTimer(Ingredient ingredient) {
        if (ingredient.getState() == org.example.item.IngredientState.COOKED) {
            this.isBurnTimerActive = true;
            LOGGER.info("Burn timer started for {} ({}s delay).", ingredient.getName(), BURN_DELAY_SECONDS);

            new Thread(() -> {
                try {
                    Thread.sleep(BURN_DELAY_SECONDS * 1000L);

                    if (isBurnTimerActive && itemOnTile instanceof KitchenUtensil utensil && !utensil.getContents().isEmpty()) {
                        Ingredient itemInUtensil = (Ingredient) utensil.getContents().get(0);

                        if (itemInUtensil == ingredient && itemInUtensil.getState() == org.example.item.IngredientState.COOKED) {
                            itemInUtensil.burn();
                            LOGGER.warn("!!! {} OVERCOOKED AND BURNED !!!", itemInUtensil.getName());
                        }
                    }
                } catch (InterruptedException e) {
                    LOGGER.info("Burn timer interrupted/cancelled.");
                    Thread.currentThread().interrupt();
                } finally {
                    this.isBurnTimerActive = false;
                }
            }, "BurnTimerThread").start();
        }
    }

    private void stopBurnTimer() {
        this.isBurnTimerActive = false;
        LOGGER.debug("Burn timer logic cancelled via flag.");
    }


    @Override
    public void pickUp(Chef chef) {
        if (chef.getCurrentAction() == org.example.chef.ChefActionState.BUSY) {
            LOGGER.info("{} is busy and cannot interact.", chef.getName());
            return;
        }

        Item heldItem = chef.getInventory();
        Item itemOnStation = itemOnTile;

        if (heldItem != null) {
            if (itemOnStation instanceof KitchenUtensil utensil && heldItem instanceof Ingredient ingredient) {
                if (utensil.canAccept(ingredient)) {
                    utensil.addIngredient(ingredient);
                    chef.dropItem();
                    LOGGER.info("{} placed {} into {}.", chef.getName(), ingredient.getName(), utensil.getName());
                    return;
                }
            }

            if (itemOnStation == null) {
                if (heldItem instanceof FryingPan) {
                    this.itemOnTile = chef.dropItem();
                    LOGGER.info("{} placed Frying Pan on Cooking Station.", chef.getName());
                } else {
                    LOGGER.warn("Cannot place {} directly on stove! Need a Frying Pan.", heldItem.getName());
                }
                return;
            }
        }

        if (heldItem == null) {
            if (itemOnStation != null) {
                stopBurnTimer();
                chef.setInventory(itemOnStation);
                this.itemOnTile = null;
                LOGGER.info("{} picked up {} from Cooking Station.", chef.getName(), chef.getInventory().getName());
                return;
            }
        }
        
        LOGGER.warn("{} tried to interact, but nothing happened.", chef.getName());
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        Item itemOnStation = itemOnTile;

        if (heldItem == null){
            if (itemOnStation instanceof KitchenUtensil utensil && !utensil.getContents().isEmpty()) {
                if (utensil.getContents().get(0) instanceof Ingredient ingredient) {

                    if (ingredient.canBeCooked() && ingredient.getState() != org.example.item.IngredientState.COOKED) {
                        LOGGER.info("{} started cooking {}.", chef.getName(), ingredient.getName());

                        chef.performLongAction(COOKING_TIME_SECONDS, () -> {
                            ingredient.cook();
                            LOGGER.info("Cooking finished. {} is now cooked inside {}.", ingredient.getName(), utensil.getName());
                            startBurnTimer(ingredient);
                        });
                    }
                    else if (ingredient.getState() == org.example.item.IngredientState.COOKED) {
                        LOGGER.warn("{} attempted to cook already COOKED item.", chef.getName());
                        if (!isBurnTimerActive && ingredient.getState() != org.example.item.IngredientState.BURNED) {
                            startBurnTimer(ingredient);
                        }
                    } else if (ingredient.getState() == org.example.item.IngredientState.BURNED) {
                        LOGGER.warn("{} attempted to interact with a BURNED item.", chef.getName());
                    }
                }
            }
        }
    }
}