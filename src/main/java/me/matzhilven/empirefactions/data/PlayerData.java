package me.matzhilven.empirefactions.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface PlayerData {

    Map<UUID,PlayerData> users = new HashMap<>();

    static Map<UUID,PlayerData> get() {
        return users;
    }

    static PlayerData get(UUID uuid) {
        return users.get(uuid);
    }

    String getUUID();

    void setUUID(String uuid);

    int getPower();

    void setPower(int power);

    void addPower(int power);

    void removePower(int power);
}
