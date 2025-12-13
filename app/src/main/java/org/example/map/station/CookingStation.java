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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.animation.AnimationTimer;

public class CookingStation extends AbstractStation {
    private static final Logger LOGGER = LoggerFactory.getLogger(CookingStation.class);

    private static final double COOKING_TIME_SECONDS = 12.0;
    private static final double BURN_DELAY_SECONDS = 12.0;

    private DoubleProperty cookingProgress = new SimpleDoubleProperty(0.0);
    private DoubleProperty burnProgress = new SimpleDoubleProperty(0.0);

    private long lastUpdate = 0;
    private AnimationTimer cookingTimer = null;
    private AnimationTimer burnTimer = null;


    public CookingStation(Position position) {
        super(position);
        this.itemOnTile = new FryingPan();
    }

    // New Getter Methods for Progress
    public DoubleProperty cookingProgressProperty() {
        return cookingProgress;
    }

    public DoubleProperty burnProgressProperty() {
        return burnProgress;
    }

    private void startCooking() {
        if (!(itemOnTile instanceof FryingPan fryingPan) || fryingPan.getContents().isEmpty()) return;
        Ingredient ingredient = (Ingredient) fryingPan.getContents().get(0);

        if (ingredient.canBeCooked() && ingredient.getState() != org.example.item.IngredientState.COOKED) {

            stopTimers();
            ingredient.cooking();
            LOGGER.info("Cooking timer started for {}.", ingredient.getName());
            cookingProgress.set(0.0);

            cookingTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (lastUpdate > 0) {
                        double elapsedSeconds = (now - lastUpdate) / 1_000_000_000.0;
                        double currentProgress = cookingProgress.get() + (elapsedSeconds / COOKING_TIME_SECONDS);

                        if (currentProgress >= 1.0) {
                            stop();
                            ingredient.cooked();
                            LOGGER.info("Cooking finished. {} is now cooked.", ingredient.getName());
                            cookingProgress.set(0.0);

                            startBurnTimer(ingredient);
                        } else {
                            cookingProgress.set(currentProgress);
                        }
                    }
                    lastUpdate = now;
                }
            };

            fryingPan.startCooking();
            cookingTimer.start();
        }
    }

    private void startBurnTimer(Ingredient ingredient) {
        if (ingredient.getState() == org.example.item.IngredientState.COOKED && burnTimer == null) {
            LOGGER.info("Burn timer started for {} ({}s delay).", ingredient.getName(), BURN_DELAY_SECONDS);
            burnProgress.set(0.0);
            lastUpdate = 0;

            burnTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (lastUpdate > 0) {
                        double elapsedSeconds = (now - lastUpdate) / 1_000_000_000.0;
                        double currentProgress = burnProgress.get() + (elapsedSeconds / BURN_DELAY_SECONDS);

                        if (itemOnTile instanceof FryingPan fp && !fp.getContents().isEmpty() && fp.getContents().get(0) == ingredient) {
                            if (currentProgress >= 1.0) {
                                stop();
                                ingredient.burn();
                                LOGGER.warn("!!! {} OVERCOOKED AND BURNED !!!", ingredient.getName());
                                burnProgress.set(1.0);
                            } else {
                                burnProgress.set(currentProgress);
                            }
                        } else {
                            stop();
                            burnProgress.set(0.0);
                        }
                    }
                    lastUpdate = now;
                }
            };
            burnTimer.start();
        }
    }

    private void stopTimers() {
        if (cookingTimer != null) {
            cookingTimer.stop();
            cookingTimer = null;
            cookingProgress.set(0.0);
        }
        if (burnTimer != null) {
            burnTimer.stop();
            burnTimer = null;
            burnProgress.set(0.0);
        }
        if (itemOnTile instanceof FryingPan) {
            ((FryingPan) itemOnTile).stopCooking();
        }
        lastUpdate = 0;
    }


    @Override
    public void pickUp(Chef chef) {
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
                stopTimers();
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
        Item itemOnStation = itemOnTile;

        if (itemOnStation instanceof FryingPan fryingPan && !fryingPan.getContents().isEmpty()) {
            Ingredient ingredient = (Ingredient) fryingPan.getContents().get(0);

            if (ingredient.canBeCooked() && ingredient.getState() != org.example.item.IngredientState.COOKED) {
                startCooking();
                LOGGER.info("{} started cooking {}.", chef.getName(), ingredient.getName());
            }
            else if (ingredient.getState() == org.example.item.IngredientState.COOKED) {
                if (ingredient.getState() != org.example.item.IngredientState.BURNED && burnTimer == null) {
                    startBurnTimer(ingredient);
                    LOGGER.info("{} monitored the cooked item, ensuring the burn timer is active.", chef.getName());
                } else {
                    LOGGER.warn("{} attempted to interact with an already COOKED or BURNED item.", chef.getName());
                }
            } else {
                LOGGER.warn("{} tried to interact with an item that cannot be cooked or is burned.", chef.getName());
            }
        }
    }
}