package org.example.map.station;

import org.example.chef.Chef;
import org.example.chef.Position;
import org.example.item.CookingDevice;
import org.example.item.Ingredient;
import org.example.item.Item;
import org.example.item.KitchenUtensil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookingStation extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(CookingStation.class);
    
    // Time constants for cooking logic
    private static final int COOKING_TIME_SECONDS = 12; 
    private static final int BURN_DELAY_SECONDS = 12; // Time to wait after COOKED before BURNING
    
    // Status flag to manage the burn timer state across threads.
    private volatile boolean isBurnTimerActive = false; 

    public CookingStation(Position position) {
        super(position);
    }
    
    /**
     * Starts a non-blocking thread to monitor the cooked ingredient for overcooking/burning.
     */
    private void startBurnTimer(Ingredient ingredient) {
        // If the ingredient is cooked, activate the burn timer logic.
        if (ingredient.getState() == org.example.item.IngredientState.COOKED) {
            this.isBurnTimerActive = true;
            LOGGER.info("Burn timer started for {} ({}s delay).", ingredient.getName(), BURN_DELAY_SECONDS);

            new Thread(() -> {
                try {
                    // Wait for the burning delay
                    Thread.sleep(BURN_DELAY_SECONDS * 1000L);

                    // Check if the item is still on the station AND the timer is still active
                    if (isBurnTimerActive && itemOnTile instanceof KitchenUtensil utensil && !utensil.getContents().isEmpty()) {
                        Ingredient itemInUtensil = (Ingredient) utensil.getContents().get(0);
                        
                        // Check if it's the same item and it hasn't been burnt yet
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
    
    /**
     * Stops the burn timer logic, called when the item is removed.
     */
    private void stopBurnTimer() {
        this.isBurnTimerActive = false;
        LOGGER.debug("Burn timer logic cancelled via flag.");
    }


    @Override
    public void interact(Chef chef) {
        if (chef.getCurrentAction() == org.example.chef.ChefActionState.BUSY) {
            LOGGER.info("{} is busy and cannot interact.", chef.getName());
            return;
        }

        Item heldItem = chef.getInventory();
        Item itemOnStation = itemOnTile;

        // --- Case 1: Chef has an item (Try to Place/Combine) ---
        if (heldItem != null) {
            // A. Add Ingredient into a Utensil on the Station
            if (itemOnStation instanceof KitchenUtensil utensil && heldItem instanceof Ingredient ingredient) {
                if (utensil.canAccept(ingredient)) {
                    utensil.addIngredient(ingredient);
                    chef.dropItem();
                    LOGGER.info("{} placed {} into {}.", chef.getName(), ingredient.getName(), utensil.getName());
                    return;
                }
            }
            
            // B. Place item on empty station (this includes placing the FryingPan/Utensil)
            if (itemOnStation == null) {
                this.itemOnTile = chef.dropItem();
                LOGGER.info("{} placed {} on Cooking Station.", chef.getName(), itemOnTile.getName());
                return;
            }
        }
        
        // --- Case 2: Chef is empty (Try to Pick Up/Start Cooking) ---
        if (heldItem == null) {
            // A. Start Cooking (Requires Ingredient inside KitchenUtensil on tile)
            if (itemOnStation instanceof KitchenUtensil utensil && !utensil.getContents().isEmpty()) {
                if (utensil.getContents().get(0) instanceof Ingredient ingredient) {
                    
                    // Logic to start cooking only if it's raw/chopped and cookable (MEAT)
                    if (ingredient.canBeCooked() && ingredient.getState() != org.example.item.IngredientState.COOKED) {
                        LOGGER.info("{} started cooking {}.", chef.getName(), ingredient.getName());
                        
                        // Perform the cooking action (Stage 1: Cook)
                        chef.performLongAction(COOKING_TIME_SECONDS, () -> {
                            // On successful cook, update state and trigger the Burn Timer (Stage 2: Burn)
                            ingredient.cook();
                            LOGGER.info("Cooking finished. {} is now cooked inside {}.", ingredient.getName(), utensil.getName());
                            startBurnTimer(ingredient);
                        });
                        return;
                    }
                    // Handle case where chef interacts but item is done/burnt
                    else if (ingredient.getState() == org.example.item.IngredientState.COOKED) {
                        LOGGER.warn("{} attempted to cook already COOKED item.", chef.getName());
                        // If already cooked, ensure burn timer is active if it's not burnt
                        if (!isBurnTimerActive && ingredient.getState() != org.example.item.IngredientState.BURNED) {
                             startBurnTimer(ingredient); // Re-activate burn timer if chef nudges the station
                        }
                    } 
                    else if (ingredient.getState() == org.example.item.IngredientState.BURNED) {
                        LOGGER.warn("{} attempted to interact with a BURNED item.", chef.getName());
                    }
                }
            }
            
            // B. Pick up item from station
            if (itemOnStation != null) {
                stopBurnTimer(); // Stop burn timer BEFORE item is picked up
                chef.setInventory(itemOnStation);
                this.itemOnTile = null;
                LOGGER.info("{} picked up {} from Cooking Station.", chef.getName(), chef.getInventory().getName());
                return;
            }
        }
        
        LOGGER.warn("{} tried to interact, but nothing happened.", chef.getName());
    }
}