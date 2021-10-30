package me.matzhilven.empirefactions.commands.faction.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.empire.rank.EmpireRank;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Optional;

public class FactionTransferSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionTransferSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length != 2) {
            StringUtils.sendMessage(sender, getUsage());
            return;
        }

        Player player = (Player) sender;

        Optional<Empire> optionalEmpire = main.getEmpireManager().getEmpire(player);

        if (!optionalEmpire.isPresent()) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_EMPIRE);
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

        if (Bukkit.getPlayer(args[1]) == null) {
            StringUtils.sendMessage(sender, Messager.INVALID_TARGET);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (player == target) {
            StringUtils.sendMessage(sender, Messager.INVALID_TARGET_SELF);
            return;
        }

        faction.setLeader(target.getUniqueId());

        StringUtils.sendMessage(sender, Messager.TRANSFER_SUCCESS_FACTION.replace("%target%", target.getName()));
        StringUtils.sendMessage(target, Messager.TRANSFER_SUCCESS_FACTION_TARGET);


    }

    @Override
    public String getName() {
        return "transfer";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_TRANSFER_FACTION;
    }
}
