package me.matzhilven.empirefactions.empire.rank;

public enum FactionRank {
    LEADER("Leader"),
    ADMIN("Admin"),
    MODERATOR("Moderator"),
    MEMBER("Member");

    private final String name;

    FactionRank(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
