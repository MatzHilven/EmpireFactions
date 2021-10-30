package me.matzhilven.empirefactions.commands.faction.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class FactionReloadSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionReloadSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (!main.getAdminManager().isIn((Player) sender)) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_ADMIN_MODE);
            return;
        }

        if (args.length != 1) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        main.reloadFiles();
        StringUtils.sendMessage(sender, Messager.RELOADED);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "empirefactions.admin";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_ADMIN;
    }
}
