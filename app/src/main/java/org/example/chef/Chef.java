package org.example.chef;

import org.example.item.Item;
import org.example.item.KitchenUtensil;
import org.example.item.Dish;
import org.example.item.Ingredient;
import org.example.map.WalkableTile;
import org.example.map.GameMap;
import org.example.map.Tile;
import org.example.map.station.AbstractStation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

    public class Chef {
        private static final Logger LOGGER = LoggerFactory.getLogger(org.example.chef.Chef.class.getName());

    private final String name;
    private Position position;
    private Direction direction;
    private Item inventory; 
    
    private volatile ChefActionState currentAction; 
    private boolean isActive; 

    public Chef(String id, String name, Position startPosition) {
        this.name = name;
        this.position = startPosition;
        this.direction = Direction.RIGHT; 
        this.inventory = null;
        this.currentAction = ChefActionState.IDLE;
        this.isActive = false;
    }

    public void move(Direction dir, GameMap map) {
        if (currentAction == ChefActionState.BUSY) {
            LOGGER.info(name + " is busy and cannot move.");
            return;
        }

        this.direction = dir; 

        int targetX = position.getX() + dir.dx;
        int targetY = position.getY() + dir.dy;

        Tile targetTile = map.getTile(targetX, targetY);

        if (targetTile.isWalkable()) {
            this.position.setX(targetX);
            this.position.setY(targetY);
        } else {
            LOGGER.debug(name + " bumped into an obstacle.");
        }
    }


    public void interact(GameMap map) {
        if (currentAction == ChefActionState.BUSY) return;
    
        int targetX = position.getX() + direction.dx;
        int targetY = position.getY() + direction.dy;
    
        try {
        Tile targetTile = map.getTile(targetX, targetY);
        targetTile.interact(this);
        
        } catch (Exception e) {
            LOGGER.error("Interaction error: {}", e.getMessage());
        }
}


    public void setInventory(Item item) {
        if (this.inventory != null && item != null) {
            if (tryCombine(this.inventory, item)) {
                return; 
            }
            LOGGER.warn("Inventory full! Cannot take " + item.getName());
            return;
        }
        this.inventory = item;
    }

    public Item getInventory() {
        return inventory;
    }

    public Item dropItem() {
        Item temp = this.inventory;
        this.inventory = null;
        return temp;
    }

    private boolean tryCombine(Item handItem, Item targetItem) {
        if (handItem instanceof KitchenUtensil && targetItem instanceof Dish) {
            return true;
        }
        return false;
    }


    public void performLongAction(int durationSeconds, Runnable onComplete) {
        if (currentAction == ChefActionState.BUSY) return;

        new Thread(() -> {
            try {
                LOGGER.info("{} started working (BUSY)...", name);
                this.currentAction = ChefActionState.BUSY;

                Thread.sleep(durationSeconds * 1000L);

                if (onComplete != null) {
                    onComplete.run();
                }

            } catch (InterruptedException e) {
                LOGGER.error("{} action interrupted!", name);
                Thread.currentThread().interrupt();
            } finally {
                this.currentAction = ChefActionState.IDLE;
                LOGGER.info("{} finished working (IDLE).", name);
            }
        }).start();
    }

    public Position getPosition() { return position; }
    public Direction getDirection() { return direction; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    public ChefActionState getCurrentAction() { return currentAction; }
    public String getName() { return name; }
}