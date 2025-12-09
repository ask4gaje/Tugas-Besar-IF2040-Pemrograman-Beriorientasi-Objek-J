package items.interfaces;

public interface Preparable {
    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();
    void chop();
    void cook();
    void burn(); 
}


package items.interfaces;

public interface CookingDevice {
    boolean isPortable();
    int capacity();
    boolean canAccept(Preparable ingredient);
    void addIngredient(Preparable ingredient);
    void startCooking();
}