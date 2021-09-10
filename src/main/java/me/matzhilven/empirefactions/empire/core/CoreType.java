package me.matzhilven.empirefactions.empire.core;

public enum CoreType {
    BASE(300),
    OUTPOST(100);

    CoreType(int power) {
        this.power = power;
    }

    private final int power;

    public int getPower() {
        return power;
    }
}
