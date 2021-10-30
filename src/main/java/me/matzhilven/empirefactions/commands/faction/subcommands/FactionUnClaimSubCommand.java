package me.matzhilven.empirefactions.commands.faction.subcommands;

import me.matzhilven.empirefactions.EmpireFactions;
import me.matzhilven.empirefactions.commands.SubCommand;
import me.matzhilven.empirefactions.empire.Empire;
import me.matzhilven.empirefactions.empire.faction.Faction;
import me.matzhilven.empirefactions.utils.Messager;
import me.matzhilven.empirefactions.utils.StringUtils;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Optional;

public class FactionUnClaimSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionUnClaimSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        if (args.length < 1) {
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

        if (!faction.isClaimed(player.getLocation().getChunk())) {
            StringUtils.sendMessage(sender,Messager.NOT_CLAIMED);
            return;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
            faction.clearChunks();
            StringUtils.sendMessage(sender,Messager.UNCLAIM_ALL);
        }

        faction.removeChunk(player.getLocation().getChunk());
        StringUtils.sendMessage(sender,Messager.UNCLAIM);
    }

    @Override
    public String getName() {
        return "unclaim";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_UNCLAIM;
    }
}
