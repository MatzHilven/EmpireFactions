package me.matzhilven.empirefactions.utils;

import me.matzhilven.empirefactions.EmpireFactions;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Logger {

    public static void log(String s) {
        Bukkit.getLogger().log(Level.INFO, String.format("[%s] " + s, EmpireFactions.getPlugin(EmpireFactions.class).getDescription().getName()));
    }

    public static void severe(String s) {
        Bukkit.getLogger().log(Level.SEVERE, String.format("[%s] " + s, EmpireFactions.getPlugin(EmpireFactions.class).getDescription().getName()));
    }
}
