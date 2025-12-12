package org.example.model;

public class Order {
    private int id;
    private String recipe;
    private int timeLeft;
    private int reward;

    public Order(int id, String recipe, int timeLeft, int reward) {
        this.id = id;
        this.recipe = recipe;
        this.timeLeft = timeLeft;
        this.reward = reward;
    }

    public int getId() { return id; }
    public String getRecipe() { return recipe; }
    public int getTimeLeft() { return timeLeft; }
    public int getReward() { return reward; }

    @Override
    public String toString() {
        return "#" + id + "  " + recipe + " (" + timeLeft + "s)";
    }
}