package me.matzhilven.empirefactions.commands.faction.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FactionDisbandSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionDisbandSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length != 1) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Player player = (Player) sender;

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

        empire.removeSubFaction(faction);

        StringUtils.sendMessage(sender, Messager.DISBAND_SUCCESS_FACTION.replace("%faction%", faction.getNameColored()));
        empire.getStaff().stream().filter(player1 -> !player1.equals(player))
                .forEach(player1 -> StringUtils.sendMessage(player1, Messager.DISBAND_SUCCESS_FACTION.replace("%faction%", faction.getNameColored())));
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

        if (!optionalEmpire.isPresent()) return null;

        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1],
                    optionalEmpire.get().getSubFactions().stream().map(Faction::getName).collect(Collectors.toList()),
                    new ArrayList<>());
        }
        return null;
    }

    @Override
    public String getName() {
        return "disband";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_DISBAND_FACTION;
    }

    @Override
    public String getPermission() {
        return "faction.disband";
    }
}
