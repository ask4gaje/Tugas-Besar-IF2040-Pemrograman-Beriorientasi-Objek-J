package org.example.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Order {
    private int id;
    private String recipe;
    private IntegerProperty timeLeft;
    private int reward;

    public Order(int id, String recipe, int timeLeft, int reward) {
        this.id = id;
        this.recipe = recipe;
        this.timeLeft = new SimpleIntegerProperty(timeLeft);
        this.reward = reward;
    }

    public int getId() { return id; }
    public String getRecipe() { return recipe; }
    public int getTimeLeft() { return timeLeft.get(); }
    public int getReward() { return reward; }

    public void setTimeLeft(int timeLeft) { this.timeLeft.set(timeLeft); }

    public IntegerProperty timeLeftProperty() { return timeLeft; }

    @Override
    public String toString() {
        return "#" + id + "  " + recipe + " (" + timeLeft.get() + "s)";
    }
}