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

public class FactionSetTagSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionSetTagSubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        Player player = (Player) sender;

        if (main.getAdminManager().isIn(player)) {
            if (args.length != 3) {
                StringUtils.sendMessage(sender, Messager.HELP_ADMIN);
                return;
            }

            Faction faction = null;

            for (Empire loopEmpire : main.getEmpireManager().getEmpires()) {
                Optional<Faction> optionalFaction = loopEmpire.getFaction(args[1]);
                if (optionalFaction.isPresent()) {
                    faction = optionalFaction.get();
                    break;
                }
            }

            if (faction == null) {
                StringUtils.sendMessage(sender, Messager.INVALID_FACTION);
                return;
            }

            faction.setTag(args[2]);

            StringUtils.sendMessage(sender, Messager.SET_TAG.replace("%tag%", args[2]));
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

        String tag = args[1];

        faction.setTag(tag);

        StringUtils.sendMessage(sender, Messager.SET_TAG.replace("%tag%", tag));
    }

    @Override
    public String getName() {
        return "settag";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_SET_TAG;
    }
}
