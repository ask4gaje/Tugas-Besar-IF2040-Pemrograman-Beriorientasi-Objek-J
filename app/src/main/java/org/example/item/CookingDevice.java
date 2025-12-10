package org.example.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface CookingDevice {
    boolean isPortable();
    int capacity();
    boolean canAccept(Ingredient ingredient);
    void addIngredient(Ingredient ingredient);
    void startCooking();
}
