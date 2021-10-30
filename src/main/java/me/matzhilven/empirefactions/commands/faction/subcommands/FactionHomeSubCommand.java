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

import java.util.ArrayList;
import java.util.Optional;

public class FactionHomeSubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionHomeSubCommand(EmpireFactions main) {
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

        if (args.length == 1) {
            if (faction.getHome() == null) {
                StringUtils.sendMessage(sender, Messager.INVALID_HOME);
                return;
            }

            player.teleport(faction.getHome());
            StringUtils.sendMessage(sender, Messager.HOME);
        } else {
            Optional<Faction> optionalTargetFaction = empire.getFaction(args[1]);
            if (!optionalTargetFaction.isPresent()) {
                StringUtils.sendMessage(sender, Messager.INVALID_FACTION);
                return;
            }

            Faction targetFaction = optionalTargetFaction.get();

            if (!targetFaction.isAlly(faction)) {
                StringUtils.sendMessage(sender, Messager.INVALID_ALLY.replace("%faction%", targetFaction.getName()));
                return;
            }

            if (targetFaction.getHome() == null) {
                StringUtils.sendMessage(sender, Messager.INVALID_HOME_OTHER.replace("%faction%", targetFaction.getName()));
                return;
            }

            player.teleport(targetFaction.getHome());
            StringUtils.sendMessage(sender, Messager.HOME_OTHER.replace("%faction%", targetFaction.getName()));
        }


    }

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_HOME;
    }
}
