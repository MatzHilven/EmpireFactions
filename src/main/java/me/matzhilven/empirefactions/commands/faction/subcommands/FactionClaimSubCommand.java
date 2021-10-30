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

public class FactionClaimSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionClaimSubCommand(EmpireFactions main) {
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

        if (!empire.isInJurisdiction(player)) {
            StringUtils.sendMessage(sender,Messager.NOT_IN_JURISDICTION);
            return;
        }

        if (faction.getClaimed().size() == main.getConfig().getInt("max-claimed-chunks")) {
            StringUtils.sendMessage(sender,Messager.MAX_CLAIMED);
            return;
        }

        if (main.getEmpireManager().isClaimed(player.getLocation().getChunk()).isPresent()) {
            StringUtils.sendMessage(sender,Messager.ALREADY_CLAIMED);
            return;
        }

        if (!faction.canClaim()) {
            StringUtils.sendMessage(sender,Messager.INSUFFICIENT_POWER);
            return;
        }

        faction.addChunk(player.getLocation().getChunk());
        StringUtils.sendMessage(sender,Messager.CLAIM);
    }

    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_CLAIM;
    }
}
