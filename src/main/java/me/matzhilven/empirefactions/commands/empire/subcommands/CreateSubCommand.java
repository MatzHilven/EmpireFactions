package me.matzhilven.empirefactions.commands.empire.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CreateSubCommand implements SubCommand {

    private final EmpireFactions main;

    public CreateSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length != 3) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        if (Bukkit.getPlayer(args[2]) == null) {
            StringUtils.sendMessage(sender, Messager.INVALID_TARGET);
            return;
        }

        main.getEmpireManager().addEmpire(new Empire(args[1], Bukkit.getPlayer(args[2]).getUniqueId()));
        StringUtils.sendMessage(sender, Messager.CREATE_SUCCESS.replace("%empire%", args[1]));
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_CREATE;
    }

    @Override
    public String getPermission() {
        return "empire.create";
    }
}
