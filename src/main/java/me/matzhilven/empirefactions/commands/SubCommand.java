package me.matzhilven.empirefactions.commands;

import me.matzhilven.empirefactions.EmpireFactions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public interface SubCommand {

    void onCommand(CommandSender sender, Command command, String[] args);

    ArrayList<String> onTabComplete(CommandSender sender, String[] args);

    String getName();

    String getUsage();

    String getPermission();
}
