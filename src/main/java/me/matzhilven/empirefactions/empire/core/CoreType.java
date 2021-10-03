package me.matzhilven.empirefactions.empire.core;

public enum CoreType {
    BASE(300, "BASE"),
    OUTPOST(100, "OUTPOST");

    CoreType(int power, String name) {
        this.power = power;
        this.name = name;
    }

    private final int power;
    private final String name;

    public int getPower() {
        return power;
    }

    public String getName() {
        return name;
    }
}
