package me.matzhilven.empirefactions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public interface SubCommand {

    void onCommand(CommandSender sender, Command command, String[] args);

    default ArrayList<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    String getName();

    String getUsage();

    default String getPermission() {
        return null;
    }

    default String[] getAliases() {
        return new String[]{};
    }

}
