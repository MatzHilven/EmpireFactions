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

public class FactionChatSpySubCommand implements SubCommand {

    private final EmpireFactions main;

    public FactionChatSpySubCommand(EmpireFactions main) {
        this.main = main;
    }

    @Override
    public void onCommand(CommandSender sender, Command command, String[] args) {
        Player player = (Player) sender;

        if (!main.getAdminManager().isIn(player)) {
            StringUtils.sendMessage(sender, Messager.NOT_IN_ADMIN_MODE);
            return;
        }

        if (args.length != 2) {
            StringUtils.sendMessage(sender, getUsage());
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

        if (faction.isInChatSpy(player)) {
            faction.removeFromChatSpy(player);
            StringUtils.sendMessage(sender, Messager.CHATSPY_DISABLED.replace("%faction%",faction.getName()));
        } else {
            faction.addToChatSpy(player);
            StringUtils.sendMessage(sender, Messager.CHATSPY_ENABLED.replace("%faction%",faction.getName()));
        }
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
        return "chatspy";
    }

    @Override
    public String getPermission() {
        return "empirefactions.admin";
    }

    @Override
    public String getUsage() {
        return Messager.USAGE_CHATSPY;
    }
}
