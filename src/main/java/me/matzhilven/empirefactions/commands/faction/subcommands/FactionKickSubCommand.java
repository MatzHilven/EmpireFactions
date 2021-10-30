package me.matzhilven.empirefactions.commands.faction.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.utils.FontMetrics;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionKickSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionKickSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        Player player = (Player) sender;

        if (main.getAdminManager().isIn(player)) {
            if (args.length != 2) {
                StringUtils.sendMessage(sender, Messager.HELP_ADMIN);
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                StringUtils.sendMessage(sender,Messager.INVALID_TARGET);
                return;
            }

            Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

            if (!optionalEmpire.isPresent()) {
                StringUtils.sendMessage(sender,Messager.NOT_IN_EMPIRE);
                return;
            }

            Empire empire = optionalEmpire.get();

            Optional<Faction> optionalFaction = empire.getFaction(target);

            if (!optionalFaction.isPresent()) {
                StringUtils.sendMessage(sender,Messager.NOT_IN_FACTION);
                return;
            }

            Faction faction = optionalFaction.get();

            if (faction.getLeader().toString().equals(target.getUniqueId().toString())) {
                StringUtils.sendMessage(sender,Messager.KICK_LEADER);
                return;
            }

            faction.kick(target);

            StringUtils.sendMessage(sender,Messager.KICKED_PLAYER.replace("%player%", target.getName()));
            return;
        }

        if (args.length != 2) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender,Messager.NOT_IN_EMPIRE);
            return;
        }

        Empire empire = optionalEmpire.get();

        Optional<Faction> optionalFaction = empire.getFaction(player);

        if (!optionalFaction.isPresent()) {
            StringUtils.sendMessage(sender,Messager.NOT_IN_FACTION);
            return;
        }

        Faction faction = optionalFaction.get();

        if (!faction.isLeader(player)) {
            StringUtils.sendMessage(sender,Messager.INVALID_PERMISSION);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null || !faction.isIn(target)) {
            StringUtils.sendMessage(sender,Messager.INVALID_TARGET);
            return;
        }

        faction.kick(target);
        StringUtils.sendMessage(sender,Messager.KICKED_PLAYER.replace("%player%", target.getName()));

    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_KICK;
    }
}
