package org.example.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Preparable {
    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();
    void chop();
    void cooked();
    void burn();
    void cooking();
}

