package me.matzhilven.empirefactions.empire.rank;

public enum EmpireRank {
    LEADER("Leader"),
    ADMIN("Admin"),
    MODERATOR("Moderator"),
    MEMBER("Member");

    private final String name;

    EmpireRank(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
