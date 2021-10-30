package me.matzhilven.empirefactions.data.mysql;

import me.matzhilven.empirefactions.data.PlayerData;

public class MySQLPlayerData implements PlayerData {

    private String UUID;

    private int power;

    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public void setUUID(String uuid) {
        this.UUID = uuid;
    }

    @Override
    public int getPower() {
        return this.power;
    }

    @Override
    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public void addPower(int power) {
        this.power += power;
    }

    @Override
    public void removePower(int power) {
        if (this.power - power < 0) {
            this.power = 0;
            return;
        }
        this.power -= power;
    }

}
