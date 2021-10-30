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
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class FactionAdminSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionAdminSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length != 1) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Player player = (Player) sender;

        if (main.getAdminManager().isIn(player)) {
            StringUtils.sendMessage(sender, Messager.ADMIN_OFF);
            main.getAdminManager().removePlayer(player);
        } else {
            StringUtils.sendMessage(sender, Messager.ADMIN_ON);
            main.getAdminManager().addPlayer(player);
        }
    }

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_ADMIN;
    }

    @Override
    public String getPermission() {
        return "empirefactions.admin";
    }
}
